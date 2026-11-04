import java.time.LocalDate;
import java.util.*;

public class Tienda {
    private final List<Artesano> artesanos = new ArrayList<>();
    private final List<Productos> artesanias = new ArrayList<>();

    public List<Artesano> getArtesanos() { return artesanos; }
    public List<Productos> getArtesanias() { return artesanias; }
    public void registrarArtesano(Artesano a) { artesanos.add(a); }

    public void recibirArtesania(Productos p) {
        if (!artesanos.contains(p.getArtesano()))
            throw new IllegalArgumentException("El artesano no esta registrado");
        artesanias.add(p);
        p.getArtesano().getArtesanias().add(p);
    }

    public void vender(Productos p, int unidades, LocalDate fecha) { p.vender(unidades, fecha); }

    public void devolver(Productos p, int unidades, Artesano a, LocalDate fecha) { p.devolver(unidades, a, fecha); }

    public boolean eliminarArtesano(Artesano a) {
        if (!a.puedeEliminarse()) return false; // tiene artesanías registradas
        return artesanos.remove(a);
    }

    public boolean eliminarArtesania(Productos p) {
        if (p.getMovimientos().size() > 1 || p.tieneMovimientosDistintosARecepcion()) return false;
        p.getArtesano().getArtesanias().remove(p);
        return artesanias.remove(p);
    }

    public void reporteLiquidacion(Artesano a, LocalDate desde, LocalDate hasta) {
        double totalVendido = 0, totalComision = 0;
        for (Productos p : a.getArtesanias()) {
            totalVendido += p.montoVendido(desde, hasta);
            totalComision += p.comision(desde, hasta);
        }
        System.out.println("=== Liquidación de " + a + " (" + desde + " a " + hasta + ") ===");
        System.out.printf("Total vendido:      $%.2f%n", totalVendido);
        System.out.printf("Comision tienda:    $%.2f%n", totalComision);
        System.out.printf("Monto a pagar:      $%.2f%n", totalVendido - totalComision);
    }

    public void reporteDevoluciones(LocalDate hoy) {
        System.out.println("=== Artesanias para devolver ===");
        for (Productos p : artesanias) {
            p.actualizarMarcaDevolucion(hoy);
            if (p.isMarcadaParaDevolucion())
                System.out.println(p.getNombre() + " | " + p.getCantidad() + " u. | artesano: " + p.getArtesano());
        }
    }

    public void reporteComunidades() {
        Map<String, Double> ventas = new HashMap<>();
        for (Productos p : artesanias) {
            ventas.merge(p.getArtesano().getComunidad(),
                    p.montoVendido(LocalDate.MIN, LocalDate.MAX), Double::sum);
        }
        System.out.println("=== Comunidades con mas ventas ===");
        ventas.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .forEach(e -> System.out.printf("%s: $%.2f%n", e.getKey(), e.getValue()));
    }

    public void reporteMasVendidas() {
        System.out.println("=== Artesanias mas vendidas ===");
        artesanias.stream()
                .sorted(Comparator.comparingInt(Productos::unidadesVendidas).reversed())
                .forEach(p -> System.out.println(p.getNombre() + ": " + p.unidadesVendidas() + " u."));
    }

    public void auditoria() {
        System.out.println("=== Auditoria de unidades disponibles ===");
        for (Productos p : artesanias) {
            int calculado = p.disponiblesSegunMovimientos();
            String estado = (calculado == p.getCantidad()) ? "OK" : "DIFERENCIA";
            System.out.println(p.getNombre() + " | registrado: " + p.getCantidad()
                    + " | según movimientos: " + calculado + " -> " + estado);
        }
    }
}
