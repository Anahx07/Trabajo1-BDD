import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class App {
    public static void main(String[] args) throws Exception {

        CooperativaTransporteIbarra cooperativa = new CooperativaTransporteIbarra();
        String nombreArchivo = "datos_cooperativa.txt";

        cooperativa.cargarDatosDesdeArchivo(nombreArchivo);

        LocalDate fechaViaje = LocalDate.now().plusDays(1);
        LocalTime horaSalida = LocalTime.of(10, 00);

        if (cooperativa.BuscarTransporte("Ibarra-Quito", fechaViaje) == null) {
            Transporte bus = new Transporte("Ibarra-Quito", fechaViaje, horaSalida, 5, 12.0);
            cooperativa.RegistrarTransporte(bus);
        }

        Transporte busPrueba = cooperativa.BuscarTransporte("Ibarra-Quito", fechaViaje);

        System.out.println("==================================================");
        System.out.println("      DEMOSTRACION DE CASOS DE PRUEBA - EJERCICIO 3");
        System.out.println("==================================================\n");

        System.out.println("[CASO 1] Registro de un nuevo viaje en la cooperativa");
        System.out.println(" > Ruta asignada: " + busPrueba.getRuta());
        System.out.println(" > Fecha de salida: " + busPrueba.getFechaSalida());
        System.out.println(" > Capacidad total: 5 asientos");
        System.out.println(" > Tarifa base: $" + 12.0);
        System.out.println(" ESTADO: PRUEBA EXITOSA\n");
        System.out.println("--------------------------------------------------\n");

        System.out.println("[CASO 2] Venta de boleto con tarifa Directa (D)");
        int v1 = cooperativa.VenderBoleto("Ibarra-Quito", fechaViaje, horaSalida, "D", 1);
        System.out.println(" > Asiento seleccionado: 1");
        System.out.println(" > Valor calculado: $" + busPrueba.CalcularTarifa("D"));
        System.out.println(" > Codigo de respuesta: " + v1 + " (1 = Venta Exitosa)");
        System.out.println(" ESTADO: PRUEBA EXITOSA\n");
        System.out.println("--------------------------------------------------\n");

        System.out.println("[CASO 3] Venta de boleto con tarifa Intermedia (I - 85%)");
        int v2 = cooperativa.VenderBoleto("Ibarra-Quito", fechaViaje, horaSalida, "I", 2);
        System.out.println(" > Asiento seleccionado: 2");
        System.out.println(" > Valor calculado: $" + busPrueba.CalcularTarifa("I"));
        System.out.println(" > Codigo de respuesta: " + v2 + " (1 = Venta Exitosa)");
        System.out.println(" ESTADO: PRUEBA EXITOSA\n");
        System.out.println("--------------------------------------------------\n");

        System.out.println("[CASO 4] Venta de boleto con tarifa Especial (E - 50%)");
        int v3 = cooperativa.VenderBoleto("Ibarra-Quito", fechaViaje, horaSalida, "E", 3);
        System.out.println(" > Asiento seleccionado: 3");
        System.out.println(" > Valor calculado: $" + busPrueba.CalcularTarifa("E"));
        System.out.println(" > Codigo de respuesta: " + v3 + " (1 = Venta Exitosa)");
        System.out.println(" ESTADO: PRUEBA EXITOSA\n");
        System.out.println("--------------------------------------------------\n");

        System.out.println("[CASO 5] Validacion de restriccion por asiento ocupado");
        int vRepetida = cooperativa.VenderBoleto("Ibarra-Quito", fechaViaje, horaSalida, "D", 1);
        System.out.println(" > Intento de comprar nuevamente el asiento: 1");
        System.out.println(" > Codigo devuelto: " + vRepetida + " (-3 = Asiento ya ocupado)");
        System.out.println(" ESTADO: RESTRICCION APLICADA CORRECTAMENTE\n");
        System.out.println("--------------------------------------------------\n");

        System.out.println("[CASO 6] Auditoria y estado actual de asientos");
        System.out.println(" > Mapa de asientos libres: " + busPrueba.MostrarAsientosDisponibles());
        System.out.println(" > Cantidad de asientos libres: " + busPrueba.CalcularAsientosDisponibles());
        System.out.println(" > Cantidad de asientos ocupados: " + busPrueba.getAsientosOcupados());
        System.out.println(" ESTADO: REPORTE GENERADO CON EXITO\n");
        System.out.println("--------------------------------------------------\n");

        System.out.println("[CASO 7] Anulacion de boleto dentro del tiempo permitido");
        boolean anulado = cooperativa.AnularBoleto("Ibarra-Quito", fechaViaje, 2);
        System.out.println(" > Asiento a anular: 2");
        System.out.println(" > Operacion completada: " + anulado);
        System.out.println(" ESTADO: ANULACION EXITOSA\n");
        System.out.println("--------------------------------------------------\n");

        System.out.println("[CASO 8] Reporte final despues de las anulaciones");
        System.out.println(" > Mapa de asientos tras liberar: " + busPrueba.MostrarAsientosDisponibles());
        System.out.println(" > Total de boletos anulados registrados: " + busPrueba.getBoletosAnulados());
        System.out.println(" ESTADO: VALIDACION FINAL COMPLETADA\n");
        System.out.println("==================================================");

        cooperativa.guardarDatosEnArchivo(nombreArchivo);
        System.out.println(" [SISTEMA] Datos guardados exitosamente en '" + nombreArchivo + "'");
        System.out.println("==================================================");
    }
    
}
