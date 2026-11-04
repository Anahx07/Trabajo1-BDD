package cooperativa.modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Clase base abstracta de las cuentas. Contiene las reglas comunes (monto > 0,
 * cuenta activa, fechas cronologicas, cierre) y delega en las subclases las reglas
 * que cambian segun el tipo (saldo minimo / sobregiro / intereses): polimorfismo.
 */
public abstract class Cuenta {

    private final String numero;
    private final Socio socio;
    private BigDecimal saldo;
    private boolean activa;
    private final ArrayList<Movimiento> movimientos = new ArrayList<>();

    /** Constructor para una cuenta nueva: saldo 0 y activa. */
    protected Cuenta(String numero, Socio socio) {
        this(numero, socio, BigDecimal.ZERO, true);
    }

    /** Constructor completo, usado tambien al cargar desde archivo. */
    protected Cuenta(String numero, Socio socio, BigDecimal saldo, boolean activa) {
        if (numero == null || numero.trim().isEmpty() || numero.contains(";")) {
            throw new OperacionInvalidaException("Numero de cuenta invalido.");
        }
        if (socio == null) {
            throw new OperacionInvalidaException("La cuenta debe pertenecer a un socio.");
        }
        if (saldo == null) {
            throw new OperacionInvalidaException("Saldo inicial invalido.");
        }
        this.numero = numero.trim();
        this.socio = socio;
        this.saldo = saldo.setScale(2, RoundingMode.HALF_UP);
        this.activa = activa;
    }

    // ---------- Reglas que dependen del tipo de cuenta (polimorfismo) ----------

    /** "AHORRO" o "CORRIENTE". */
    public abstract String getTipo();

    /** Lanza OperacionInvalidaException si el saldo resultante de un retiro no esta permitido. */
    protected abstract void validarSaldoTrasRetiro(BigDecimal saldoResultante);

    /** Interes mensual que generaria un saldo base (0.00 si la cuenta no genera intereses). */
    public abstract BigDecimal calcularInteresMensual(BigDecimal saldoBase);

    // ---------- Operaciones ----------

    public Movimiento depositar(BigDecimal monto, LocalDate fecha, String descripcion) {
        return depositar(monto, fecha, descripcion, false);
    }

    public Movimiento depositar(BigDecimal monto, LocalDate fecha, String descripcion, boolean porTransferencia) {
        BigDecimal m = normalizarMonto(monto);
        validarOperable(fecha);
        saldo = saldo.add(m);
        return registrar(TipoMovimiento.DEPOSITO, m, fecha, descripcion, porTransferencia);
    }

    public Movimiento retirar(BigDecimal monto, LocalDate fecha, String descripcion) {
        return retirar(monto, fecha, descripcion, false);
    }

    public Movimiento retirar(BigDecimal monto, LocalDate fecha, String descripcion, boolean porTransferencia) {
        BigDecimal m = normalizarMonto(monto);
        validarOperable(fecha);
        BigDecimal resultante = saldo.subtract(m);
        validarSaldoTrasRetiro(resultante);
        saldo = resultante;
        return registrar(TipoMovimiento.RETIRO, m, fecha, descripcion, porTransferencia);
    }

    /** Acredita un interes ya calculado. Uso interno de las subclases que generan intereses. */
    protected Movimiento registrarInteres(BigDecimal interes, LocalDate fecha, String descripcion) {
        BigDecimal m = normalizarMonto(interes);
        validarOperable(fecha);
        saldo = saldo.add(m);
        return registrar(TipoMovimiento.INTERES, m, fecha, descripcion, false);
    }

    /**
     * Cierra la cuenta (la marca inactiva). Solo es posible con saldo exactamente cero.
     */
    public void cerrar() {
        if (!activa) {
            throw new OperacionInvalidaException("La cuenta " + numero + " ya esta inactiva.");
        }
        if (saldo.compareTo(BigDecimal.ZERO) != 0) {
            throw new OperacionInvalidaException("No se puede cerrar la cuenta " + numero
                    + ": su saldo es " + dinero(saldo) + " (debe ser $0.00).");
        }
        activa = false;
    }

