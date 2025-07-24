package es.cic.curso25.proy008.service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.cic.curso25.proy008.model.Moto;
import es.cic.curso25.proy008.controller.ModificationSecurityException;
import es.cic.curso25.proy008.exception.MotoristaException;
import es.cic.curso25.proy008.model.Motorista;
import es.cic.curso25.proy008.repository.MotoristaRepository;

//Le decimos a Spring que es un service
@Service
@Transactional
public class MotoristaService {

    // Usamos Logger para mantener un registro de la actividad y los herrores
    private final static Logger LOGGER = LoggerFactory.getLogger(Motorista.class);

    private final MotoristaRepository motoristaRepository;

    /**
     * Constructor de Motoristaservice.
     * Llama a una instancia de motoristarepository para asegurar el correcto
     * funcionamiento
     * 
     * @param motoristaRepository
     */
    public MotoristaService(MotoristaRepository motoristaRepository) {
        this.motoristaRepository = motoristaRepository;
    }

    // CRUD

    // ····CREATE····

    /**
     * Crea una instancia de la entidad de motorista.
     * 
     * @param motorista
     * @return
     */
    public Motorista create(Motorista motorista) {
        if (motorista.getId() != null) {
            throw new ModificationSecurityException(
                    "No se puede crear una Moto con un id existente " + motorista.getId());
        }
        return motoristaRepository.save(motorista);
    }

    // ····READ····

    /**
     * Obtiene todas las entradas de "motorista" en el repositorio.
     * 
     * @return
     */
    @Transactional(readOnly = true)
    public List<Motorista> get() {
        return motoristaRepository.findAll();
    }

    /**
     * Obtiene la instancia de "motorista" que coincida con el id
     * en caso de que no existan coincidencias, devuelve una MotoristaException.
     * 
     * @param id
     * @return Entidad Motorista encontrada o Error si no existe
     */
    @Transactional(readOnly = true)
    public Motorista get(long id) {

        // Utilizamos un placeholder {} ya que con + se evalúa siempre la concatenación.
        LOGGER.info("Buscando motorista con id: {}", id);

        return motoristaRepository.findById(id)
                .orElseThrow(() -> new MotoristaException(id));

    }

    // ····UPDATE····

    /**
     * Actualiza una entidad de motorista.
     * La moto tiene que existir y el id no puede ser Null.
     * 
     * @param motorista
     * @return Entidad Motorista actualuzada o Error si no existe
     */
    public Motorista update(Motorista motorista) {

        // Si el ID proporcionado es null
        if (motorista.getId() == null) {
            // Lanzamos una Excepcion de seguridad
            throw new ModificationSecurityException("El id no puede ser nulo");
            // Si el ID ya existe
        } else if (!motoristaRepository.existsById(motorista.getId())) {
            // Lanzamos una MotoristaException
            throw new MotoristaException(motorista.getId());
            // En el resto de casos
        } else {
            // Devolvemos la moto creada
            return motoristaRepository.save(motorista);
        }
    }

    // ····DELETE····

    /**
     * Elimina una instancia de moto que coincida con el id proporcionado.
     * En caso de no haber coincidencia lanza una MotoristaException
     * 
     * @param id
     */
    public void delete(long id) {

        if (!motoristaRepository.existsById(id)) {
            throw new MotoristaException(id);
        } else {
            motoristaRepository.deleteById(id);
        }
    }

    /**
     * Elimina todas las instancias de moto almacenadas en la BBDD.
     */
    public void deleteAll() {
        motoristaRepository.deleteAll();
    }

}
