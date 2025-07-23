package es.cic.curso25.proy008.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.cic.curso25.proy008.model.Coche;
import es.cic.curso25.proy008.model.Concesionario;
import es.cic.curso25.proy008.service.CocheService;
import es.cic.curso25.proy008.service.ConcesionarioService;

@RestController
@RequestMapping("/concesionarios")
public class ConcesionarioController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConcesionarioController.class);

    @Autowired
    private ConcesionarioService concesionarioService;

    @Autowired
    private CocheService cocheService;

    @GetMapping("/{id}")
    public Concesionario get(@PathVariable Long id) {
        LOGGER.info(String.format("Buscando el concesionario con id: %d", id));
        return concesionarioService.get(id);
    }

    @GetMapping
    public List<Concesionario> get() {
        LOGGER.info("Buscando todos los concesionarios");
        return concesionarioService.get();
    }

    @PostMapping
    public Concesionario create(@RequestBody Concesionario concesionario) {
        if(concesionario.getId() != null) {
            throw new ModificationSecurityException("Intento de modificación en el create");
        }
        LOGGER.info("Creando un concesionario");
        return concesionarioService.create(concesionario);
    }

    @PostMapping("/ventas")
    public Coche create(@RequestBody Coche coche) {
        Coche cocheCreado = cocheService.create(coche);
        return cocheCreado;
    }

    @PutMapping
    public Concesionario update(@RequestBody Concesionario concesionario) {
        return concesionario;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        concesionarioService.delete(id);
    }
}
