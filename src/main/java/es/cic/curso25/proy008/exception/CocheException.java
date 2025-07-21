package es.cic.curso25.proy008.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * ╔════════════════════════════════════════════════════════════════════╗
 * ║                  🚫  C O C H E   E X C E P T I O N                 ║
 * ╠════════════════════════════════════════════════════════════════════╣
 * ║  PROPÓSITO                                                         ║
 * ║  ───────────────────────────────────────────────────────────────   ║
 * ║  Señalizar que un coche con el ID solicitado **no existe** en la   ║
 * ║  base de datos.                                                    ║
 * ║                                                                    ║
 * ║  DETALLES TÉCNICOS                                                 ║
 * ║  ───────────────────────────────────────────────────────────────   ║
 * ║  • Extiende {@link RuntimeException}:                              ║
 * ║       – Es *unchecked*, no obliga a “throws” en la firma.          ║
 * ║       – Permite propagarla libremente hasta que un handler         ║
 * ║         (Advice) la transforme en respuesta HTTP.                  ║
 * ║  • @ResponseStatus(HttpStatus.NOT_FOUND):                          ║
 * ║       – Spring detecta esta anotación y, si ningún                 ║
 * ║         @ExceptionHandler la intercepta, devuelve automáticamente  ║
 * ║         **HTTP 404**.                                              ║
 * ║       – El cuerpo será vacío a menos que un ControllerAdvice       ║
 * ║         incluya el mensaje.                                        ║
 * ║  • Mensaje constructor:                                            ║
 * ║       – “Coche con id X no encontrado.”                            ║
 * ║       – Se conserva en logs y se puede enviar al cliente.          ║
 * ║                                                                    ║
 * ║  USO                                                               ║
 * ║  ───────────────────────────────────────────────────────────────   ║
 * ║  CocheService#get(id):                                             ║
 * ║     return repo.findById(id).orElseThrow(()                        ║
 * ║         -> new CocheException(id));                                ║
 * ║                                                                    ║
 * ║  Y el flujo HTTP final:                                            ║
 * ║     • ControllerAdvice lo captura  → 404 + mensaje.                ║
 * ║     • Sin Advice → 404 + cuerpo vacío (Spring por defecto).        ║
 * ╚════════════════════════════════════════════════════════════════════╝ */

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CocheException extends RuntimeException {

    /**
     * Constructor que personaliza el mensaje con el ID inexistente.
     *
     * @param id  identificador buscado en la base de datos
     */
    public CocheException(long id) {
        super("Coche con id " + id + " no encontrado.");   
    }
}

