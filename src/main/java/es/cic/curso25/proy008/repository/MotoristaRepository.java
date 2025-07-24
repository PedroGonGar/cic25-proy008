package es.cic.curso25.proy008.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.cic.curso25.proy008.model.Motorista;

/*
 * @interface
 * Clase Repository, mutada a una interfaz que hereda de JPAREPOSITORY Los métodos básicos
 * de manipulación de BBDD
 */
public interface MotoristaRepository extends JpaRepository<Motorista, Long> {

}
