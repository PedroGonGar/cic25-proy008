package es.cic.curso25.proy008.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

/**
 * ╔════════════════════════════════════════════════════════════════╗
 * ║                            🚗  COCHE                           ║
 * ╠════════════════════════════════════════════════════════════════╣
 * ║  Entidad JPA que representa la tabla «coche» en la BD H2.      ║
 * ║  Cada instancia se convierte en una fila.                      ║
 * ║                                                                ║
 * ║  NOTAS                                                         ║
 * ║  · Las anotaciones @Entity y @Table informan a Hibernate de    ║
 * ║    que esta clase debe persisitirse.                           ║
 * ║  · @Id + @GeneratedValue generan el ID automáticamente.        ║
 * ║  · @Version implementa bloqueo optimista (control de           ║
 * ║    concurrencia).                                              ║
 * ║  · Los getters/setters son necesarios para que JPA acceda a    ║
 * ║    los campos en tiempo de ejecución.                          ║
 * ╚════════════════════════════════════════════════════════════════╝
 */

@Entity
@Table(name = "coche") // Nombre exacto de la tabla
public class Coche {

     /*────────────────────────────
     *  PRIMARY KEY (Id autogen.)
     *───────────────────────────*/
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    /*────────────────────────────
     *  CONTROL DE CONCURRENCIA
     *  Hibernate incrementa "version"
     *  cada vez que hace UPDATE.
     *───────────────────────────*/
    @Version
    private Long version;

    /*────────────────────────────
     *  CAMPO POTENCIA
     *  double ≈ FLOAT(53) en DB.
     *───────────────────────────*/
    @Column(name = "potencia") // Nombre de la columna distinto al de la propiedad
    private double potencia;
    
    /*────────────────────────────
     *  CAMPO MARCA
     *  Longitud máxima 20 chars.
     *───────────────────────────*/
    @Column(length = 20)
    private String marca;

    /*────────────────────────────
     *  CAMPO BOOLEANO
     *  Hibernate lo mapea a BIT.
     *───────────────────────────*/
    private boolean encendido;

     /*────────────────────────────
     *  CONSTRUCTORES
     *───────────────────────────*/
    /**
     * Constructor sin argumentos requerido por JPA.
     * Java lo crearía implícitamente, pero lo declaramos
     * para que quede claro.
     */
    public Coche() {}

    // Constructor de conveniencia (no obligatorio).
    public Coche(String marca, double potencia) {
        this.marca     = marca;
        this.potencia  = potencia;
        this.encendido = false;
    }

    /*────────────────────────────
     *  GETTERS & SETTERS
     *───────────────────────────*/
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getPotencia() {
        return potencia;
    }

    public void setPotencia(double potencia) {
        this.potencia = potencia;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public boolean isEncendido() {
        return encendido;
    }

    public void setEncendido(boolean encendido) {
        this.encendido = encendido;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
