import java.time.LocalDate;

public class Movimiento {
    private final TipoMovimiento tipo;
    private final int cantidad;
    private final LocalDate fecha;

    public Movimiento(TipoMovimiento tipo, int cantidad, LocalDate fecha) {
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.fecha = fecha;
    }

    public TipoMovimiento getTipo() { return tipo; }
    public int getCantidad() { return cantidad; }
    public LocalDate getFecha() { return fecha; }
}
