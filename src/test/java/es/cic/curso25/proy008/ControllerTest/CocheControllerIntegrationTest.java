package es.cic.curso25.proy008.ControllerTest;

import static org.junit.jupiter.api.Assertions.*;
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
 * Pruebas de integración para {@code CocheController}.
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
@DisplayName("CocheControllerIntegrationTest")
public class CocheControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CocheRepository cocheRepository;

    @Autowired
    private ConcesionarioRepository concesionarioRepository;

    /**
     * POST /coches
     * <p>
     * Dado un concesionario persistido, crea un coche sin {@code id} asociado a él,
     * espera HTTP 200 y que el JSON devuelva un {@code id} positivo. Además
     * comprueba que dicho coche se ha guardado en la BD.
     * </p>
     *
     * @throws Exception Si la petición HTTP falla.
     */
    @Test
    @DisplayName("POST /coches guarda el coche y persiste en BD")
    public void shouldCreateCoche() throws Exception {
        // 1) Dado un concesionario en BD
        Concesionario cons = new Concesionario(
            "TestCons", 600123456, "Madrid",
            LocalTime.of(9, 0), LocalTime.of(18, 0)
        );
        cons = concesionarioRepository.save(cons);

        // 2) Cuando envío POST /coches
        Coche coche = new Coche("Audi", 90, cons);
        String jsonIn = objectMapper.writeValueAsString(coche);

        MvcResult res = mockMvc.perform(post("/coches")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonIn))
            .andExpect(status().isOk())
            .andReturn();

        // 3) Entonces JSON contiene un id > 0
        Coche created = objectMapper.readValue(
            res.getResponse().getContentAsString(),
            Coche.class
        );
        assertNotNull(created.getId(), "El id no debe ser null");
        assertTrue(created.getId() > 0, "El id debe ser positivo");

        // 4) Y el coche realmente existe en la BD
        assertTrue(cocheRepository.existsById(created.getId()),
                   "El coche debería persistirse en la BD");
    }

    /**
     * GET /coches/{id}
     * <p>
     * Comprueba que:
     * <ul>
     *   <li>200 OK + JSON con los campos correctos cuando existe.</li>
     *   <li>404 Not Found + mensaje plano cuando no existe.</li>
     * </ul>
     * </p>
     *
     * @throws Exception Si la petición HTTP falla.
     */
    @Test
    @DisplayName("GET /coches/{id} devuelve 200 o 404 según existencia")
    public void shouldReturnCocheOrNotFound() throws Exception {
        // Preparamos concesionario y coche en BD
        Concesionario cons = new Concesionario(
            "TestCons2", 600654321, "Barcelona",
            LocalTime.of(8, 0), LocalTime.of(17, 0)
        );
        cons = concesionarioRepository.save(cons);

        Coche saved = new Coche("BMW", 120, cons);
        saved.setEncendido(true);
        saved = cocheRepository.save(saved);

        // — Caso existe → 200 + JSON
        mockMvc.perform(get("/coches/{id}", saved.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(saved.getId()))
            .andExpect(jsonPath("$.marca").value("BMW"));

        // — Caso no existe → 404 + mensaje
        long missingId = saved.getId() + 999;
        String msg = "Coche con id " + missingId + " no encontrado.";

        mockMvc.perform(get("/coches/{id}", missingId))
            .andExpect(status().isNotFound())
            .andExpect(content().string(msg));
    }

    /**
     * PUT /coches/{id}
     * <p>
     * Envía JSON con el mismo {@code id} y datos nuevos, espera
     * 204 No Content y verifica en BD que se actualizaron.
     * </p>
     *
     * @throws Exception Si la petición HTTP falla.
     */
    @Test
    @DisplayName("PUT /coches/{id} actualiza en BD y devuelve 204")
    public void shouldUpdateCoche() throws Exception {
        Concesionario cons = concesionarioRepository.save(
            new Concesionario("TestCons3", 600111222, "Valencia",
                              LocalTime.of(7, 30), LocalTime.of(16, 30))
        );
        Coche original = cocheRepository.save(new Coche("Seat", 75, cons));

        original.setMarca("Volkswagen");
        original.setPotencia(110);
        String jsonIn = objectMapper.writeValueAsString(original);

        mockMvc.perform(put("/coches/{id}", original.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonIn))
            .andExpect(status().isNoContent());

        Coche updated = cocheRepository.findById(original.getId()).orElseThrow();
        assertEquals("Volkswagen", updated.getMarca());
        assertEquals(110, updated.getPotencia());
    }

    /**
     * DELETE /coches/{id}
     * <p>
     * Elimina un coche existente, espera 200 OK y comprueba
     * que ya no esté en la BD.
     * </p>
     *
     * @throws Exception Si la petición HTTP falla.
     */
    @Test
    @DisplayName("DELETE /coches/{id} elimina el registro y devuelve 200")
    public void shouldDeleteCoche() throws Exception {
        Concesionario cons = concesionarioRepository.save(
            new Concesionario("TestCons4", 600333444, "Sevilla",
                              LocalTime.of(10, 0), LocalTime.of(19, 0))
        );
        Coche toDelete = cocheRepository.save(new Coche("Seat", 75, cons));

        mockMvc.perform(delete("/coches/{id}", toDelete.getId()))
            .andExpect(status().isOk());

        assertFalse(cocheRepository.existsById(toDelete.getId()),
                    "El coche debería haber sido eliminado de la BD");
    }
}
