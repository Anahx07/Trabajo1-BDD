/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ejercicio1bdd;

/**
 *
 * @author m0993
 */
public class Usuario {

    public static final int MAX_PRESTAMOS_ACTIVOS = 3;

    private String idUsuario;
    private String nombre;
    private String correo;
    private String carrera;
    private int prestamosActivos;
    private double multaPendiente;

    public Usuario() {
    }

    public Usuario(String idUsuario, String nombre, String correo, String carrera) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.correo = correo;
        this.carrera = carrera;
        this.prestamosActivos = 0;
        this.multaPendiente = 0.0;
    }

    public Usuario(String idUsuario, String nombre, String correo, String carrera, int prestamosActivos, double multaPendiente) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.correo = correo;
        this.carrera = carrera;
        this.prestamosActivos = Math.max(0, prestamosActivos);
        this.multaPendiente = Math.max(0.0, multaPendiente);
    }

    public boolean puedePrestar() {
        return this.prestamosActivos < MAX_PRESTAMOS_ACTIVOS && this.multaPendiente <= 0.001;
    }

    public String getMotivoBloqueo() {
        if (this.multaPendiente > 0.001) {
            return "Tiene una multa pendiente de $" + String.format("%.2f", this.multaPendiente);
        }
        if (this.prestamosActivos >= MAX_PRESTAMOS_ACTIVOS) {
            return "Ha alcanzado el limite maximo de " + MAX_PRESTAMOS_ACTIVOS + " prestamos activos";
        }
        return "Habilitado";
    }

    public boolean incrementarPrestamos() {
        if (this.prestamosActivos < MAX_PRESTAMOS_ACTIVOS) {
            this.prestamosActivos++;
            return true;
        }
        return false;
    }

    public void decrementarPrestamos() {
        if (this.prestamosActivos > 0) {
            this.prestamosActivos--;
        }
    }

    public void agregarMulta(double monto) {
        if (monto > 0) {
            this.multaPendiente += monto;
        }
    }

    public double pagarMulta(double monto) {
        if (monto > 0) {
            this.multaPendiente = Math.max(0.0, this.multaPendiente - monto);
        }
        return this.multaPendiente;
    }

    public String toLineaArchivo() {
        return idUsuario + ";" + nombre + ";" + correo + ";" + carrera + ";" + prestamosActivos + ";" + multaPendiente;
    }

    public static Usuario fromLineaArchivo(String linea) {
        if (linea == null || linea.trim().isEmpty()) {
            return null;
        }
        String[] partes = linea.split(";");
        if (partes.length < 6) {
            return null;
        }
        try {
            String id = partes[0].trim();
            String nombre = partes[1].trim();
            String correo = partes[2].trim();
            String carrera = partes[3].trim();
            int prestamos = Integer.parseInt(partes[4].trim());
            double multa = Double.parseDouble(partes[5].trim());
            return new Usuario(id, nombre, correo, carrera, prestamos, multa);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getCarrera() {
        return carrera;
    }

    public void setCarrera(String carrera) {
        this.carrera = carrera;
    }

    public int getPrestamosActivos() {
        return prestamosActivos;
    }

    public void setPrestamosActivos(int prestamosActivos) {
        this.prestamosActivos = Math.max(0, prestamosActivos);
    }

    public double getMultaPendiente() {
        return multaPendiente;
    }

    public void setMultaPendiente(double multaPendiente) {
        this.multaPendiente = Math.max(0.0, multaPendiente);
    }

    @Override
    public String toString() {
        return "Usuario [" +
                "ID='" + idUsuario + '\'' +
                ", Nombre='" + nombre + '\'' +
                ", Carrera='" + carrera + '\'' +
                ", Prestamos Activos=" + prestamosActivos + "/" + MAX_PRESTAMOS_ACTIVOS +
                ", Multa Pendiente=$" + String.format("%.2f", multaPendiente) +
                ", Habilitado=" + (puedePrestar() ? "SI" : "NO (" + getMotivoBloqueo() + ")") +
                ']';
    }
}
