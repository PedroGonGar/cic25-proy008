package es.cic.curso25.proy008.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import es.cic.curso25.proy008.model.Coche;

/**
 * ╔═══════════════════════════════════════════════╗
 * ║    🔌  C O C H E   R E P O S I T O R Y        ║
 * ╠═══════════════════════════════════════════════╣
 * ║  FUNCIONA COMO “DAO” (Data Access Object)     ║
 * ║  ‑ Hereda de {@link JpaRepository}.           ║
 * ║  ‑ Obtiene los métodos CRUD:                  ║
 * ║       • findById, findAll, save, deleteById…  ║
 * ║  ‑ No requiere @Repository: Spring Data crea  ║
 * ║    el proxy automáticamente al escanear la    ║
 * ║    interfaz.                                  ║
 * ║                                               ║
 * ║  PARÁMETROS GENERICS                          ║
 * ║  ───────────────────────────────────────────  ║
 * ║   <Coche, Long>                               ║
 * ║    └───┘  └──┘                                ║
 * ║     │      │                                  ║
 * ║     │      └─ Tipo de la PK (@Id)             ║
 * ║     └──────── Tipo de la Entidad              ║
 * ╚═══════════════════════════════════════════════╝
 */

public interface CocheRepository extends JpaRepository<Coche, Long> {

     /**
     * No es necesario escribir nada más:
     * Spring Data implementa la interfaz en tiempo de ejecución.
     */
}
