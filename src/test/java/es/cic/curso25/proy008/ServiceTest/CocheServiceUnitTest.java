package es.cic.curso25.proy008.ServiceTest;

// ————————————————————————————————————————————————————————————————————————
// Importaciones de aserciones de JUnit 5
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

// ————————————————————————————————————————————————————————————————————————
// Importaciones de Mockito para configurar mocks y verificarlos
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

// Extensiones de JUnit y Mockito
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

// Excepciones y clases del proyecto
import es.cic.curso25.proy008.controller.ModificationSecurityException;
import es.cic.curso25.proy008.exception.CocheException;
import es.cic.curso25.proy008.model.Coche;
import es.cic.curso25.proy008.repository.CocheRepository;
import es.cic.curso25.proy008.service.CocheService;

/**
 * Anotamos con @ExtendWith para que JUnit use el motor de Mockito:
 *  - Inicializa los mocks (@Mock)
 *  - Inyecta los mocks en la clase bajo prueba (@InjectMocks)
 */
@ExtendWith(MockitoExtension.class)
public class CocheServiceUnitTest {

    // Creamos un mock de CocheRepository para simular la capa de datos
    @Mock
    private CocheRepository cocheRepository;

    // Inyectamos el mock anterior en una instancia real de CocheService
    @InjectMocks
    private CocheService cocheService;

    // ———————————————————————————————————————————————————————————————————————
    // TEST: flujo exitoso de create()
    // ———————————————————————————————————————————————————————————————————————
    @Test
    void testCreateSuccess() {
        // 1) Preparamos un objeto sin ID (flujo normal de creación)
        Coche coche = new Coche("Seat", 50);

        // 2) Configuramos el mock: cuando se llame a save(coche), devolverá un coche con ID=5
        Coche cocheGuardado = new Coche("Seat", 50);
        cocheGuardado.setId(5L);
        when(cocheRepository.save(coche))
            .thenReturn(cocheGuardado);

        // 3) Llamamos al método que queremos probar
        Coche res = cocheService.create(coche);

        // 4) Comprobamos que el servicio devolvió el objeto con el ID esperado
        assertEquals(5L, res.getId());

        // 5) Verificamos que se llamó exactamente una vez a repository.save(coche)
        verify(cocheRepository, times(1)).save(coche);
    }

    // ———————————————————————————————————————————————————————————————————————
    // TEST: create() rechaza cuando el objeto ya trae ID
    // ———————————————————————————————————————————————————————————————————————
    @Test
    void testCreateRejectWithId() {
        // 1) Preparamos un coche 'fake' que ya tiene ID
        Coche coche = new Coche("Fake", 10);
        coche.setId(99L);

        // 2) Debe lanzar ModificationSecurityException porque no se permite crear con ID
        assertThrows(ModificationSecurityException.class,
            () -> cocheService.create(coche));
        // No hay verify porque la excepción ocurre antes de tocar el repositorio
    }

    // ———————————————————————————————————————————————————————————————————————
    // TEST: get(id) cuando el ID existe
    // ———————————————————————————————————————————————————————————————————————
    @Test
    void testGetExisting() {
        // 1) Preparamos un coche con ID=1
        Coche coche = new Coche("Fiat", 80);
        coche.setId(1L);

        // 2) Configuramos el mock: findById(1L) devuelve Optional.of(coche)
        when(cocheRepository.findById(1L))
            .thenReturn(Optional.of(coche));

        // 3) Llamamos al servicio
        Coche resultado = cocheService.get(1L);

        // 4) Aserción: la marca coincide con la esperada
        assertEquals("Fiat", resultado.getMarca());

        // 5) Verificamos que se invocó findById(1L) exactamente una vez
        verify(cocheRepository, times(1)).findById(1L);
    }

    // ———————————————————————————————————————————————————————————————————————
    // TEST: get(id) cuando el ID NO existe → 404 de dominio (CocheException)
    // ———————————————————————————————————————————————————————————————————————
    @Test
    void testGetNonExisting() {
        // 1) Configuramos que findById(42L) devuelva Optional.empty()
        when(cocheRepository.findById(42L))
            .thenReturn(Optional.empty());

        // 2) Debe lanzar CocheException al no encontrar el coche
        assertThrows(CocheException.class,
            () -> cocheService.get(42L));
        // No es necesario verify explícito sobre findById
    }

