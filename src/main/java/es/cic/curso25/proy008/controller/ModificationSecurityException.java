package es.cic.curso25.proy008.controller;

/**
 * ╔════════════════════════════════════════════════════════════════╗
 * ║           ✋  MODIFICATION‑SECURITY‑EXCEPTION (DOMINIO)         ║
 * ╠════════════════════════════════════════════════════════════════ ╣
 * ║ ‑ Se lanza cuando un usuario intenta **CREAR** (POST) un        ║
 * ║   recurso que en realidad ya tiene ID → está “modificando”      ║
 * ║   algo que debería haberse creado sin identificador.            ║
 * ║ ‑ Extiende {@link RuntimeException} para no obligar a poner     ║
 * ║   `throws` ni try‑catch en todas las llamadas (unchecked).      ║
 * ║ ‑ Es capturada globalmente por {@code ControllerAdviceException}║
 * ║   y convertida en HTTP 400 Bad Request.                         ║
 * ╚════════════════════════════════════════════════════════════════╝
 */

public class ModificationSecurityException extends RuntimeException {

    /*─────────────────────────────────────────────────────────────
     * 1) CONSTRUCTOR POR DEFECTO
     *    ‑ Usa un mensaje genérico, útil el 90 % de los casos.
     *────────────────────────────────────────────────────────────*/

    public ModificationSecurityException() {
        super("Has tratado de modificar mediante creación");
    }

    /*─────────────────────────────────────────────────────────────
     * 2) CONSTRUCTOR CON MENSAJE CUSTOM
     *    ‑ Permite indicar más contexto: ID, usuario, etc.
     *      Ej.:  new ModificationSecurityException(
     *              "La moto con id 5 ya existe; no se puede crear");
     *────────────────────────────────────────────────────────────*/

    public ModificationSecurityException(String message) {
        super(message);
    }

    /*─────────────────────────────────────────────────────────────
     * 3) CONSTRUCTOR CON MENSAJE + CAUSA
     *    ‑ Encadena la excepción original 
     *      para no perder la traza cuando llega al handler global.
     *────────────────────────────────────────────────────────────*/

    public ModificationSecurityException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
