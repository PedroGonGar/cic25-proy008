package es.cic.curso25.proy008.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.cic.curso25.proy008.model.Motorista;
import es.cic.curso25.proy008.service.MotoristaService;

//Le decimos a rest que es un controller
@RestController
// Le exigimos una url
@RequestMapping("/motorista")
public class MotoristaController {

    // Nos creamos una instancia de servucio y le damos un
    // constructir dentro de la propia clase
    private final MotoristaService motoristaService;

    public MotoristaController(MotoristaService motoristaService) {
        this.motoristaService = motoristaService;
    }

    // ─────────────────────────CMETODOLOGIA
    // CRUD──────────────────────────────────────

    // ───────────────────────────────────────────────────────────────
    /**
     * Método de Crear (POST)
     * Crea una entidad de moto
     * 
     * @param motorista
     * @return
     */
    @PostMapping
    public Motorista crearMotorista(@RequestBody Motorista motorista) {
        return motoristaService.create(motorista);
    }

    @GetMapping("/{id}")
    public Motorista get(@PathVariable long id) {
        return motoristaService.get(id);
    }

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
