/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author EMELY
 */
public class CuentaAhorro extends Cuenta {
    public CuentaAhorro(String numeroCuenta, double saldo) {
        super(numeroCuenta, saldo, true);
    }

    public double calcularInteresMensual() {
        return saldo * (0.04 / 12);
    }
}
