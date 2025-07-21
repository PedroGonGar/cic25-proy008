package es.cic.curso25.proy008.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import es.cic.curso25.proy008.model.Coche;
import es.cic.curso25.proy008.service.CocheService;

/**
 * ╔═══════════════════════════════════════════════════════════════════════════╗
 * ║                           🌐  C O C H E  C O N T R O L L E R              ║
 * ╠═══════════════════════════════════════════════════════════════════════════╣
 * ║ • Expone la API REST bajo el recurso /coches.                             ║
 * ║ • Convierte peticiones HTTP ⇆ llamadas a {@link CocheService}.            ║
 * ║ • Devuelve objetos Java; Spring los serializa a JSON (Jackson).           ║
 * ║                                                                           ║
 * ║ GESTIÓN DE ERRORES                                                        ║
 * ║ ────────────────────────────────────────────────────────────────────────  ║
 * ║ · No hay try‑catch aquí:                                                  ║
 * ║     – Si el coche no existe, el Service lanza CocheException.             ║
 * ║     – {@link es.cic.curso25.proy008.exception.ControllerAdviceException}  ║
 * ║       la traduce en **HTTP 404 + mensaje**.                               ║
 * ║ · Si se intenta crear con id≠null, el Service lanza                       ║
 *     ModificationSecurityException → el mismo Advice responde **400**.       ║
 * ╚═══════════════════════════════════════════════════════════════════════════╝*/

@RestController
@RequestMapping("/coches")
public class CocheController {

    /**
     * ───────────────────────────────────────────────────────────────
     * DEPENDENCY INJECTION
     * – Constructor‑injection (solo un constructor ⇒ @Autowired implícito).
     * ──────────────────────────────────────────────────────────────*/
    private final CocheService cocheService;

    public CocheController(CocheService cocheService) {
        this.cocheService = cocheService;
    }

    /**
     * ───────────────────────────────────────────────────────────────
     * GET /coches/{id}
     * – OK ..............→ 200 + JSON del coche
     * – No existe .......→ 404 + (gestionado por el Advice)
     * ──────────────────────────────────────────────────────────────*/
    @GetMapping("/{id}")
    public Coche get(@PathVariable long id) {

        /**
         * Si el coche no existe, CocheService lanza CocheException
         * y el Advice devuelve automáticamente 404 + mensaje. 
        */ 
        return cocheService.get(id); // Puede lanzar CocheException
    }

    /**
     * ───────────────────────────────────────────────────────────────
     * GET /coches
     * – Devuelve la lista completa (puede estar vacía)........ 200 OK
     * ──────────────────────────────────────────────────────────────*/
    @GetMapping
    public List<Coche> get() {
        return cocheService.get();
    }

    /**
     * ───────────────────────────────────────────────────────────────
     * POST /coches
     * – Crea un coche (id generado por JPA).................. 200 OK
     * – Si el JSON trae id ⇒ el Service lanza 400 BAD_REQUEST
     * ModificationSecurityException (interceptado por Advice).
     * ──────────────────────────────────────────────────────────────*/
    @PostMapping
    public Coche create(@RequestBody Coche coche) {
        return cocheService.create(coche); // El ID se asigna en service
    }

    /**
     * ───────────────────────────────────────────────────────────────
     * PUT /coches
     * – Actualiza un coche existente........................ 200 OK
     * ──────────────────────────────────────────────────────────────*/
    @PutMapping
    public void update(@RequestBody Coche coche) {
        cocheService.update(coche);
    }

    /**
     * ───────────────────────────────────────────────────────────────
     * DELETE /coches/{id}
     * – Borra el registro................................... 200 OK
     * ──────────────────────────────────────────────────────────────*/
    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        cocheService.delete(id);
    }
}
