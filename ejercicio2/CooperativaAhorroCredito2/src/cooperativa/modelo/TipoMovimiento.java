package cooperativa.modelo;

/**
 * Tipos de movimiento que puede tener una cuenta.
 * Una transferencia NO es un tipo propio: se registra como RETIRO en el origen
 * y DEPOSITO en el destino (ambos con porTransferencia = true).
 */
public enum TipoMovimiento {
    DEPOSITO,
    RETIRO,
    INTERES
}
