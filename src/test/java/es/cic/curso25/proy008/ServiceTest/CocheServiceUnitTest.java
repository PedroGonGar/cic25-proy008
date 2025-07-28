package es.cic.curso25.proy008.ServiceTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.cic.curso25.proy008.exception.CocheException;
import es.cic.curso25.proy008.exception.ModificationSecurityException;
import es.cic.curso25.proy008.model.Coche;
import es.cic.curso25.proy008.repository.CocheRepository;
import es.cic.curso25.proy008.service.CocheService;

/**
 * Pruebas unitarias para {@link CocheService}.
 * <p>
 * Utiliza Mockito para mockear el repositorio y aislar la lógica de negocio.
 * </p>
 * 
 * @author Pedro González
 * @version 1.0
 * @since 1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CocheServiceUnitTest")
public class CocheServiceUnitTest {

    @Mock
    private CocheRepository cocheRepository;

    @InjectMocks
    private CocheService cocheService;

    /**
     * Verifica que {@code create(coche)} persiste correctamente cuando
     * el coche no tiene ID.
     */
    @Test
    @DisplayName("create() éxito sin ID asignado")
    void testCreateSuccess() {
        // Preparación: Coche sin ID
        Coche input = new Coche();
        input.setMarca("Seat");
        input.setPotencia(50);

        // El repositorio mock devuelve un coche con ID=5
        Coche persisted = new Coche();
        persisted.setMarca("Seat");
        persisted.setPotencia(50);
        persisted.setId(5L);

        when(cocheRepository.save(input)).thenReturn(persisted);

        // Ejecución
        Coche result = cocheService.create(input);

        // Verificación
        assertEquals(5L, result.getId(), "Debe devolver el ID asignado por el repositorio");
        verify(cocheRepository, times(1)).save(input);
    }

    /**
     * Verifica que {@code create(coche)} lance {@link ModificationSecurityException}
     * si el coche ya trae un ID (regla de negocio).
     */
    @Test
    @DisplayName("create() rechaza cuando el coche ya tiene ID")
    void testCreateRejectWithId() {
        Coche input = new Coche();
        input.setMarca("Fake");
        input.setPotencia(10);
        input.setId(99L);

        assertThrows(ModificationSecurityException.class,
            () -> cocheService.create(input),
            "Debe lanzar ModificationSecurityException cuando el coche trae ID");
        verifyNoInteractions(cocheRepository);
    }

    /**
     * Verifica que {@code get(id)} retorne el coche existente.
     */
    @Test
    @DisplayName("get(id) devuelve coche cuando existe")
    void testGetExisting() {
        Coche existing = new Coche();
        existing.setId(1L);
        existing.setMarca("Fiat");
        existing.setPotencia(80);

        when(cocheRepository.findById(1L)).thenReturn(Optional.of(existing));

        Coche result = cocheService.get(1L);

        assertEquals("Fiat", result.getMarca(), "La marca debe coincidir");
        verify(cocheRepository, times(1)).findById(1L);
    }

    /**
     * Verifica que {@code get(id)} lance {@link CocheException} si no existe.
     */
    @Test
    @DisplayName("get(id) lanza excepción cuando no existe")
    void testGetNonExisting() {
        when(cocheRepository.findById(42L)).thenReturn(Optional.empty());

        assertThrows(CocheException.class,
            () -> cocheService.get(42L),
            "Debe lanzar CocheException para ID inexistente");
    }

    /**
     * Verifica que {@code get()} devuelva la lista completa de coches.
     */
    @Test
    @DisplayName("get() devuelve todos los coches")
    void testGetAll() {
        Coche c1 = new Coche();
        c1.setMarca("A"); c1.setPotencia(10);
        Coche c2 = new Coche();
        c2.setMarca("B"); c2.setPotencia(20);
        List<Coche> mockList = List.of(c1, c2);

        when(cocheRepository.findAll()).thenReturn(mockList);

        List<Coche> result = cocheService.get();

        assertEquals(2, result.size(), "Debe devolver dos coches");
        assertEquals("A", result.get(0).getMarca(), "El primer coche debe ser 'A'");
        verify(cocheRepository, times(1)).findAll();
    }

    /**
     * Verifica que {@code update(coche)} persista los cambios cuando el coche existe.
     */
    @Test
    @DisplayName("update() éxito cuando existe ID")
    void testUpdateSuccess() {
        Coche input = new Coche();
        input.setId(8L);
        input.setMarca("Ford");
        input.setPotencia(80);

        when(cocheRepository.existsById(8L)).thenReturn(true);

        cocheService.update(input);

        verify(cocheRepository, times(1)).existsById(8L);
        verify(cocheRepository, times(1)).save(input);
    }

    /**
     * Verifica que {@code update(coche)} lance {@link ModificationSecurityException}
     * si el coche no trae ID.
     */
    @Test
    @DisplayName("update() rechaza sin ID")
    void testUpdateRejectWithoutId() {
        Coche input = new Coche();
        input.setMarca("X");
        input.setPotencia(0);

        assertThrows(ModificationSecurityException.class,
            () -> cocheService.update(input),
            "Debe lanzar ModificationSecurityException si no hay ID");
        verifyNoInteractions(cocheRepository);
    }

    /**
     * Verifica que {@code update(coche)} lance {@link CocheException}
     * cuando el ID no existe en la base de datos.
     */
    @Test
    @DisplayName("update() lanza excepción si ID no existe")
    void testUpdateRejectNonExisting() {
        Coche input = new Coche();
        input.setId(99L);
        input.setMarca("Ghost");
        input.setPotencia(1);

        when(cocheRepository.existsById(99L)).thenReturn(false);

        assertThrows(CocheException.class,
            () -> cocheService.update(input),
            "Debe lanzar CocheException si no existe el ID");
        verify(cocheRepository, times(1)).existsById(99L);
        verify(cocheRepository, never()).save(any());
    }

    /**
     * Verifica que {@code delete(id)} elimine correctamente cuando el coche existe.
     */
    @Test
    @DisplayName("delete(id) elimina coche existente")
    void testDeleteSuccess() {
        long id = 3L;
        when(cocheRepository.existsById(id)).thenReturn(true);

        cocheService.delete(id);

        verify(cocheRepository, times(1)).existsById(id);
        verify(cocheRepository, times(1)).deleteById(id);
    }

    /**
     * Verifica que {@code delete(id)} lance {@link CocheException}
     * cuando el coche no existe.
     */
    @Test
    @DisplayName("delete(id) lanza excepción si no existe")
    void testDeleteRejectNonExisting() {
        long id = 7L;
        when(cocheRepository.existsById(id)).thenReturn(false);

        assertThrows(CocheException.class,
            () -> cocheService.delete(id),
            "Debe lanzar CocheException para ID inexistente");
        verify(cocheRepository, times(1)).existsById(id);
        verify(cocheRepository, never()).deleteById(id);
    }
}
