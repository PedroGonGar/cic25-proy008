package es.cic.curso25.proy008.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import es.cic.curso25.proy008.exception.CocheException;
import es.cic.curso25.proy008.exception.ModificationSecurityException;
import es.cic.curso25.proy008.model.Coche;
import es.cic.curso25.proy008.repository.CocheRepository;

/**
 * Servicio de negocio para la gestión de {@link Coche}.
 * <p>
 * Actúa como capa intermedia entre los controladores REST y el repositorio
 * JPA, encapsulando la lógica de negocio relativa a los coches:
 * creación, consulta, actualización y eliminación.
 * </p>
 * <p>
 * Cada método abre su propia transacción al invocar al repositorio
 * (no se utiliza @Transactional a nivel de clase).
 * </p>
 * 
 * @author Pedro González
 * @version 1.0
 * @since 1.0
 */
@Service
public class CocheService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CocheService.class);

    private final CocheRepository cocheRepository;

    /**
     * Constructor para inyección de dependencias.
     * 
     * @param cocheRepository Repositorio JPA que gestiona la persistencia de {@link Coche}.
     */
    public CocheService(CocheRepository cocheRepository) {
        this.cocheRepository = cocheRepository;
    }

    /**
     * Recupera un coche por su identificador.
     * 
     * @param id Identificador único del coche.
     * @return El {@link Coche} correspondiente al ID.
     * @throws CocheException Si no existe un coche con el ID indicado.
     */
    public Coche get(long id) {
        LOGGER.info("Buscando coche con id: {}", id);
        return cocheRepository.findById(id)
                .orElseThrow(() -> new CocheException(id));
    }

    /**
     * Obtiene el listado completo de coches.
     * 
     * @return Lista de todas las entidades {@link Coche} existentes.
     */
    public List<Coche> get() {
        LOGGER.info("Obteniendo listado de coches");
        return cocheRepository.findAll();
    }

    /**
     * Crea un nuevo coche en la base de datos.
     * <p>
     * Regla de negocio: no se permite crear un coche que ya incluya un ID,
     * ya que ello implicaría una modificación encubierta.
     * </p>
     * 
     * @param coche Entidad {@link Coche} a crear; debe tener {@code id == null}.
     * @return La entidad {@link Coche} recién persistida, con su ID generado.
     * @throws ModificationSecurityException Si {@code coche.getId() != null}.
     */
    public Coche create(Coche coche) {
        LOGGER.info("Creando coche: {}", coche);
        if (coche.getId() != null) {
            throw new ModificationSecurityException(
                "No se puede crear un coche con id existente: " + coche.getId()
            );
        }
        return cocheRepository.save(coche);
    }

    /**
     * Actualiza un coche existente.
     * <p>
     * Valida que el coche incluya un ID y que dicho ID ya exista en la base de datos,
     * para mantener la semántica de UPDATE. Utiliza el control optimista
     * basado en el campo {@code @Version}.
     * </p>
     * 
     * @param coche Entidad {@link Coche} con ID no nulo y datos modificados.
     * @throws ModificationSecurityException Si {@code coche.getId() == null}.
     * @throws CocheException              Si no existe un coche con el ID indicado.
     */
    public void update(Coche coche) {
        LOGGER.info("Actualizando coche con id: {}", coche.getId());
        if (coche.getId() == null) {
            throw new ModificationSecurityException(
                "Para actualizar es obligatorio enviar el id"
            );
        }
        if (!cocheRepository.existsById(coche.getId())) {
            throw new CocheException(coche.getId());
        }
        cocheRepository.save(coche);
    }

    /**
     * Elimina un coche por su identificador.
     * <p>
     * Verifica la existencia previa del coche; si no existe,
     * lanza {@link CocheException}.
     * </p>
     * 
     * @param id Identificador del coche a eliminar.
     * @throws CocheException Si no existe un coche con el ID indicado.
     */
    public void delete(long id) {
        LOGGER.info("Borrando coche con id: {}", id);
        if (!cocheRepository.existsById(id)) {
            throw new CocheException(id);
        }
        cocheRepository.deleteById(id);
    }
}
