package cooperativa;

import cooperativa.modelo.Cuenta;
import cooperativa.modelo.Socio;
import cooperativa.servicio.ArchivoService;
import cooperativa.servicio.CooperativaService;
import cooperativa.servicio.ReporteService;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Scanner;

/**
 * Punto de entrada. Sin argumentos abre el menu interactivo; con el argumento "demo"
 * ejecuta automaticamente los casos de prueba sobre los datos de prueba.
 */
public class Main {

    private static final Scanner IN = new Scanner(System.in);
    private static CooperativaService servicio;
    private static ReporteService reportes;
    private static ArchivoService archivo;

    public static void main(String[] args) {
        archivo = new ArchivoService("datos", "datos_prueba");
        try {
            if (args.length > 0 && args[0].equalsIgnoreCase("demo")) {
                iniciarDemo();
                return;
            }
            if (!archivo.existenDatos()) {
                archivo.restaurarDatosPrueba();
            }
            iniciar();
            menu();
        } catch (IOException | RuntimeException e) {
            System.out.println("ERROR FATAL: " + e.getMessage());
        }
    }

    private static void iniciar() {
        servicio = new CooperativaService(archivo);
        reportes = new ReporteService(servicio);
    }

    // =================================================================== DEMO

    private static void iniciarDemo() throws IOException {
        archivo.restaurarDatosPrueba();
        iniciar();
        ejecutarDemo();
    }

