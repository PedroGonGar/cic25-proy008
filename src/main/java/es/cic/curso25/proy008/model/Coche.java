package es.cic.curso25.proy008.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;

/**
 * Entidad JPA que representa un coche en el sistema de concesionarios.
 * Cada instancia se corresponde con una fila de la tabla {@code coche}.
 * <p>
 * Se modela una relación Many-to-One con {@link Concesionario}, de modo que
 * cada coche debe pertenecer obligatoriamente a un concesionario.
 * </p>
 * 
 * @author Pedro González
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "coche")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Coche {

    /**
     * Identificador único de la entidad. Se genera automáticamente.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * Control de concurrencia optimista. Hibernate incrementa este valor
     * en cada actualización para evitar sobrescrituras no intencionadas.
     */
    @Version
    private Long version;

    /**
     * Potencia del motor, en CV (caballos de vapor).
     * Se mapea a la columna {@code potencia} de tipo DOUBLE/FLOAT.
     */
    @Column(name = "potencia")
    private double potencia;

    /**
     * Marca del coche. Longitud máxima de 20 caracteres y no puede ser nulo.
     */
    @Column(length = 20, nullable = false)
    private String marca;

    /**
     * Estado de encendido del motor.
     * Hibernate lo mapea a un BIT/BOOLEAN en la base de datos.
     */
    private boolean encendido = false;

    /**
     * Concesionario al que pertenece este coche.
     * Lado “propietario” de la relación Many-to-One.
     * Nunca puede ser nulo.
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "concesionario_id", nullable = false)
    private Concesionario concesionario;

    /**
     * Constructor vacío exigido por JPA.
     * Permite que Hibernate cree instancias por reflexión.
     */
    public Coche() {

    }

    /**
     * Constructor de conveniencia para crear un coche ya asociado
     * a un concesionario.
     * 
     * @param marca         Marca del coche. No nula.
     * @param potencia      Potencia en CV.
     * @param concesionario Concesionario propietario. No nulo.
     */
    public Coche(String marca, double potencia, Concesionario concesionario) {
        this.marca = marca;
        this.potencia = potencia;
        this.encendido = false;
        this.concesionario = concesionario;
    }

    /**
     * Obtiene el identificador único de este coche.
     * 
     * @return el {@code id} de la entidad, o {@code null} si aún no se ha persistido.
     */
    public Long getId() {
        return id;
    }

    /**
     * Modifica el identificador único de este coche
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obtiene la versión de concurrencia de la entidad.
     * 
     * @return el valor de {@code version} para control optimista.
     */
    public Long getVersion() {
        return version;
    }

    /**
     * Modifica la versión de concurrencia de la entidad
     * @param version
     */
    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     * Obtiene la potencia del coche en CV.
     * 
     * @return la potencia del motor.
     */
    public double getPotencia() {
        return potencia;
    }

    /**
     * Actualiza la potencia del coche.
     * 
     * @param potencia la nueva potencia en CV.
     */
    public void setPotencia(double potencia) {
        this.potencia = potencia;
    }

    /**
     * Obtiene la marca del coche.
     * 
     * @return la marca, con longitud máxima de 20 caracteres.
     */
    public String getMarca() {
        return marca;
    }

    /**
     * Modifica la marca del coche.
     * 
     * @param marca nueva marca. No nula.
     */
    public void setMarca(String marca) {
        this.marca = marca;
    }

    /**
     * Indica si el motor está encendido.
     * 
     * @return {@code true} si está encendido; {@code false} en caso contrario.
     */
    public boolean isEncendido() {
        return encendido;
    }

    /**
     * Cambia el estado de encendido del motor.
     * 
     * @param encendido {@code true} para encender el motor; {@code false} para apagarlo.
     */
    public void setEncendido(boolean encendido) {
        this.encendido = encendido;
    }

    /**
     * Obtiene el concesionario al que pertenece este coche.
     * 
     * @return la entidad {@link Concesionario} propietaria. Nunca {@code null}.
     */
    public Concesionario getConcesionario() {
        return concesionario;
    }

    /**
     * Asocia este coche a un concesionario.
     * 
     * @param concesionario nueva entidad propietaria. No nulo.
     */
    public void setConcesionario(Concesionario concesionario) {
        this.concesionario = concesionario;
    }

    /**
     * Compara dos coches por su identificador.
     * <p>
     * Dos instancias se consideran iguales si tienen el mismo {@code id} no nulo.
     * <strong>No</strong> se consideran iguales si ambos {@code id} son {@code null}.
     * </p>
     * 
     * @param o objeto a comparar.
     * @return {@code true} si son la misma entidad persistida; {@code false} en otro caso.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Coche)) return false;
        Coche other = (Coche) o;
        return id != null && id.equals(other.id);
    }

    /**
     * Calcula el código hash basado en el {@code id}.
     * <p>
     * Si {@code id} es {@code null}, devuelve un valor constante (31)
     * para mantener la estabilidad durante el ciclo de vida de la entidad.
     * </p>
     * 
     * @return el código hash de la entidad.
     */
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 31;
    }

    /**
     * Representación en texto de la entidad.
     * Incluye {@code id}, {@code marca} y {@code potencia}, útiles para registros.
     * 
     * @return cadena descriptiva del coche.
     */
    @Override
    public String toString() {
        return "Coche{" +
               "id=" + id +
               ", marca='" + marca + '\'' +
               ", potencia=" + potencia +
               '}';
    }
}
