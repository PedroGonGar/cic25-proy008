package es.cic.curso25.proy008.uc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

//Imports de Mock
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.cic.curso25.proy008.enums.TipoCarnet;
import es.cic.curso25.proy008.model.Moto;
import es.cic.curso25.proy008.model.Motorista;

@SpringBootTest
@AutoConfigureMockMvc
public class MotoristaTieneMotoIntegrationTest {

    //Autowired de MockMvc
    @Autowired
    private MockMvc mockMvc;

    //Object Mapper
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testMotoristaTieneMoto() throws Exception{

        //Creamos una moto
        Moto moto = new Moto();
        moto.setMarca("Yamaha");
        moto.setTipo("Naked");
        moto.setPotencia(30);
        moto.setEncendido(false);

        //Creamos un motorista
        Motorista motoristaTest = new Motorista();
        motoristaTest.setNombre("Pepito");
        motoristaTest.setApellidos("Perez Perez");
        motoristaTest.setEdad(24);
        motoristaTest.setTipoCarnet(TipoCarnet.A2);
        motoristaTest.setEsMayorDeEdad(true);
        
        //Le asignamos la moto al Motorista de Test Y VICEVERSA
        motoristaTest.setMoto(moto);
        moto.setMotorista(motoristaTest);

        //Convertimos la relacion en un string para poder manipularla
        String motoristaTieneMotoJson = objectMapper.writeValueAsString(motoristaTest);
        System.out.println("MOTORISTA TEST A JSON VALOR:"+motoristaTest);

        //Utilizamos MockMVC para simular una peticion HTTP de creacion de moto (POST)
        MvcResult mvcResult = mockMvc.perform(post("/motos/montura") //Hazme un Post
                                .contentType("application/json")     //Pasandote un JSON
                                .content(motoristaTieneMotoJson))               //Concretamente el Json que hicimos antes
                                .andExpect(status().isOk())                     //Si esta correcto
                                .andExpect( personaResult ->{                   //Y el resultado
                                    assertNotNull(                              //No es nulo
                                        objectMapper.readValue(                 //Si tomamos como referencia el 
                                            personaResult.getResponse().getContentAsString(), Motorista.class), //Contenido de persona como string
                                                "El Motorista Tiene Una Montura"); //Y lanzamos el mensaje
                                    })
                                .andReturn(); //Y devolvemos el resultado de mockResult

        mvcResult.toString(); //Masamos el Result a string para verlo por aqui
        
        Motorista motoristaCreado = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Motorista.class);
        Long id = motoristaCreado.getId();

        mockMvc.perform(get("/motoristas/" + id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(result -> {
                    assertEquals(objectMapper.readValue(result.getResponse().getContentAsString(), Motorista.class).getId(),
                            id);
                });   
         
        //Le cambiamos la marca a la moto del motorista
        motoristaCreado.getMoto().setMarca("Honda");

        //Variables de apoyo en fase de test
        // Motorista socorro = motoristaCreado;
        // socorro.getMoto().setMarca("Honda");
        
        String motoristaJson = objectMapper.writeValueAsString(motoristaCreado);

        //Utilizamos MockMVC para simular una peticion HTTP de actualizacion de moto (POST)
        mockMvc.perform(put("/motoristas")
                .contentType("application/json")
                .content(motoristaJson))
                .andDo(print())                
                .andExpect(status().isOk());

        //Hacemos el delete para eliminar el motorista
        mockMvc.perform(delete("/motoristas/" + id))
                .andDo(print())        
                .andExpect(status().isOk());  
    }


}
