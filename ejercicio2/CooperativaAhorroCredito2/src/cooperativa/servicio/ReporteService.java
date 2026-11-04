package cooperativa.servicio;

import cooperativa.modelo.Cuenta;
import cooperativa.modelo.CuentaAhorro;
import cooperativa.modelo.CuentaCorriente;
import cooperativa.modelo.Movimiento;
import cooperativa.modelo.TipoMovimiento;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Locale;

/**
 * Genera los reportes de la gerencia. Cada metodo devuelve el texto del reporte.
 */
public class ReporteService {

    private static final String LINEA = "============================================================================";

    private final CooperativaService servicio;

    public ReporteService(CooperativaService servicio) {
        this.servicio = servicio;
    }

    /** Estado de cuenta con el saldo despues de cada movimiento. */
    public String estadoDeCuenta(String numero) {
        Cuenta c = servicio.buscarCuenta(numero);
        StringBuilder sb = new StringBuilder();
        sb.append(LINEA).append('\n');
        sb.append("ESTADO DE CUENTA ").append(c.getNumero()).append('\n');
        sb.append("Socio : ").append(c.getSocio().getNombre()).append(" (CI ").append(c.getSocio().getCedula()).append(")\n");
        sb.append("Tipo  : ").append(c.getTipo()).append("   Estado: ").append(c.isActiva() ? "ACTIVA" : "INACTIVA").append('\n');
        sb.append(LINEA).append('\n');
        sb.append(String.format("%-11s %-8s %-30s %12s %12s%n", "FECHA", "TIPO", "DESCRIPCION", "MONTO", "SALDO"));
        sb.append("----------------------------------------------------------------------------\n");
        if (!c.tieneMovimientos()) {
            sb.append("(sin movimientos)\n");
        }
        for (Movimiento m : c.getMovimientos()) {
            String monto = (m.getTipo() == TipoMovimiento.RETIRO ? "-" : "+") + fmt(m.getMonto());
            sb.append(String.format("%-11s %-8s %-30s %12s %12s%n", m.getFecha(), m.getTipo(),
                    recortar(m.getDescripcion(), 30), monto, fmt(m.getSaldoDespues())));
        }
        sb.append("----------------------------------------------------------------------------\n");
        sb.append("SALDO ACTUAL: $").append(fmt(c.getSaldo())).append('\n');
        return sb.toString();
    }

    /** Intereses del mes de todas las cuentas de ahorro (saldo base = saldo al ultimo dia del mes). */
    public String interesesDelMes(YearMonth mes) {
        LocalDate fin = mes.atEndOfMonth();
        StringBuilder sb = new StringBuilder();
        sb.append(LINEA).append('\n');
        sb.append("INTERESES DEL MES ").append(mes).append(" - CUENTAS DE AHORRO (4% anual / 12)\n");
        sb.append(LINEA).append('\n');
        sb.append(String.format("%-9s %-18s %12s %10s  %s%n", "CUENTA", "SOCIO", "SALDO BASE", "INTERES", "ESTADO"));
        sb.append("----------------------------------------------------------------------------\n");
        BigDecimal total = BigDecimal.ZERO.setScale(2);
        int cantidad = 0;
        for (Cuenta c : servicio.getCuentas()) {
            if (!(c instanceof CuentaAhorro)) {
                continue;
            }
            BigDecimal base = c.saldoAl(fin);
            BigDecimal interes = c.calcularInteresMensual(base);
            boolean acreditado = c.tieneMovimiento(TipoMovimiento.INTERES, CuentaAhorro.descripcionInteres(mes));
            String estado = acreditado ? "acreditado" : (interes.signum() == 0 ? "sin interes" : "pendiente");
            sb.append(String.format("%-9s %-18s %12s %10s  %s%n", c.getNumero(),
                    recortar(c.getSocio().getNombre(), 18), fmt(base), fmt(interes), estado));
            total = total.add(interes);
            cantidad++;
        }
        sb.append("----------------------------------------------------------------------------\n");
        sb.append("Cuentas de ahorro: ").append(cantidad).append("   TOTAL INTERESES: $").append(fmt(total)).append('\n');
        return sb.toString();
    }

