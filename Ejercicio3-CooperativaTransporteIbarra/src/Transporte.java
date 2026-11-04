import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;

public class Transporte {

    private Object [] asientos;
    private String ruta;
    private LocalDate fechaSalida;
    private LocalTime horaSalida;
    private int asientosOcupados;
    private double tarifaBase;
    private int boletosAnulados;
    
    public Transporte (String ruta,LocalDate fechDateSalida,LocalTime horaSalida,int capacidad,int tarifaBase)
    {
        this.ruta = ruta;
        this.fechaSalida = fechDateSalida;
        this.horaSalida = horaSalida;
        this.asientos = new Object [capacidad];
        this.tarifaBase = tarifaBase;
        this.asientosOcupados = 0;
    }

    public void setAsientosDisponibles(int numAsientos)
    { 
        if (numAsientos > 0 && numAsientos <= CalcularAsientosDisponibles()) {
            asientosOcupados+=numAsientos;
        }
    }

    
    public String getRuta() {
        return ruta;
    }

    public void registrarAnulacion() {
        this.boletosAnulados++;
    }

    public int getBoletosAnulados() {
        return boletosAnulados;
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

    public double CalcularTarifa(String tipoViaje)
    {        
        if (tipoViaje == null || tipoViaje.isEmpty() )
           {
            return 0;
           }

           switch (tipoViaje.toUpperCase()) {
            case "I":
                return this.tarifaBase*0.85;
            case "E":
                return this.tarifaBase*0.5;
            case "D":
                return this.tarifaBase*0.85;
                
            default:
                return 0;
           }
        }
        public int CalcularAsientosDisponibles()
        {
            return (this.asientos.length) - asientosOcupados;
        }    
          
        public String MostrarAsientosDisponibles()
        {
            String r= "";
            Boleto  b;
            for (int i = 0; i < asientos.length; i++) {
                b =  (Boleto)asientos[i];
                if (b == null) {
                    r+= "["+(i+1)+"] - ";
                }
                
            }
            return r;
        } 
}
