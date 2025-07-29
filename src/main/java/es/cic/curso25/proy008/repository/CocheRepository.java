package es.cic.curso25.proy008.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import es.cic.curso25.proy008.model.Coche;

/**
 * Repositorio JPA para la entidad {@link Coche}.
 * <p>
 * Extiende {@link JpaRepository}, lo que le proporciona automáticamente
 * métodos CRUD como:
 * <ul>
 *   <li>{@code findById(Long id)}</li>
 *   <li>{@code findAll()}</li>
 *   <li>{@code save(Coche entity)}</li>
 *   <li>{@code deleteById(Long id)}</li>
 *   <li>...y muchos más.</li>
 * </ul>
 * </p>
 * <p>
 * No es necesario anotar esta interfaz con {@code @Repository}, ya que
 * Spring Data JPA detecta y crea el proxy correspondiente en tiempo de
 * ejecución.
 * </p>
 * <p>
 * Parámetros genéricos:
 * <ul>
 *   <li><code>Coche</code> – Tipo de la entidad gestionada.</li>
 *   <li><code>Long</code> – Tipo de la clave primaria ({@code @Id}).</li>
 * </ul>
 * </p>
 * 
 * @author Pedro González
 * @version 1.0
 * @since 1.0
 */
public interface CocheRepository extends JpaRepository<Coche, Long> {
    // No se requieren métodos adicionales: Spring Data JPA implementa
    // automáticamente todos los métodos heredados de JpaRepository.
}
