package es.cic.curso25.proy008.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.cic.curso25.proy008.model.Concesionario;

public interface ConcesionarioRepository extends JpaRepository<Concesionario, Long> {

}
