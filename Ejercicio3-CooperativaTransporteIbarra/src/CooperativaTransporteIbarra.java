import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CooperativaTransporteIbarra {

    private List<Transporte> listaTransportes;

    public CooperativaTransporteIbarra ()
    {
        listaTransportes = new ArrayList<>();
    }

    public void RegistrarTransporte(Transporte t)
    {
        this.listaTransportes.add(t);
    }

    public Transporte BuscarTransporte(String ruta, LocalDate fecha)
    {
        for (Transporte t : listaTransportes) {
            if (t.getRuta().equalsIgnoreCase(ruta) && LocalDateTime.of(t.getFechaSalida(),t.getHoraSalida()).equals(fecha)) {
                return  t;
            }
        }
        return null;
    }

    public int VenderBoleto(String ruta,LocalDate fecha,String tipoViaje, int numeAsientos)
    {
        Transporte t;
        if (BuscarTransporte(ruta, fecha)==null) return 0;
        t = BuscarTransporte(ruta, fecha);
        if (numeAsientos< 1 || numeAsientos> t.getAsientos().length ) return -2;
        if (t.getAsientos()[numeAsientos-1]!=null) return -3;
        t.getAsientos()[numeAsientos-1] = new Boleto(numeAsientos, tipoViaje);

        return 1;
    }
    
}

