public class Asiento {

    private int numAsiento;
    private char estado;

    public Asiento(int numAsiento)
    {
        this.numAsiento = numAsiento;
        this.estado = 'D';
    }

    
    public void setEstado(char estado) {
        this.estado = estado;
    }

    public char getEstado() {
        return estado;
    }


    public int getNumAsiento() {
        return numAsiento;
    }
    
    
}
