package cooperativa.modelo;

import java.util.ArrayList;

/**
 * Socio de la cooperativa. Puede tener una o varias cuentas (asociacion 1 a *).
 */
public class Socio {

    private final int id;
    private String nombre;
    private String cedula;
    private final ArrayList<Cuenta> cuentas = new ArrayList<>();

    public Socio(int id, String nombre, String cedula) {
        if (id <= 0) {
            throw new OperacionInvalidaException("El id del socio debe ser positivo.");
        }
        if (nombre == null || nombre.trim().isEmpty() || nombre.contains(";")) {
            throw new OperacionInvalidaException("Nombre de socio invalido.");
        }
        if (cedula == null || cedula.trim().isEmpty() || cedula.contains(";")) {
            throw new OperacionInvalidaException("Cedula de socio invalida.");
        }
        this.id = id;
        this.nombre = nombre.trim();
        this.cedula = cedula.trim();
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getCedula() { return cedula; }

    /** Devuelve una copia: la lista interna solo se modifica mediante agregar/quitar. */
    public ArrayList<Cuenta> getCuentas() {
        return new ArrayList<>(cuentas);
    }

    public void agregarCuenta(Cuenta cuenta) {
        if (cuenta != null && !cuentas.contains(cuenta)) {
            cuentas.add(cuenta);
        }
    }

    public void quitarCuenta(Cuenta cuenta) {
        cuentas.remove(cuenta);
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Socio) && ((Socio) o).id == id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public String toString() {
        return "Socio[" + id + "] " + nombre + " (CI " + cedula + ")";
    }
}
