package es.cic.curso25.proy008.ServiceTest;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import es.cic.curso25.proy008.exception.CocheException;
import es.cic.curso25.proy008.exception.ModificationSecurityException;
import es.cic.curso25.proy008.model.Coche;
import es.cic.curso25.proy008.model.Concesionario;
import es.cic.curso25.proy008.repository.CocheRepository;
import es.cic.curso25.proy008.repository.ConcesionarioRepository;
import es.cic.curso25.proy008.service.CocheService;

/**
 * Integración de pruebas para {@link CocheService}.
 * <p>
 * Utiliza {@code @SpringBootTest} para arrancar el contexto completo,
 * incluyendo JPA, H2 en memoria y transacciones. Cada test se ejecuta
 * dentro de su propia transacción, que se revierte al finalizar,
 * gracias a {@code @Transactional}.
 * </p>
 * 
 * @author Pedro González
 * @version 1.0
 * @since 1.0
 */
@SpringBootTest
@Transactional
class CocheServiceIntegrationTest {

    @Autowired
    private CocheService cocheService;

    @Autowired
    private CocheRepository cocheRepository;

    @Autowired
    private ConcesionarioRepository concesionarioRepository;

    /**
     * Verifica que crear un coche sin ID persiste la entidad
     * y le asigna un identificador.
     */
    @Test
    @DisplayName("Persiste un coche nuevo y asigna ID")
    void shouldCreateCoche() {
        // Dado un concesionario en BD
        Concesionario cons = new Concesionario("ConsTest", 600123456, "Madrid",
                LocalTime.of(9,0), LocalTime.of(18,0));
        cons = concesionarioRepository.save(cons);

        // Cuando creo un coche con ese concesionario
        Coche coche = new Coche("Tesla", 500, cons);
        Coche resultado = cocheService.create(coche);

        // Entonces debe tener ID y existir en BD
        assertNotNull(resultado.getId(), "El ID no debe ser null");
        assertTrue(cocheRepository.existsById(resultado.getId()),
                   "El coche debería existir en BD");
    }

    /**
     * Verifica que intentar crear un coche con ID preexistente
     * lanza {@link ModificationSecurityException}.
     */
    @Test
    @DisplayName("Rechaza create() cuando el objeto trae ID")
    void shouldRejectCreateWithId() {
        Concesionario cons = new Concesionario("ConsTest2", 600654321, "Valencia",
                LocalTime.of(8,0), LocalTime.of(17,0));
        cons = concesionarioRepository.save(cons);

        Coche coche = new Coche("Fake", 1, cons);
        coche.setId(99L);

        assertThrows(ModificationSecurityException.class,
                     () -> cocheService.create(coche),
                     "Debe lanzar ModificationSecurityException");
    }

    /**
     * Verifica que get(id) retorna el coche cuando existe.
     */
    @Test
    @DisplayName("get(id) devuelve el coche existente")
    void shouldGetExisting() {
        Concesionario cons = new Concesionario("ConsTest3", 600111222, "Sevilla",
                LocalTime.of(7,30), LocalTime.of(16,30));
        cons = concesionarioRepository.save(cons);

        Coche guardado = cocheRepository.save(new Coche("Ford", 150, cons));

        Coche encontrado = cocheService.get(guardado.getId());

        assertEquals("Ford", encontrado.getMarca(), "Marca debe coincidir");
        assertEquals(150, encontrado.getPotencia(), "Potencia debe coincidir");
        assertFalse(encontrado.isEncendido(), "encendido debe ser false por defecto");
    }

    /**
     * Verifica que get(id) lanza {@link CocheException}
     * cuando el ID no existe.
     */
    @Test
    @DisplayName("get(id) lanza CocheException si no existe")
    void shouldThrowWhenNotFound() {
        assertThrows(CocheException.class,
                     () -> cocheService.get(999L),
                     "Debe lanzar CocheException para ID inexistente");
    }

