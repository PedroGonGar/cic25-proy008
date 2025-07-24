package es.cic.curso25.proy008.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import es.cic.curso25.proy008.model.Coche;
import es.cic.curso25.proy008.model.Concesionario;
import es.cic.curso25.proy008.service.CocheService;
import es.cic.curso25.proy008.service.ConcesionarioService;

/**
 * Controlador REST que expone los endpoints para gestionar concesionarios
 * y registrar ventas de coches.
 * <p>
 * Ruta base: <code>/concesionarios</code>
 * </p>
 * 
 * @author Pedro González
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/concesionarios")
public class ConcesionarioController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConcesionarioController.class);

    @Autowired
    private ConcesionarioService concesionarioService;

    @Autowired
    private CocheService cocheService;

    /**
     * Recupera un concesionario por su identificador.
     *
     * @param id Identificador del concesionario a buscar.
     * @return La entidad {@link Concesionario} correspondiente al ID proporcionado.
     * @throws EntityNotFoundException Si no existe un concesionario con el ID indicado.
     */
    @GetMapping("/{id}")
    public Concesionario get(@PathVariable Long id) {
        LOGGER.info("Buscando el concesionario con id: {}", id);
        return concesionarioService.get(id);
    }

    /**
     * Obtiene la lista completa de concesionarios.
     *
     * @return Colección de todas las entidades {@link Concesionario} almacenadas.
     */
    @GetMapping
    public List<Concesionario> get() {
        LOGGER.info("Buscando todos los concesionarios");
        return concesionarioService.get();
    }

    /**
     * Crea un nuevo concesionario.
     *
     * @param concesionario Objeto JSON con los datos del concesionario a crear.
     *                       No debe incluir un <code>id</code> (nuevo recurso).
     * @return La entidad {@link Concesionario} recién creada, con su <code>id</code> asignado.
     * @throws ModificationSecurityException Si el cuerpo de la petición incluye un <code>id</code>,
     *                                       indicando un intento de modificación.
     */
    @PostMapping
    public Concesionario create(@RequestBody Concesionario concesionario) {
        if (concesionario.getId() != null) {
            throw new ModificationSecurityException("Intento de modificación en el create");
        }
        LOGGER.info("Creando un concesionario");
        return concesionarioService.create(concesionario);
    }

    /**
     * Registra la venta (creación) de un coche.
     *
     * @param coche Objeto JSON con los datos del coche a crear y asociar
     *              a un concesionario existente.
     * @return La entidad {@link Coche} recién creada, con su <code>id</code> asignado.
     */
    @PostMapping("/ventas")
    public Coche create(@RequestBody Coche coche) {
        LOGGER.info("Registrando venta de coche: {}", coche);
        return cocheService.create(coche);
    }

    /**
     * Actualiza un concesionario existente.
     *
     * @param id            Identificador del concesionario en la URL.
     * @param concesionario Objeto JSON con los datos actualizados. Debe
     *                      incluir el mismo <code>id</code> que el de la ruta.
     * @return {@link ResponseEntity} con la entidad {@link Concesionario} ya actualizada.
     * @throws ModificationSecurityException Si el <code>id</code> del cuerpo no coincide
     *                                       con el de la ruta.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Concesionario> update(
            @PathVariable Long id,
            @RequestBody Concesionario concesionario) {
        LOGGER.info("Actualizando concesionario con id {}", id);

        if (!id.equals(concesionario.getId())) {
            throw new ModificationSecurityException(
                    String.format("ID en ruta (%d) no coincide con ID en body (%d)", id, concesionario.getId())
            );
        }

        Concesionario actualizado = concesionarioService.update(concesionario);
        return ResponseEntity.ok(actualizado);
    }

    /**
     * Elimina un concesionario por su identificador.
     *
     * @param id Identificador del concesionario a borrar.
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        LOGGER.info("Borrando concesionario con id {}", id);
        concesionarioService.delete(id);
    }
}
