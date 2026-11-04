/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ejercicio1bdd;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 *
 * @author m0993
 */
public class Prestamo {

    public static final String ESTADO_ACTIVO = "ACTIVO";
    public static final String ESTADO_DEVUELTO = "DEVUELTO";

    private String idPrestamo;
    private String idUsuario;
    private String codigoEjemplar;
    private String idMaterial;
    private String tituloMaterial;
    private String tipoMaterial;
    private LocalDate fechaPrestamo;
    private LocalDate fechaLimite;
    private LocalDate fechaDevolucion;
    private String estado;
    private int diasRetraso;
    private double multaGenerada;

    public Prestamo() {
        this.estado = ESTADO_ACTIVO;
    }

    public Prestamo(String idPrestamo, String idUsuario, String codigoEjemplar, String idMaterial, String tituloMaterial, String tipoMaterial, LocalDate fechaPrestamo) {
        this.idPrestamo = idPrestamo;
        this.idUsuario = idUsuario;
        this.codigoEjemplar = codigoEjemplar;
        this.idMaterial = idMaterial;
        this.tituloMaterial = tituloMaterial;
        this.tipoMaterial = tipoMaterial.toUpperCase().trim();
        this.fechaPrestamo = fechaPrestamo;
        
        if (this.tipoMaterial.equalsIgnoreCase("REVISTA") || this.tipoMaterial.equalsIgnoreCase("REVISTAS")) {
            this.fechaLimite = fechaPrestamo.plusDays(Revistas.DIAS_PRESTAMO);
        } else {
            this.fechaLimite = fechaPrestamo.plusDays(Libros.DIAS_PRESTAMO);
        }

        this.fechaDevolucion = null;
        this.estado = ESTADO_ACTIVO;
        this.diasRetraso = 0;
        this.multaGenerada = 0.0;
    }

    public Prestamo(String idPrestamo, String idUsuario, String codigoEjemplar, String idMaterial, 
                    String tituloMaterial, String tipoMaterial, LocalDate fechaPrestamo, 
                    LocalDate fechaLimite, LocalDate fechaDevolucion, String estado, 
                    int diasRetraso, double multaGenerada) {
        this.idPrestamo = idPrestamo;
        this.idUsuario = idUsuario;
        this.codigoEjemplar = codigoEjemplar;
        this.idMaterial = idMaterial;
        this.tituloMaterial = tituloMaterial;
        this.tipoMaterial = tipoMaterial;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaLimite = fechaLimite;
        this.fechaDevolucion = fechaDevolucion;
        this.estado = estado;
        this.diasRetraso = diasRetraso;
        this.multaGenerada = multaGenerada;
    }

    public boolean isDevuelto() {
        return ESTADO_DEVUELTO.equalsIgnoreCase(this.estado);
    }

    public boolean isActivo() {
        return ESTADO_ACTIVO.equalsIgnoreCase(this.estado);
    }

    public boolean isVencido(LocalDate fechaActual) {
        if (!isActivo()) {
            return false;
        }
        return fechaActual.isAfter(this.fechaLimite);
    }

    public int calcularDiasRetraso(LocalDate fechaReferencia) {
        if (fechaReferencia == null || !fechaReferencia.isAfter(this.fechaLimite)) {
            return 0;
        }
        return (int) ChronoUnit.DAYS.between(this.fechaLimite, fechaReferencia);
    }

    public boolean registrarDevolucion(LocalDate fechaEntrega) {
        if (isDevuelto()) {
            return false;
        }

        this.fechaDevolucion = fechaEntrega;
        this.estado = ESTADO_DEVUELTO;
        this.diasRetraso = calcularDiasRetraso(fechaEntrega);

        if (this.diasRetraso > 0) {
            if (this.tipoMaterial.equalsIgnoreCase("REVISTA") || this.tipoMaterial.equalsIgnoreCase("REVISTAS")) {
                this.multaGenerada = this.diasRetraso * Revistas.MULTA_POR_DIA;
            } else {
                this.multaGenerada = this.diasRetraso * Libros.MULTA_POR_DIA;
            }
        } else {
            this.multaGenerada = 0.0;
        }

        return true;
    }

