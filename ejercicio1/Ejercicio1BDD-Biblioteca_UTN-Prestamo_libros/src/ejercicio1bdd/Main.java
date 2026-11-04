package ejercicio1bdd;

import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   SISTEMA DE BIBLIOTECA UTN - GESTIÓN DE PRÉSTAMOS");
        System.out.println("==================================================");

        cargarDatosIniciales();

        boolean salir = false;
        while (!salir) {
            mostrarMenuPrincipal();
            int opcion = leerEntero("Seleccione una opción: ");

            switch (opcion) {
                case 1:
                    menuGestionMateriales();
                    break;
                case 2:
                    menuGestionUsuarios();
                    break;
                case 3:
                    registrarNuevoPrestamo();
                    break;
                case 4:
                    registrarDevolucion();
                    break;
                case 5:
                    menuReportes();
                    break;
                case 6:
                    guardarDatosEnArchivos();
                    break;
                case 0:
                    guardarDatosEnArchivos();
                    System.out.println("Saliendo del sistema...");
                    salir = true;
                    break;
                default:
                    System.out.println("Opción inválida.");
            }
        }

        scanner.close();
    }

    private static void mostrarMenuPrincipal() {
        System.out.println("\n--- MENÚ PRINCIPAL ---");
        System.out.println("1. Gestión de Materiales");
        System.out.println("2. Gestión de Usuarios");
        System.out.println("3. Registrar Préstamo");
        System.out.println("4. Registrar Devolución");
        System.out.println("5. Reportes");
        System.out.println("6. Guardar Datos");
        System.out.println("0. Salir");
    }

    private static void menuGestionMateriales() {
        System.out.println("\n--- GESTIÓN DE MATERIALES ---");
        System.out.println("1. Registrar Libro");
        System.out.println("2. Registrar Revista");
        System.out.println("3. Agregar Ejemplares");
        System.out.println("4. Listar Catálogo");
        System.out.println("0. Volver");

        int op = leerEntero("Seleccione una opción: ");
        switch (op) {
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            case 0:
                break;
            default:
                System.out.println("Opción inválida.");
        }
    }

    private static void menuGestionUsuarios() {
        System.out.println("\n--- GESTIÓN DE USUARIOS ---");
        System.out.println("1. Registrar Usuario");
        System.out.println("2. Consultar Usuario");
        System.out.println("3. Cobrar Multa");
        System.out.println("4. Listar Usuarios");
        System.out.println("0. Volver");

        int op = leerEntero("Seleccione una opción: ");
        switch (op) {
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            case 0:
                break;
            default:
                System.out.println("Opción inválida.");
        }
    }

    private static void registrarNuevoPrestamo() {
        System.out.println("\n--- REGISTRO DE PRÉSTAMO ---");
    }

    private static void registrarDevolucion() {
        System.out.println("\n--- REGISTRO DE DEVOLUCIÓN ---");
    }

    private static void menuReportes() {
        System.out.println("\n--- REPORTES ---");
        System.out.println("1. Préstamos vencidos a la fecha");
        System.out.println("2. Historial de préstamos de un usuario");
        System.out.println("3. Materiales más prestados");
        System.out.println("4. Total de multas generadas en un período");
        System.out.println("5. Auditoría de ejemplares disponibles");
        System.out.println("0. Volver");

        int op = leerEntero("Seleccione una opción: ");
        switch (op) {
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
            case 0:
                break;
            default:
                System.out.println("Opción inválida.");
        }
    }

    private static void cargarDatosIniciales() {
    }

    private static void guardarDatosEnArchivos() {
    }

    private static int leerEntero(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                String linea = scanner.nextLine().trim();
                return Integer.parseInt(linea);
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Ingrese un número.");
            }
        }
    }
}
