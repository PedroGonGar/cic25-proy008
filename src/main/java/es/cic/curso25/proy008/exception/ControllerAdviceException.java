package es.cic.curso25.proy008.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * ╔════════════════════════════════════════════════════════════════╗
 * ║ ⚙ CONTROLLER‑ADVICE ║
 * ╠════════════════════════════════════════════════════════════════╣
 * ║  ¿QUÉ ES? ║
 * ║ Un “Advice” es un componente que intercepta de forma global ║
 * ║ las excepciones que se lanzan desde cualquier @RestController ║
 * ║ de la aplicación. ║
 * ║ ║
 * ║  ¿POR QUÉ? ║
 * ║ Para centralizar la gestión de errores y evitar repetir ║
 * ║ try‑catch en cada endpoint. ║
 * ╚════════════════════════════════════════════════════════════════╝
 */

@RestControllerAdvice // Actúa sobre todos los controladores REST
public class ControllerAdviceException {

    /**
     * ────────────────────────────────────────────────────────────────
     * CUÁNDO ENTRA ESTE MÉTODO
     * ───────────────────────────────────────────────────────────────
     * • Cuando cualquier parte del código (Service, Controller, …)
     * lanza {@link ModificationSecurityException}.
     *
     * QUÉ HACE
     * • Devuelve HTTP 400 (Bad Request) ← @ResponseStatus
     * • El cuerpo de la respuesta es el texto de la excepción
     * (ex.getMessage()) — por defecto: "Has tratado de modificar
     * mediante creación".
     *
     * IMPORTANTE
     * • No es necesario declarar ResponseEntity; Spring serializa
     * el String de retorno como text/plain.
     * • Si en el futuro queremos devolver JSON, bastaría con
     * retornar un objeto (Map o DTO) en lugar de String.
     * ────────────────────────────────────────────────────────────────
     *
     * @param ex la excepción capturada (inyectada por Spring)
     * @return mensaje que viajará en el cuerpo HTTP
     */

    @ResponseStatus(HttpStatus.BAD_REQUEST) // Código HTTP a enviar
    @ExceptionHandler(ModificationSecurityException.class) // La excepción que captura
    public String handleModificationSecurity(ModificationSecurityException ex) {

        // Devolvemos el mensaje personalizado definido en la excepción
        return ex.getMessage(); // "Has tratado de modificar mediante creación"
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(CocheException.class)
    public String handleCocheNotFound(CocheException ex) {
        return ex.getMessage(); // 404 + «Coche con id … no encontrado.»
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(MotoException.class)
    public String handleMotoNotFound(MotoException ex) {
        return ex.getMessage(); // 404 + «Coche con id … no encontrado.»
    }
}
