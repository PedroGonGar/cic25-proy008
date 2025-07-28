package es.cic.curso25.proy008.controller;

import java.util.List;
import java.util.Optional;

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

import es.cic.curso25.proy008.exception.ModificationSecurityException;
import es.cic.curso25.proy008.model.Motorista;
import es.cic.curso25.proy008.service.MotoristaService;

//Le decimos a rest que es un controller
@RestController
// Le exigimos una url
@RequestMapping("/motoristas")
public class MotoristaController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MotoristaController.class);


    // Nos creamos una instancia de servucio y le damos un
    // constructir dentro de la propia clase
    @Autowired
    private MotoristaService motoristaService;

    // public MotoristaController(MotoristaService motoristaService) {
    //     this.motoristaService = motoristaService;
    // }

    // ─────────────────────────METODOLOGIA CRUD──────────────────────────────────────

    /**
     * Método de Crear (POST)
     * Crea una entidad de moto
     * 
     * @param motorista
     * @return
     */
    @PostMapping
    public Motorista crearMotorista(@RequestBody Motorista motorista) {

        if (motorista.getId()!= null){
            throw new ModificationSecurityException("el id no puede ser 0");
        }

        LOGGER.info("Creando Motorista");
        return motoristaService.create(motorista);
    }

    @GetMapping("/{id}")
    public Optional<Motorista> get (@PathVariable Long id){
        Optional<Motorista> motorista = motoristaService.get(id);

        return motorista;
    }

    // @GetMapping("/{id}")
    // public Motorista get(@PathVariable long id) {
    //     return motoristaService.get(id);
    // }

    @GetMapping
    public List<Motorista> get() {
        return motoristaService.get();
    }

    @PutMapping
    public Motorista update(@RequestBody Motorista motorista) {
        return motoristaService.update(motorista);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        motoristaService.delete(id);
    }

    @DeleteMapping()
    public void deleteAll() {
        motoristaService.deleteAll();
    }

}
