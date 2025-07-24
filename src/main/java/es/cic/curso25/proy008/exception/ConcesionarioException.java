package es.cic.curso25.proy008.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

/**
 * Excepción que indica que no se ha encontrado un {@code Concesionario}
 * con el identificador proporcionado.
 * <p>
 * Esta clase extiende {@link RuntimeException} (unchecked) y
 * está anotada con {@link ResponseStatus @ResponseStatus(HttpStatus.NOT_FOUND)},
 * de modo que, si se propaga sin ser capturada, Spring devolverá
 * automáticamente una respuesta HTTP 404 Not Found.
 * </p>
 * 
 * @author Pedro González
 * @version 1.0
 * @since 1.0
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ConcesionarioException extends RuntimeException {

    /**
     * Construye una nueva {@code ConcesionarioException} con un mensaje
     * que incluye el ID del concesionario no encontrado.
     *
     * @param id Identificador del concesionario que se intentó recuperar.
     */
    public ConcesionarioException(long id) {
        super("Concesionario con id " + id + " no encontrado.");
    }
}
