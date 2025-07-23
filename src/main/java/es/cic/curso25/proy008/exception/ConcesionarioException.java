package es.cic.curso25.proy008.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ConcesionarioException extends RuntimeException{
    public ConcesionarioException(long id) {
        super("Concesionario con id " + id + " no encontrado."); 
    }
}
