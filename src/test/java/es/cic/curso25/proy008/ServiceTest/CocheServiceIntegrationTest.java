package es.cic.curso25.proy008.ServiceTest;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import es.cic.curso25.proy008.controller.ModificationSecurityException;
import es.cic.curso25.proy008.exception.CocheException;
import es.cic.curso25.proy008.model.Coche;
import es.cic.curso25.proy008.repository.CocheRepository;
import es.cic.curso25.proy008.service.CocheService;

/**
 * ╔═══════════════════════════════════════════════════════════════════════════╗
 * ║ 🔬  C O C H E S E R V I C E   –   I N T E G R A T I O N   T E S T S       ║
 * ╠═══════════════════════════════════════════════════════════════════════════╣
 * ║ ✔  @SpringBootTest   ▸ arranca todo Spring (Service, Repository, H2…).    ║
 * ║ ✔  @Transactional    ▸ cada test corre en su transacción ⇒ rollback.     ║
 * ║ ✔  No se usa MockMvc: aquí probamos la capa de servicio directamente.     ║
 * ║                                                                           ║
 * ║    Pruebas de integración para CocheService.                              ║
 * ║    Verifica la correcta interacción entre el servicio y la base de datos. ║
 * ║    Usa una base de datos H2 en memoria y transacciones que se revierten   ║
 * ║     después de cada test.                                                 ║
 * ╚═══════════════════════════════════════════════════════════════════════════╝
 */
@SpringBootTest
@Transactional
class CocheServiceIntegrationTest {

    /*─────────────────────────────────────────────────────────────
     * I N Y E C C I Ó N   D E   B E A N S
     *───────────────────────────────────────────────────────────*/
    @Autowired
    private CocheService cocheService;      // SUT (System Under Test)

    @Autowired
    private CocheRepository cocheRepository; // Para preparar/verificar datos

    /*=======================================================================
     * 1)  C  R  E  A  T  E
     *=====================================================================*/

    /** 
     *  Comprueba un flujo de creación correcto
    */
    @Test
    @DisplayName("persiste un coche nuevo y asigna ID")
    void shouldCreateCoche() {
        // ── Preparación 
        Coche coche = new Coche("Tesla", 500); // id == null

        // ── Ejecución
        Coche res = cocheService.create(coche);

        // ── Verificación 
        assertNotNull(res.getId(), "El ID no debe ser null");
        assertTrue(cocheRepository.existsById(res.getId()),
                   "El coche debería existir en BD");
    }

    /**
     * Intento de crear con id debería ser rechazado (regla de negocio)
    */
    @Test
    @DisplayName("lanza ModificationSecurityException si viene con ID")
    void shouldRejectCreateWithId() {
        Coche coche = new Coche("Fake", 1);
        coche.setId(99L);                              // id trucado

        /**
         * assertThrows comprueba que se lanza la excepción esperada
         * y, de paso, deja fallar el test si no se produce                 
        */
        assertThrows(ModificationSecurityException.class,
                     () -> cocheService.create(coche));
    }

    /*=======================================================================
     * 2)  R  E  A  D
     *=====================================================================*/

    /**
     * get(id) devuelve el objeto cuando existe
    */
    @Test
    @DisplayName("devuelve el coche existente")
    void shouldGetExisting() {
        // Persistimos un coche para la prueba
        Coche guardado = cocheRepository.save(new Coche("Ford", 150));

        Coche res = cocheService.get(guardado.getId());

        // Atributos deben coincidir exactamente
        assertEquals("Ford", res.getMarca());
        assertEquals(150,     res.getPotencia());
        assertFalse(res.isEncendido(), "encendido debería ser false por defecto");
    }

    /**
     * get(id) para un id inexistente ⇒ 404 de dominio (CocheException)
    */
    @Test
    @DisplayName("lanza CocheException si el ID no existe")
    void shouldThrowWhenNotFound() {
        assertThrows(CocheException.class,
                     () -> cocheService.get(999L));
    }

    /**
     * get() sin parámetros ⇒ lista completa
    */
    @Test
    @DisplayName("get() sin args devuelve todos los coches")
    void shouldGetAll() {
        cocheRepository.save(new Coche("BMW",      200));
        cocheRepository.save(new Coche("Mercedes", 250));

        List<Coche> lista = cocheService.get();

        assertEquals(2, lista.size(), "Debe haber 2 coches");
    }

    /*=======================================================================
     * 3)  U  P  D  A  T  E
     *=====================================================================*/

    /**
     * Actualización correcta de un registro existente
    */
    @Test
    @DisplayName("modifica un coche existente")
    void shouldUpdateCoche() {
        Coche coche = cocheRepository.save(new Coche("Seat", 120));

        coche.setMarca("Volkswagen"); 
        coche.setPotencia(150);

        cocheService.update(coche);

        Coche actualizado = cocheRepository.findById(coche.getId()).orElseThrow();
        assertEquals("Volkswagen", actualizado.getMarca());
        assertEquals(150,          actualizado.getPotencia());
    }

    /**
     * update() sin id debe provocar 400 (ModificationSecurityException)
    */
    @Test
    @DisplayName("lanza 400 si el coche no lleva ID")
    void shouldRejectUpdateWithoutId() {
        Coche sinId = new Coche("N/A", 0);
        
        assertThrows(ModificationSecurityException.class,
                     () -> cocheService.update(sinId));
    }

    /**
     * update() sobre id inexistente ⇒ 404 (CocheException)             
    */
    @Test
    @DisplayName("lanza 404 si el coche no existe en BD")
    void shouldRejectUpdateNonExisting() {
        Coche fantasma = new Coche("Ghost", 1);
        fantasma.setId(1234L);

        assertThrows(CocheException.class,
                     () -> cocheService.update(fantasma));
    }

    /*=======================================================================
     * 4)  D  E  L  E  T  E
     *=====================================================================*/

    /**
     * delete() elimina correctamente cuando el recurso existe 
    */
    @Test
    @DisplayName("elimina un coche existente")
    void shouldDeleteCoche() {
        Coche coche = cocheRepository.save(new Coche("Opel", 100));
        Long id = coche.getId();

        cocheService.delete(id);

        assertFalse(cocheRepository.existsById(id),
                    "El coche debería haber sido eliminado");
    }

    /**
     * delete() con id inexistente ⇒ 404 (CocheException)
    */
    @Test
    @DisplayName("lanza 404 si el coche no existe")
    void shouldRejectDeleteNonExisting() {
        assertThrows(CocheException.class,
                     () -> cocheService.delete(888L));
    }
}