public class Boleto {

    private int numAsiento;
    private boolean anulado;

    public Boleto(int numAsiento,String tipoViaje)
    {
        this.numAsiento = numAsiento;
        this.anulado = false;
    }

    public int getNumAsiento() {
        return numAsiento;
    }

    public void setNumAsiento(int numAsiento) {
        this.numAsiento = numAsiento;
    }

    public boolean isAnulado() {
        return anulado;
    }

    public void setAnulado(boolean anulado) {
        this.anulado = anulado;
    }
    
}
