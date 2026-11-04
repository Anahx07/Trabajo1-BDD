import java.time.LocalDate;

public class Textiles extends Productos {
    public Textiles(String nombre, int cantidad, double precio, Artesano artesano, LocalDate fechaRecepcion) {
        super(nombre, cantidad, precio, artesano, fechaRecepcion);
    }

    @Override
    public double getPorcentajeComision() { return 0.20; }
}