    /** Cuentas corrientes con saldo negativo. */
    public String cuentasEnSobregiro() {
        StringBuilder sb = new StringBuilder();
        sb.append(LINEA).append('\n');
        sb.append("CUENTAS EN SOBREGIRO (limite $").append(fmt(CuentaCorriente.LIMITE_SOBREGIRO)).append(")\n");
        sb.append(LINEA).append('\n');
        sb.append(String.format("%-9s %-18s %12s %12s %12s%n", "CUENTA", "SOCIO", "SALDO", "SOBREGIRO", "DISPONIBLE"));
        sb.append("----------------------------------------------------------------------------\n");
        int cantidad = 0;
        BigDecimal total = BigDecimal.ZERO.setScale(2);
        for (Cuenta c : servicio.getCuentas()) {
            if (c instanceof CuentaCorriente && ((CuentaCorriente) c).estaEnSobregiro()) {
                CuentaCorriente cc = (CuentaCorriente) c;
                sb.append(String.format("%-9s %-18s %12s %12s %12s%n", cc.getNumero(),
                        recortar(cc.getSocio().getNombre(), 18), fmt(cc.getSaldo()),
                        fmt(cc.getMontoSobregirado()), fmt(cc.getSobregiroDisponible())));
                total = total.add(cc.getMontoSobregirado());
                cantidad++;
            }
        }
        if (cantidad == 0) {
            sb.append("(ninguna cuenta en sobregiro)\n");
        }
        sb.append("----------------------------------------------------------------------------\n");
        sb.append("Cuentas en sobregiro: ").append(cantidad).append("   TOTAL SOBREGIRADO: $").append(fmt(total)).append('\n');
        return sb.toString();
    }

