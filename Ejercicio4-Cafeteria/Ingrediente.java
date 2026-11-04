public class Ingrediente {
    private String id;
    private String nombre;
    private String unidadMedida;
    private double stockActual;
    private double stockMinimo;
    private double costoUnitario;

    public Ingrediente(String id, String nombre, String unidadMedida, double stockActual, double stockMinimo, double costoUnitario) {
        this.id = id;
        this.nombre = nombre;
        this.unidadMedida = unidadMedida;
        this.stockActual = stockActual;
        this.stockMinimo = stockMinimo;
        this.costoUnitario = costoUnitario;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getUnidadMedida() { return unidadMedida; }
    public double getStockActual() { return stockActual; }
    public void setStockActual(double stockActual) { this.stockActual = stockActual; }
    public double getStockMinimo() { return stockMinimo; }
    public double getCostoUnitario() { return costoUnitario; }
    public void setCostoUnitario(double costoUnitario) { this.costoUnitario = costoUnitario; }

    public void agregarStock(double cantidad) {
        if (cantidad > 0) this.stockActual += cantidad;
    }

    public boolean reducirStock(double cantidad) {
        if (this.stockActual >= cantidad) {
            this.stockActual -= cantidad;
            return true;
        }
        return false;
    }
    public String toCSV() {
        return id + ";" + nombre + ";" + unidadMedida + ";" + stockActual + ";" + stockMinimo + ";" + costoUnitario;
    }

    public static Ingrediente fromCSV(String linea) {
        String[] p = linea.split(";");
        return new Ingrediente(p[0], p[1], p[2], Double.parseDouble(p[3]), Double.parseDouble(p[4]), Double.parseDouble(p[5]));
    }
}