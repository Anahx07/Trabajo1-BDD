package cooperativa.modelo;

import java.math.BigDecimal;

/**
 * Cuenta corriente: no genera intereses y permite un sobregiro de hasta $200.00.
 */
public class CuentaCorriente extends Cuenta {

    public static final BigDecimal LIMITE_SOBREGIRO = new BigDecimal("200.00");

    public CuentaCorriente(String numero, Socio socio) {
        super(numero, socio);
    }

    public CuentaCorriente(String numero, Socio socio, BigDecimal saldo, boolean activa) {
        super(numero, socio, saldo, activa);
    }

    @Override
    public String getTipo() {
        return "CORRIENTE";
    }

    @Override
    protected void validarSaldoTrasRetiro(BigDecimal saldoResultante) {
        if (saldoResultante.compareTo(LIMITE_SOBREGIRO.negate()) < 0) {
            throw new OperacionInvalidaException("Retiro rechazado en " + getNumero()
                    + ": excede el sobregiro maximo de " + dinero(LIMITE_SOBREGIRO)
                    + " (quedaria en " + dinero(saldoResultante) + ").");
        }
    }

    /** Las cuentas corrientes no generan intereses. */
    @Override
    public BigDecimal calcularInteresMensual(BigDecimal saldoBase) {
        return BigDecimal.ZERO.setScale(2);
    }

    public boolean estaEnSobregiro() {
        return getSaldo().signum() < 0;
    }

    /** Monto sobregirado (positivo) o 0.00. */
    public BigDecimal getMontoSobregirado() {
        return estaEnSobregiro() ? getSaldo().negate() : BigDecimal.ZERO.setScale(2);
    }

    /** Cuanto sobregiro adicional queda disponible. */
    public BigDecimal getSobregiroDisponible() {
        return LIMITE_SOBREGIRO.add(getSaldo());
    }
}
