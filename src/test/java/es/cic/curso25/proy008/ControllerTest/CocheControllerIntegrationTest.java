package es.cic.curso25.proy008.ControllerTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

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
import es.cic.curso25.proy008.repository.CocheRepository;

/**
 * ╔═══════════════════════════════════════════════════════════════════════════╗
 * ║                🔬 C O C H E C O N T R O L L E R T E S T                   ║
 * ╠═══════════════════════════════════════════════════════════════════════════╣
 * ║ INTEGRACIÓN COMPLETA                                                      ║
 * ║ – Usa @SpringBootTest: levanta todo el contenedor Spring, incluida la     ║
 * ║ BD embebida H2 y el DispatcherServlet.                                    ║
 * ║ – @AutoConfigureMockMvc inyecta un {@link MockMvc} que simula peticiones  ║
 * ║ HTTP contra los endpoints reales, sin necesidad de arrancar Tomcat.       ║
 * ╚═══════════════════════════════════════════════════════════════════════════╝
 */

@SpringBootTest
@AutoConfigureMockMvc
class CocheControllerIntegrationTest {

    /* Inyección de dependencias para las pruebas */
    @Autowired
    // Lanza HTTP contra DispatcherServlet
    private MockMvc mockMvc;

    @Autowired
    // JSON <=> Java
    private ObjectMapper objectMapper;

    @Autowired
    // Acceso directo a la BD para checks
    private CocheRepository cocheRepository;

    /**
     * ───────────────────────────────────────────────────────────────────────────
     * 1) POST /coches → Guarda y devuelve coche con id
     * ───────────────────────────────────────────────────────────────────────────
     * 
     * @throws Exception
     */
    @Test
    @DisplayName("POST /coches guarda el coche y devuelve JSON con id")
    void shouldCreateCoche() throws Exception {

        // PREPARACIÓN
        // Coche sin id (JPA lo generará)
        Coche coche = new Coche("Audi", 90);
        String json = objectMapper.writeValueAsString(coche);

        // EJECUCIÓN
        MvcResult res = mockMvc.perform(post("/coches")
                .contentType(MediaType.APPLICATION_JSON) // Cabecera
                .content(json)) // Cuerpo
                .andDo(print())
                .andExpect(status().isOk()) // 200 esperado
                .andReturn();

        // COMPROBACIÓN
        // Convertimos la respuesta a Coche
        Coche body = objectMapper.readValue(res.getResponse().getContentAsString(),
                Coche.class);
        assertTrue(body.getId() > 0, "El id debe ser positivo");
    }

    /**
     * ───────────────────────────────────────────────────────────────────────────
     * 2) GET /coches/{id} → 200 si existe, 404 + mensaje si no
     * ───────────────────────────────────────────────────────────────────────────
     * 
     * @throws Exception
     */
    @Test
    @DisplayName("GET /coches/{id} devuelve 200 con objeto y 404 con mensaje si no existe")
    void shouldReturnCocheOrNotFound() throws Exception {

        // Persistimos un coche real
        Coche coche = new Coche("BMW", 120);
        coche.setEncendido(true);
        coche = cocheRepository.save(coche);

        // ── Caso 1: existe
        mockMvc.perform(get("/coches/{id}", coche.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(coche.getId()))
                .andExpect(jsonPath("$.marca").value("BMW"))
                .andDo(print());

        // ── Caso 2: NO existe
        long idInexistente = coche.getId() + 999;
        String mensaje = "Coche con id " + idInexistente + " no encontrado.";

        mockMvc.perform(get("/coches/{id}", idInexistente))
                .andExpect(status().isNotFound())
                .andExpect(content().string(mensaje)) // cuerpo plano (Advice)
                .andDo(print()); 
    }

    /**
     * ───────────────────────────────────────────────────────────────────────────
     * 3) PUT /coches → actualiza el coche existente
     * ───────────────────────────────────────────────────────────────────────────
     * 
     * @throws Exception
     */
    @Test
    @DisplayName("PUT /coches actualiza en BD (sin cuerpo de respuesta)")
    void shouldUpdateCoche() throws Exception {

        // 1. Persistimos un coche inicial
        Coche coche = cocheRepository.save(new Coche("Seat", 75));

        // 2. Preparamos el JSON con los nuevos valores
        coche.setMarca("Volkswagen");
        coche.setPotencia(110);
        String json = objectMapper.writeValueAsString(coche);

        // 3. Ejecutamos el PUT -> 200 vacío
        mockMvc.perform(put("/coches")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isOk());

        // 4. Verificamos contra la base de datos
        Coche actualizado = cocheRepository.findById(coche.getId()).orElseThrow();
        assertEquals("Volkswagen", actualizado.getMarca());
        assertEquals(110, actualizado.getPotencia());
    }

    /**
     * ───────────────────────────────────────────────────────────────────────────
     * 4) DELETE /coches/{id} → elimina el registro
     * ───────────────────────────────────────────────────────────────────────────
     * 
     * @throws Exception
     */
    @Test
    @DisplayName("DELETE /coches/{id} elimina el registro")
    void shouldDeleteCoche() throws Exception {

        // Persistimos un coche que después borraremos
        Coche coche = new Coche("Seat", 75);
        coche = cocheRepository.save(coche);

        // Ejecutamos la petición DELETE
        mockMvc.perform(delete("/coches/{id}", coche.getId()))
                .andDo(print())
                .andExpect(status().isOk());

        // Afirmamos que ya no existe
        assertTrue(cocheRepository.findById(coche.getId()).isEmpty(),
                "El coche debería haber sido eliminado");
    }
}
