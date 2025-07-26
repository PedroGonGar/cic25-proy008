package es.cic.curso25.proy008.ControllerTest;

import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.cic.curso25.proy008.enums.TipoCarnet;
import es.cic.curso25.proy008.model.Motorista;
import es.cic.curso25.proy008.repository.MotoristaRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class MotoristaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MotoristaRepository motoristaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * ########################
     * #         CRUD         #
     * ########################
     */

     /**
     * ########################
     * #        CREATE        #
     * ########################
     */

     @Test // Es un metodo de test para spting
     @DisplayName("post /motorista Guarda la moto y devuelve un Json con el Id")
     void shouldCreateMotorista () throws Exception{

        //PREPARAMOS 
        Motorista motorista = new Motorista();
        motorista.setNombre("El Pepe");
        motorista.setApellidos("Perez Perez");
        motorista.setEdad(27);
        motorista.setEsMayorDeEdad(true);
        motorista.setTipoCarnet(TipoCarnet.A2);
        motorista.setEsMayorDeEdad(true);

        String motoristaJson = objectMapper.writeValueAsString(motorista);

        //EJECUTAMOS
        MvcResult res = mockMvc.perform(post("/motos")
                                .contentType("application/json")
                                .content(motoristaJson))
                                .andExpect(status().isOk())
                                .andReturn();

        //COMPROBAMOS
        Motorista cuerpo = objectMapper.readValue(
                            res.getResponse().getContentAsString(), 
                            Motorista.class);

        assertTrue(cuerpo.getId() > 0, "El id tuene que ser positivo");
     }

     /**
     * ########################
     * #         READ         #
     * ########################
     */


     /**
     * ########################
     * #        UPDATE        #
     * ########################
     */


     /**
     * ########################
     * #        DELETE        #
     * ########################
     */

}
