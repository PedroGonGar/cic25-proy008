package es.cic.curso25.proy008.ControllerTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.cic.curso25.proy008.controller.MotoController;
import es.cic.curso25.proy008.model.Moto;
import es.cic.curso25.proy008.repository.MotoRepository;

@WebMvcTest(MotoController.class) //Lo vinculamos a la clase MotoController
public class MotoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MotoRepository motoRepository;

    /**───────────────────────────────────────────────────────────────
     * @ObjectMapper es una clase del paquete FasterXml, que nos permite 
     * de una forma sencilla, convertir objetos java a constructos JSON
     * Utilizará automáticamente las siguientes instancias para
     * implementar la lectura/escritura de Json
     *      -JsonParser
     *      -JsonGenerator
     *───────────────────────────────────────────────────────────────*/
    @Autowired
    private ObjectMapper objectMapper;


    //──────────────────────────CRUD─────────────────────────────────────


    /**───────────────────────────────────────────────────────────────
     * Test CREATE (Post)
     * Metodo para probar el Método de crear del controller
     * @throws Exception
     *───────────────────────────────────────────────────────────────*/
    @Test
    void testCreate() throws Exception{

        //Creamos una Moto
        Moto moto = new Moto();
        moto.setMarca("Honda");
        moto.setPotencia(80);
        moto.setTipo("Naked");
        moto.setEncendido(false);
        
        //El metodo utilizado nos sirve para parsear cualquier
        //Valor java a String 
        String motoJson = objectMapper.writeValueAsString(moto);

        mockMvc.perform(post("/moto")
                .contentType("application/json")
                .content(motoJson))
                .andExpect(status().isOk())
                .andExpect(result -> {
                                String respuesta = result.getResponse().getContentAsString();
                                Moto registroCreado = objectMapper.readValue(respuesta, Moto.class);
                                assertTrue(registroCreado.getId() > 0, "El valor debe ser mayor que 0");

                                Optional<Moto> registroRealmenteCreado = motoRepository.findById(registroCreado.getId());
                                assertTrue(registroRealmenteCreado.isPresent());
                             });

    }

    @Test
    void testGetSinID() throws Exception{

        //Creamos una Moto
        Moto moto = new Moto();
        moto.setMarca("Honda");
        moto.setPotencia(80);
        moto.setTipo("Naked");
        moto.setEncendido(false);

        //Creamos un mapper para traducir json.
        //Lo guardamos en un Stirng
        String motoJson = objectMapper.writeValueAsString(moto);


        mockMvc.perform(post("/moto")
                .content("application/json")
                .content(motoJson))
                .andExpect(status().isOk());

    }


}
