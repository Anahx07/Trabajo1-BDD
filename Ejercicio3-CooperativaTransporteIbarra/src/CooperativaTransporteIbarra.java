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
}
