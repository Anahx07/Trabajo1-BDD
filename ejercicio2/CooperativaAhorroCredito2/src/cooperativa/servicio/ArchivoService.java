package cooperativa.servicio;

import cooperativa.modelo.Cuenta;
import cooperativa.modelo.CuentaAhorro;
import cooperativa.modelo.CuentaCorriente;
import cooperativa.modelo.Movimiento;
import cooperativa.modelo.Socio;
import cooperativa.modelo.TipoMovimiento;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Persistencia en archivos TXT (UTF-8, campos separados por ';'):
 *   socios.txt      -> id;nombre;cedula
 *   cuentas.txt     -> numero;tipo;idSocio;saldo;activa
 *   movimientos.txt -> numeroCuenta;fecha;tipo;monto;saldoDespues;porTransferencia;descripcion
 * Las lineas vacias y las que empiezan con '#' se ignoran.
 * Al guardar se escribe primero en archivos temporales y luego se reemplazan los originales,
 * para no dejar un archivo a medias si ocurre un error.
 */
public class ArchivoService {

    private static final String SOCIOS = "socios.txt";
    private static final String CUENTAS = "cuentas.txt";
    private static final String MOVIMIENTOS = "movimientos.txt";

    private final Path directorio;
    private final Path directorioPrueba;

    public ArchivoService(String directorio, String directorioPrueba) {
        this.directorio = Paths.get(directorio);
        this.directorioPrueba = Paths.get(directorioPrueba);
    }

    // ------------------------------------------------------------------ carga

    /** Reemplaza el contenido de las listas con lo leido de los archivos (si no existen, quedan vacias). */
    public void cargar(ArrayList<Socio> socios, ArrayList<Cuenta> cuentas) throws IOException {
        socios.clear();
        cuentas.clear();

        for (String linea : leerLineas(directorio.resolve(SOCIOS))) {
            try {
                String[] p = linea.split(";", -1);
                exigirCampos(p, 3);
                socios.add(new Socio(Integer.parseInt(p[0].trim()), p[1], p[2]));
            } catch (RuntimeException e) {
                throw new IOException("Linea invalida en " + SOCIOS + ": " + linea, e);
            }
        }

        for (String linea : leerLineas(directorio.resolve(CUENTAS))) {
            try {
                String[] p = linea.split(";", -1);
                exigirCampos(p, 5);
                Socio socio = buscarSocio(socios, Integer.parseInt(p[2].trim()));
                BigDecimal saldo = new BigDecimal(p[3].trim());
                boolean activa = Boolean.parseBoolean(p[4].trim());
                Cuenta c;
                if (p[1].trim().equalsIgnoreCase("AHORRO")) {
                    c = new CuentaAhorro(p[0], socio, saldo, activa);
                } else if (p[1].trim().equalsIgnoreCase("CORRIENTE")) {
                    c = new CuentaCorriente(p[0], socio, saldo, activa);
                } else {
                    throw new IllegalArgumentException("Tipo de cuenta desconocido: " + p[1]);
                }
                socio.agregarCuenta(c);
                cuentas.add(c);
            } catch (RuntimeException e) {
                throw new IOException("Linea invalida en " + CUENTAS + ": " + linea, e);
            }
        }

        for (String linea : leerLineas(directorio.resolve(MOVIMIENTOS))) {
            try {
                String[] p = linea.split(";", 7);
                exigirCampos(p, 7);
                Cuenta c = buscarCuenta(cuentas, p[0].trim());
                c.cargarMovimiento(new Movimiento(c.getNumero(), LocalDate.parse(p[1].trim()),
                        TipoMovimiento.valueOf(p[2].trim()), new BigDecimal(p[3].trim()),
                        new BigDecimal(p[4].trim()), Boolean.parseBoolean(p[5].trim()), p[6]));
            } catch (RuntimeException e) {
                throw new IOException("Linea invalida en " + MOVIMIENTOS + ": " + linea, e);
            }
        }
    }

    // ---------------------------------------------------------------- guardado

