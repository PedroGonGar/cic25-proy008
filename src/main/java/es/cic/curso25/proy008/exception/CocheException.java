package es.cic.curso25.proy008.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CocheException extends RuntimeException {

    public CocheException(long id) {
        super("Coche con id " + id + " no encontrado.");   
    }
}

