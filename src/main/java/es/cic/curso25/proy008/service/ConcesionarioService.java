package es.cic.curso25.proy008.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.cic.curso25.proy008.exception.ConcesionarioException;
import es.cic.curso25.proy008.model.Concesionario;
import es.cic.curso25.proy008.repository.ConcesionarioRepository;
import jakarta.transaction.Transactional;

/**
 * Servicio de negocio para la gestión de concesionarios.
 * <p>
 * Proporciona operaciones CRUD sobre la entidad {@link Concesionario}
 * dentro de un contexto transaccional.
 * </p>
 * 
 * @author Pedro González
 * @version 1.0
 * @since 1.0
 */
@Service
@Transactional
public class ConcesionarioService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConcesionarioService.class);

    @Autowired
    private ConcesionarioRepository concesionarioRepository;

    /**
     * Recupera un concesionario por su identificador.
     *
     * @param id Identificador del concesionario a buscar.
     * @return El {@link Concesionario} correspondiente al ID proporcionado.
     * @throws ConcesionarioException Si no existe un concesionario con el ID indicado.
     */
    public Concesionario get(Long id) {
        LOGGER.info("Buscando el concesionario con id: {}", id);
        return concesionarioRepository.findById(id)
                .orElseThrow(() -> new ConcesionarioException(id));
    }

    /**
     * Obtiene la lista completa de concesionarios.
     *
     * @return Colección de todas las entidades {@link Concesionario} almacenadas.
     */
    public List<Concesionario> get() {
        LOGGER.info("Buscando todos los concesionarios");
        return concesionarioRepository.findAll();
    }

    /**
     * Crea un nuevo concesionario en la base de datos.
     *
     * @param concesionario Objeto {@link Concesionario} a persistir.
     * @return La entidad {@link Concesionario} recién creada, con su ID asignado.
     */
    public Concesionario create(Concesionario concesionario) {
        LOGGER.info("Creando un concesionario");
        return concesionarioRepository.save(concesionario);
    }

    /**
     * Actualiza un concesionario existente.
     *
     * @param concesionario Objeto {@link Concesionario} con los datos modificados.
     *                      Debe incluir un ID válido.
     * @return La entidad {@link Concesionario} ya actualizada.
     */
    public Concesionario update(Concesionario concesionario) {
        LOGGER.info("Actualizando el concesionario con id: {}", concesionario.getId());
        return concesionarioRepository.save(concesionario);
    }

    /**
     * Elimina un concesionario por su identificador.
     *
     * @param id Identificador del concesionario a borrar.
     */
    public void delete(Long id) {
        LOGGER.info("Eliminando concesionario con id: {}", id);
        concesionarioRepository.deleteById(id);
    }
}
