package es.cic.curso25.proy008.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import es.cic.curso25.proy008.model.Motorista;
import es.cic.curso25.proy008.exception.ModificationSecurityException;
import es.cic.curso25.proy008.exception.MotoException;
import es.cic.curso25.proy008.model.Moto;
import es.cic.curso25.proy008.repository.MotoRepository;
import jakarta.transaction.Transactional;

/**
 * ───────────────────────────────────────────────────────────────
 * Servicio de dominio encargado de gestionar las operaciones CRUD de Moto
 * Expone la lógica de negocio relacionada con motos y
 * delega la persistencia a MotoRepository.
 * Por el momento no utilizamos @Transactional debido a la simplicidad de
 * nuestro CRUD.
 * ───────────────────────────────────────────────────────────────
 */
@Service
@Transactional
public class MotoService {

    /**
     * ───────────────────────────────────────────────────────────────
     * Usamos Logger para mantener un registro de la actividad y los herrores
     * ───────────────────────────────────────────────────────────────
     */
    private final static Logger LOGGER = LoggerFactory.getLogger(Moto.class);

    
    private final MotoRepository motoRepository;

    public MotoService(MotoRepository motoRepository) {
        this.motoRepository = motoRepository;
    }

    // CRUD

    // ····CREATE····

    /**
     * ───────────────────────────────────────────────────────────────
     * Crea una moto en la BBDD
     * 
     * @param moto
     * @return Objeto Moto almacenado
     *         ───────────────────────────────────────────────────────────────
     */
    public Moto create(Moto moto) {
        if (moto.getId() != null) {
            throw new ModificationSecurityException(
                    "No se puede crear una Moto con un id existente " + moto.getId());
        }
        return motoRepository.save(moto);
    }

    // ····READ····

    /**
     * ───────────────────────────────────────────────────────────────
     * Obtiene todos las entradas del repositorio
     * 
     * @return Lista de entradas encontradas (Puede ser NULL, o estar vacía)
     *         ───────────────────────────────────────────────────────────────
     */
    public List<Moto> get() {
        return motoRepository.findAll();
    }

    /**
     * ───────────────────────────────────────────────────────────────
     * Metodo para devolver una entidad Moto que tenga un ID determinado
     * 
     * @param id
     * @return Entidad moto en caso de que exista. En caso contrario, devuelve un
     *         mensaje de error
     *         ───────────────────────────────────────────────────────────────
     */
    public Moto get(long id) {

        // Utilizamos un placeholder {} ya que con + se evalúa siempre la concatenación.
        LOGGER.info("Buscando moto con id: {}", id);

        return motoRepository.findById(id)
                .orElseThrow(() -> new MotoException(id));

    }

    // ····UPDATE····

    /**
     * ───────────────────────────────────────────────────────────────
     * Acualiza una moto. Se necesita mandar un id que no sea nulo
     * 
     * @param moto
     * @param id
     * @return Moto actualizada
     *         ───────────────────────────────────────────────────────────────
     */
    public Moto update(Moto moto) {

        //Si el ID proporcionado es null
        if (moto.getId() == null){
            //Lanzamos una Excepcion de seguridad
            throw new ModificationSecurityException("El id no puede ser nulo");
        //Si el ID ya existe
        }else if (!motoRepository.existsById(moto.getId())){
            //Lanzamos una MotoException
            throw new MotoException(moto.getId());
        //En el resto de casos
        }else{
            //Devolvemos la moto creada
            return motoRepository.save(moto);
        }
    }

    // ····DELETE····

    /**
     * ───────────────────────────────────────────────────────────────
     * Elimina una Entidad motor con un ID determinado
     * 
     * @param id
     *           ───────────────────────────────────────────────────────────────
     */
    public void delete(long id) {

        if (!motoRepository.existsById(id)){
            throw new MotoException(id);
        }else{
            motoRepository.deleteById(id);
        }
    }

    /**
     * ───────────────────────────────────────────────────────────────
     * Borra todas las entidades de Moto existentes
     * ───────────────────────────────────────────────────────────────
     */
    public void deleteAll() {
        motoRepository.deleteAll();
    }

}
