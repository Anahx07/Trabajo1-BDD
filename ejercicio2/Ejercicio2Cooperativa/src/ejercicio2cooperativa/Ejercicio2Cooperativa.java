/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package ejercicio2cooperativa;
import modelo.*;
/**
 *
 * @author EMELY
 */
public class Ejercicio2Cooperativa {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        System.out.println("Sistema Cooperativa");
        CuentaAhorro ahorro =
                new CuentaAhorro("A001", 1000);

        CuentaCorriente corriente =
                new CuentaCorriente("C001", 100);

        ahorro.depositar(500);

        corriente.retirar(250);

        System.out.println(
                "Interés mensual: "
                + ahorro.calcularInteresMensual());

    
    }
    
}
