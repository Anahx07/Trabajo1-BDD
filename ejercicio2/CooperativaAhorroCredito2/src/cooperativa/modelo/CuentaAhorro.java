package cooperativa.modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;

/**
 * Cuenta de ahorros: interes anual del 4 % calculado mensualmente sobre el saldo
 * y saldo minimo de $5.00.
 *
 * Interpretacion: un retiro es valido si el saldo resultante es >= $5.00, o exactamente
 * $0.00 (retiro total, necesario para poder cerrar la cuenta, ya que solo se cierra con saldo 0).
 */
public class CuentaAhorro extends Cuenta {

    public static final BigDecimal TASA_ANUAL = new BigDecimal("0.04");
    public static final BigDecimal SALDO_MINIMO = new BigDecimal("5.00");
    private static final BigDecimal MESES = new BigDecimal("12");

    public CuentaAhorro(String numero, Socio socio) {
        super(numero, socio);
    }

    public CuentaAhorro(String numero, Socio socio, BigDecimal saldo, boolean activa) {
        super(numero, socio, saldo, activa);
    }

    @Override
    public String getTipo() {
        return "AHORRO";
    }

    @Override
    protected void validarSaldoTrasRetiro(BigDecimal saldoResultante) {
        if (saldoResultante.signum() < 0) {
            throw new OperacionInvalidaException("Retiro rechazado en " + getNumero()
                    + ": fondos insuficientes (la cuenta de ahorros no admite sobregiro; faltarian "
                    + dinero(saldoResultante.negate()) + ").");
        }
        boolean retiroTotal = saldoResultante.signum() == 0;
        if (!retiroTotal && saldoResultante.compareTo(SALDO_MINIMO) < 0) {
            throw new OperacionInvalidaException("Retiro rechazado en " + getNumero()
                    + ": la cuenta de ahorros debe mantener un saldo minimo de " + dinero(SALDO_MINIMO)
                    + " (quedaria en " + dinero(saldoResultante) + ").");
        }
    }

    /** saldo * 4 % / 12, redondeado a 2 decimales (HALF_UP). Saldos <= 0 no generan interes. */
    @Override
    public BigDecimal calcularInteresMensual(BigDecimal saldoBase) {
        if (saldoBase == null || saldoBase.signum() <= 0) {
            return BigDecimal.ZERO.setScale(2);
        }
        return saldoBase.multiply(TASA_ANUAL).divide(MESES, 2, RoundingMode.HALF_UP);
    }

    public static String descripcionInteres(YearMonth mes) {
        return "Interes " + mes;
    }

    /**
     * Acredita el interes del mes indicado (calculado sobre el saldo al ultimo dia de ese mes).
     * Solo puede acreditarse cuando el mes ya termino y una sola vez por mes.
     *
     * @return el movimiento generado, o null si no corresponde acreditar (ya acreditado o interes 0.00)
     */
    public Movimiento acreditarInteres(YearMonth mes, LocalDate fechaAcreditacion) {
        LocalDate finDeMes = mes.atEndOfMonth();
        if (fechaAcreditacion == null || fechaAcreditacion.isBefore(finDeMes)) {
            throw new OperacionInvalidaException("Los intereses de " + mes
                    + " solo se acreditan desde el " + finDeMes + ".");
        }
        String descripcion = descripcionInteres(mes);
        if (tieneMovimiento(TipoMovimiento.INTERES, descripcion)) {
            return null;
        }
        BigDecimal interes = calcularInteresMensual(saldoAl(finDeMes));
        if (interes.signum() == 0) {
            return null;
        }
        return registrarInteres(interes, fechaAcreditacion, descripcion);
    }
}
