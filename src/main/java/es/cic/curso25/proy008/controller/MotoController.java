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

import es.cic.curso25.proy008.model.Moto;

import es.cic.curso25.proy008.service.MotoService;

@RestController // Le decimos a Spring que es una clase de controlador
@RequestMapping("/motos") // Prefijo para todas las rutas de este controlador
public class MotoController {

    // // Nos creamos un usuario Service con Autowired
    // @Autowired // Buscame en @BEAN uno de los uno de los campos definidos y creame una
    //            // instancia
    private final MotoService motoService;

    public MotoController(MotoService motoService) {
        this.motoService = motoService;
    }


    /**───────────────────────────────────────────────────────────────
     * CREATE (Post)
     * Crea una entidad de Moto.
     * 
     * @param Moto
     * @return Entidad moto creada + código 201 (Created + Location)
     * ───────────────────────────────────────────────────────────────*/
    @PostMapping
    public Moto crearMoto(@RequestBody Moto moto) {
        return motoService.create(moto);
    }

    /**───────────────────────────────────────────────────────────────
     * READ (Get)
     * Devuelve la entidad de moto que coincida con el ID proporcionado
     * 
     * @param id
     * @return entidad Moto, o un error en caso de que no exista ninguna 
     *         moto con ese id
     * ───────────────────────────────────────────────────────────────*/
    @GetMapping("/{id}")
     public Moto get(@PathVariable long id) {
        return motoService.get(id);
    }

    /**───────────────────────────────────────────────────────────────
     * READ (Get)
     * Obtiene una lista de motos
     * 
     * @return lista de todas las motos
     * ───────────────────────────────────────────────────────────────*/
    @GetMapping
    public List<Moto> get() {
        return motoService.get();
    }

    /**───────────────────────────────────────────────────────────────
     * UPDATE (Put)
     * Metodo para actualizar Entidad moto
     * 
     * @param moto
     * @param id
     * ───────────────────────────────────────────────────────────────*/
    @PutMapping
    public void update(@RequestBody Moto moto) {
        motoService.update(moto);
    }

    /**───────────────────────────────────────────────────────────────
     * DELETE (Delete)
     * Metodo para borrar motos en base a un id
     * 
     * @param id
     * ───────────────────────────────────────────────────────────────*/
    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        motoService.delete(id);
    }

    /**───────────────────────────────────────────────────────────────
     * DELETE (Delete)
     * Borra todas las entidades de Moto Existentes
     * ───────────────────────────────────────────────────────────────*/
    @DeleteMapping()
    public void deleteAll() {
        motoService.deleteAll();
        ;
    }

}
