package es.cic.curso25.proy008.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class MotoException extends RuntimeException {

    public MotoException(long id) {
        super("No se ha encontrado una moto con el id " + id);
    }

}