    public String toLineaArchivo() {
        String strFechaDev = (fechaDevolucion != null) ? fechaDevolucion.toString() : "SIN_DEVOLVER";
        return idPrestamo + ";" + idUsuario + ";" + codigoEjemplar + ";" + idMaterial + ";" +
               tituloMaterial + ";" + tipoMaterial + ";" + fechaPrestamo + ";" + 
               fechaLimite + ";" + strFechaDev + ";" + estado + ";" + diasRetraso + ";" + multaGenerada;
    }

    public static Prestamo fromLineaArchivo(String linea) {
        if (linea == null || linea.trim().isEmpty()) {
            return null;
        }
        String[] partes = linea.split(";");
        if (partes.length < 12) {
            return null;
        }
        try {
            String idPrestamo = partes[0].trim();
            String idUsuario = partes[1].trim();
            String codigoEjemplar = partes[2].trim();
            String idMaterial = partes[3].trim();
            String tituloMaterial = partes[4].trim();
            String tipoMaterial = partes[5].trim();
            LocalDate fPrestamo = LocalDate.parse(partes[6].trim());
            LocalDate fLimite = LocalDate.parse(partes[7].trim());
            
            String strFechaDev = partes[8].trim();
            LocalDate fDev = strFechaDev.equalsIgnoreCase("SIN_DEVOLVER") ? null : LocalDate.parse(strFechaDev);
            
            String estado = partes[9].trim();
            int retraso = Integer.parseInt(partes[10].trim());
            double multa = Double.parseDouble(partes[11].trim());

            return new Prestamo(idPrestamo, idUsuario, codigoEjemplar, idMaterial, tituloMaterial, 
                                tipoMaterial, fPrestamo, fLimite, fDev, estado, retraso, multa);
        } catch (Exception e) {
            return null;
        }
    }

    public String getIdPrestamo() {
        return idPrestamo;
    }

    public void setIdPrestamo(String idPrestamo) {
        this.idPrestamo = idPrestamo;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getCodigoEjemplar() {
        return codigoEjemplar;
    }

    public void setCodigoEjemplar(String codigoEjemplar) {
        this.codigoEjemplar = codigoEjemplar;
    }

    public String getIdMaterial() {
        return idMaterial;
    }

    public void setIdMaterial(String idMaterial) {
        this.idMaterial = idMaterial;
    }

    public String getTituloMaterial() {
        return tituloMaterial;
    }

    public void setTituloMaterial(String tituloMaterial) {
        this.tituloMaterial = tituloMaterial;
    }

    public String getTipoMaterial() {
        return tipoMaterial;
    }

    public void setTipoMaterial(String tipoMaterial) {
        this.tipoMaterial = tipoMaterial;
    }

    public LocalDate getFechaPrestamo() {
        return fechaPrestamo;
    }

    public void setFechaPrestamo(LocalDate fechaPrestamo) {
        this.fechaPrestamo = fechaPrestamo;
    }

    public LocalDate getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(LocalDate fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public LocalDate getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(LocalDate fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getDiasRetraso() {
        return diasRetraso;
    }

    public void setDiasRetraso(int diasRetraso) {
        this.diasRetraso = Math.max(0, diasRetraso);
    }

    public double getMultaGenerada() {
        return multaGenerada;
    }

    public void setMultaGenerada(double multaGenerada) {
        this.multaGenerada = Math.max(0.0, multaGenerada);
    }

    @Override
    public String toString() {
        return "Prestamo [" +
                "ID='" + idPrestamo + '\'' +
                ", Usuario='" + idUsuario + '\'' +
                ", Ejemplar='" + codigoEjemplar + '\'' +
                ", Titulo='" + tituloMaterial + '\'' +
                ", Tipo=" + tipoMaterial +
                ", Fecha Prestamo=" + fechaPrestamo +
                ", Fecha Limite=" + fechaLimite +
                ", Estado=" + estado +
                (isDevuelto() ? ", Fecha Devolucion=" + fechaDevolucion + ", Retraso=" + diasRetraso + " dias, Multa=$" + String.format("%.2f", multaGenerada) : "") +
                ']';
    }
}
