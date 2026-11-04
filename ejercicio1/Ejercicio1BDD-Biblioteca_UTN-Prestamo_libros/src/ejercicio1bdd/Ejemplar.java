/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ejercicio1bdd;

/**
 *
 * @author m0993
 */
public class Ejemplar {

    private String codigoEjemplar;
    private String idMaterial;
    private String tipoMaterial;
    private String tituloMaterial;
    private boolean disponible;
    private String ubicacion;

    public Ejemplar() {
        this.disponible = true;
    }

    public Ejemplar(String codigoEjemplar, String idMaterial, String tipoMaterial, String tituloMaterial, String ubicacion) {
        this.codigoEjemplar = codigoEjemplar;
        this.idMaterial = idMaterial;
        this.tipoMaterial = tipoMaterial;
        this.tituloMaterial = tituloMaterial;
        this.disponible = true;
        this.ubicacion = ubicacion;
    }

    public Ejemplar(String codigoEjemplar, String idMaterial, String tipoMaterial, String tituloMaterial, boolean disponible, String ubicacion) {
        this.codigoEjemplar = codigoEjemplar;
        this.idMaterial = idMaterial;
        this.tipoMaterial = tipoMaterial;
        this.tituloMaterial = tituloMaterial;
        this.disponible = disponible;
        this.ubicacion = ubicacion;
    }

    public boolean prestar() {
        if (this.disponible) {
            this.disponible = false;
            return true;
        }
        return false;
    }

    public boolean devolver() {
        if (!this.disponible) {
            this.disponible = true;
            return true;
        }
        return false;
    }

    public String toLineaArchivo() {
        return codigoEjemplar + ";" + idMaterial + ";" + tipoMaterial + ";" + tituloMaterial + ";" + disponible + ";" + ubicacion;
    }

    public static Ejemplar fromLineaArchivo(String linea) {
        if (linea == null || linea.trim().isEmpty()) {
            return null;
        }
        String[] partes = linea.split(";");
        if (partes.length < 6) {
            return null;
        }
        String codigo = partes[0].trim();
        String idMat = partes[1].trim();
        String tipo = partes[2].trim();
        String titulo = partes[3].trim();
        boolean disp = Boolean.parseBoolean(partes[4].trim());
        String ubic = partes[5].trim();
        return new Ejemplar(codigo, idMat, tipo, titulo, disp, ubic);
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

    public String getTipoMaterial() {
        return tipoMaterial;
    }

    public void setTipoMaterial(String tipoMaterial) {
        this.tipoMaterial = tipoMaterial;
    }

    public String getTituloMaterial() {
        return tituloMaterial;
    }

    public void setTituloMaterial(String tituloMaterial) {
        this.tituloMaterial = tituloMaterial;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    @Override
    public String toString() {
        return "Ejemplar [" +
                "Codigo='" + codigoEjemplar + '\'' +
                ", Titulo='" + tituloMaterial + '\'' +
                ", Tipo=" + tipoMaterial +
                ", Estado=" + (disponible ? "DISPONIBLE" : "PRESTADO") +
                ", Ubicacion='" + ubicacion + '\'' +
                ']';
    }
}
