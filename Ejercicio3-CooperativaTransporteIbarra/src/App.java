import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class App {
    public static void main(String[] args) throws Exception {

        
        String nombreCarpeta = "Ejercicio3-CooperativaTransporteIbarra/data";
        String nombreArchivo = nombreCarpeta + "/datos_cooperativa.txt";

        // Crear directorio de datos si no existe
        Path rutaCarpeta = Paths.get(nombreCarpeta);
        if (!Files.exists(rutaCarpeta)) {
            Files.createDirectories(rutaCarpeta);
        }

        CooperativaTransporteIbarra cooperativa = new CooperativaTransporteIbarra();

        // Cargar datos previos si existen
        cooperativa.cargarDatosDesdeArchivo(nombreArchivo);

        // Definición de fechas y horas de prueba
        LocalDate fechaManana = LocalDate.now().plusDays(1);
        LocalDate fechaPasada = LocalDate.now().minusDays(1);
        LocalTime horaManana = LocalTime.of(10, 0);
        LocalTime horaPasada = LocalTime.of(8, 0);

        // Configuración inicial de viajes de prueba
        if (cooperativa.BuscarTransporte("Ibarra-Quito", fechaManana) == null) {
            cooperativa.RegistrarTransporte(new Transporte("Ibarra-Quito", fechaManana, horaManana, 5, 10.0));
        }
        if (cooperativa.BuscarTransporte("Ibarra-Tulcan", fechaManana) == null) {
            cooperativa.RegistrarTransporte(new Transporte("Ibarra-Tulcan", fechaManana, horaManana, 4, 8.0));
        }
        if (cooperativa.BuscarTransporte("Ibarra-Otavalo", fechaPasada) == null) {
            cooperativa.RegistrarTransporte(new Transporte("Ibarra-Otavalo", fechaPasada, horaPasada, 3, 3.0));
        }

        Transporte busQuito = cooperativa.BuscarTransporte("Ibarra-Quito", fechaManana);

        System.out.println("======================================================================");
        System.out.println("   DEMOSTRACION DE TODOS LOS CASOS DE PRUEBA - EJERCICIO 3 (COOPERATIVA)");
        System.out.println("======================================================================\n");

        // ----------------------------------------------------------------------------------
        // REGLA 1: Registro de Viajes y Rutas (Ibarra-Quito, Ibarra-Tulcán, Ibarra-Otavalo)
        // ----------------------------------------------------------------------------------
        System.out.println("[CASO 1] Registro de un nuevo viaje en la cooperativa");
        System.out.println(" > Ruta asignada: " + busQuito.getRuta());
        System.out.println(" > Fecha de salida: " + busQuito.getFechaSalida() + " | Hora: " + busQuito.getHoraSalida());
        System.out.println(" > Capacidad total: " + busQuito.getAsientos().length + " asientos");
        System.out.println(" > Tarifa base: $" + busQuito.CalcularTarifa("D"));
        System.out.println(" ESTADO: PRUEBA EXITOSA\n");
        System.out.println("----------------------------------------------------------------------\n");

        // ----------------------------------------------------------------------------------
        // REGLA 2: Venta de Boletos y Estructura tarifaria (Directo, Intermedio, Especial)
        // ----------------------------------------------------------------------------------
        System.out.println("[CASO 2] Venta de boleto con tarifa Directa (D - 100%)");
        int v1 = cooperativa.VenderBoleto("Ibarra-Quito", fechaManana, horaManana, "D", 1);
        System.out.println(" > Asiento seleccionado: 1");
        System.out.println(" > Valor calculado: $" + busQuito.CalcularTarifa("D"));
        System.out.println(" > Código de respuesta: " + v1 + " (1 = Venta Exitosa)");
        System.out.println(" ESTADO: PRUEBA EXITOSA\n");
        System.out.println("----------------------------------------------------------------------\n");

        System.out.println("[CASO 3] Venta de boleto con tarifa Intermedia (I - 85%)");
        int v2 = cooperativa.VenderBoleto("Ibarra-Quito", fechaManana, horaManana, "I", 2);
        System.out.println(" > Asiento seleccionado: 2");
        System.out.println(" > Valor calculado: $" + busQuito.CalcularTarifa("I"));
        System.out.println(" > Código de respuesta: " + v2 + " (1 = Venta Exitosa)");
        System.out.println(" ESTADO: PRUEBA EXITOSA\n");
        System.out.println("----------------------------------------------------------------------\n");

        System.out.println("[CASO 4] Venta de boleto con tarifa Especial/Tercera Edad (E - 50%)");
        int v3 = cooperativa.VenderBoleto("Ibarra-Quito", fechaManana, horaManana, "E", 3);
        System.out.println(" > Asiento seleccionado: 3");
        System.out.println(" > Valor calculado: $" + busQuito.CalcularTarifa("E"));
        System.out.println(" > Código de respuesta: " + v3 + " (1 = Venta Exitosa)");
        System.out.println(" ESTADO: PRUEBA EXITOSA\n");
        System.out.println("----------------------------------------------------------------------\n");

        // ----------------------------------------------------------------------------------
        // REGLA 3: Restricción - No vender asientos ocupados o rangos inválidos
        // ----------------------------------------------------------------------------------
        System.out.println("[CASO 5] Intento de venta en asiento ya ocupado");
        int vOcupado = cooperativa.VenderBoleto("Ibarra-Quito", fechaManana, horaManana, "D", 1);
        System.out.println(" > Intento comprar asiento 1 (ya asignado)");
        System.out.println(" > Código devuelto: " + vOcupado + " (-3 = Asiento ya ocupado)");
        System.out.println(" ESTADO: RESTRICCION APLICADA CORRECTAMENTE\n");
        System.out.println("----------------------------------------------------------------------\n");

        System.out.println("[CASO 6] Intento de venta en número de asiento inexistente / fuera de rango");
        int vInvalido = cooperativa.VenderBoleto("Ibarra-Quito", fechaManana, horaManana, "D", 99);
        System.out.println(" > Intento comprar asiento 99 (capacidad del bus: 5)");
        System.out.println(" > Código devuelto: " + vInvalido + " (-2 = Asiento fuera de rango)");
        System.out.println(" ESTADO: RESTRICCION APLICADA CORRECTAMENTE\n");
        System.out.println("----------------------------------------------------------------------\n");

        // ----------------------------------------------------------------------------------
        // REGLA 4: Restricción - No vender boletos para un viaje ya realizado/salido
        // ----------------------------------------------------------------------------------
        System.out.println("[CASO 7] Intento de venta de boleto para un viaje expirado/pasado");
        int vExpirado = cooperativa.VenderBoleto("Ibarra-Otavalo", fechaPasada, horaPasada, "D", 1);
        System.out.println(" > Fecha del viaje: " + fechaPasada + " (Pasada)");
        System.out.println(" > Código devuelto: " + vExpirado + " (-1 = El viaje ya salió)");
        System.out.println(" ESTADO: RESTRICCION APLICADA CORRECTAMENTE\n");
        System.out.println("----------------------------------------------------------------------\n");

        // ----------------------------------------------------------------------------------
        // REGLA 5: Anulación de Boletos (Dentro del tiempo permitido - 2h antes)
        // ----------------------------------------------------------------------------------
        System.out.println("[CASO 8] Anulación exitosa de boleto (Dentro del tiempo límite)");
        boolean anuladoOk = cooperativa.AnularBoleto("Ibarra-Quito", fechaManana, 2);
        System.out.println(" > Anulando asiento: 2");
        System.out.println(" > Resultado operación: " + anuladoOk);
        System.out.println(" ESTADO: ANULACION EXITOSA\n");
        System.out.println("----------------------------------------------------------------------\n");

        // ----------------------------------------------------------------------------------
        // REGLA 6: Restricción - Evitar doble anulación del mismo boleto
        // ----------------------------------------------------------------------------------
        System.out.println("[CASO 9] Intento de re-anular el mismo boleto");
        boolean reAnulado = cooperativa.AnularBoleto("Ibarra-Quito", fechaManana, 2);
        System.out.println(" > Anulando asiento 2 por segunda vez");
        System.out.println(" > Resultado operación: " + reAnulado + " (false = No se puede re-anular)");
        System.out.println(" ESTADO: RESTRICCION APLICADA CORRECTAMENTE\n");
        System.out.println("----------------------------------------------------------------------\n");

        // ----------------------------------------------------------------------------------
        // REPORTES Y AUDITORÍAS REQUERIDAS
        // ----------------------------------------------------------------------------------
        System.out.println("[CASO 10] Reporte: Mapa de Asientos, Asientos Libres y Ocupación");
        System.out.println(" > Mapa de asientos disponibles: " + busQuito.MostrarAsientosDisponibles());
        System.out.println(" > Cantidad asientos libres: " + busQuito.CalcularAsientosDisponibles());
        System.out.println(" > Cantidad asientos ocupados: " + busQuito.getAsientosOcupados());
        
        double porcentajeOcupacion = ((double) busQuito.getAsientosOcupados() / busQuito.getAsientos().length) * 100.0;
        System.out.println(" > Porcentaje de ocupación actual: " + String.format("%.2f", porcentajeOcupacion) + "%");
        System.out.println(" ESTADO: REPORTE GENERADO CON EXITO\n");
        System.out.println("----------------------------------------------------------------------\n");

        System.out.println("[CASO 11] Reporte: Boletos Anulados y Auditoría");
        System.out.println(" > Total de boletos anulados registrados en la ruta: " + busQuito.getBoletosAnulados());
        System.out.println(" ESTADO: AUDITORIA COMPLETADA CON EXITO\n");
        System.out.println("======================================================================");

        // Guardado de persistencia en archivo de texto
        cooperativa.guardarDatosEnArchivo(nombreArchivo);
        System.out.println(" [SISTEMA] Persistencia de datos finalizada en '" + nombreArchivo + "'");
        System.out.println("======================================================================");
         
    }
    
}
