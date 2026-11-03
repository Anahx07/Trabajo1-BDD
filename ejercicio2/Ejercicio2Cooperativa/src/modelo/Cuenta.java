/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author EMELY
 */
public class Cuenta {
    protected String numeroCuenta;
    protected double saldo;
    protected boolean activa;

    public Cuenta() {
    }

    public Cuenta(String numeroCuenta, double saldo, boolean activa) {
        this.numeroCuenta = numeroCuenta;
        this.saldo = saldo;
        this.activa = activa;
    }

    public void depositar(double monto) {
        saldo += monto;
    }

    public boolean retirar(double monto) {
        if (monto > saldo) {
            return false;
        }

        saldo -= monto;
        return true;
    }
}
