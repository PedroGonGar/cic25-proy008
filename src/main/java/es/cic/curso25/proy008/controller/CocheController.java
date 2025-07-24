package es.cic.curso25.proy008.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import es.cic.curso25.proy008.exception.ModificationSecurityException;
import es.cic.curso25.proy008.model.Coche;
import es.cic.curso25.proy008.service.CocheService;

/**
 * Controlador REST para la gestión de coches.
 * <p>
 * Expone los endpoints CRUD sobre la entidad {@link Coche} bajo el
 * recurso base <code>/coches</code>. Convierte peticiones HTTP en
 * llamadas al {@link CocheService} y devuelve objetos Java que Spring
 * serializa a JSON.
 * </p>
 * <p>
 * La gestión de errores se delega en un {@code @ControllerAdvice}:
 * <ul>
 * <li>Si no existe un coche, el servicio lanza {@code CocheException} → 404 NOT
 * FOUND.</li>
 * <li>Si se intenta crear un coche con un ID no nulo, se lanza
 * {@code ModificationSecurityException} → 400 BAD REQUEST.</li>
 * </ul>
 * </p>
 * 
 * @author TuNombre
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/coches")
public class CocheController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CocheController.class);

    private final CocheService cocheService;

    /**
     * Constructor para la inyección del servicio de coches.
     * 
     * @param cocheService Servicio que implementa la lógica de negocio
     *                     para la entidad {@link Coche}.
     */
    public CocheController(CocheService cocheService) {
        this.cocheService = cocheService;
    }

    /**
     * GET /coches/{id} : Recupera un coche por su identificador.
     * <p>
     * Responde con un 200 OK y el JSON del coche si existe,
     * o con un 404 NOT FOUND si no se encuentra.
     * </p>
     * 
     * @param id Identificador único del coche a recuperar.
     * @return La entidad {@link Coche} correspondiente.
     */
    @GetMapping("/{id}")
    public Coche get(@PathVariable Long id) {
        LOGGER.info("Buscando coche con id {}", id);
        return cocheService.get(id);
    }

    /**
     * GET /coches : Recupera todos los coches.
     * <p>
     * Devuelve un 200 OK y la lista (puede estar vacía).
     * </p>
     * 
     * @return Lista de todas las entidades {@link Coche}.
     */
    @GetMapping
    public List<Coche> getAll() {
        LOGGER.info("Buscando todos los coches");
        return cocheService.get();
    }

    /**
     * POST /coches : Crea un nuevo coche.
     * <p>
     * El objeto JSON no debe incluir un campo <code>id</code>.
     * Si lo incluye, se lanza {@code ModificationSecurityException}
     * y se devuelve un 400 BAD REQUEST.
     * </p>
     * 
     * @param coche Datos del coche a crear.
     * @return La entidad {@link Coche} recién creada, con su <code>id</code>
     *         asignado.
     */
    @PostMapping
    public Coche create(@RequestBody Coche coche) {
        if (coche.getId() != null) {
            throw new ModificationSecurityException("Intento de modificación en el create");
        }
        LOGGER.info("Creando un coche");
        return cocheService.create(coche);
    }

    /**
     * PUT /coches/{id} : Actualiza un coche existente.
     * <p>
     * Si el ID de la URL no coincide con el ID del JSON, lanza
     * {@link ModificationSecurityException} (400 Bad Request).
     * </p>
     * 
     * @param id    Identificador del coche que se va a actualizar.
     * @param coche Objeto {@link Coche} con los datos modificados.
     * @throws ModificationSecurityException Si los IDs no coinciden.
     */
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(
            @PathVariable Long id,
            @RequestBody Coche coche) {

        LOGGER.info("Actualizando coche con id {}", id);

        if (!id.equals(coche.getId())) {
            throw new ModificationSecurityException(
                    String.format("ID en ruta (%d) no coincide con ID en body (%d)", id, coche.getId()));
        }

        cocheService.update(coche);
    }

    /**
     * DELETE /coches/{id} : Elimina un coche por su identificador.
     * <p>
     * Responde con un 200 OK si la operación es satisfactoria.
     * </p>
     * 
     * @param id Identificador del coche a borrar.
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        LOGGER.info("Borrando coche con id {}", id);
        cocheService.delete(id);
    }
}
