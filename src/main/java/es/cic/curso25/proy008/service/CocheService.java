package es.cic.curso25.proy008.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import es.cic.curso25.proy008.exception.CocheException;
import es.cic.curso25.proy008.model.Coche;
import es.cic.curso25.proy008.repository.CocheRepository;

/**
 * Servicio de dominio encargado de gestionar las operaciones CRUD de Coche
 * Expone la lógica de negocio relacionada con coches y
 * delega la persistencia a CocheRepository.
 * Por el momento no utilizamos @Transactional debido a la simplicidad de nuestro CRUD.
 */
@Service
public class CocheService {

    /**
     * Utilizamos LOGGER para registrar la actividad interna de CocheService.
     * con distintos niveles de detalle según el entorno de ejecución.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CocheService.class);

    private final CocheRepository cocheRepository;

    /**
     * Constructor injection (un único constructor ⇒ @Autowired opcional)
     * Spring lo asume implícitamente.
     */
    public CocheService(CocheRepository cocheRepository) {
        this.cocheRepository = cocheRepository;
    }

    /**
     * MÉTODOS CRUD (CREAR, LEER, ACTUALIZAR, ELIMINAR)
     * 
     * Recuperamos un coche por su identificador => SELECT * FROM coches WHERE id = ?
     */
    public Coche get(long id) {
        // Utilizamos un placeholder {} ya que con + se evalúa siempre la concatenación.
        LOGGER.info("Buscando coche con id: {}", id);
        return cocheRepository.findById(id)
                .orElseThrow(() -> new CocheException(id));
    }
    
    /**
     * Devuelve la lista de coches. Puede estar vacía.
     * SELECT * FROM coches
     */
    public List<Coche> get() {
        return cocheRepository.findAll();
    }

    /**
     * Crea (o actualiza, si ya existe id) un coche en la base de datos.
     * JPA decidirá INSERT/UPDATE según el valor del id.
     */
    public Coche create(Coche coche) {
        return cocheRepository.save(coche);
    }

    /**
     * Actualiza un coche existente.
     * Equivalente a llamar a save() con un id no nulo.
     * UPDATE coches SET ... WHERE id = ?
     */
    public void update(Coche coche) {
        cocheRepository.save(coche);
    }

    /**
     * Elimina un coche por su identificador.
     * DELETE FROM coches WHERE id = ?
     */
    public void delete(long id) {
        cocheRepository.deleteById(id);
    }
}
