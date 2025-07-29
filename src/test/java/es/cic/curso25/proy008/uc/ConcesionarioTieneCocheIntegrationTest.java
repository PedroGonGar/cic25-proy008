package es.cic.curso25.proy008.uc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

/**
 * Prueba de integración que verifica la relación entre
 * {@link Concesionario} y {@link Coche} y recorre su ciclo CRUD completo.
 * <p>
 * Flujo:
 * <ol>
 *   <li>POST /concesionarios → crea un concesionario.</li>
 *   <li>GET /concesionarios/{id} → recupera el concesionario.</li>
 *   <li>PUT /concesionarios/{id} → actualiza datos del concesionario.</li>
 *   <li>POST /concesionarios/ventas → crea un coche asociado.</li>
 *   <li>GET /coches/{id} → recupera el coche.</li>
 *   <li>PUT /coches/{id} → actualiza datos del coche.</li>
 *   <li>DELETE /coches/{id} → elimina el coche.</li>
 *   <li>DELETE /concesionarios/{id} → elimina el concesionario.</li>
 * </ol>
 * </p>
 * 
 * @author Pedro González
 * @version 1.0
 * @since 1.0
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("🔄 Concesionario–Coche Integration Workflow")
public class ConcesionarioTieneCocheIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Verifica el flujo completo de creación, lectura,
     * actualización y eliminación de concesionario y coche.
     *
     * @throws Exception si falla alguna petición HTTP simulada.
     */
    @Test
    @DisplayName("Flujo CRUD Concesionario con Coche asociado")
    void testConcesionarioTieneCoche() throws Exception {
        // 1) Crear concesionario
        Concesionario nuevo = new Concesionario(
            "MiConcesionario", 912345678, "Madrid",
            LocalTime.of(9, 0), LocalTime.of(18, 0)
        );
        String nuevoJson = objectMapper.writeValueAsString(nuevo);

        MvcResult consCreateResult = mockMvc.perform(post("/concesionarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(nuevoJson))
            .andExpect(status().isOk())
            .andReturn();

        Concesionario creadoCons = objectMapper.readValue(
            consCreateResult.getResponse().getContentAsString(),
            Concesionario.class
        );
        assertNotNull(creadoCons.getId(), "Debe asignarse ID al concesionario");
        Long consId = creadoCons.getId();

        // 2) Recuperar concesionario
        mockMvc.perform(get("/concesionarios/{id}", consId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nombre").value("MiConcesionario"));

        // 3) Actualizar concesionario
        creadoCons.setCiudad("Barcelona");
        String updatedConsJson = objectMapper.writeValueAsString(creadoCons);
        mockMvc.perform(put("/concesionarios/{id}", consId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedConsJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.ciudad").value("Barcelona"));

        // 4) Registrar venta de coche asociado
        Coche nuevoCoche = new Coche("Audi", 150, creadoCons);
        String cocheJson = objectMapper.writeValueAsString(nuevoCoche);

        MvcResult cocheCreateResult = mockMvc.perform(post("/concesionarios/ventas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(cocheJson))
            .andExpect(status().isOk())
            .andReturn();

        Coche creadoCoche = objectMapper.readValue(
            cocheCreateResult.getResponse().getContentAsString(),
            Coche.class
        );
        assertNotNull(creadoCoche.getId(), "Debe asignarse ID al coche");
        assertEquals(consId, creadoCoche.getConcesionario().getId(),
                     "El coche debe asociarse al concesionario");
        Long cocheId = creadoCoche.getId();

        // 5) Recuperar coche
        mockMvc.perform(get("/coches/{id}", cocheId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.marca").value("Audi"));

        // 6) Actualizar coche
        creadoCoche.setMarca("BMW");
        String updatedCocheJson = objectMapper.writeValueAsString(creadoCoche);
        mockMvc.perform(put("/coches/{id}", cocheId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedCocheJson))
            .andExpect(status().isNoContent());

        // 7) Eliminar coche
        mockMvc.perform(delete("/coches/{id}", cocheId))
            .andExpect(status().isOk());

        // 8) Eliminar concesionario
        mockMvc.perform(delete("/concesionarios/{id}", consId))
            .andExpect(status().isOk());
    }
}