    // ---------- Consultas ----------

    /** Saldo despues del ultimo movimiento con fecha <= la indicada (0.00 si no hay ninguno). */
    public BigDecimal saldoAl(LocalDate fecha) {
        BigDecimal resultado = BigDecimal.ZERO.setScale(2);
        for (Movimiento m : movimientos) {
            if (!m.getFecha().isAfter(fecha)) {
                resultado = m.getSaldoDespues();
            }
        }
        return resultado;
    }

    public boolean tieneMovimientos() {
        return !movimientos.isEmpty();
    }

    public boolean tieneMovimiento(TipoMovimiento tipo, String descripcion) {
        for (Movimiento m : movimientos) {
            if (m.getTipo() == tipo && m.getDescripcion().equals(descripcion)) {
                return true;
            }
        }
        return false;
    }

    /** Agrega un movimiento leido de archivo, sin revalidar ni recalcular el saldo. */
    public void cargarMovimiento(Movimiento m) {
        movimientos.add(m);
    }

    // ---------- Rollback (usado por la transferencia segura) ----------

    /** Fotografia inmutable del estado de la cuenta. */
    public static final class Estado {
        private final BigDecimal saldo;
        private final int cantidadMovimientos;
        private final boolean activa;

        private Estado(BigDecimal saldo, int cantidadMovimientos, boolean activa) {
            this.saldo = saldo;
            this.cantidadMovimientos = cantidadMovimientos;
            this.activa = activa;
        }
    }

    public Estado capturarEstado() {
        return new Estado(saldo, movimientos.size(), activa);
    }

    public void restaurarEstado(Estado e) {
        this.saldo = e.saldo;
        this.activa = e.activa;
        while (movimientos.size() > e.cantidadMovimientos) {
            movimientos.remove(movimientos.size() - 1);
        }
    }

    // ---------- Validaciones privadas ----------

    private static BigDecimal normalizarMonto(BigDecimal monto) {
        if (monto == null || monto.signum() <= 0) {
            throw new OperacionInvalidaException("El monto debe ser mayor que cero.");
        }
        if (monto.stripTrailingZeros().scale() > 2) {
            throw new OperacionInvalidaException("El monto admite como maximo 2 decimales.");
        }
        return monto.setScale(2, RoundingMode.UNNECESSARY);
    }

    private void validarOperable(LocalDate fecha) {
        if (!activa) {
            throw new OperacionInvalidaException("La cuenta " + numero + " esta inactiva.");
        }
        if (fecha == null) {
            throw new OperacionInvalidaException("La fecha es obligatoria.");
        }
        if (!movimientos.isEmpty()) {
            LocalDate ultima = movimientos.get(movimientos.size() - 1).getFecha();
            if (fecha.isBefore(ultima)) {
                throw new OperacionInvalidaException("La fecha " + fecha
                        + " es anterior al ultimo movimiento de la cuenta (" + ultima + ").");
            }
        }
    }

    private Movimiento registrar(TipoMovimiento tipo, BigDecimal monto, LocalDate fecha,
            String descripcion, boolean porTransferencia) {
        Movimiento m = new Movimiento(numero, fecha, tipo, monto, saldo, porTransferencia, descripcion);
        movimientos.add(m);
        return m;
    }

    /** Formatea dinero para mensajes: $12.50 o -$12.50. */
    protected static String dinero(BigDecimal v) {
        return (v.signum() < 0 ? "-$" : "$") + v.abs().toPlainString();
    }

    // ---------- Getters ----------

    public String getNumero() { return numero; }
    public Socio getSocio() { return socio; }
    public BigDecimal getSaldo() { return saldo; }
    public boolean isActiva() { return activa; }

    public ArrayList<Movimiento> getMovimientos() {
        return new ArrayList<>(movimientos);
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Cuenta) && ((Cuenta) o).numero.equals(numero);
    }

    @Override
    public int hashCode() {
        return numero.hashCode();
    }

    @Override
    public String toString() {
        return getTipo() + " " + numero + " | saldo $" + saldo.toPlainString()
                + " | " + (activa ? "ACTIVA" : "INACTIVA");
    }
}
