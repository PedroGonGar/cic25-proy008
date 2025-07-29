package es.cic.curso25.proy008.ControllerTest;

import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
      * # CRUD #
      * ########################
      */

      /**
       * ########################
      * # CREATE #
      * ########################
      */

      @Test // Es un metodo de test para spting
      @DisplayName("post /motorista Guarda la moto y devuelve un Json con el Id")
      void shouldCreateMotorista() throws Exception {

         // PREPARAMOS
         Motorista motorista = new Motorista();
         motorista.setNombre("El Pepe");
         motorista.setApellidos("Perez Perez");
         motorista.setEdad(27);
         motorista.setEsMayorDeEdad(true);
         motorista.setTipoCarnet(TipoCarnet.A2);
         motorista.setEsMayorDeEdad(true);

         String motoristaJson = objectMapper.writeValueAsString(motorista);

         // EJECUTAMOS
         MvcResult res = mockMvc.perform(post("/motoristas")
               .contentType("application/json")
               .content(motoristaJson))
               .andExpect(status().isOk())
               .andReturn();

         // COMPROBAMOS
         Motorista cuerpo = objectMapper.readValue(
               res.getResponse().getContentAsString(),
               Motorista.class);

         assertTrue(cuerpo.getId() > 0, "El id tuene que ser positivo");
      }

      /**
      * ########################
      * # READ #
      * ########################
      */

      /**
       * Test de Metodo READ sin argunemtos
       * @throws Exception
       */
      @Test
      void testGetSinID() throws Exception{

         // PREPARAMOS
         Motorista motorista = new Motorista();
         motorista.setNombre("El Pepe");
         motorista.setApellidos("Perez Perez");
         motorista.setEdad(27);
         motorista.setEsMayorDeEdad(true);
         motorista.setTipoCarnet(TipoCarnet.A2);
         motorista.setEsMayorDeEdad(true);

         motorista = motoristaRepository.save(motorista);

         //Hacemos el Get
         mockMvc.perform(get("/motoristas"))
                        .andExpect(status().isOk());
         
      }

      /**
       * 
       * @throws Exception
       */
      @Test
      @DisplayName("GET /motoristas/{id} devuelve 200 con objeto y 404 si no encuentra referencia")
      void shoudReturnMotoristaOrNotFound() throws Exception {

            // PREPARAMOS
         Motorista motorista = new Motorista();
         motorista.setNombre("El Pepe");
         motorista.setApellidos("Perez Perez");
         motorista.setEdad(27);
         motorista.setEsMayorDeEdad(true);
         motorista.setTipoCarnet(TipoCarnet.A2);
         motorista.setEsMayorDeEdad(true);

         motorista = motoristaRepository.save(motorista);

         // EJECUTAMOS
                // 1) Existe la moto
                mockMvc.perform(get("/motoristas/{id}", motorista.getId()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(motorista.getId()))
                                .andExpect(jsonPath("$.apellidos").value("Perez Perez"))
                                .andExpect(jsonPath("$.edad").value(27));

                // 2) No existe la moto
                long idInexistente = motorista.getId() + 999;// Cogemos el id y le sumamos 100 para que no coincida
                String expectedString = "No existe el motorista con id " + idInexistente;

                mockMvc.perform(get("/motoristas/{id}", idInexistente))
                                .andExpect(status().isNotFound())
                                .andExpect(content().string(expectedString));
        }

      /**
       * ########################
      * # UPDATE #
      * ########################
      */

      @Test
      void shouldUpdateMotorista() throws Exception {

            // PREPARAMOS
            Motorista motorista = new Motorista();
            motorista.setNombre("El Pepe");
            motorista.setApellidos("Perez Perez");
            motorista.setEdad(27);
            motorista.setEsMayorDeEdad(true);
            motorista.setTipoCarnet(TipoCarnet.A2);
            motorista.setEsMayorDeEdad(true);

            //Añadimos al motorista a la BBDD
            motorista = motoristaRepository.save(motorista);

            //modificamos al motorista para inyectarlo
            motorista.setNombre("Felipe Uve Palito");
            motorista.setApellidos("yee-ha");

            String jsonStroString = objectMapper.writeValueAsString(motorista);

            //Ejecutamos
            mockMvc.perform(put("/motoristas")
                  .contentType(MediaType.APPLICATION_JSON).content(jsonStroString))
                  .andExpect(jsonPath("$.id").value(motorista.getId()))
                  .andExpect(jsonPath("$.apellidos").value(motorista.getApellidos()))
                  .andExpect(jsonPath("$.edad").value(motorista.getEdad()))
                  .andExpect(status().isOk());
            
      }

      /**
       * ########################
      * # DELETE #
      * ########################
      */

      /**
         * Metodo que comprueba la funcionalidad de borrar una moto en caso de que esta
         * exista
         * 
         * @throws Exception
         */
        @Test
        @DisplayName("DELETE /motos/{id} Elimina el registro")
        void shouldDeleteCoche() throws Exception {

                // PEC

                // PREPARAMOS
                  Motorista motorista = new Motorista();
                  motorista.setNombre("El Pepe");
                  motorista.setApellidos("Perez Perez");
                  motorista.setEdad(27);
                  motorista.setEsMayorDeEdad(true);
                  motorista.setTipoCarnet(TipoCarnet.A2);
                  motorista.setEsMayorDeEdad(true);

                // Guardamos la moto en la BBDD
                motorista = motoristaRepository.save(motorista);

                // EJECUTAMOS
                // Le decimos con mock que nos haga el delete
                mockMvc.perform(delete("/motoristas/{id}", motorista.getId()))
                                .andExpect(status().isOk());

                // COMPROBAMOS
                assertTrue((motoristaRepository.findById(motorista.getId()).isEmpty()),
                                "El motorista ha sido eliminado");

        }

}
