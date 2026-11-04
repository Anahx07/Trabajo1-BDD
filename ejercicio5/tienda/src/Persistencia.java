import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class Persistencia {
    private Persistencia() {}

    public static void guardar(Tienda tienda, Path ruta) throws IOException {
        List<Object> artesanos = new ArrayList<>();
        for (Artesano a : tienda.getArtesanos()) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", a.getId());
            m.put("nombre", a.getNombre());
            m.put("comunidad", a.getComunidad());
            artesanos.add(m);
        }

        List<Object> artesanias = new ArrayList<>();
        for (Productos p : tienda.getArtesanias()) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("tipo", p instanceof Textiles ? "TEXTIL" : "MADERA");
            m.put("nombre", p.getNombre());
            m.put("cantidad", p.getCantidad());
            m.put("precio", p.getPrecio());
            m.put("artesanoId", p.getArtesano().getId());
            m.put("fechaRecepcion", p.getFechaRecepcion().toString());
            m.put("marcadaParaDevolucion", p.isMarcadaParaDevolucion());

            List<Object> movs = new ArrayList<>();
            for (Movimiento mov : p.getMovimientos()) {
                Map<String, Object> mm = new LinkedHashMap<>();
                mm.put("tipo", mov.getTipo().name());
                mm.put("cantidad", mov.getCantidad());
                mm.put("fecha", mov.getFecha().toString());
                movs.add(mm);
            }
            m.put("movimientos", movs);
            artesanias.add(m);
        }

        Map<String, Object> raiz = new LinkedHashMap<>();
        raiz.put("artesanos", artesanos);
        raiz.put("artesanias", artesanias);

        Path tmp = ruta.resolveSibling(ruta.getFileName() + ".tmp");
        Files.write(tmp, Json.escribir(raiz).getBytes(StandardCharsets.UTF_8));
        Files.move(tmp, ruta, StandardCopyOption.REPLACE_EXISTING);
    }

    @SuppressWarnings("unchecked")
    public static Tienda cargar(Path ruta) throws IOException {
        Tienda tienda = new Tienda();
        if (!Files.exists(ruta)) return tienda;

        String texto = new String(Files.readAllBytes(ruta), StandardCharsets.UTF_8);
        Map<String, Object> raiz = (Map<String, Object>) Json.leer(texto);

        Map<String, Artesano> porId = new HashMap<>();
        for (Object o : (List<Object>) raiz.get("artesanos")) {
            Map<String, Object> m = (Map<String, Object>) o;
            Artesano a = new Artesano((String) m.get("id"), (String) m.get("nombre"), (String) m.get("comunidad"));
            tienda.registrarArtesano(a);
            porId.put(a.getId(), a);
        }

        for (Object o : (List<Object>) raiz.get("artesanias")) {
            Map<String, Object> m = (Map<String, Object>) o;

            List<Movimiento> movs = new ArrayList<>();
            int recibidas = 0;
            for (Object om : (List<Object>) m.get("movimientos")) {
                Map<String, Object> mm = (Map<String, Object>) om;
                TipoMovimiento tipo = TipoMovimiento.valueOf((String) mm.get("tipo"));
                int cant = ((Number) mm.get("cantidad")).intValue();
                movs.add(new Movimiento(tipo, cant, LocalDate.parse((String) mm.get("fecha"))));
                if (tipo == TipoMovimiento.RECEPCION) recibidas += cant;
            }

            Artesano dueno = porId.get((String) m.get("artesanoId"));
            String nombre = (String) m.get("nombre");
            double precio = ((Number) m.get("precio")).doubleValue();
            LocalDate fecha = LocalDate.parse((String) m.get("fechaRecepcion"));

            Productos p = "TEXTIL".equals(m.get("tipo"))
                    ? new Textiles(nombre, recibidas, precio, dueno, fecha)
                    : new Maderas(nombre, recibidas, precio, dueno, fecha);

            p.restaurar(((Number) m.get("cantidad")).intValue(),
                        (Boolean) m.get("marcadaParaDevolucion"),
                        movs);
            tienda.recibirArtesania(p);
        }
        return tienda;
    }
}
