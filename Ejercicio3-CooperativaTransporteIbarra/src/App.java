import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class App {
    public static void main(String[] args) throws Exception {
        LocalDate fecha = LocalDate.of(2026, 11, 12);
        LocalTime hora = LocalTime.of(12, 1);
        
        LocalDateTime fechasalida = fecha.atTime(hora);
        
        LocalDateTime fechaYHora = LocalDateTime.of(2026, 12, 25, 14, 30);
        System.out.println(fechasalida.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
    }
}
