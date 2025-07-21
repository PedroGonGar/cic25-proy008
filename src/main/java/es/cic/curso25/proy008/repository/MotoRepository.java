package es.cic.curso25.proy008.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.cic.curso25.proy008.model.Moto;

/*
 * Clase Repository, mutada a una interfaz que hereda de JPAREPOSITORY Los métodos básicos
 * de manipulación de BBDD
 */
public interface MotoRepository extends JpaRepository <Moto, Long> {

}
