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

@Service
@Transactional
public class ConcesionarioService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConcesionarioService.class);

    @Autowired
    private ConcesionarioRepository concesionarioRepository;

    public Concesionario get(Long id) {
        LOGGER.info(String.format("Buscando el concesionario con id: %d", id));
        return concesionarioRepository.findById(id)
                .orElseThrow(() -> new ConcesionarioException(id));
    }

    public List<Concesionario> get() {
        LOGGER.info("Buscando todos los concesionarios");
        return concesionarioRepository.findAll();
    }

    public Concesionario create(Concesionario concesionario) {
        LOGGER.info("Creando un concesionario");
        return concesionarioRepository.save(concesionario);
    }

    public Concesionario update(Concesionario concesionario) {
        LOGGER.info(String.format("Actualizando el concesionario con id: %d", concesionario.getId()));
        return concesionarioRepository.save(concesionario);
    }

    public void delete(Long id) {
        LOGGER.info("Eliminando concesionario con id: %d", id);
        concesionarioRepository.deleteById(id);
    }
}