    private static void ejecutarDemo() {
        titulo("DEMO - CASOS DE PRUEBA (datos de prueba restaurados)");
        System.out.println(reportes.estadoDeCuenta("AH-0001"));
        System.out.println(reportes.interesesDelMes(YearMonth.of(2026, 10)));
        System.out.println(reportes.cuentasEnSobregiro());
        System.out.println(reportes.auditoriaSaldos());

        titulo("CP-01 a CP-05: depositos y retiros");
        probar("CP-01 Deposito de $0.00 en AH-0001 (debe rechazarse)",
                () -> servicio.depositar("AH-0001", new BigDecimal("0"), LocalDate.of(2026, 11, 1)));
        probar("CP-02 Retiro de -$10 en AH-0001 (debe rechazarse)",
                () -> servicio.retirar("AH-0001", new BigDecimal("-10"), LocalDate.of(2026, 11, 1)));
        probar("CP-03 Retiro de $1.00 en AH-0003 (saldo 5.00 -> 4.00 < minimo, debe rechazarse)",
                () -> servicio.retirar("AH-0003", new BigDecimal("1.00"), LocalDate.of(2026, 11, 1)));
        probar("CP-04 Retiro de $350 en CC-0001 (180 -> -170, sobregiro permitido)",
                () -> servicio.retirar("CC-0001", new BigDecimal("350"), LocalDate.of(2026, 11, 1)));
        probar("CP-05 Retiro de $50 en CC-0001 (-170 -> -220 > $200, debe rechazarse)",
                () -> servicio.retirar("CC-0001", new BigDecimal("50"), LocalDate.of(2026, 11, 1)));

        titulo("CP-06: intereses de octubre 2026");
        probar("CP-06 Acreditar intereses de 2026-10 el 2026-11-01",
                () -> System.out.println("   Total acreditado: $"
                        + servicio.acreditarIntereses(YearMonth.of(2026, 10), LocalDate.of(2026, 11, 1))));
        probar("CP-06b Acreditar de nuevo el mismo mes (no debe duplicar: total 0.00)",
                () -> System.out.println("   Total acreditado: $"
                        + servicio.acreditarIntereses(YearMonth.of(2026, 10), LocalDate.of(2026, 11, 1))));

        titulo("CP-07 a CP-11: transferencias");
        probar("CP-07 Transferir $150 de AH-0001 a AH-0002 (valida)",
                () -> servicio.transferir("AH-0001", "AH-0002", new BigDecimal("150"), LocalDate.of(2026, 11, 2)));
        probar("CP-08 Transferir a la misma cuenta AH-0001 -> AH-0001 (debe rechazarse)",
                () -> servicio.transferir("AH-0001", "AH-0001", new BigDecimal("10"), LocalDate.of(2026, 11, 2)));
        probar("CP-09 Transferir $10 de AH-0003 a AH-0001 (fondos insuficientes en el origen, rechazada)",
                () -> servicio.transferir("AH-0003", "AH-0001", new BigDecimal("10"), LocalDate.of(2026, 11, 2)));
        Cuenta origen = servicio.buscarCuenta("AH-0001");
        BigDecimal saldoOrigenAntes = origen.getSaldo();
        int movOrigenAntes = origen.getMovimientos().size();
        probar("CP-10 Transferir $20 de AH-0001 a AH-0004 INACTIVA (el deposito falla: rollback del retiro)",
                () -> servicio.transferir("AH-0001", "AH-0004", new BigDecimal("20"), LocalDate.of(2026, 11, 2)));
        System.out.println("   Verificacion: saldo AH-0001 antes $" + saldoOrigenAntes + ", despues $" + origen.getSaldo()
                + " | movimientos antes " + movOrigenAntes + ", despues " + origen.getMovimientos().size());
        probar("CP-11 Transferir $5000 de AH-0002 a CC-0002 (fondos insuficientes, rechazada)",
                () -> servicio.transferir("AH-0002", "CC-0002", new BigDecimal("5000"), LocalDate.of(2026, 11, 2)));

        titulo("CP-12 a CP-16: cierre, eliminacion y apertura de cuentas");
        probar("CP-12 Cerrar AH-0001 con saldo distinto de cero (debe rechazarse)",
                () -> servicio.cerrarCuenta("AH-0001"));
        probar("CP-13 Abrir CC-0003 para el socio 3 sin movimientos y cerrarla (se ELIMINA)", () -> {
            Cuenta nueva = servicio.abrirCuenta(3, "CORRIENTE", BigDecimal.ZERO, LocalDate.of(2026, 11, 3));
            System.out.println("   Cuenta creada: " + nueva);
            System.out.println("   Eliminada: " + servicio.cerrarCuenta(nueva.getNumero()));
        });
        probar("CP-14 Retirar todo de AH-0003 y cerrarla (tiene movimientos: queda INACTIVA, no se elimina)", () -> {
            BigDecimal todo = servicio.buscarCuenta("AH-0003").getSaldo();
            servicio.retirar("AH-0003", todo, LocalDate.of(2026, 11, 3));
            boolean eliminada = servicio.cerrarCuenta("AH-0003");
            System.out.println("   Eliminada: " + eliminada + " | " + servicio.buscarCuenta("AH-0003"));
        });
        probar("CP-15 Depositar en AH-0003 ya inactiva (debe rechazarse)",
                () -> servicio.depositar("AH-0003", new BigDecimal("10"), LocalDate.of(2026, 11, 4)));
        probar("CP-16 Abrir ahorro con $3 (menor al minimo, debe rechazarse)",
                () -> servicio.abrirCuenta(1, "AHORRO", new BigDecimal("3"), LocalDate.of(2026, 11, 4)));

        titulo("REPORTES FINALES");
        System.out.println(reportes.estadoDeCuenta("AH-0001"));
        System.out.println(reportes.estadoDeCuenta("AH-0002"));
        System.out.println(reportes.interesesDelMes(YearMonth.of(2026, 10)));
        System.out.println(reportes.cuentasEnSobregiro());
        System.out.println(reportes.totalesPeriodo(LocalDate.of(2026, 10, 1), LocalDate.of(2026, 11, 30)));
        System.out.println(reportes.auditoriaSaldos());
    }

    private static void probar(String descripcion, Runnable accion) {
        System.out.println(">> " + descripcion);
        try {
            accion.run();
            System.out.println("   RESULTADO: ACEPTADA");
        } catch (RuntimeException e) {
            System.out.println("   RESULTADO: RECHAZADA - " + e.getMessage());
        }
    }

    // =================================================================== MENU

