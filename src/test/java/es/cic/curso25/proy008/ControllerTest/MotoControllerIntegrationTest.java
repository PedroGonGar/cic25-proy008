package es.cic.curso25.proy008.ControllerTest;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.contains;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.cic.curso25.proy008.controller.MotoController;
import es.cic.curso25.proy008.model.Moto;
import es.cic.curso25.proy008.repository.MotoRepository;
import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
//@WebMvcTest(MotoController.class) //Lo vinculamos a la clase MotoController
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
    @DisplayName("POST /motos Guarda la moto y devuelve JSON ocn id")
    void shouldCreateCoche() throws Exception {

        //METODOLOGIA PEC 

        //PREPARACIÓN
        //Creamos una Moto
        Moto moto = new Moto();
        moto.setMarca("Honda");
        moto.setPotencia(80);
        moto.setTipo("Naked");
        moto.setEncendido(false);

        //El metodo utilizado nos sirve para parsear cualquier
        //Valor java a String 
        String motoJson = objectMapper.writeValueAsString(moto);

        //EJECUCIÓN
        MvcResult res = mockMvc.perform(post("/motos")
                .contentType("application/json")
                .content(motoJson))
                .andExpect(status().isOk())
                .andReturn();

        //COMPROBACIÓN
        Moto body = objectMapper.readValue(res.getResponse().getContentAsString(), Moto.class);

        assertTrue(body.getId() > 0, "El id tiene que ser positivo");


    }

    /**───────────────────────────────────────────────────────────────────────
     * READ (Get)
     * Test del metodo de Controller Get con el constructor que no necesita ID
     * SI existe -> Code 200
     * Si NO existe -> Code 404 + mensaje
     * @throws Exception
     *────────────────────────────────────────────────────────────────────────*/
    @Test
    void testGetSinID() throws Exception{

        //PEC

        //PREPARAMOS
        //Creamos una Moto
        Moto moto = new Moto();
        moto.setMarca("Honda");
        moto.setPotencia(80);
        moto.setTipo("Naked");
        moto.setEncendido(false);

        moto = motoRepository.save(moto);

        //Creamos un mapper para traducir json.
        //Lo guardamos en un Stirng
        String motoJson = objectMapper.writeValueAsString(moto);

        //Hacemos un get 
        mockMvc.perform(get("/motos"))
                .andExpect(status().isOk());

    }

    @Test 
    @DisplayName("GET /motos/{id} devuelve 200 con objeto y 404 si no encuentra referencia")
    void shoudReturnMotoOrNotFound()throws Exception{

        //PEC

        //PREPARAMOS
        //Creamos una Moto
        Moto moto = new Moto();
        moto.setMarca("Honda");
        moto.setPotencia(80);
        moto.setTipo("Naked");
        moto.setEncendido(false);

        //Guardamos la moto en la BBDD
        moto = motoRepository.save(moto);

        //EJECUTAMOS
        //1) Existe la moto
        mockMvc.perform(get("/motos/{id}", moto.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(moto.getId()))
        .andExpect(jsonPath("$.marca").value("Honda"))
        .andExpect(jsonPath("$.tipo").value("Naked"));

        //2) No existe la moto
        long idInexistente = moto.getId()+999;//Cogemos el id y le sumamos 100 para que no coincida
        String espectedString = "No se ha encontrado una moto con el id " + idInexistente;

        mockMvc.perform(get("/motos/{id}", idInexistente))
        .andExpect(status().isNotFound())
        .andExpect(content().string(espectedString));
    }

     /**───────────────────────────────────────────────────────────────────────
     * Test del metodo de Controller Get con el constructor que no necesita ID
     * @throws Exception
     *───────────────────────────────────────────────────────────────────────*/
    @Test
    void testGetConID() throws Exception{

        //Creamos una Moto
        Moto moto = new Moto();
        moto.setMarca("Honda");
        moto.setPotencia(80);
        moto.setTipo("Naked");
        moto.setEncendido(false);

        //Guardamos la moto en la BBDD
        moto = motoRepository.save(moto);

        //Creamos un mapper para traducir json.
        //Lo guardamos en un Stirng
        String motoJson = objectMapper.writeValueAsString(moto);

        //Hacemos un get 
        mockMvc.perform(get("/motos/1", moto.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(moto.getId()))
        .andExpect(jsonPath("$.marca").value("Honda"))
        .andExpect(jsonPath("$.tipo").value("Naked"));

    }




}
