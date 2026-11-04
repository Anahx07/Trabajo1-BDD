/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ejercicio1bdd;

/**
 *
 * @author m0993
 */
public class Revistas {

    public static final int DIAS_PRESTAMO = 3;
    public static final double MULTA_POR_DIA = 0.50;

    private String id;
    private String titulo;
    private int numeroEdicion;
    private String issn;
    private int anioPublicacion;
    private int ejemplaresTotales;
    private int ejemplaresDisponibles;

    public Revistas() {
    }

    public Revistas(String id, String titulo, int numeroEdicion, String issn, int anioPublicacion, int ejemplaresTotales) {
        this.id = id;
        this.titulo = titulo;
        this.numeroEdicion = numeroEdicion;
        this.issn = issn;
        this.anioPublicacion = anioPublicacion;
        this.ejemplaresTotales = Math.max(0, ejemplaresTotales);
        this.ejemplaresDisponibles = this.ejemplaresTotales;
    }

    public Revistas(String id, String titulo, int numeroEdicion, String issn, int anioPublicacion, int ejemplaresTotales, int ejemplaresDisponibles) {
        this.id = id;
        this.titulo = titulo;
        this.numeroEdicion = numeroEdicion;
        this.issn = issn;
        this.anioPublicacion = anioPublicacion;
        this.ejemplaresTotales = Math.max(0, ejemplaresTotales);
        this.ejemplaresDisponibles = Math.max(0, Math.min(ejemplaresDisponibles, this.ejemplaresTotales));
    }

    public boolean hayDisponibles() {
        return this.ejemplaresDisponibles > 0;
    }

    public boolean prestarEjemplar() {
        if (hayDisponibles()) {
            this.ejemplaresDisponibles--;
            return true;
        }
        return false;
    }

    public boolean devolverEjemplar() {
        if (this.ejemplaresDisponibles < this.ejemplaresTotales) {
            this.ejemplaresDisponibles++;
            return true;
        }
        return false;
    }

    public void agregarEjemplares(int cantidad) {
        if (cantidad > 0) {
            this.ejemplaresTotales += cantidad;
            this.ejemplaresDisponibles += cantidad;
        }
    }

    public double calcularMulta(int diasRetraso) {
        if (diasRetraso <= 0) {
            return 0.0;
        }
        return diasRetraso * MULTA_POR_DIA;
    }

    public String toLineaArchivo() {
        return id + ";" + titulo + ";" + numeroEdicion + ";" + issn + ";" + anioPublicacion + ";" + ejemplaresTotales + ";" + ejemplaresDisponibles;
    }

    public static Revistas fromLineaArchivo(String linea) {
        if (linea == null || linea.trim().isEmpty()) {
            return null;
        }
        String[] partes = linea.split(";");
        if (partes.length < 7) {
            return null;
        }
        try {
            String id = partes[0].trim();
            String titulo = partes[1].trim();
            int edicion = Integer.parseInt(partes[2].trim());
            String issn = partes[3].trim();
            int anio = Integer.parseInt(partes[4].trim());
            int totales = Integer.parseInt(partes[5].trim());
            int disponibles = Integer.parseInt(partes[6].trim());
            return new Revistas(id, titulo, edicion, issn, anio, totales, disponibles);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getNumeroEdicion() {
        return numeroEdicion;
    }

    public void setNumeroEdicion(int numeroEdicion) {
        this.numeroEdicion = numeroEdicion;
    }

    public String getIssn() {
        return issn;
    }

    public void setIssn(String issn) {
        this.issn = issn;
    }

    public int getAnioPublicacion() {
        return anioPublicacion;
    }

    public void setAnioPublicacion(int anioPublicacion) {
        this.anioPublicacion = anioPublicacion;
    }

    public int getEjemplaresTotales() {
        return ejemplaresTotales;
    }

    public void setEjemplaresTotales(int ejemplaresTotales) {
        this.ejemplaresTotales = Math.max(0, ejemplaresTotales);
        if (this.ejemplaresDisponibles > this.ejemplaresTotales) {
            this.ejemplaresDisponibles = this.ejemplaresTotales;
        }
    }

    public int getEjemplaresDisponibles() {
        return ejemplaresDisponibles;
    }

    public void setEjemplaresDisponibles(int ejemplaresDisponibles) {
        this.ejemplaresDisponibles = Math.max(0, Math.min(ejemplaresDisponibles, this.ejemplaresTotales));
    }

    public int getEjemplaresPrestados() {
        return this.ejemplaresTotales - this.ejemplaresDisponibles;
    }

    public int getDiasPrestamo() {
        return DIAS_PRESTAMO;
    }

    public double getMultaPorDia() {
        return MULTA_POR_DIA;
    }

    @Override
    public String toString() {
        return "Revista [" +
                "ID='" + id + '\'' +
                ", Título='" + titulo + '\'' +
                ", Edición N°=" + numeroEdicion +
                ", ISSN='" + issn + '\'' +
                ", Año=" + anioPublicacion +
                ", Disponibles=" + ejemplaresDisponibles + "/" + ejemplaresTotales +
                ", Días préstamo=" + DIAS_PRESTAMO +
                ", Multa/día=$" + MULTA_POR_DIA +
                ']';
    }
}
