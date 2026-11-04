package cooperativa.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Movimiento inmutable de una cuenta. Guarda el saldo resultante despues de aplicarse,
 * lo que permite emitir el estado de cuenta y auditar los saldos.
 */
public class Movimiento {

    private final String numeroCuenta;
    private final LocalDate fecha;
    private final TipoMovimiento tipo;
    private final BigDecimal monto;
    private final BigDecimal saldoDespues;
    private final boolean porTransferencia;
    private final String descripcion;

    public Movimiento(String numeroCuenta, LocalDate fecha, TipoMovimiento tipo, BigDecimal monto,
            BigDecimal saldoDespues, boolean porTransferencia, String descripcion) {
        if (numeroCuenta == null || fecha == null || tipo == null || monto == null || saldoDespues == null) {
            throw new OperacionInvalidaException("Movimiento con datos incompletos.");
        }
        this.numeroCuenta = numeroCuenta;
        this.fecha = fecha;
        this.tipo = tipo;
        this.monto = monto;
        this.saldoDespues = saldoDespues;
        this.porTransferencia = porTransferencia;
        this.descripcion = (descripcion == null) ? "" : descripcion.replace(";", ",").replace("\n", " ").trim();
    }

    public String getNumeroCuenta() { return numeroCuenta; }
    public LocalDate getFecha() { return fecha; }
    public TipoMovimiento getTipo() { return tipo; }
    public BigDecimal getMonto() { return monto; }
    public BigDecimal getSaldoDespues() { return saldoDespues; }
    public boolean isPorTransferencia() { return porTransferencia; }
    public String getDescripcion() { return descripcion; }

    @Override
    public String toString() {
        return fecha + " " + tipo + " " + monto + " -> saldo " + saldoDespues + " (" + descripcion + ")";
    }
}
