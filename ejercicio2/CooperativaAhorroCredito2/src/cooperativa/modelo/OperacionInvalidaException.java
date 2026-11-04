package cooperativa.modelo;

/**
 * Excepcion de negocio: se lanza cuando una operacion viola una regla de la cooperativa.
 */
public class OperacionInvalidaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public OperacionInvalidaException(String mensaje) {
        super(mensaje);
    }
}
