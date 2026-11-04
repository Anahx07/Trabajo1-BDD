public class Boleto {

    private int numAsiento;
    private boolean anulado;
    private String tipoViaje;

    public Boleto(int numAsiento, String tipoViaje) {
        this.numAsiento = numAsiento;
        this.anulado = false;
        this.tipoViaje = tipoViaje;
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

    public String getTipoViaje() {
        return tipoViaje;
    }

    public void setTipoViaje(String tipoViaje) {
        this.tipoViaje = tipoViaje;
    }
}