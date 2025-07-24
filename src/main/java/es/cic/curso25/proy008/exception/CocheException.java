package es.cic.curso25.proy008.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción que indica que no se ha encontrado un {@code Coche}
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
public class CocheException extends RuntimeException {

    /**
     * Construye una nueva {@code CocheException} con un mensaje
     * que incluye el ID del coche no encontrado.
     *
     * @param id Identificador del coche que se intentó recuperar.
     */
    public CocheException(long id) {
        super("Coche con id " + id + " no encontrado.");
    }
}
