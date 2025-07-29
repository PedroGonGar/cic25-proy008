package es.cic.curso25.proy008.exception;

/**
 * Excepción de dominio que se lanza cuando se detecta un intento
 * de “crear” (POST) un recurso que ya incluye un identificador,
 * violando la regla de no modificar durante la creación.
 * <p>
 * Extiende {@link RuntimeException} (unchecked) para no obligar a
 * declarar `throws` en cada llamada. Es capturada por
 * {@code ControllerAdviceException} y traducida a HTTP 400 Bad Request.
 * </p>
 * 
 * @author Pedro González
 * @version 1.0
 * @since 1.0
 */
public class ModificationSecurityException extends RuntimeException {

    /**
     * Construye una nueva {@code ModificationSecurityException}
     * con un mensaje genérico indicando un intento de modificación
     * durante la creación de un recurso.
     */
    public ModificationSecurityException() {
        super("Has tratado de modificar mediante creación");
    }

    /**
     * Construye una {@code ModificationSecurityException} con un
     * mensaje de error personalizado.
     *
     * @param message Mensaje que detalla el contexto de la excepción.
     */
    public ModificationSecurityException(String message) {
        super(message);
    }

    /**
     * Construye una {@code ModificationSecurityException} con un
     * mensaje personalizado y una causa subyacente, preservando
     * la traza original.
     *
     * @param message   Mensaje que detalla el contexto de la excepción.
     * @param throwable Causa original que provocó esta excepción.
     */
    public ModificationSecurityException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
