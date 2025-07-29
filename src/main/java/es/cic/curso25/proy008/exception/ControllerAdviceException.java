package es.cic.curso25.proy008.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Controlador de excepciones global para los controladores REST.
 * <p>
 * Intercepta excepciones de dominio y las transforma en respuestas
 * HTTP con códigos y mensajes adecuados, evitando repetir lógica
 * de manejo de errores en cada endpoint.
 * </p>
 * 
 * @author Pedro González
 * @version 1.0
 * @since 1.0
 */
@RestControllerAdvice
public class ControllerAdviceException {

    /**
     * Maneja las {@link ModificationSecurityException}.
     * <p>
     * Se invoca cuando se intenta crear un recurso incluyendo un ID,
     * vulnerando las reglas de creación. Retorna HTTP 400 Bad Request
     * con el mensaje de la excepción en el cuerpo.
     * </p>
     *
     * @param ex excepción capturada que detalla el motivo de la violación
     * @return mensaje de error descriptivo para el cliente
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ModificationSecurityException.class)
    public String handleModificationSecurity(ModificationSecurityException ex) {
        return ex.getMessage();
    }

    /**
     * Maneja las {@link CocheException}.
     * <p>
     * Se invoca cuando no se encuentra un coche con el ID proporcionado.
     * Retorna HTTP 404 Not Found con el mensaje de la excepción en el cuerpo.
     * </p>
     *
     * @param ex excepción capturada que indica la ausencia del recurso
     * @return mensaje de error descriptivo para el cliente
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(CocheException.class)
    public String handleCocheNotFound(CocheException ex) {
        return ex.getMessage();
    }

    /**
     * Maneja las {@link ConcesionarioException}.
     * <p>
     * Se invoca cuando no se encuentra un concesionario con el ID proporcionado.
     * Retorna HTTP 404 Not Found con el mensaje de la excepción en el cuerpo.
     * </p>
     *
     * @param ex excepción capturada que indica la ausencia del concesionario
     * @return mensaje de error descriptivo para el cliente
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ConcesionarioException.class)
    public String handleConcesionarioNotFound(ConcesionarioException ex) {
        return ex.getMessage();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(MotoException.class)
    public String handleMotoNotFound(MotoException ex) {
        return ex.getMessage(); // 404 + «Coche con id … no encontrado.»
    }
}
