/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author EMELY
 */
public class CuentaCorriente extends Cuenta {
    public CuentaCorriente(String numeroCuenta, double saldo) {
        super(numeroCuenta, saldo, true);
    }

    @Override
    public boolean retirar(double monto) {

        if ((saldo - monto) < -200) {
            return false;
        }

        saldo -= monto;
        return true;
    }
}
