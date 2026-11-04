package cooperativa.servicio;

import cooperativa.modelo.Cuenta;
import cooperativa.modelo.CuentaAhorro;
import cooperativa.modelo.CuentaCorriente;
import cooperativa.modelo.Movimiento;
import cooperativa.modelo.OperacionInvalidaException;
import cooperativa.modelo.Socio;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Logica de negocio de la cooperativa. Toda operacion que modifica datos se ejecuta de forma
 * atomica: se toma una fotografia de las cuentas afectadas, se aplica la operacion, se guardan los
 * archivos y, si cualquier paso falla, las cuentas vuelven a su estado anterior (rollback).
 */
public class CooperativaService {

    private final ArchivoService archivo;
    private final ArrayList<Socio> socios = new ArrayList<>();
    private final ArrayList<Cuenta> cuentas = new ArrayList<>();

    public CooperativaService(ArchivoService archivo) {
        this.archivo = archivo;
        recargar();
    }

    /** Vuelve a leer todo desde los archivos TXT. */
    public final void recargar() {
        try {
            archivo.cargar(socios, cuentas);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    // ------------------------------------------------------------------ socios

    public Socio registrarSocio(String nombre, String cedula) {
        int id = 1;
        for (Socio s : socios) {
            if (s.getCedula().equals(cedula == null ? "" : cedula.trim())) {
                throw new OperacionInvalidaException("Ya existe un socio con la cedula " + cedula + ".");
            }
            id = Math.max(id, s.getId() + 1);
        }
        Socio nuevo = new Socio(id, nombre, cedula);
        socios.add(nuevo);
        try {
            persistir();
        } catch (RuntimeException e) {
            socios.remove(nuevo);
            throw e;
        }
        return nuevo;
    }

    public Socio buscarSocio(int id) {
        for (Socio s : socios) {
            if (s.getId() == id) {
                return s;
            }
        }
        throw new OperacionInvalidaException("No existe el socio " + id + ".");
    }

    // ------------------------------------------------------------------ cuentas

    /**
     * Abre una cuenta. Ahorro: deposito inicial minimo $5.00. Corriente: deposito inicial opcional (>= 0).
     * El numero se genera automaticamente (AH-0001, CC-0001, ...).
     */
    public Cuenta abrirCuenta(int idSocio, String tipo, BigDecimal montoInicial, LocalDate fecha) {
        Socio socio = buscarSocio(idSocio);
        if (montoInicial == null || montoInicial.signum() < 0) {
            throw new OperacionInvalidaException("El deposito inicial no puede ser negativo.");
        }
        Cuenta nueva;
        if ("AHORRO".equalsIgnoreCase(tipo)) {
            if (montoInicial.compareTo(CuentaAhorro.SALDO_MINIMO) < 0) {
                throw new OperacionInvalidaException("Una cuenta de ahorros requiere un deposito inicial de al menos $"
                        + CuentaAhorro.SALDO_MINIMO.toPlainString() + ".");
            }
            nueva = new CuentaAhorro(generarNumero("AH-"), socio);
        } else if ("CORRIENTE".equalsIgnoreCase(tipo)) {
            nueva = new CuentaCorriente(generarNumero("CC-"), socio);
        } else {
            throw new OperacionInvalidaException("Tipo de cuenta desconocido: " + tipo + " (use AHORRO o CORRIENTE).");
        }
        socio.agregarCuenta(nueva);
        cuentas.add(nueva);
        try {
            if (montoInicial.signum() > 0) {
                nueva.depositar(montoInicial, fecha, "Apertura de cuenta");
            }
            persistir();
        } catch (RuntimeException e) {
            cuentas.remove(nueva);
            socio.quitarCuenta(nueva);
            throw e;
        }
        return nueva;
    }

    public Cuenta buscarCuenta(String numero) {
        for (Cuenta c : cuentas) {
            if (c.getNumero().equalsIgnoreCase(numero == null ? "" : numero.trim())) {
                return c;
            }
        }
        throw new OperacionInvalidaException("No existe la cuenta " + numero + ".");
    }

    /**
     * Cierra una cuenta (solo con saldo 0.00). Si nunca tuvo movimientos se elimina; si tiene
     * movimientos NO se elimina: queda marcada como inactiva.
     *
     * @return true si fue eliminada, false si quedo inactiva
     */
    public boolean cerrarCuenta(String numero) {
        Cuenta c = buscarCuenta(numero);
        Cuenta.Estado antes = c.capturarEstado();
        boolean eliminar = !c.tieneMovimientos();
        try {
            c.cerrar();
            if (eliminar) {
                cuentas.remove(c);
                c.getSocio().quitarCuenta(c);
            }
            persistir();
            return eliminar;
        } catch (RuntimeException e) {
            c.restaurarEstado(antes);
            if (eliminar && !cuentas.contains(c)) {
                cuentas.add(c);
                c.getSocio().agregarCuenta(c);
            }
            throw e;
        }
    }

    // -------------------------------------------------------------- movimientos

    public void depositar(String numero, BigDecimal monto, LocalDate fecha) {
        final Cuenta c = buscarCuenta(numero);
        ejecutarAtomico(() -> c.depositar(monto, fecha, "Deposito en ventanilla"), Arrays.asList(c));
    }

    public void retirar(String numero, BigDecimal monto, LocalDate fecha) {
        final Cuenta c = buscarCuenta(numero);
        ejecutarAtomico(() -> c.retirar(monto, fecha, "Retiro en ventanilla"), Arrays.asList(c));
    }

    /**
     * Transferencia segura: retiro en el origen + deposito en el destino. Si cualquiera de las dos
     * operaciones (o el guardado en archivo) falla, ambas cuentas regresan a su estado anterior.
     */
    public void transferir(String numeroOrigen, String numeroDestino, BigDecimal monto, LocalDate fecha) {
        final Cuenta origen = buscarCuenta(numeroOrigen);
        final Cuenta destino = buscarCuenta(numeroDestino);
        if (origen.equals(destino)) {
            throw new OperacionInvalidaException("No se puede transferir a la misma cuenta.");
        }
        ejecutarAtomico(() -> {
            origen.retirar(monto, fecha, "Transferencia a " + destino.getNumero(), true);
            destino.depositar(monto, fecha, "Transferencia de " + origen.getNumero(), true);
        }, Arrays.asList(origen, destino));
    }

    /**
     * Acredita los intereses del mes a todas las cuentas de ahorro activas (una sola vez por mes).
     *
     * @return total de intereses acreditados
     */
    public BigDecimal acreditarIntereses(YearMonth mes, LocalDate fecha) {
        final List<Cuenta> ahorros = new ArrayList<>();
        for (Cuenta c : cuentas) {
            if (c instanceof CuentaAhorro && c.isActiva()) {
                ahorros.add(c);
            }
        }
        final BigDecimal[] total = {BigDecimal.ZERO.setScale(2)};
        ejecutarAtomico(() -> {
            for (Cuenta c : ahorros) {
                Movimiento m = ((CuentaAhorro) c).acreditarInteres(mes, fecha);
                if (m != null) {
                    total[0] = total[0].add(m.getMonto());
                }
            }
        }, ahorros);
        return total[0];
    }

    // ------------------------------------------------------------------ consultas

    public ArrayList<Socio> getSocios() {
        return new ArrayList<>(socios);
    }

    public ArrayList<Cuenta> getCuentas() {
        return new ArrayList<>(cuentas);
    }

    // -------------------------------------------------------------------- internos

    private void ejecutarAtomico(Runnable accion, List<Cuenta> afectadas) {
        List<Cuenta.Estado> estados = new ArrayList<>();
        for (Cuenta c : afectadas) {
            estados.add(c.capturarEstado());
        }
        try {
            accion.run();
            persistir();
        } catch (RuntimeException e) {
            for (int i = 0; i < afectadas.size(); i++) {
                afectadas.get(i).restaurarEstado(estados.get(i));
            }
            throw e;
        }
    }

    private void persistir() {
        try {
            archivo.guardar(socios, cuentas);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo guardar en los archivos TXT: " + e.getMessage(), e);
        }
    }

    private String generarNumero(String prefijo) {
        int n = 1;
        while (true) {
            String candidato = prefijo + String.format("%04d", n);
            boolean existe = false;
            for (Cuenta c : cuentas) {
                if (c.getNumero().equals(candidato)) {
                    existe = true;
                    break;
                }
            }
            if (!existe) {
                return candidato;
            }
            n++;
        }
    }
}
