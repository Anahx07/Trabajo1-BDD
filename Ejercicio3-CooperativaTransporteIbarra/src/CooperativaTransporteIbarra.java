import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class CooperativaTransporteIbarra {

    private List<Transporte> listaTransportes;

    public CooperativaTransporteIbarra() {
        listaTransportes = new ArrayList<>();
    }

    public void RegistrarTransporte(Transporte t) {
        this.listaTransportes.add(t);
    }

    public Transporte BuscarTransporte(String ruta, LocalDate fecha) {
        for (Transporte t : listaTransportes) {
            if (t.getRuta().equalsIgnoreCase(ruta) && t.getFechaSalida().equals(fecha)) {
                return t;
            }
        }
        return null;
    }

    public int VenderBoleto(String ruta, LocalDate fecha, LocalTime hora, String tipoViaje, int numAsiento) {
        Transporte t = BuscarTransporte(ruta, fecha);
        if (t == null) return 0; // El transporte no existe

        LocalDateTime fechaHoraSalida = LocalDateTime.of(t.getFechaSalida(), t.getHoraSalida());
        if (LocalDateTime.now().isAfter(fechaHoraSalida)) {
            return -1; 
        }

        if (numAsiento < 1 || numAsiento > t.getAsientos().length) {
            return -2;
        }

        Boleto b = (Boleto) t.getAsientos()[numAsiento - 1];
        if (b != null && !b.isAnulado()) {
            return -3;
        }

        // Crear y registrar el boleto en el asiento
        t.getAsientos()[numAsiento - 1] = new Boleto(numAsiento, tipoViaje);
        t.setAsientosOcupados(t.getAsientosOcupados() + 1);

        return 1;
    }

    public boolean AnularBoleto(String ruta, LocalDate fecha, int numAsiento) {
        Transporte t = BuscarTransporte(ruta, fecha);
        if (t == null) return false;

        if (numAsiento < 1 || numAsiento > t.getAsientos().length) return false;

        Boleto b = (Boleto) t.getAsientos()[numAsiento - 1];
        if (b == null || b.isAnulado()) return false;

        LocalDateTime fechaHoraSalida = LocalDateTime.of(t.getFechaSalida(), t.getHoraSalida());
        LocalDateTime horaLimiteAnulacion = fechaHoraSalida.minusHours(2);

        if (LocalDateTime.now().isAfter(horaLimiteAnulacion)) {
            return false;
        }

        b.setAnulado(true);
        t.registrarAnulacion();
        return true;
    }
}