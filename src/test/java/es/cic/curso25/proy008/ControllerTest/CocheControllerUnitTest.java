package es.cic.curso25.proy008.ControllerTest;

import static org.mockito.Mockito.*;                                
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*; 
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;    

import java.util.List;                                                 

import org.junit.jupiter.api.DisplayName;                              
import org.junit.jupiter.api.Test;                                     
import org.junit.jupiter.api.extension.ExtendWith;                    
import org.springframework.beans.factory.annotation.Autowired;        
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;                            
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;  
import org.springframework.test.web.servlet.MockMvc;                   
import com.fasterxml.jackson.databind.ObjectMapper;                    

import es.cic.curso25.proy008.controller.CocheController;
import es.cic.curso25.proy008.exception.CocheException;
import es.cic.curso25.proy008.exception.ModificationSecurityException;
import es.cic.curso25.proy008.model.Coche;                            
import es.cic.curso25.proy008.service.CocheService;                   

/**
 * Pruebas unitarias para CocheController.
 * - Usamos @WebMvcTest para levantar solo el contexto web.
 * - Mockeamos CocheService para aislar la capa de controlador.
 */
@ExtendWith({ SpringExtension.class })     // Integra Spring TestContext con JUnit 5
@WebMvcTest(CocheController.class)        // Solo carga CocheController y componentes web necesarios
@DisplayName("🔬 CocheControllerUnitTest")
class CocheControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;               // Simula peticiones HTTP a nuestro controlador

    @Autowired
    private ObjectMapper objectMapper;     // Convierte objetos Java a JSON y viceversa

    @MockitoBean
    private CocheService cocheService;     // Sustituye el bean real por un mock en el contexto web

    // ───────────────────────────────────────────────────────────────────────────
    // 1) GET /coches/{id} – caso éxito: devuelve 200 + JSON
    // ───────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("GET /coches/{id} devuelve 200 con JSON cuando existe")
    void getCoche_Existing_Returns200AndJson() throws Exception {
        // 1. Preparo un coche de ejemplo con id=1
        Coche ejemplo = new Coche("Tesla", 300);
        ejemplo.setId(1L);

        // 2. Configuro el mock del servicio: service.get(1) devuelve ese coche
        when(cocheService.get(1L)).thenReturn(ejemplo);

        // 3. Simulo petición GET /coches/1
        mockMvc.perform(get("/coches/{id}", 1L)
                .accept(MediaType.APPLICATION_JSON))         // espero JSON
            // 4. Compruebo status 200 OK
            .andExpect(status().isOk())
            // 5. Compruebo que el JSON devuelto tiene los campos correctos
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.marca").value("Tesla"))
            .andExpect(jsonPath("$.potencia").value(300.0))
            ;
        
        // 6. Verifico que se llamó exactamente una vez al servicio
        verify(cocheService, times(1)).get(1L);
    }

    // ───────────────────────────────────────────────────────────────────────────
    // 2) GET /coches/{id} – caso no existe: lanza CocheException → 404 + mensaje
    // ───────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("GET /coches/{id} devuelve 404 si no existe")
    void getCoche_NonExisting_Returns404() throws Exception {
        // 1. Configuro el mock: service.get(2) lanza CocheException
        doThrow(new CocheException(2L)).when(cocheService).get(2L);

        // 2. Simulo petición GET /coches/2
        mockMvc.perform(get("/coches/{id}", 2L))
            // 3. Compruebo status 404 Not Found
            .andExpect(status().isNotFound())
            // 4. Compruebo que el cuerpo contiene el mensaje de la excepción
            .andExpect(content().string("Coche con id 2 no encontrado."))
            ;
        
        // 5. Verifico interacción
        verify(cocheService, times(1)).get(2L);
    }

    // ───────────────────────────────────────────────────────────────────────────
    // 3) GET /coches – devuelve lista completa
    // ───────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("GET /coches devuelve 200 con lista de coches")
    void getAllCoches_Returns200AndJsonArray() throws Exception {
        // 1. Preparo lista de prueba
        Coche c1 = new Coche("A", 10); c1.setId(1L);
        Coche c2 = new Coche("B", 20); c2.setId(2L);
        List<Coche> lista = List.of(c1, c2);

        // 2. Mock del servicio: service.get() devuelve esa lista
        when(cocheService.get()).thenReturn(lista);

        // 3. Petición GET /coches
        mockMvc.perform(get("/coches")
                .accept(MediaType.APPLICATION_JSON))
            // 4. Debe responder 200 OK
            .andExpect(status().isOk())
            // 5. JSON debe ser un array de longitud 2
            .andExpect(jsonPath("$.length()").value(2))
            // 6. Controles puntuales de los elementos
            .andExpect(jsonPath("$[0].marca").value("A"))
            .andExpect(jsonPath("$[1].marca").value("B"))
            ;
        
        // 7. Verifico llamada al servicio
        verify(cocheService, times(1)).get();
    }

    // ───────────────────────────────────────────────────────────────────────────
    // 4) POST /coches – caso éxito: 200 + JSON con ID generado
    // ───────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("POST /coches crea coche y devuelve JSON con id")
    void postCoche_CreateSuccess_Returns200AndJson() throws Exception {
        // 1. Preparo el request: coche sin ID
        Coche input = new Coche("Seat", 50);
        String jsonIn = objectMapper.writeValueAsString(input);

        // 2. Mock del servicio: create(input) devuelve el coche con id=7
        Coche saved = new Coche("Seat", 50);
        saved.setId(7L);
        when(cocheService.create(any(Coche.class))).thenReturn(saved);

        // 3. Petición POST /coches con JSON en body
        mockMvc.perform(post("/coches")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonIn))
            // 4. Compruebo 200 OK
            .andExpect(status().isOk())
            // 5. JSON de respuesta contiene id=7
            .andExpect(jsonPath("$.id").value(7L))
            .andExpect(jsonPath("$.marca").value("Seat"))
            ;
        
        // 6. Verifico que el servicio.create() se llamó una vez
        verify(cocheService, times(1)).create(any(Coche.class));
    }

    // ───────────────────────────────────────────────────────────────────────────
    // 5) POST /coches – caso error: body trae ID → 400 BAD_REQUEST
    // ───────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("POST /coches devuelve 400 si el JSON trae id")
    void postCoche_CreateRejectWithId_Returns400() throws Exception {
        // 1. Preparo input con ID ilegal
        Coche input = new Coche("Fake", 10);
        input.setId(99L);
        String jsonIn = objectMapper.writeValueAsString(input);

        // 2. Mock: create() lanza ModificationSecurityException
        doThrow(new ModificationSecurityException(
                 "No se puede crear un coche con id existente: 99"))
            .when(cocheService).create(any(Coche.class));

        // 3. Petición POST
        mockMvc.perform(post("/coches")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonIn))
            // 4. Debe responder 400 Bad Request
            .andExpect(status().isBadRequest())
            // 5. El cuerpo contiene el mensaje de la excepción
            .andExpect(content().string(
                "No se puede crear un coche con id existente: 99"))
            ;
        
        // 6. Verifico llamada al servicio
        verify(cocheService, times(1)).create(any(Coche.class));
    }

    // ───────────────────────────────────────────────────────────────────────────
    // 6) PUT /coches – caso éxito: 200 + JSON actualizado
    // ───────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("PUT /coches actualiza y devuelve JSON actualizado")
    void putCoche_UpdateSuccess_Returns200AndJson() throws Exception {
        // 1. Preparo input con cambios
        Coche input = new Coche("VW", 100);
        input.setId(5L);
        String jsonIn = objectMapper.writeValueAsString(input);

        // 2. El servicio.update() no devuelve nada (void), solo debe no lanzar
        doNothing().when(cocheService).update(any(Coche.class));
        // 3. Después, controller hace service.get(5) para devolver el objeto
        Coche updated = new Coche("VW", 100);
        updated.setId(5L);
        when(cocheService.get(5L)).thenReturn(updated);

        // 4. Petición PUT /coches
        mockMvc.perform(put("/coches")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonIn))
            // 5. Debe ser 200 OK
            .andExpect(status().isOk())
            // 6. JSON devuelto coincide con el updated
            .andExpect(jsonPath("$.id").value(5L))
            .andExpect(jsonPath("$.marca").value("VW"))
            ;

        // 7. Verifico que se llamó a update() y a get()
        verify(cocheService, times(1)).update(any(Coche.class));
        verify(cocheService, times(1)).get(5L);
    }

    // ───────────────────────────────────────────────────────────────────────────
    // 7) PUT /coches – caso error: update lanza ModificationSecurityException → 400
    // ───────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("PUT /coches devuelve 400 si update() lanza 400")
    void putCoche_UpdateReject_Returns400() throws Exception {
        // 1. Preparo input inválido (sin id)
        Coche input = new Coche("X", 0);
        String jsonIn = objectMapper.writeValueAsString(input);

        // 2. Mock: update() lanza ModificationSecurityException
        doThrow(new ModificationSecurityException(
                 "Para actualizar es obligatorio enviar el id"))
            .when(cocheService).update(any(Coche.class));

        // 3. Petición PUT
        mockMvc.perform(put("/coches")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonIn))
            // 4. Debe responder 400 Bad Request
            .andExpect(status().isBadRequest())
            // 5. Mensaje de la excepción en el cuerpo
            .andExpect(content().string(
                "Para actualizar es obligatorio enviar el id"))
            ;
        
        // 6. Verifico llamada a update()
        verify(cocheService, times(1)).update(any(Coche.class));
    }

    // ───────────────────────────────────────────────────────────────────────────
    // 8) DELETE /coches/{id} – caso éxito: 200 OK
    // ───────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("DELETE /coches/{id} devuelve 200 cuando existe")
    void deleteCoche_Existing_Returns200() throws Exception {
        // 1. Mock: delete() no lanza excepción
        doNothing().when(cocheService).delete(3L);

        // 2. Petición DELETE /coches/3
        mockMvc.perform(delete("/coches/{id}", 3L))
            // 3. Debe devolver 200 OK
            .andExpect(status().isOk())
            // 4. Cuerpo vacío (void)
            .andExpect(content().string(""))
            ;
        
        // 5. Verifico llamada al servicio.delete(3)
        verify(cocheService, times(1)).delete(3L);
    }

    // ───────────────────────────────────────────────────────────────────────────
    // 9) DELETE /coches/{id} – caso no existe: 404 + mensaje
    // ───────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("DELETE /coches/{id} devuelve 404 si no existe")
    void deleteCoche_NonExisting_Returns404() throws Exception {
        // 1. Mock: delete(4) lanza CocheException
        doThrow(new CocheException(4L)).when(cocheService).delete(4L);

        // 2. Petición DELETE /coches/4
        mockMvc.perform(delete("/coches/{id}", 4L))
            // 3. Debe responder 404 Not Found
            .andExpect(status().isNotFound())
            // 4. Mensaje de error en cuerpo
            .andExpect(content().string("Coche con id 4 no encontrado."))
            ;
        
        // 5. Verifico interacción
        verify(cocheService, times(1)).delete(4L);
    }
}