    /**
     * Verifica que get() devuelve la lista completa de coches.
     */
    @Test
    @DisplayName("get() devuelve todos los coches")
    void shouldGetAll() {
        Concesionario cons = new Concesionario("ConsTest4", 600333444, "Bilbao",
                LocalTime.of(10,0), LocalTime.of(19,0));
        cons = concesionarioRepository.save(cons);

        cocheRepository.save(new Coche("BMW", 200, cons));
        cocheRepository.save(new Coche("Mercedes", 250, cons));

        List<Coche> lista = cocheService.get();

        assertEquals(2, lista.size(), "Debe haber 2 coches en la lista");
    }

    /**
     * Verifica que update(coche) modifica correctamente
     * un coche existente.
     */
    @Test
    @DisplayName("update(coche) modifica un coche existente")
    void shouldUpdateCoche() {
        Concesionario cons = new Concesionario("ConsTest5", 600777888, "Granada",
                LocalTime.of(8,30), LocalTime.of(17,30));
        cons = concesionarioRepository.save(cons);

        Coche coche = cocheRepository.save(new Coche("Seat", 120, cons));
        coche.setMarca("Volkswagen");
        coche.setPotencia(150);

        cocheService.update(coche);

        Coche actualizado = cocheRepository.findById(coche.getId()).orElseThrow();
        assertEquals("Volkswagen", actualizado.getMarca(), "Marca debe haber cambiado");
        assertEquals(150, actualizado.getPotencia(), "Potencia debe haber cambiado");
    }

    /**
     * Verifica que update(coche) sin ID lanza
     * {@link ModificationSecurityException}.
     */
    @Test
    @DisplayName("update() lanza 400 si no se envía ID")
    void shouldRejectUpdateWithoutId() {
        Concesionario cons = new Concesionario("ConsTest6", 600999000, "Zaragoza",
                LocalTime.of(9,15), LocalTime.of(18,15));
        cons = concesionarioRepository.save(cons);

        Coche sinId = new Coche("N/A", 0, cons);

        assertThrows(ModificationSecurityException.class,
                     () -> cocheService.update(sinId),
                     "Debe lanzar ModificationSecurityException cuando id es null");
    }

    /**
     * Verifica que update(coche) sobre un ID inexistente
     * lanza {@link CocheException}.
     */
    @Test
    @DisplayName("update() lanza 404 si el coche no existe")
    void shouldRejectUpdateNonExisting() {
        Concesionario cons = new Concesionario("ConsTest7", 601111222, "Bilbao",
                LocalTime.of(7,45), LocalTime.of(16,45));
        cons = concesionarioRepository.save(cons);

        Coche fantasma = new Coche("Ghost", 1, cons);
        fantasma.setId(1234L);

        assertThrows(CocheException.class,
                     () -> cocheService.update(fantasma),
                     "Debe lanzar CocheException para ID inexistente");
    }

    /**
     * Verifica que delete(id) elimina un coche existente.
     */
    @Test
    @DisplayName("delete(id) elimina un coche existente")
    void shouldDeleteCoche() {
        Concesionario cons = new Concesionario("ConsTest8", 602222333, "Alicante",
                LocalTime.of(8,0), LocalTime.of(17,0));
        cons = concesionarioRepository.save(cons);

        Coche coche = cocheRepository.save(new Coche("Opel", 100, cons));
        Long id = coche.getId();

        cocheService.delete(id);

        assertFalse(cocheRepository.existsById(id),
                    "El coche debería haber sido eliminado");
    }

    /**
     * Verifica que delete(id) sobre ID inexistente lanza
     * {@link CocheException}.
     */
    @Test
    @DisplayName("delete(id) lanza 404 si el coche no existe")
    void shouldRejectDeleteNonExisting() {
        assertThrows(CocheException.class,
                     () -> cocheService.delete(888L),
                     "Debe lanzar CocheException para ID inexistente");
    }
}
