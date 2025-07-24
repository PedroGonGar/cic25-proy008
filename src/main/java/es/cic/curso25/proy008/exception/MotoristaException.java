package es.cic.curso25.proy008.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class MotoristaException extends RuntimeException {

    public MotoristaException(long id) {
        super("No existe el motorista con id " + id);
    }

}
