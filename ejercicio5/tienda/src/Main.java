import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner in = new Scanner(System.in);
    private static final Path RUTA = Paths.get("tienda.json");
    private static Tienda tienda;
    public static void main(String[] args) {
        try {
            tienda = Persistencia.cargar(RUTA);
        } catch (IOException | RuntimeException e) {
            System.err.println("No se pudo leer " + RUTA.toAbsolutePath() + ": " + e.getMessage());
            return;
        }
        Runtime.getRuntime().addShutdownHook(new Thread(Main::guardar));

        boolean salir = false;
        while (!salir) {
            mostrarMenu();
            int opcion = leerEntero("Opcion: ");
            System.out.println();
            switch (opcion) {
                case 1: registrarArtesano(); break;
                case 2: eliminarArtesano(); break;
                case 3: recibirArtesania(); break;
                case 4: eliminarArtesania(); break;
                case 5: registrarVenta(); break;
                case 6: registrarDevolucion(); break;
                case 7: listarArtesanos(); break;
                case 8: listarArtesanias(); break;
                case 9: menuReportes(); break;
                case 0: salir = true; break;
                default: System.out.println("Opcion invalida.");
            }
        }
        System.out.println("Hasta luego.");
    }
// Aqui esta el menu :) 
    private static void mostrarMenu() {
        System.out.println();
        System.out.println("========== TIENDA DE ARTESANIAS - OTAVALO ==========");
        System.out.println("1. Registrar artesano");
        System.out.println("2. Eliminar artesano");
        System.out.println("3. Recibir artesania");
        System.out.println("4. Eliminar artesania");
        System.out.println("5. Registrar venta");
        System.out.println("6. Registrar devolucion al artesano");
        System.out.println("7. Listar artesanos");
        System.out.println("8. Listar artesanias");
        System.out.println("9. Reportes");
        System.out.println("0. Guardar y salir");
    }

    private static void menuReportes() {
        System.out.println("--- Reportes ---");
        System.out.println("1. Liquidacion de un artesano");
        System.out.println("2. Artesanias que deben devolverse (mas de 90 dias)");
        System.out.println("3. Comumidades con mas ventas");
        System.out.println("4. Artesanias mas vendidas");
        System.out.println("5. Auditoria de unidades disponibles");
        System.out.println("0. Volver");
        int opcion = leerEntero("Opcion: ");
        System.out.println();
        switch (opcion) {
            case 1: reporteLiquidacion(); break;
            case 2:
                 tienda.reporteDevoluciones(LocalDate.now());
                guardar(); // las marcas de devolución quedan registradas
                break;
            case 3: tienda.reporteComunidades(); break;
            case 4: tienda.reporteMasVendidas(); break;
            case 5: tienda.auditoria(); break;
            case 0: break;
            default: System.out.println("Opcion inva lida.");
        }
    }

    private static void registrarArtesano() {
        String nombre = leerTexto("Nombre del artesano: ");
        String comunidad = leerTexto("Comunidad: ");
        if (nombre.isEmpty() || comunidad.isEmpty()) {
            System.out.println("El nombre del artesano "
                    + "y el nombre de la comunidad son obligatorios.");
            return;
        }
        Artesano a = new Artesano(siguienteId(), nombre, comunidad);
        tienda.registrarArtesano(a);
        guardar();
        System.out.println("Artesano registrado: " + a + " [id " + a.getId() + "]");
    }

    private static void eliminarArtesano() {
        Artesano a = seleccionarArtesano();
        if (a == null) return;
        if (tienda.eliminarArtesano(a)) {
            guardar();
            System.out.println("Artesano eliminado.");
        } else {
            System.out.println("No se puede eliminar: el artesano tiene artesanias registradas.");
        }
    }

    private static void recibirArtesania() {
        Artesano a = seleccionarArtesano();
        if (a == null) return;

        System.out.println("Tipo:");
        System.out.println("1. Textil (comision 20%)");
        System.out.println("2. Talla en madera (comision 25%)");
        int tipo = leerEntero("Tipo: ");
        if (tipo != 1 && tipo != 2) {
            System.out.println("Tipo invalido.");
            return;
        }
        String nombre = leerTexto("Nombre de la artesania: ");
        int cantidad = leerEntero("Cantidad recibida: ");
        double precio = leerDecimal("Precio de venta: ");
        LocalDate fecha = leerFecha("Fecha de recepcion (aaaa-mm-dd, Enter = hoy): ");

        try {
            Productos p = (tipo == 1)
                    ? new Textiles(nombre, cantidad, precio, a, fecha)
                    : new Maderas(nombre, cantidad, precio, a, fecha);
            tienda.recibirArtesania(p);
            guardar();
            System.out.println("Artesania recibida.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void eliminarArtesania() {
        Productos p = seleccionarArtesania();
        if (p == null) return;
        if (tienda.eliminarArtesania(p)) {
            guardar();
            System.out.println("Artesania eliminada.");
        } else {
            System.out.println("No se puede eliminar: la artesania ya tiene movimientos (ventas o devolucion).");
        }
    }

    private static void registrarVenta() {
        Productos p = seleccionarArtesania();
        if (p == null) return;
        int unidades = leerEntero("Unidades vendidas: ");
        LocalDate fecha = leerFecha("Fecha de la venta (aaaa-mm-dd, Enter = hoy): ");
        try {
            tienda.vender(p, unidades, fecha);
            guardar();
            System.out.println("Venta registrada.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void registrarDevolucion() {
        Productos p = seleccionarArtesania();
        if (p == null) return;
        int unidades = leerEntero("Unidades a devolver: ");
        LocalDate fecha = leerFecha("Fecha de la devolucion (aaaa-mm-dd, Enter = hoy): ");
        try {
            tienda.devolver(p, unidades, p.getArtesano(), fecha);
            guardar();
            System.out.println("Devolucion registrada a " + p.getArtesano() + ".");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void reporteLiquidacion() {
        Artesano a = seleccionarArtesano();
        if (a == null) return;
        LocalDate desde = leerFecha("Desde (aaaa-mm-dd, Enter = hoy): ");
        LocalDate hasta = leerFecha("Hasta (aaaa-mm-dd, Enter = hoy): ");
        if (hasta.isBefore(desde)) {
            System.out.println("La fecha final no puede ser anterior a la inicial.");
            return;
        }
        tienda.reporteLiquidacion(a, desde, hasta);
    }
    private static void listarArtesanos() {
        List<Artesano> lista = tienda.getArtesanos();
        if (lista.isEmpty()) {
            System.out.println("No hay artesanos registrados.");
            return;
        }
        for (int i = 0; i < lista.size(); i++) {
            Artesano a = lista.get(i);
            System.out.println((i + 1) + ". " + a + " [id " + a.getId() + "] - "
                    + a.getArtesanias().size() + " artesanias");
        }
    }

    private static void listarArtesanias() {
        List<Productos> lista = tienda.getArtesanias();
        if (lista.isEmpty()) {
            System.out.println("No hay artesanias registradas.");
            return;
        }
        for (int i = 0; i < lista.size(); i++) {
            System.out.println((i + 1) + ". " + descripcion(lista.get(i)));
        }
    }

    private static String descripcion(Productos p) {
        String tipo = (p instanceof Textiles) ? "Textil" : "Madera";
        return p.getNombre() + " (" + tipo + ") | artesano: " + p.getArtesano()
                + " | precio: $" + String.format("%.2f", p.getPrecio())
                + " | disponibles: " + p.getCantidad()
                + " | recibida: " + p.getFechaRecepcion()
                + (p.isMarcadaParaDevolucion() ? " | MARCADA PARA DEVOLUCION" : "");
    }

    private static Artesano seleccionarArtesano() {
        List<Artesano> lista = tienda.getArtesanos();
        if (lista.isEmpty()) {
            System.out.println("No hay artesanos registrados.");
            return null;
        }
        listarArtesanos();
        int n = leerEntero("Numero de artesano (0 para cancelar): ");
        if (n < 1 || n > lista.size()) return null;
        return lista.get(n - 1);
    }

    private static Productos seleccionarArtesania() {
        List<Productos> lista = tienda.getArtesanias();
        if (lista.isEmpty()) {
            System.out.println("No hay artesanias registradas.");
            return null;
        }
        listarArtesanias();
        int n = leerEntero("Numero de artesania (0 para cancelar): ");
        if (n < 1 || n > lista.size()) return null;
        return lista.get(n - 1);
    }
    private static String siguienteId() {
        int n = tienda.getArtesanos().size() + 1;
        while (existeId(String.valueOf(n))) n++;
        return String.valueOf(n);
    }

    private static boolean existeId(String id) {
        for (Artesano a : tienda.getArtesanos()) {
            if (a.getId().equals(id)) return true;
        }
        return false;
    }
    private static void guardar() {
        try {
            Persistencia.guardar(tienda, RUTA);
        } catch (IOException e) {
            System.err.println("No se pudo guardar: " + e.getMessage());
        }
    }
    private static String leerTexto(String mensaje) {
        System.out.print(mensaje);
        if (!in.hasNextLine()) System.exit(0);
        return in.nextLine().trim();
    }

    private static int leerEntero(String mensaje) {
        while (true) {
            String s = leerTexto(mensaje);
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                System.out.println("Ingresa un numero entero valido.");
            }
        }
    }

    private static double leerDecimal(String mensaje) {
        while (true) {
            String s = leerTexto(mensaje).replace(',', '.');
            try {
                return Double.parseDouble(s);
            } catch (NumberFormatException e) {
                System.out.println("Ingresa un numero valido (ej. 25.50).");
            }
        }
    }

    private static LocalDate leerFecha(String mensaje) {
        while (true) {
            String s = leerTexto(mensaje);
            if (s.isEmpty()) return LocalDate.now();
            try {
                return LocalDate.parse(s);
            } catch (DateTimeParseException e) {
                System.out.println("Formato invalido. Usa aaaa-mm-dd, por ejemplo 2026-11-03.");
            }
        }
    }
}
