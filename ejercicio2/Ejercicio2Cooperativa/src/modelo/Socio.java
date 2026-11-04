/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;
import java.util.ArrayList;
/**
 *
 * @author EMELY
 */
public class Socio {
    private String cedula;
    private String nombre;
    private ArrayList<Cuenta> cuentas;

    public Socio(String cedula, String nombre) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.cuentas = new ArrayList<>();
    }

    public void agregarCuenta(Cuenta cuenta) {
        cuentas.add(cuenta);
    }
}
