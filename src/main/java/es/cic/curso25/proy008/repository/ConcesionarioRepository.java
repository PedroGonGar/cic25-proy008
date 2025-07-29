package es.cic.curso25.proy008.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import es.cic.curso25.proy008.model.Concesionario;

/**
 * Repositorio JPA para la entidad {@link Concesionario}.
 * <p>
 * Extiende {@link JpaRepository}, proporcionando métodos CRUD
 * como:
 * <ul>
 *   <li>{@code findById(Long id)}</li>
 *   <li>{@code findAll()}</li>
 *   <li>{@code save(Concesionario entity)}</li>
 *   <li>{@code deleteById(Long id)}</li>
 *   <li>...y otros métodos de paginación, ordenación, etc.</li>
 * </ul>
 * </p>
 * <p>
 * Spring Data JPA detecta esta interfaz y crea automáticamente
 * un proxy sin necesidad de anotar con {@code @Repository}.
 * </p>
 * <p>
 * Parámetros genéricos:
 * <ul>
 *   <li><code>Concesionario</code> – Tipo de la entidad gestionada.</li>
 *   <li><code>Long</code> – Tipo de la clave primaria ({@code @Id}).</li>
 * </ul>
 * </p>
 * 
 * @author Pedro González
 * @version 1.0
 * @since 1.0
 */
public interface ConcesionarioRepository extends JpaRepository<Concesionario, Long> {
    // No es necesario declarar métodos adicionales:
    // Spring Data JPA implementa automáticamente los heredados de JpaRepository.
}
