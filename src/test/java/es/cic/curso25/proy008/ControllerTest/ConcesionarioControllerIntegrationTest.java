package es.cic.curso25.proy008.ControllerTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.cic.curso25.proy008.model.Coche;
import es.cic.curso25.proy008.model.Concesionario;
import es.cic.curso25.proy008.repository.CocheRepository;
import es.cic.curso25.proy008.repository.ConcesionarioRepository;

/**
 * Pruebas de integración para {@link es.cic.curso25.proy008.controller.ConcesionarioController}.
 * <p>
 * Arranca el contexto completo de Spring Boot, incluyendo H2 en memoria,
 * repositorios JPA y DispatcherServlet. Utiliza {@link MockMvc} para
 * simular peticiones HTTP reales sin arrancar un servidor externo.
 * </p>
 * 
 * @author Pedro González
 * @version 1.0
 * @since 1.0
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("ConcesionarioControllerIntegrationTest")
public class ConcesionarioControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ConcesionarioRepository concesionarioRepository;

    @Autowired
    private CocheRepository cocheRepository;

    /**
     * Verifica que POST /concesionarios persista un nuevo concesionario
     * y devuelva JSON con el campo {@code id} generado.
     *
     * @throws Exception Si la petición HTTP falla.
     */
    @Test
    @DisplayName("POST /concesionarios crea concesionario y devuelve JSON con id")
    public void shouldCreateConcesionario() throws Exception {
        Concesionario input = new Concesionario(
            "ConsTest", 600123456, "Madrid",
            LocalTime.of(9, 0), LocalTime.of(18, 0)
        );
        String jsonIn = objectMapper.writeValueAsString(input);

        MvcResult res = mockMvc.perform(post("/concesionarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonIn))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.nombre").value("ConsTest"))
            .andExpect(jsonPath("$.ciudad").value("Madrid"))
            .andReturn();

        // Además, verificamos que realmente quedó en BD
        Concesionario created = objectMapper.readValue(
            res.getResponse().getContentAsString(),
            Concesionario.class
        );
        assertTrue(concesionarioRepository.existsById(created.getId()),
                   "El concesionario debería persistirse en la BD");
    }

    /**
     * Verifica que GET /concesionarios/{id} devuelva 200 OK cuando existe
     * y 404 Not Found con mensaje de error cuando no.
     *
     * @throws Exception Si la petición HTTP falla.
     */
    @Test
    @DisplayName("GET /concesionarios/{id} devuelve 200 o 404 según existencia")
    public void shouldReturnConcesionarioOrNotFound() throws Exception {
        // Persistir un concesionario para la prueba
        Concesionario saved = concesionarioRepository.save(
            new Concesionario("Exist", 600654321, "Barcelona",
                              LocalTime.of(8, 0), LocalTime.of(17, 0))
        );

        // Caso existe → 200 OK + JSON correcto
        mockMvc.perform(get("/concesionarios/{id}", saved.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(saved.getId()))
            .andExpect(jsonPath("$.nombre").value("Exist"));

        // Caso no existe → 404 Not Found + mensaje plano
        long missingId = saved.getId() + 999;
        String expectedMessage = "Concesionario con id " + missingId + " no encontrado.";

        mockMvc.perform(get("/concesionarios/{id}", missingId))
            .andExpect(status().isNotFound())
            .andExpect(content().string(expectedMessage));
    }

    /**
     * Verifica que GET /concesionarios devuelva todos los concesionarios en BD.
     *
     * @throws Exception Si la petición HTTP falla.
     */
    @Test
    @DisplayName("GET /concesionarios devuelve lista completa")
    public void shouldGetAllConcesionarios() throws Exception {
        concesionarioRepository.deleteAll();
        Concesionario c1 = concesionarioRepository.save(
            new Concesionario("A", 1, "X",
                              LocalTime.of(7, 0), LocalTime.of(16, 0))
        );
        Concesionario c2 = concesionarioRepository.save(
            new Concesionario("B", 2, "Y",
                              LocalTime.of(8, 0), LocalTime.of(17, 0))
        );

        mockMvc.perform(get("/concesionarios")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value(c1.getId()))
            .andExpect(jsonPath("$[1].id").value(c2.getId()));
    }

    /**
     * Verifica que POST /concesionarios/ventas registre una venta de coche,
     * creando un {@link Coche} asociado a un {@link Concesionario} existente,
     * y lo persista en la base de datos.
     *
     * @throws Exception Si la petición HTTP falla.
     */
    @Test
    @DisplayName("POST /concesionarios/ventas crea coche y persiste en BD")
    public void shouldCreateVentaDeCoche() throws Exception {
        Concesionario cons = concesionarioRepository.save(
            new Concesionario("VentaCons", 600999888, "Valencia",
                              LocalTime.of(9, 30), LocalTime.of(18, 30))
        );
        Coche input = new Coche("Audi", 120, cons);
        String jsonIn = objectMapper.writeValueAsString(input);

        MvcResult res = mockMvc.perform(post("/concesionarios/ventas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonIn))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.marca").value("Audi"))
            .andExpect(jsonPath("$.potencia").value(120))
            .andReturn();

        // Verificamos persistencia en BD usando cocheRepository
        Coche created = objectMapper.readValue(
            res.getResponse().getContentAsString(),
            Coche.class
        );
        assertTrue(cocheRepository.existsById(created.getId()),
                   "El coche debería persistirse en la BD");
        assertTrue(created.getConcesionario().getId().equals(cons.getId()),
                   "El coche debe estar asociado al concesionario correcto");
    }

    /**
     * Verifica que PUT /concesionarios/{id} actualice un concesionario existente
     * y devuelva la entidad actualizada.
     *
     * @throws Exception Si la petición HTTP falla.
     */
    @Test
    @DisplayName("PUT /concesionarios/{id} actualiza y devuelve JSON")
    public void shouldUpdateConcesionario() throws Exception {
        Concesionario original = concesionarioRepository.save(
            new Concesionario("Orig", 5, "O",
                              LocalTime.of(7, 15), LocalTime.of(16, 15))
        );
        original.setCiudad("NuevoLugar");
        String jsonIn = objectMapper.writeValueAsString(original);

        mockMvc.perform(put("/concesionarios/{id}", original.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonIn))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.ciudad").value("NuevoLugar"));

        // Verificamos también en BD
        Concesionario updated = concesionarioRepository.findById(original.getId()).orElseThrow();
        assertTrue(updated.getCiudad().equals("NuevoLugar"),
                   "El cambio debe reflejarse en la BD");
    }

    /**
     * Verifica que DELETE /concesionarios/{id} elimine un concesionario
     * y devuelva HTTP 200 OK.
     *
     * @throws Exception Si la petición HTTP falla.
     */
    @Test
    @DisplayName("DELETE /concesionarios/{id} elimina concesionario")
    public void shouldDeleteConcesionario() throws Exception {
        Concesionario toDelete = concesionarioRepository.save(
            new Concesionario("DelCons", 7, "D",
                              LocalTime.of(6, 0), LocalTime.of(15, 0))
        );

        mockMvc.perform(delete("/concesionarios/{id}", toDelete.getId()))
            .andExpect(status().isOk());

        assertFalse(concesionarioRepository.existsById(toDelete.getId()),
                    "El concesionario debería haber sido eliminado");
    }
}
