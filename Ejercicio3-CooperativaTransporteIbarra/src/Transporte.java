import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;

public class Transporte {

    private String ruta;
    private LocalDate fechaSalida;
    private LocalTime horaSalida;
    private int capacidad;
    private double tarifaBase;
    
    public Transporte (String ruta,LocalDate fechDateSalida,LocalTime horaSalida,int capacidad,int tarifaBase)
    {
        this.ruta = ruta;
        this.fechaSalida = fechDateSalida;
        this.horaSalida = horaSalida;
        this.capacidad = capacidad;
        this.tarifaBase = tarifaBase;
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
}