    /** Total depositado y retirado entre dos fechas (inclusive). Los intereses no cuentan como deposito. */
    public String totalesPeriodo(LocalDate desde, LocalDate hasta) {
        BigDecimal dep = BigDecimal.ZERO, ret = BigDecimal.ZERO;
        BigDecimal depTr = BigDecimal.ZERO, retTr = BigDecimal.ZERO;
        int nDep = 0, nRet = 0;
        for (Cuenta c : servicio.getCuentas()) {
            for (Movimiento m : c.getMovimientos()) {
                if (m.getFecha().isBefore(desde) || m.getFecha().isAfter(hasta)) {
                    continue;
                }
                if (m.getTipo() == TipoMovimiento.DEPOSITO) {
                    dep = dep.add(m.getMonto());
                    nDep++;
                    if (m.isPorTransferencia()) {
                        depTr = depTr.add(m.getMonto());
                    }
                } else if (m.getTipo() == TipoMovimiento.RETIRO) {
                    ret = ret.add(m.getMonto());
                    nRet++;
                    if (m.isPorTransferencia()) {
                        retTr = retTr.add(m.getMonto());
                    }
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append(LINEA).append('\n');
        sb.append("TOTAL DEPOSITADO Y RETIRADO  ").append(desde).append(" a ").append(hasta).append('\n');
        sb.append(LINEA).append('\n');
        sb.append(String.format("Total depositado : $%12s  (%d depositos, de los cuales $%s por transferencias)%n",
                fmt(dep), nDep, fmt(depTr)));
        sb.append(String.format("Total retirado   : $%12s  (%d retiros,   de los cuales $%s por transferencias)%n",
                fmt(ret), nRet, fmt(retTr)));
        sb.append(String.format("Deposito externo : $%12s  (sin transferencias internas)%n", fmt(dep.subtract(depTr))));
        sb.append(String.format("Retiro externo   : $%12s  (sin transferencias internas)%n", fmt(ret.subtract(retTr))));
        sb.append("Nota: las transferencias internas aparecen en ambos totales (retiro + deposito).\n");
        return sb.toString();
    }

    /**
     * Auditoria de saldos: recalcula cada saldo desde los movimientos y verifica el saldo
     * registrado en cada movimiento, el orden de fechas y las reglas de saldo minimo / sobregiro.
     */
    public String auditoriaSaldos() {
        StringBuilder sb = new StringBuilder();
        sb.append(LINEA).append('\n');
        sb.append("AUDITORIA DE SALDOS\n");
        sb.append(LINEA).append('\n');
        int errores = 0;
        BigDecimal totalSaldos = BigDecimal.ZERO.setScale(2);
        for (Cuenta c : servicio.getCuentas()) {
            StringBuilder problemas = new StringBuilder();
            BigDecimal calculado = BigDecimal.ZERO.setScale(2);
            LocalDate anterior = null;
            for (Movimiento m : c.getMovimientos()) {
                calculado = (m.getTipo() == TipoMovimiento.RETIRO)
                        ? calculado.subtract(m.getMonto()) : calculado.add(m.getMonto());
                if (calculado.compareTo(m.getSaldoDespues()) != 0) {
                    problemas.append("     - ").append(m.getFecha()).append(": saldo registrado $")
                            .append(fmt(m.getSaldoDespues())).append(" pero el calculado es $")
                            .append(fmt(calculado)).append('\n');
                    calculado = m.getSaldoDespues();
                }
                if (anterior != null && m.getFecha().isBefore(anterior)) {
                    problemas.append("     - movimiento con fecha fuera de orden: ").append(m.getFecha()).append('\n');
                }
                anterior = m.getFecha();
            }
            if (calculado.compareTo(c.getSaldo()) != 0) {
                problemas.append("     - saldo de la cuenta $").append(fmt(c.getSaldo()))
                        .append(" no coincide con el de sus movimientos $").append(fmt(calculado)).append('\n');
            }
            if (c instanceof CuentaAhorro && c.getSaldo().signum() != 0
                    && c.getSaldo().compareTo(CuentaAhorro.SALDO_MINIMO) < 0) {
                problemas.append("     - ahorro por debajo del saldo minimo de $")
                        .append(fmt(CuentaAhorro.SALDO_MINIMO)).append('\n');
            }
            if (c instanceof CuentaCorriente && c.getSaldo().compareTo(CuentaCorriente.LIMITE_SOBREGIRO.negate()) < 0) {
                problemas.append("     - sobregiro por encima del limite de $")
                        .append(fmt(CuentaCorriente.LIMITE_SOBREGIRO)).append('\n');
            }
            if (!c.isActiva() && c.getSaldo().signum() != 0) {
                problemas.append("     - cuenta inactiva con saldo distinto de cero\n");
            }
            totalSaldos = totalSaldos.add(c.getSaldo());
            sb.append(String.format("%-9s %-10s saldo $%11s  %d mov.  %s%n", c.getNumero(), c.getTipo(),
                    fmt(c.getSaldo()), c.getMovimientos().size(), problemas.length() == 0 ? "OK" : "ERROR"));
            if (problemas.length() > 0) {
                sb.append(problemas);
                errores++;
            }
        }
        sb.append("----------------------------------------------------------------------------\n");
        sb.append("Cuentas auditadas: ").append(servicio.getCuentas().size())
                .append("   con errores: ").append(errores)
                .append("   Suma de saldos: $").append(fmt(totalSaldos)).append('\n');
        sb.append(errores == 0 ? "RESULTADO: TODOS LOS SALDOS SON CONSISTENTES\n"
                : "RESULTADO: SE ENCONTRARON INCONSISTENCIAS\n");
        return sb.toString();
    }

    // ----------------------------------------------------------------- helpers

    private static String fmt(BigDecimal v) {
        return String.format(Locale.US, "%,.2f", v);
    }

    private static String recortar(String texto, int max) {
        return texto.length() <= max ? texto : texto.substring(0, max - 1) + ".";
    }
}
