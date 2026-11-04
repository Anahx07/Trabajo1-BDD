import java.time.LocalDate;
import java.time.LocalTime;

public class Transporte {

    private Object[] asientos;
    private String ruta;
    private LocalDate fechaSalida;
    private LocalTime horaSalida;
    private int asientosOcupados;
    private double tarifaBase;
    private int boletosAnulados;

    public Transporte(String ruta, LocalDate fechaSalida, LocalTime horaSalida, int capacidad, double tarifaBase) {
        this.ruta = ruta;
        this.fechaSalida = fechaSalida;
        this.horaSalida = horaSalida;
        this.asientos = new Object[capacidad];
        this.tarifaBase = tarifaBase;
        this.asientosOcupados = 0;
        this.boletosAnulados = 0;
    }

    public String getRuta() {
        return ruta;
    }

    public LocalDate getFechaSalida() {
        return fechaSalida;
    }

    public LocalTime getHoraSalida() {
        return horaSalida;
    }

    public Object[] getAsientos() {
        return asientos;
    }

    public int getAsientosOcupados() {
        return asientosOcupados;
    }

    public void setAsientosOcupados(int asientosOcupados) {
        this.asientosOcupados = asientosOcupados;
    }

    public void registrarAnulacion() {
        this.boletosAnulados++;
        this.asientosOcupados--;
    }

    public int getBoletosAnulados() {
        return boletosAnulados;
    }

    public double CalcularTarifa(String tipoViaje) {
        if (tipoViaje == null || tipoViaje.isEmpty()) {
            return 0;
        }

        switch (tipoViaje.toUpperCase()) {
            case "D":
                return this.tarifaBase;
            case "I":
                return this.tarifaBase * 0.85;
            case "E":
                return this.tarifaBase * 0.50;
            default:
                return this.tarifaBase;
        }
    }

    public int CalcularAsientosDisponibles() {
        return (this.asientos.length) - asientosOcupados;
    }

    public String MostrarAsientosDisponibles() {
        String r = "";
        Boleto b;
        for (int i = 0; i < asientos.length; i++) {
            b = (Boleto) asientos[i];
            if (b == null || b.isAnulado()) {
                r += "[" + (i + 1) + "] - ";
            }
        }
        return r;
    }
}