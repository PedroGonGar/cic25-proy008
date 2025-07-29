package es.cic.curso25.proy008.ServiceTest;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import es.cic.curso25.proy008.exception.ConcesionarioException;
import es.cic.curso25.proy008.model.Concesionario;
import es.cic.curso25.proy008.repository.ConcesionarioRepository;
import es.cic.curso25.proy008.service.ConcesionarioService;

/**
 * Pruebas de integración para {@link ConcesionarioService}.
 * <p>
 * Se arranca todo el contexto de Spring Boot con JPA y H2 en memoria.
 * Cada test se ejecuta dentro de una transacción que se revierte al finalizar
 * gracias a {@code @Transactional}.
 * </p>
 * 
 * @author Pedro González
 * @version 1.0
 * @since 1.0
 */
@SpringBootTest
@Transactional
@DisplayName("ConcesionarioServiceIntegrationTest")
public class ConcesionarioServiceIntegrationTest {

    @Autowired
    private ConcesionarioService concesionarioService;

    @Autowired
    private ConcesionarioRepository concesionarioRepository;

    /**
     * Verifica que {@code create()} persista un nuevo concesionario
     * y le asigne un ID.
     */
    @Test
    @DisplayName("Persiste un concesionario nuevo y asigna ID")
    void shouldCreateConcesionario() {
        Concesionario input = new Concesionario(
            "IntegrationTest", 987654321, "TestCity",
            LocalTime.of(9, 0), LocalTime.of(18, 0)
        );

        Concesionario result = concesionarioService.create(input);

        assertNotNull(result.getId(), "El ID no debe ser null");
        assertTrue(concesionarioRepository.existsById(result.getId()),
                   "El concesionario debe existir en la base de datos");
    }

    /**
     * Verifica que {@code get(id)} retorne el concesionario cuando existe.
     */
    @Test
    @DisplayName("get(id) devuelve concesionario existente")
    void shouldGetExisting() {
        Concesionario saved = concesionarioRepository.save(
            new Concesionario("Existing", 123123123, "ExistCity",
                              LocalTime.of(8, 30), LocalTime.of(17, 30))
        );

        Concesionario found = concesionarioService.get(saved.getId());

        assertEquals("Existing", found.getNombre(), "El nombre debe coincidir");
        assertEquals("ExistCity", found.getCiudad(), "La ciudad debe coincidir");
    }

    /**
     * Verifica que {@code get(id)} lance {@link ConcesionarioException}
     * si no existe un concesionario con el ID proporcionado.
     */
    @Test
    @DisplayName("get(id) lanza excepción cuando no existe")
    void shouldThrowWhenNotFound() {
        long nonexistentId = 999L;
        assertThrows(ConcesionarioException.class,
            () -> concesionarioService.get(nonexistentId),
            "Debe lanzar ConcesionarioException para ID inexistente");
    }

    /**
     * Verifica que {@code get()} devuelva la lista completa de concesionarios.
     */
    @Test
    @DisplayName("get() devuelve todos los concesionarios")
    void shouldGetAll() {
        concesionarioRepository.deleteAll();
        concesionarioRepository.save(new Concesionario("A", 1, "Alpha",
            LocalTime.of(7, 0), LocalTime.of(16, 0)
        ));
        concesionarioRepository.save(new Concesionario("B", 2, "Beta",
            LocalTime.of(8, 0), LocalTime.of(17, 0)
        ));

        List<Concesionario> list = concesionarioService.get();
        assertEquals(2, list.size(), "Deben existir dos concesionarios");
    }

    /**
     * Verifica que {@code update()} modifique un concesionario existente.
     */
    @Test
    @DisplayName("update() modifica un concesionario existente")
    void shouldUpdateConcesionario() {
        Concesionario existing = concesionarioRepository.save(
            new Concesionario("OldName", 111111111, "OldCity",
                              LocalTime.of(6, 0), LocalTime.of(15, 0))
        );
        existing.setCiudad("NewCity");

        Concesionario updated = concesionarioService.update(existing);

        Concesionario fromDb = concesionarioRepository.findById(existing.getId())
            .orElseThrow();
        assertEquals("NewCity", fromDb.getCiudad(), "La ciudad debe haberse actualizado");
        assertEquals(existing.getId(), updated.getId(), "El ID no debe cambiar");
    }

    /**
     * Verifica que {@code delete(id)} elimine correctamente un concesionario existente.
     */
    @Test
    @DisplayName("delete(id) elimina concesionario existente")
    void shouldDeleteConcesionario() {
        Concesionario toDelete = concesionarioRepository.save(
            new Concesionario("ToDelete", 222222222, "DeleteCity",
                              LocalTime.of(7, 15), LocalTime.of(16, 15))
        );
        Long id = toDelete.getId();

        concesionarioService.delete(id);

        assertFalse(concesionarioRepository.existsById(id),
                    "El concesionario debería haber sido eliminado");
    }
}
