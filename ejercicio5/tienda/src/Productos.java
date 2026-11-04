import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public abstract class Productos {
    protected String nombre;
    protected int cantidad;         
    protected double precio;
    protected Artesano artesano;     
    protected LocalDate fechaRecepcion;
    protected boolean marcadaParaDevolucion;
    protected List<Movimiento> movimientos = new ArrayList<>();

    protected Productos(String nombre, int cantidad, double precio,
                        Artesano artesano, LocalDate fechaRecepcion) {
        if (cantidad <= 0) throw new IllegalArgumentException("La cantidad recibida debe ser mayor a 0");
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.precio = precio;
        this.artesano = artesano;
        this.fechaRecepcion = fechaRecepcion;
        movimientos.add(new Movimiento(TipoMovimiento.RECEPCION, cantidad, fechaRecepcion));
    }

    void restaurar(int cantidadActual, boolean marcada, List<Movimiento> movs) {
        this.cantidad = cantidadActual;
        this.marcadaParaDevolucion = marcada;
        this.movimientos = new ArrayList<>(movs);
    }
    public abstract double getPorcentajeComision();

    public void vender(int unidades, LocalDate fecha) {
        validarSalida(unidades);
        cantidad -= unidades;
        movimientos.add(new Movimiento(TipoMovimiento.VENTA, unidades, fecha));
    }

    public void devolver(int unidades, Artesano quienRecibe, LocalDate fecha) {
        if (quienRecibe != artesano)
            throw new IllegalArgumentException("Solo se devuelve al artesano dueño de la pieza");
        validarSalida(unidades);
        cantidad -= unidades;
        movimientos.add(new Movimiento(TipoMovimiento.DEVOLUCION, unidades, fecha));
    }

    private void validarSalida(int unidades) {
        if (unidades <= 0) throw new IllegalArgumentException("Cantidad invalida");
        if (unidades > cantidad)
            throw new IllegalArgumentException("No hay suficientes unidades disponibles de " + nombre);
    }

    public void actualizarMarcaDevolucion(LocalDate hoy) {
        if (cantidad > 0 && ChronoUnit.DAYS.between(fechaRecepcion, hoy) > 90) {
            marcadaParaDevolucion = true;
        }
    }

    public int unidadesPorTipo(TipoMovimiento tipo, LocalDate desde, LocalDate hasta) {
        int total = 0;
        for (Movimiento m : movimientos) {
            boolean enPeriodo = !m.getFecha().isBefore(desde) && !m.getFecha().isAfter(hasta);
            if (m.getTipo() == tipo && enPeriodo) total += m.getCantidad();
        }
        return total;
    }

    public int unidadesVendidas() {
        return unidadesPorTipo(TipoMovimiento.VENTA, LocalDate.MIN, LocalDate.MAX);
    }

    public int disponiblesSegunMovimientos() {
        int total = 0;
        for (Movimiento m : movimientos) {
            total += (m.getTipo() == TipoMovimiento.RECEPCION) ? m.getCantidad() : -m.getCantidad();
        }
        return total;
    }

    public double montoVendido(LocalDate desde, LocalDate hasta) {
        return unidadesPorTipo(TipoMovimiento.VENTA, desde, hasta) * precio;
    }

    public double comision(LocalDate desde, LocalDate hasta) {
        return montoVendido(desde, hasta) * getPorcentajeComision();
    }

    public boolean tieneMovimientosDistintosARecepcion() {
        for (Movimiento m : movimientos) {
            if (m.getTipo() != TipoMovimiento.RECEPCION) return true;
        }
        return false;
    }

    public String getNombre() { return nombre; }
    public int getCantidad() { return cantidad; }
    public double getPrecio() { return precio; }
    public Artesano getArtesano() { return artesano; }
    public LocalDate getFechaRecepcion() { return fechaRecepcion; }
    public boolean isMarcadaParaDevolucion() { return marcadaParaDevolucion; }
    public List<Movimiento> getMovimientos() { return movimientos; }
}