    private static void menu() {
        boolean salir = false;
        while (!salir) {
            titulo("COOPERATIVA DE AHORRO Y CREDITO");
            System.out.println(" 1. Registrar socio            8. Estado de cuenta");
            System.out.println(" 2. Abrir cuenta               9. Intereses del mes (reporte)");
            System.out.println(" 3. Depositar                 10. Cuentas en sobregiro");
            System.out.println(" 4. Retirar                   11. Total depositado/retirado en un periodo");
            System.out.println(" 5. Transferir                12. Auditoria de saldos");
            System.out.println(" 6. Cerrar cuenta             13. Listar socios y cuentas");
            System.out.println(" 7. Acreditar intereses       14. Guardar un reporte en archivo");
            System.out.println(" D. Ejecutar DEMO (restaura datos de prueba)     0. Salir");
            String op = leer("Opcion: ").toUpperCase();
            try {
                switch (op) {
                    case "1":
                        Socio s = servicio.registrarSocio(leer("Nombre: "), leer("Cedula: "));
                        System.out.println("Registrado: " + s);
                        break;
                    case "2":
                        Cuenta c = servicio.abrirCuenta(Integer.parseInt(leer("Id del socio: ")),
                                leer("Tipo (AHORRO/CORRIENTE): "), leerMonto("Deposito inicial: "), leerFecha());
                        System.out.println("Cuenta creada: " + c);
                        break;
                    case "3":
                        servicio.depositar(leer("Cuenta: "), leerMonto("Monto: "), leerFecha());
                        System.out.println("Deposito realizado.");
                        break;
                    case "4":
                        servicio.retirar(leer("Cuenta: "), leerMonto("Monto: "), leerFecha());
                        System.out.println("Retiro realizado.");
                        break;
                    case "5":
                        servicio.transferir(leer("Cuenta origen: "), leer("Cuenta destino: "),
                                leerMonto("Monto: "), leerFecha());
                        System.out.println("Transferencia realizada.");
                        break;
                    case "6":
                        boolean eliminada = servicio.cerrarCuenta(leer("Cuenta: "));
                        System.out.println(eliminada ? "Cuenta sin movimientos: eliminada."
                                : "Cuenta con movimientos: marcada como INACTIVA.");
                        break;
                    case "7":
                        YearMonth mes = leerMes();
                        System.out.println("Total acreditado: $" + servicio.acreditarIntereses(mes, leerFecha()));
                        break;
                    case "8":
                        System.out.println(reportes.estadoDeCuenta(leer("Cuenta: ")));
                        break;
                    case "9":
                        System.out.println(reportes.interesesDelMes(leerMes()));
                        break;
                    case "10":
                        System.out.println(reportes.cuentasEnSobregiro());
                        break;
                    case "11":
                        System.out.println(reportes.totalesPeriodo(LocalDate.parse(leer("Desde (AAAA-MM-DD): ")),
                                LocalDate.parse(leer("Hasta (AAAA-MM-DD): "))));
                        break;
                    case "12":
                        System.out.println(reportes.auditoriaSaldos());
                        break;
                    case "13":
                        listar();
                        break;
                    case "14":
                        Path p = archivo.guardarReporte("auditoria_saldos.txt", reportes.auditoriaSaldos());
                        System.out.println("Reporte guardado en " + p);
                        break;
                    case "D":
                        iniciarDemo();
                        break;
                    case "0":
                        salir = true;
                        break;
                    default:
                        System.out.println("Opcion no valida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("RECHAZADO: valor numerico invalido.");
            } catch (java.time.format.DateTimeParseException e) {
                System.out.println("RECHAZADO: fecha invalida (use AAAA-MM-DD).");
            } catch (IOException | RuntimeException e) {
                System.out.println("RECHAZADO: " + e.getMessage());
            }
        }
        System.out.println("Hasta pronto.");
    }

    private static void listar() {
        for (Socio s : servicio.getSocios()) {
            System.out.println(s);
            for (Cuenta c : s.getCuentas()) {
                System.out.println("    " + c);
            }
        }
    }

    // ================================================================= ENTRADA

    private static void titulo(String texto) {
        System.out.println();
        System.out.println("########## " + texto + " ##########");
    }

    private static String leer(String mensaje) {
        System.out.print(mensaje);
        return IN.hasNextLine() ? IN.nextLine().trim() : "0";
    }

    private static BigDecimal leerMonto(String mensaje) {
        return new BigDecimal(leer(mensaje).replace(',', '.'));
    }

    private static LocalDate leerFecha() {
        String t = leer("Fecha (AAAA-MM-DD, vacio = hoy): ");
        return t.isEmpty() ? LocalDate.now() : LocalDate.parse(t);
    }

    private static YearMonth leerMes() {
        return YearMonth.parse(leer("Mes (AAAA-MM): "));
    }
}
