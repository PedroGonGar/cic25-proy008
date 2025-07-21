package es.cic.curso25.proy008.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import es.cic.curso25.proy008.exception.CocheException;
import es.cic.curso25.proy008.model.Coche;
import es.cic.curso25.proy008.service.CocheService;

/**
 * CAPA CONTROLLER (REST)
 *
 * ‑ Expone el recurso /coches => Colección de coches
 * ‑ Convierte HTTP <=> Lógica de negocio (CocheService)
 *
 * NOTA TEMPORAL
 *    
 *    Este controller devuelve directamente Optional<Coche> en
 *    GET /{id}.   Es *correcto* pero **no óptimo**: el cliente recibirá
 *    200 OK + cuerpo null cuando el coche no exista.  Próximos pasos:
 *       1. Cambiar a ResponseEntity (200/404).
 *       2. Finalmente usar una excepción de dominio (CocheException).
 */
@RestController
@RequestMapping("/coches")        
public class CocheController {

    /**
     * INYECCIÓN DE DEPENDENCIAS
     *
     *  Se usa constructor‑injection (sin @Autowired) porque solo hay un constructor. 
     */
    private final CocheService cocheService;

    public CocheController(CocheService cocheService) {
        this.cocheService = cocheService;
    }

    // MÉTODOS CRUD EXPUESTOS COMO ENDPOINTS

    /**
     * GET /coches/{id}
     *
     * IMPORTANTE: al no envolverlo en ResponseEntity, Spring serializa
     * Optional.empty() como `null`, devolviendo 200 OK. 
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable long id) {
        try {
            Coche coche = cocheService.get(id);
            return ResponseEntity.ok(coche);
        } catch (CocheException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body(ex.getMessage());
        }
    }

    /**
     * GET /coches
     *
     * Lista de todos los coches.
     */
    @GetMapping
    public List<Coche> get() {
        return cocheService.get();
    }

    /**
     * POST /coches
     *
     * Crea un coche.  Devuelve la entidad recibida con el ID rellenado.
     * En una versión REST ideal devolveríamos 201 Created + Location.
     */
    @PostMapping
    public Coche create(@RequestBody Coche coche) {
        return cocheService.create(coche);          // El ID se asigna en service
    }

    /**
     * PUT /coches
     *
     * Actualiza un coche existente.  Próxima mejora: usar
     * PUT /coches/{id} para que sea coherente con REST.
     */
    @PutMapping
    public void update(@RequestBody Coche coche) {
        cocheService.update(coche);
    }

    /**
     * DELETE /coches/{id}
     *
     * Elimina el coche indicado.  De momento no devuelve cuerpo.
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        cocheService.delete(id);
    }
}