    // ———————————————————————————————————————————————————————————————————————
    // TEST: get() sin parámetros → devuelve lista completa
    // ———————————————————————————————————————————————————————————————————————
    @Test
    void testGetAll() {
        // 1) Preparamos una lista de prueba
        List<Coche> lista = List.of(
            new Coche("A", 10),
            new Coche("B", 20)
        );

        // 2) Configuramos el mock: findAll() devuelve nuestra lista
        when(cocheRepository.findAll())
            .thenReturn(lista);

        // 3) Llamamos al servicio get()
        List<Coche> res = cocheService.get();

        // 4) Aserciones: tamaño y primer elemento
        assertEquals(2, res.size());
        assertEquals("A", res.get(0).getMarca());

        // 5) Verificamos que findAll() se llamó exactamente una vez
        verify(cocheRepository, times(1)).findAll();
    }

    // ———————————————————————————————————————————————————————————————————————
    // TEST: update(coche) cuando todo es válido
    // ———————————————————————————————————————————————————————————————————————
    @Test
    void testUpdateSuccess() {
        // 1) Preparamos coche con ID=8
        Coche coche = new Coche("Ford", 80);
        coche.setId(8L);

        // 2) Simulamos que existsById(8L) devuelve true → pasa la validación
        when(cocheRepository.existsById(8L))
            .thenReturn(true);

        // 3) Llamamos al servicio
        cocheService.update(coche);

        // 4) Verificaciones:
        //    - Se comprobó existencia
        verify(cocheRepository, times(1)).existsById(8L);
        //    - Se guardó el coche (save)
        verify(cocheRepository, times(1)).save(coche);
    }

    // ———————————————————————————————————————————————————————————————————————
    // TEST: update(coche) sin ID → 400 Bad Request (ModificationSecurityException)
    // ———————————————————————————————————————————————————————————————————————
    @Test
    void testUpdateRejectWithoutId() {
        // 1) Preparamos coche sin ID
        Coche coche = new Coche("X", 0);

        // 2) Debe lanzar ModificationSecurityException
        assertThrows(ModificationSecurityException.class,
            () -> cocheService.update(coche));

        // 3) Verificamos que NO hubo interacción con el repositorio
        verifyNoInteractions(cocheRepository);
    }

    // ———————————————————————————————————————————————————————————————————————
    // TEST: update(coche) con ID inexistente → 404 Not Found (CocheException)
    // ———————————————————————————————————————————————————————————————————————
    @Test
    void testUpdateRejectNonExisting() {
        // 1) Preparamos coche con ID=99
        Coche coche = new Coche("Ghost", 1);
        coche.setId(99L);

        // 2) Simulamos que existsById(99L) devuelve false
        when(cocheRepository.existsById(99L))
            .thenReturn(false);

        // 3) Debe lanzar CocheException
        assertThrows(CocheException.class,
            () -> cocheService.update(coche));

        // 4) Verificaciones:
        //    - Se comprobó existsById
        verify(cocheRepository, times(1)).existsById(99L);
        //    - Nunca se llamó a save() porque falló antes
        verify(cocheRepository, never()).save(any());
    }

    // ———————————————————————————————————————————————————————————————————————
    // TEST: delete(id) cuando existe → elimina correctamente
    // ———————————————————————————————————————————————————————————————————————
    @Test
    void testDeleteSuccess() {
        long id = 3L;

        // 1) Simulamos que existsById(3L) devuelve true
        when(cocheRepository.existsById(id))
            .thenReturn(true);

        // 2) Llamamos al servicio
        cocheService.delete(id);

        // 3) Verificaciones:
        //    - Se comprobó existencia
        verify(cocheRepository, times(1)).existsById(id);
        //    - Se llamó a deleteById(3L)
        verify(cocheRepository, times(1)).deleteById(id);
    }

    // ———————————————————————————————————————————————————————————————————————
    // TEST: delete(id) cuando NO existe → 404 Not Found (CocheException)
    // ———————————————————————————————————————————————————————————————————————
    @Test
    void testDeleteRejectNonExisting() {
        long id = 7L;

        // 1) Simulamos que existsById(7L) devuelve false
        when(cocheRepository.existsById(id))
            .thenReturn(false);

        // 2) Debe lanzar CocheException
        assertThrows(CocheException.class,
            () -> cocheService.delete(id));

        // 3) Verificaciones:
        //    - Se comprobó existsById
        verify(cocheRepository, times(1)).existsById(id);
        //    - Nunca se llamó a deleteById() porque falló antes
        verify(cocheRepository, never()).deleteById(id);
    }
}
