package es.cic.curso25.proy008.ControllerTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
 * Pruebas unitarias para {@link CocheController}.
 * <p>
 * Usa {@link WebMvcTest} para cargar sólo el controlador y el contexto web.
 * Se mockea {@link CocheService} con {@link MockBean} para aislar la capa de controlador.
 * </p>
 * 
 * @author Pedro González
 * @version 1.0
 * @since 1.0
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(CocheController.class)
@DisplayName("CocheController Unit Tests")
class CocheControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Mock de la capa de negocio inyectado en el contexto de Spring.
     * El {@link CocheController} recibirá este mock en lugar del bean real.
     */
    @MockitoBean
    private CocheService cocheService;

    /**
     * GET /coches/{id} – caso éxito.
     * <p>
     * Si el servicio devuelve un {@link Coche}, el controlador debe responder
     * con HTTP 200 OK y el JSON correspondiente.
     * </p>
     *
     * @throws Exception Si la petición HTTP falla.
     */
    @Test
    @DisplayName("GET /coches/{id} devuelve 200 con JSON cuando existe")
    void getCoche_Existing_Returns200AndJson() throws Exception {
        Coche ejemplo = new Coche();
        ejemplo.setId(1L);
        ejemplo.setMarca("Tesla");
        ejemplo.setPotencia(300);

        when(cocheService.get(1L)).thenReturn(ejemplo);

        mockMvc.perform(get("/coches/{id}", 1L)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.marca").value("Tesla"))
            .andExpect(jsonPath("$.potencia").value(300.0));

        verify(cocheService, times(1)).get(1L);
    }

    /**
     * GET /coches/{id} – caso no existente.
     * <p>
     * Si el servicio lanza {@link CocheException}, el controlador debe responder
     * con HTTP 404 Not Found y el mensaje de la excepción en el cuerpo.
     * </p>
     *
     * @throws Exception Si la petición HTTP falla.
     */
    @Test
    @DisplayName("GET /coches/{id} devuelve 404 si no existe")
    void getCoche_NonExisting_Returns404() throws Exception {
        doThrow(new CocheException(2L))
            .when(cocheService).get(2L);

        mockMvc.perform(get("/coches/{id}", 2L))
            .andExpect(status().isNotFound())
            .andExpect(content().string("Coche con id 2 no encontrado."));

        verify(cocheService, times(1)).get(2L);
    }

    /**
     * GET /coches – devuelve la lista completa.
     * <p>
     * Si el servicio devuelve una lista de {@link Coche}, el controlador debe
     * responder con HTTP 200 OK y un array JSON de los objetos.
     * </p>
     *
     * @throws Exception Si la petición HTTP falla.
     */
    @Test
    @DisplayName("GET /coches devuelve 200 con lista de coches")
    void getAllCoches_Returns200AndJsonArray() throws Exception {
        Coche c1 = new Coche(); c1.setId(1L); c1.setMarca("A"); c1.setPotencia(10);
        Coche c2 = new Coche(); c2.setId(2L); c2.setMarca("B"); c2.setPotencia(20);

        when(cocheService.get()).thenReturn(List.of(c1, c2));

        mockMvc.perform(get("/coches")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].marca").value("A"))
            .andExpect(jsonPath("$[1].marca").value("B"));

        verify(cocheService, times(1)).get();
    }

    /**
     * POST /coches – caso éxito.
     * <p>
     * Envía un JSON sin {@code id}; el mock devuelve un {@link Coche} con ID,
     * y el controlador debe responder con HTTP 200 OK y ese JSON.
     * </p>
     *
     * @throws Exception Si la petición HTTP falla.
     */
    @Test
    @DisplayName("POST /coches crea coche y devuelve JSON con id")
    void postCoche_CreateSuccess_Returns200AndJson() throws Exception {
        Coche input = new Coche();
        input.setMarca("Seat");
        input.setPotencia(50);

        Coche saved = new Coche();
        saved.setId(7L);
        saved.setMarca("Seat");
        saved.setPotencia(50);

        when(cocheService.create(any(Coche.class))).thenReturn(saved);

        mockMvc.perform(post("/coches")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(7L))
            .andExpect(jsonPath("$.marca").value("Seat"));

        verify(cocheService, times(1)).create(any(Coche.class));
    }

    /**
     * POST /coches – caso error (JSON con id).
     * <p>
     * Si el JSON incluye un {@code id}, el controlador lanza
     * {@link ModificationSecurityException} antes de llamar al servicio,
     * con el mensaje "Intento de modificación en el create".
     * Debe responder HTTP 400 Bad Request y ese mensaje, sin invocar al servicio.
     * </p>
     *
     * @throws Exception Si la petición HTTP falla.
     */
    @Test
    @DisplayName("POST /coches devuelve 400 si el JSON trae id")
    void postCoche_CreateRejectWithId_Returns400() throws Exception {
        Coche input = new Coche();
        input.setId(99L);
        input.setMarca("Fake");
        input.setPotencia(10);

        mockMvc.perform(post("/coches")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("Intento de modificación en el create"));

        // El servicio nunca debería ser invocado en este caso
        verify(cocheService, never()).create(any(Coche.class));
    }

    /**
     * PUT /coches/{id} – caso éxito.
     * <p>
     * Envía JSON con el mismo {@code id}; el mock no arroja excepción,
     * y el controlador debe responder con HTTP 204 No Content.
     * </p>
     *
     * @throws Exception Si la petición HTTP falla.
     */
    @Test
    @DisplayName("PUT /coches/{id} actualiza y devuelve 204 No Content")
    void putCoche_UpdateSuccess_Returns204() throws Exception {
        Coche input = new Coche();
        input.setId(5L);
        input.setMarca("VW");
        input.setPotencia(100);

        doNothing().when(cocheService).update(any(Coche.class));

        mockMvc.perform(put("/coches/{id}", 5L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
            .andExpect(status().isNoContent());

        verify(cocheService, times(1)).update(any(Coche.class));
    }

    /**
     * PUT /coches/{id} – caso error servicio.
     * <p>
     * Cuando el servicio {@code update(...)} arroja
     * {@link ModificationSecurityException}, el controlador debe procesarla
     * y devolver HTTP 400 Bad Request con el mensaje de la excepción.
     * El objeto JSON y la ruta deben tener el mismo {@code id}.
     * </p>
     *
     * @throws Exception Si la petición HTTP falla.
     */
    @Test
    @DisplayName("PUT /coches/{id} devuelve 400 si update lanza excepción")
    void putCoche_UpdateReject_Returns400() throws Exception {
        Coche input = new Coche();
        input.setId(5L);  // debe coincidir con el {id} de la ruta
        input.setMarca("X");
        input.setPotencia(0);

        // Stubeo el servicio para que lance la excepción
        doThrow(new ModificationSecurityException("Para actualizar es obligatorio enviar el id"))
            .when(cocheService).update(any(Coche.class));

        mockMvc.perform(put("/coches/{id}", 5L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("Para actualizar es obligatorio enviar el id"));

        // Verifico que sí se llamó exactamente una vez a update(...)
        verify(cocheService, times(1)).update(any(Coche.class));
    }

    /**
     * DELETE /coches/{id} – caso éxito.
     * <p>
     * Si el mock no arroja excepción, el controlador debe responder
     * con HTTP 200 OK y cuerpo vacío.
     * </p>
     *
     * @throws Exception Si la petición HTTP falla.
     */
    @Test
    @DisplayName("DELETE /coches/{id} devuelve 200 cuando existe")
    void deleteCoche_Existing_Returns200() throws Exception {
        doNothing().when(cocheService).delete(3L);

        mockMvc.perform(delete("/coches/{id}", 3L))
            .andExpect(status().isOk())
            .andExpect(content().string(""));

        verify(cocheService, times(1)).delete(3L);
    }

    /**
     * DELETE /coches/{id} – caso no existe.
     * <p>
     * Si el mock lanza {@link CocheException}, el controlador debe responder
     * con HTTP 404 Not Found y el mensaje de la excepción.
     * </p>
     *
     * @throws Exception Si la petición HTTP falla.
     */
    @Test
    @DisplayName("DELETE /coches/{id} devuelve 404 si no existe")
    void deleteCoche_NonExisting_Returns404() throws Exception {
        doThrow(new CocheException(4L)).when(cocheService).delete(4L);

        mockMvc.perform(delete("/coches/{id}", 4L))
            .andExpect(status().isNotFound())
            .andExpect(content().string("Coche con id 4 no encontrado."));

        verify(cocheService, times(1)).delete(4L);
    }
}