    public void guardar(List<Socio> socios, List<Cuenta> cuentas) throws IOException {
        List<String> ls = new ArrayList<>();
        ls.add("# id;nombre;cedula");
        for (Socio s : socios) {
            ls.add(s.getId() + ";" + s.getNombre() + ";" + s.getCedula());
        }

        List<String> lc = new ArrayList<>();
        lc.add("# numero;tipo;idSocio;saldo;activa");
        List<String> lm = new ArrayList<>();
        lm.add("# numeroCuenta;fecha;tipo;monto;saldoDespues;porTransferencia;descripcion");
        for (Cuenta c : cuentas) {
            lc.add(c.getNumero() + ";" + c.getTipo() + ";" + c.getSocio().getId() + ";"
                    + c.getSaldo().toPlainString() + ";" + c.isActiva());
            for (Movimiento m : c.getMovimientos()) {
                lm.add(m.getNumeroCuenta() + ";" + m.getFecha() + ";" + m.getTipo() + ";"
                        + m.getMonto().toPlainString() + ";" + m.getSaldoDespues().toPlainString() + ";"
                        + m.isPorTransferencia() + ";" + m.getDescripcion());
            }
        }

        Files.createDirectories(directorio);
        Path ts = temporal(SOCIOS), tc = temporal(CUENTAS), tm = temporal(MOVIMIENTOS);
        Files.write(ts, ls, StandardCharsets.UTF_8);
        Files.write(tc, lc, StandardCharsets.UTF_8);
        Files.write(tm, lm, StandardCharsets.UTF_8);
        Files.move(ts, directorio.resolve(SOCIOS), StandardCopyOption.REPLACE_EXISTING);
        Files.move(tc, directorio.resolve(CUENTAS), StandardCopyOption.REPLACE_EXISTING);
        Files.move(tm, directorio.resolve(MOVIMIENTOS), StandardCopyOption.REPLACE_EXISTING);
    }

    /** Copia los archivos de datos_prueba/ sobre datos/ (deja el sistema en el estado inicial de pruebas). */
    public void restaurarDatosPrueba() throws IOException {
        Files.createDirectories(directorio);
        for (String nombre : new String[]{SOCIOS, CUENTAS, MOVIMIENTOS}) {
            Path origen = directorioPrueba.resolve(nombre);
            if (!Files.exists(origen)) {
                throw new IOException("No se encontro " + origen + " (carpeta de datos de prueba).");
            }
            Files.copy(origen, directorio.resolve(nombre), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public boolean existenDatos() {
        return Files.exists(directorio.resolve(SOCIOS));
    }

    /** Guarda el texto de un reporte en reportes/nombre y devuelve la ruta creada. */
    public Path guardarReporte(String nombre, String contenido) throws IOException {
        Path carpeta = Paths.get("reportes");
        Files.createDirectories(carpeta);
        Path destino = carpeta.resolve(nombre);
        Files.write(destino, contenido.getBytes(StandardCharsets.UTF_8));
        return destino;
    }

    // ----------------------------------------------------------------- helpers

    private Path temporal(String nombre) {
        return directorio.resolve(nombre + ".tmp");
    }

    private static List<String> leerLineas(Path archivo) throws IOException {
        List<String> resultado = new ArrayList<>();
        if (!Files.exists(archivo)) {
            return resultado;
        }
        for (String linea : Files.readAllLines(archivo, StandardCharsets.UTF_8)) {
            String l = linea.trim();
            if (!l.isEmpty() && !l.startsWith("#")) {
                resultado.add(l);
            }
        }
        return resultado;
    }

    private static void exigirCampos(String[] partes, int esperados) {
        if (partes.length != esperados) {
            throw new IllegalArgumentException("Se esperaban " + esperados + " campos y hay " + partes.length);
        }
    }

    private static Socio buscarSocio(List<Socio> socios, int id) {
        for (Socio s : socios) {
            if (s.getId() == id) {
                return s;
            }
        }
        throw new IllegalArgumentException("Socio inexistente: " + id);
    }

    private static Cuenta buscarCuenta(List<Cuenta> cuentas, String numero) {
        for (Cuenta c : cuentas) {
            if (c.getNumero().equals(numero)) {
                return c;
            }
        }
        throw new IllegalArgumentException("Cuenta inexistente: " + numero);
    }
}
