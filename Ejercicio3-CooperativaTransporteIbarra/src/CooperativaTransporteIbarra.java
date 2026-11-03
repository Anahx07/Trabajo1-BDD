import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class CooperativaTransporteIbarra {

    private List<Object> transportes;

    public CooperativaTransporteIbarra ()
    {
    }

    public boolean RegistrarTransporte(Transporte t)
    {
        if (t != null) {
            transportes.add(t);
            return true;
        }
        return false;
    }

    public int VenderBoleto(Transporte t,String tipoViaje, int numeAsientos)
    {
    
        Asiento a;
        LocalDateTime fechaSalida;

        if (t == null) return 0;

        fechaSalida = LocalDateTime.of(t.getFechaSalida(),t.getHoraSalida());

        if (!fechaSalida.isAfter(LocalDateTime.now())) 
            {
                return  -2;
            }

        if (t.CalcularAsientosDisponibles() == 0 ) return -3;
        
        if (numeAsientos<1 || numeAsientos> t.getAsientos().length) return -4;

        a = (Asiento)t.getAsientos()[numeAsientos-1];

        if (a.getEstado()=='O') return -1;

        
        a.setEstado('O');

        return 1;
    }
}

