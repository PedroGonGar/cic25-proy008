package es.cic.curso25.proy008.ServiceTest;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import es.cic.curso25.proy008.controller.ModificationSecurityException;
import es.cic.curso25.proy008.exception.MotoException;
import es.cic.curso25.proy008.model.Moto;
import es.cic.curso25.proy008.repository.MotoRepository;
import es.cic.curso25.proy008.service.MotoService;

/**
 * ╔═══════════════════════════════════════════════════════════════════════════╗
 * ║ 🔬  C O C H E S E R V I C E   –   I N T E G R A T I O N   T E S T S       ║
 * ╠═══════════════════════════════════════════════════════════════════════════╣
 * ║ ✔  @SpringBootTest   ▸ arranca todo Spring (Service, Repository, H2…).    ║
 * ║ ✔  @Transactional    ▸ cada test corre en su transacción ⇒ rollback.     ║
 * ║ ✔  No se usa MockMvc: aquí probamos la capa de servicio directamente.     ║
 * ║                                                                           ║
 * ║    Pruebas de integración para CocheService.                              ║
 * ║    Verifica la correcta interacción entre el servicio y la base de datos. ║
 * ║    Usa una base de datos H2 en memoria y transacciones que se revierten   ║
 * ║     después de cada test.                                                 ║
 * ╚═══════════════════════════════════════════════════════════════════════════╝
 */
@SpringBootTest
@Transactional
class MotoServiceIntegrationTest {


    /*─────────────────────────────────────────────────────────────
     * I N Y E C C I Ó N   D E   B E A N S
     *───────────────────────────────────────────────────────────*/
    @Autowired
    private MotoService motoService;      // SUT (System Under Test)

    @Autowired
    private MotoRepository motoRepository; // Para preparar/verificar datos

    /*=======================================================================
     * 1)  C  R  E  A  T  E
     *=====================================================================*/

     /**─────────────────────────────────────────────────────────────
      * Metodo para comprobar si se crean correctamente las motos
      *─────────────────────────────────────────────────────────────*/
     @Test
     @DisplayName("Crea un coche nuevo y le asigna automaticamente un ID")
     void shouldCreateMoto(){

        //PREPARAMOS
        Moto moto = new Moto(85, "Yamaha", "Naked");

        //EJECUTAMOS
        Moto res = motoService.create(moto);

        //COMPROBAMOS
        assertNotNull(res.getId(), "El ID no debe ser null");
        assertTrue(motoRepository.existsById(res.getId()),
                    "La moto deberia existir en la BBDD");
     }
     
     //─────────────────────────────────────────────────────────────
     /**
      * Este metodo nos va a crear una moto pasandole un id, 
      * lo que generará una excepción en el método. Nuestro objetivo
      * es obtenerla.*/
      //─────────────────────────────────────────────────────────────
     @Test
     @DisplayName("Lanza una excepción cuando la moto venga con un ID")
     void shouldRejectCreateWithID(){

        //PREPARAMOS
        Moto moto = new Moto(85, "Honda", "Naked");
        moto.setId(7L);//Ponemos num L Para que entienda que es un Long, no long ni int

        //AssertThrows 
        //Syntaxis: assertThrows(Exception excepción_Esperada, String MensajeDeError)
        //Nos devuelve la excepción que se lanze durante la ejecución.
        assertThrows(ModificationSecurityException.class, () -> motoService.create(moto));

     }

    /*=======================================================================
     * 2)  R  E  A  D
     *=====================================================================*/

     @Test
     @DisplayName("Test para comprobar la devolución de un coche existente")
     void shouldGetExisting (){

        //PREPARAMOS
        Moto moto = new Moto(85, "Yamaha", "Naked");

        //EJECUTAMOS
        motoService.create(moto);
        Moto result = motoService.get(moto.getId()); //Almacenamos el resultado en una moto para la verificacion

        //COMPROBAMOS
        assertEquals(85, result.getPotencia());//la potencia es la que deberia
        assertEquals("Yamaha", result.getMarca());//La marca el la que deberia
        assertEquals("Naked", result.getTipo());//El tipo es el que deberia
        assertFalse(result.isEncendido(), "Encendido es False por defecto");//Está apagada como debería
     }

     /**─────────────────────────────────────────────────────────────
      * Método para comprobar la funcionalida del metodo Get de Service
      * en el caso de que se obtenga una referencia nula
      *─────────────────────────────────────────────────────────────*/
     @Test
     @DisplayName("Nos lanzará una excepcion si la moto no existe o es null")
     void shouldThrowWhenNotFound(){


     }

