package es.cic.curso25.proy008.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
    @Column(length = 20,
            nullable = false)
    private String marca;

    /*────────────────────────────
     *  CAMPO BOOLEANO
     *  Hibernate lo mapea a BIT.
     *───────────────────────────*/
    private boolean encendido = false;

    /**
     * ————————————————————————
     * Relación Many-to-One (lado "propietario")
     * ————————————————————————
    */ 
    @ManyToOne(optional = false,
               fetch = FetchType.LAZY)
    @JoinColumn(name = "concesionario_id",
                nullable = false)
    private Concesionario concesionario;

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
    public Coche(String marca, double potencia, Concesionario concesionario) {
        this.marca     = marca;
        this.potencia  = potencia;
        this.encendido = false;
        this.concesionario = concesionario;
    }

    /*────────────────────────────
     *  GETTERS & SETTERS
     *───────────────────────────*/
    public Long getId() {
        return id;
    }

    public Long getVersion() {
        return version;
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

    public Concesionario getConcesionario() {
        return concesionario;
    }

    public void setConcesionario(Concesionario concesionario) {
        this.concesionario = concesionario;
    }

    /**
     * ————————————————————————
     * equals & hashCode (basados en PK)
     * ————————————————————————
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Coche other = (Coche) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Coche [id=" + id + ", potencia=" + potencia + ", marca=" + marca + "]";
    }
}
