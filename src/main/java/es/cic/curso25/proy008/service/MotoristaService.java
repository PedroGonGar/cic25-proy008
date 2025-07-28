package es.cic.curso25.proy008.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.cic.curso25.proy008.model.Moto;
import es.cic.curso25.proy008.exception.ModificationSecurityException;
import es.cic.curso25.proy008.exception.MotoristaException;
import es.cic.curso25.proy008.model.Motorista;
import es.cic.curso25.proy008.repository.MotoRepository;
import es.cic.curso25.proy008.repository.MotoristaRepository;

//Le decimos a Spring que es un service
@Service
@Transactional
public class MotoristaService {

    // Usamos Logger para mantener un registro de la actividad y los herrores
    private final static Logger LOGGER = LoggerFactory.getLogger(Motorista.class);

    private final MotoristaRepository motoristaRepository;

    @Autowired
    private MotoRepository motoRepository;

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
        LOGGER.info("Creamos un Motorista");
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
        LOGGER.info("Obtenemos una lista de todos los Motoristas");
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
    public Optional<Motorista> get(Long id) {

        // Utilizamos un placeholder {} ya que con + se evalúa siempre la concatenación.
        LOGGER.info("Buscando motorista con id: {}", id);

        // return motoristaRepository.findById(id)
        //         .orElseThrow(() -> new MotoristaException(id));

        Optional<Motorista> motorista = motoristaRepository.findById(id);

        LOGGER.info("Encontrado el siguiente Motorista: "+motorista);

        return motorista;

    }

    // ····UPDATE····

    /**
     * Actualiza una entidad de motorista.
     * La moto tiene que existir y el id no puede ser Null.
     * 
     * @param motorista el motorista que se quiere actualizar
     * @return Entidad Motorista actualuzada o Error si no existe
     */
    public Motorista update(Motorista motorista) {

        LOGGER.info("Actualizamos un Motorista");

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
     * @param id ID de motorista
     */
    public void delete(long id) {
        LOGGER.info("Se intenta eliminar un Motorista con id "+id);
        
        if (!motoristaRepository.existsById(id)) {
            throw new MotoristaException(id);
        } else {
            motoristaRepository.deleteById(id);
            LOGGER.info("Borrado satisfactoriemante motorista con ID " +id);
        }
    }

    /**
     * Elimina todas las instancias de moto almacenadas en la BBDD.
     */
    public void deleteAll() {
        LOGGER.info("Borramos TODOS los motoristas existentes");
        motoristaRepository.deleteAll();
    }

    /**
     * Comprobamos que exista una Moto asociada al Motorista
     * @param moto
     * @return Boolean. Sera TRUE si hay moto, FALSE si no hay moto
     */
    private boolean hasMoto(Optional<Moto>moto){
        boolean resultado = false; //de primeras no hay moto
        if (moto.isPresent()){  //Si hay moto
            resultado = moto.get().geMotorista() != null; //le decimos a resultado que cambie el estado
        }
        return resultado;
    }

}
