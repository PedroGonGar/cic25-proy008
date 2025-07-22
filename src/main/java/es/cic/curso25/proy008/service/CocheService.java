package es.cic.curso25.proy008.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import es.cic.curso25.proy008.controller.ModificationSecurityException;
import es.cic.curso25.proy008.exception.CocheException;
import es.cic.curso25.proy008.model.Coche;
import es.cic.curso25.proy008.repository.CocheRepository;

/**
 * ╔═══════════════════════════════════════════════════════════════════════════╗
 * ║ 🛠 C O C H E S E R V I C E ║
 * ╠═══════════════════════════════════════════════════════════════════════════╣
 * ║ · Capa de NEGOCIO entre el Controller y el Repository. ║
 * ║ · Aquí vive la lógica que **no** pertenece ni al endpoint (HTTP) ║
 * ║ ni al acceso a datos (JPA). ║
 * ║ · Con @Service Spring lo registra como componente y puede inyectarlo. ║
 * ║ · Sin @Transactional: cada método ejecuta su propia transacción ║
 * ║ (Spring Data lo abre al llamar a save/find…). ║
 * ╚═══════════════════════════════════════════════════════════════════════════╝
 */

@Service
public class CocheService {

    /**
     * ───────────────────────────────────────────────────────────────
     * L O G G E R
     * ───────────────────────────────────────────────────────────────
     * private static final Logger LOGGER = LoggerFactory.getLogger(...);
     *
     * 1▸ ¿Por qué SLF4J?
     * SLF4J (Simple Logging Facade for Java) es solo una *fachada*.
     * Se elige el backend en tiempo de ejecución.
     *
     * 2▸ Nivel de log + rendimiento
     * LOGGER.info("Buscando id: {}", id);
     *
     * • El placeholder {} se sustituye **solo** si el nivel INFO
     * está activado. Si está apagado, SLF4J ni siquiera
     * construye la cadena → cero coste de concatenación.
     *
     * • Si escribiese "Buscando id: " + id, la concatenación
     * se ejecuta SIEMPRE aunque luego se descarte, malgastando
     * CPU y memoria.
     *
     * 3▸ Constante static final
     * ‑ static: un único Logger por clase (no por instancia).
     * ‑ final : no puede reasignarse → thread‑safe.
     * ──────────────────────────────────────────────────────────────
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CocheService.class);

    /**
     * ───────────────────────────────────────────────────────────────
     * DEPENDENCY INJECTION
     * ‑ Usamos constructor‑injection (mejor para pruebas).
     * ‑ Spring inyecta el proxy de CocheRepository automáticamente.
     * - Un único constructor => @Autowired opcional
     * ──────────────────────────────────────────────────────────────
     */
    private final CocheRepository cocheRepository;

    public CocheService(CocheRepository cocheRepository) {
        this.cocheRepository = cocheRepository;
    }

    /**
     * ───────────────────────────────────────────────────────────────
     * READ (GET por ID)
     * • Si no existe ⇒ lanzamos CocheException
     * El ControllerAdvice global lo convierte en HTTP 404.
     * ──────────────────────────────────────────────────────────────
     */
    public Coche get(long id) {
        LOGGER.info("Buscando coche con id: {}", id);
        return cocheRepository.findById(id)
                .orElseThrow(() -> new CocheException(id));
    }

    /**
     * ───────────────────────────────────────────────────────────────
     * READ (GET todos)
     * ──────────────────────────────────────────────────────────────
     */
    public List<Coche> get() {
        LOGGER.info("Obteniendo listado de coches: ");
        return cocheRepository.findAll(); // SELECT * FROM coche
    }

    /**
     * ───────────────────────────────────────────────────────────────
     * CREATE
     * • Regla de negocio: NO se permite “crear” si ya trae ID.
     * Eso sería una modificación encubierta.
     * • Lanzamos ModificationSecurityException → Advice ⇒ 400.
     * ──────────────────────────────────────────────────────────────
     */
    public Coche create(Coche coche) {
        LOGGER.info("Creando coche: ");
        if (coche.getId() != null) {
            throw new ModificationSecurityException(
                    "No se puede crear un coche con id existente: " + coche.getId());
        }
        return cocheRepository.save(coche); // INSERT y devuelve con id
    }

    /**
     * ───────────────────────────────────────────────────────────────
     * UPDATE
     * ───────────────────────────────────────────────────────────────
     */
    public void update(Coche coche) {
        LOGGER.info("Actualizando listado de coches: ");
        /**
         * 1) Validación de integridad 
         * El endpoint PUT debe recibir *siempre* un id.
         * Si llega null, en realidad es un intento de “create”.
         * Lanzamos 400 Bad Request (ModificationSecurityException).
         */
        if (coche.getId() == null) {
            throw new ModificationSecurityException(
                    "Para actualizar es obligatorio enviar el id");
        }

        /**
         * 2) ¿Existe en BD?
         * save() crearía un registro nuevo si no lo encontramos,
         * rompiendo la semántica de UPDATE.
         * Comprobamos con existsById y, si no existe, 404 Not Found.
         */
        if (!cocheRepository.existsById(coche.getId())) {
            throw new CocheException(coche.getId());
        }

        /**
         * 3) Persistencia
         * A partir de aquí es seguro ejecutar el UPDATE:
         * Hibernate generará “update … where id = ? and version = ?”
         * gracias al campo @Version (control optimista de concurrencia).
         */
        cocheRepository.save(coche);
    }

    /**
     * ───────────────────────────────────────────────────────────────
     * DELETE
     * ───────────────────────────────────────────────────────────────
     */
    public void delete(long id) {
        LOGGER.info("Borrando coche: ");
        /**
         * 1) Verificación de existencia
         * Si no hay fila con ese id devolvemos 404 Not Found.
         */
        if (!cocheRepository.existsById(id)) {
            throw new CocheException(id);
        }

        // 2) Eliminación segura
        cocheRepository.deleteById(id);
    }

}
