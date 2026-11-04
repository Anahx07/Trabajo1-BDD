import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CooperativaTransporteIbarra {

    private List<Transporte> listaTransportes;

    public CooperativaTransporteIbarra() {
        listaTransportes = new ArrayList<>();
    }

    public void RegistrarTransporte(Transporte t) {
        this.listaTransportes.add(t);
    }

    public Transporte BuscarTransporte(String ruta, LocalDate fecha) {
        for (Transporte t : listaTransportes) {
            if (t.getRuta().equalsIgnoreCase(ruta) && t.getFechaSalida().equals(fecha)) {
                return t;
            }
        }
        return null;
    }

    public int VenderBoleto(String ruta, LocalDate fecha, LocalTime hora, String tipoViaje, int numAsiento) {
        Transporte t = BuscarTransporte(ruta, fecha);
        if (t == null)
            return 0;

        LocalDateTime fechaHoraSalida = LocalDateTime.of(t.getFechaSalida(), t.getHoraSalida());
        if (LocalDateTime.now().isAfter(fechaHoraSalida)) {
            return -1;
        }

        if (numAsiento < 1 || numAsiento > t.getAsientos().length) {
            return -2;
        }

        Boleto b = (Boleto) t.getAsientos()[numAsiento - 1];
        if (b != null && !b.isAnulado()) {
            return -3;
        }

        t.getAsientos()[numAsiento - 1] = new Boleto(numAsiento, tipoViaje);
        t.setAsientosOcupados(t.getAsientosOcupados() + 1);

        return 1;
    }

    public boolean AnularBoleto(String ruta, LocalDate fecha, int numAsiento) {
        Transporte t = BuscarTransporte(ruta, fecha);
        if (t == null)
            return false;

        if (numAsiento < 1 || numAsiento > t.getAsientos().length)
            return false;

        Boleto b = (Boleto) t.getAsientos()[numAsiento - 1];
        if (b == null || b.isAnulado())
            return false;

        LocalDateTime fechaHoraSalida = LocalDateTime.of(t.getFechaSalida(), t.getHoraSalida());
        LocalDateTime horaLimiteAnulacion = fechaHoraSalida.minusHours(2);

        if (LocalDateTime.now().isAfter(horaLimiteAnulacion)) {
            return false;
        }

        b.setAnulado(true);
        t.registrarAnulacion();
        return true;
    }

    public void guardarDatosEnArchivo(String nombreArchivo) {
        try (PrintWriter pw = new PrintWriter(nombreArchivo)) {
            for (Transporte t : listaTransportes) {
                pw.println("TRANSPORTE;" + t.getRuta() + ";" + t.getFechaSalida() + ";"
                        + t.getHoraSalida() + ";" + t.getAsientos().length + ";" + 10.0);

                Object[] asientos = t.getAsientos();
                for (int i = 0; i < asientos.length; i++) {
                    if (asientos[i] != null) {
                        Boleto b = (Boleto) asientos[i];
                        pw.println("BOLETO;" + (i + 1) + ";" + b.getTipoViaje() + ";" + b.isAnulado());
                    }
                }
            }
            System.out.println("-> Datos guardados exitosamente en: " + nombreArchivo);
        } catch (IOException e) {
            System.out.println("Error al guardar archivo: " + e.getMessage());
        }
    }

    public void cargarDatosDesdeArchivo(String nombreArchivo) {
        File archivo = new File(nombreArchivo);
        if (!archivo.exists()) {
            System.out.println("No se encontró el archivo de datos previo. Se iniciará un registro limpio.");
            return;
        }

        try (Scanner scanner = new Scanner(archivo)) {
            Transporte transporteActual = null;

            while (scanner.hasNextLine()) {
                String linea = scanner.nextLine();
                String[] partes = linea.split(";");

                if (partes.length > 0 && partes[0].equals("TRANSPORTE")) {
                    String ruta = partes[1];
                    LocalDate fecha = LocalDate.parse(partes[2]);
                    LocalTime hora = LocalTime.parse(partes[3]);
                    int capacidad = Integer.parseInt(partes[4]);
                    double tarifa = Double.parseDouble(partes[5]);

                    transporteActual = new Transporte(ruta, fecha, hora, capacidad, tarifa);
                    this.listaTransportes.add(transporteActual);

                } else if (partes.length > 0 && partes[0].equals("BOLETO") && transporteActual != null) {
                    int numAsiento = Integer.parseInt(partes[1]);
                    String tipoViaje = partes[2];
                    boolean anulado = Boolean.parseBoolean(partes[3]);

                    Boleto b = new Boleto(numAsiento, tipoViaje);
                    b.setAnulado(anulado);

                    transporteActual.getAsientos()[numAsiento - 1] = b;
                    if (!anulado) {
                        transporteActual.setAsientosOcupados(transporteActual.getAsientosOcupados() + 1);
                    } else {
                        transporteActual.registrarAnulacion();
                    }
                }
            }
            System.out.println("Datos cargados exitosamente desde: " + nombreArchivo);
        } catch (Exception e) {
            System.out.println("Error al cargar el archivo: " + e.getMessage());
        }
    }
}