     /**─────────────────────────────────────────────────────────────
      * Método para comprobar la funcionalida del metodo get() 
      * Con el constructor vacio nos deberia devolver
      *─────────────────────────────────────────────────────────────*/
      @Test
      @DisplayName("Nos devolvera todos los coches")
      void shouldGetAll(){
        
        //PREPARAMOS
        Moto moto1 = new Moto(85, "Yamaha", "Naked");
        Moto moto2 = new Moto(72, "Honda", "Racing");

        //EJECUTAMOS
        motoService.create(moto1);  //Almacenamos en la BBDD Moto 1
        motoService.create(moto2);  //Almacenamos en la BBDD Moto 2

        //COMPROBAMOS 
        List <Moto> lista = motoService.get();  //Almacenamos los resultados en una lista para revisarlos

        //Comprobamos que todos los resultados de la lista coinciden con lo esperado
        assertEquals(2, lista.size(), "Debe haber 2 entradas");
        assertEquals(85, lista.get(0).getPotencia());
        assertEquals("Yamaha", lista.get(0).getMarca());
        assertEquals("Naked", lista.get(0).getTipo());
        assertEquals(72, lista.get(1).getPotencia());
        assertEquals("Honda", lista.get(1).getMarca());
        assertEquals("Racing", lista.get(1).getTipo());
      }

    /*=======================================================================
     * 3)  U  P  D  A  T  E
     *=====================================================================*/

     /**─────────────────────────────────────────────────────────────
      * Metodo para probar la actualización normal de una Moto
      *─────────────────────────────────────────────────────────────*/
     @Test
     @DisplayName("Modifica una moto Existente")
     void  shouldUpdateMoto(){

        //PREPARAMOS
        Moto moto1 = new Moto(85, "Yamaha", "Naked"); //Guardamos dos variables
        Moto moto2 = new Moto(72, "Honda", "Racing"); //Para luego comprobar

        motoService.create(moto1);
        motoService.create(moto2);

        Long id = moto1.getId();
        
        moto1.setPotencia(100); //Hacemos cambios en moto 1
        moto1.setMarca("Ya");       //para comprobar que solo se realizan en la misma

        //EJECUTAMOS
        motoService.update(moto1);
        //Guardamos motor1 en otra variable basandonos en la búsqueda después de la actualización
        Moto moto1Updated = motoRepository.findById(id).get();

        //COMPROBAMOS
        assertEquals("Ya", moto1Updated.getMarca());
        assertEquals(100, moto1Updated.getPotencia());}

        /**──────────────────────────────────────────────────────────────────────────────
         * Metodo para probar la funcionalidad de actualizar en caso de error
         * Devuelve un error 400 ModificationSecurity al no cumplir con los requisitos
         *──────────────────────────────────────────────────────────────────────────────*/
        @Test
        @DisplayName("Lanza un error 400 si no tiene ID")
        void shouldRejectUpdateWithoutId(){

            //PREPARAMOS
            Moto motoSinId = new Moto();    //Creamos una moto
            motoSinId.setId(null);       //Establecemos un ID nulo para la moto

            //EJECUTAMOS Y COMPROBAMOS
            assertThrows(ModificationSecurityException.class,   //Esperamos una ModificationSecirityException
                         () -> motoService.update(motoSinId));              //Al hacer el update con id Null
        }

        /**
         * Metodo para comprobar la funcionalidad de update al darle un id que no existe
         */
        @Test
        @DisplayName("Lanza un 404 si la moto no existe en la base de datos")
        void shouldRejectUpdateNonExisting(){

            //PREPARAMOS
            Moto motoVacia = new Moto ();
            motoVacia.setId(1234L);

            //EJECUTAMOS Y COMPROBAMOS
            assertThrows(MotoException.class,       //Esperamos una MotorException
                            () -> motoService.update(motoVacia));//Al hacer el update de motoVacia
        }

        /*=======================================================================
        * 4)  D  E  L  E  T  E
        *=====================================================================*/ 

        @Test
        @DisplayName("Elimina una entrada de moto existente en la BBDD")
        void shuldDeleteMoto(){

            //PREPARAMOS
            Moto moto = motoRepository.save(new Moto(85,"Yamaha","Naked"));
            Long id = moto.getId(); //Guardamos el id en una variable para mantenerla despues de borrar

            //EJECUTAMOS
            motoService.delete(id); //Borramos la moto

            //COMPROBAMOS
            assertFalse(motoRepository.existsById(id),          //Comprobamos que NO existe la entidad
                        "El coche ha sido eliminado");  //Lanzamos mensaje

        }

        /**
         * Test de metodo delete.
         * En caso de que se intente borrar con un id que no exista, lanzamos
         * un error 404 (MotoException)
         */
        @Test
        @DisplayName("Lanzamos error 404 en caso de que el coche no exista ")
        void shouldRejectDeleteNonExisting(){

            //PREPARAMOS, EJECUTAMOS Y COMPROBAMOS EN UNA LINEA
            assertThrows(MotoException.class,   //Como nuestra moto no existe desde un principio, al intentar borrarla
                        () -> motoService.delete(1234L));//Nos va a soltar una excepcion MotorException

        }

}
