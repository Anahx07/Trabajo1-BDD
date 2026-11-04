import java.util.ArrayList;
import java.util.List;

public class Artesano {
    private String id;
    private String nombre;
    private String comunidad;
    private List<Productos> artesanias = new ArrayList<>();

    public Artesano(String id, String nombre, String comunidad) {
        this.id = id;
        this.nombre = nombre;
        this.comunidad = comunidad;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getComunidad() { return comunidad; }
    public List<Productos> getArtesanias() { return artesanias; }
    public boolean puedeEliminarse() { return artesanias.isEmpty(); }

    @Override
    public String toString() { return nombre + " (" + comunidad + ")"; }
}
