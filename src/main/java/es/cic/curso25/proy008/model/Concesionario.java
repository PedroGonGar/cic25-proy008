package es.cic.curso25.proy008.model;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;

/**
 * Entidad JPA que representa un concesionario de coches.
 * Cada instancia se corresponde con una fila de la tabla {@code concesionario}.
 * Modela la relación One-to-Many con {@link Coche}, de modo que un concesionario
 * puede gestionar múltiples coches.
 * 
 * @author TuNombre
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "concesionario")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Concesionario {

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
     * Nombre comercial del concesionario. No puede ser nulo.
     */
    @Column(nullable = false)
    private String nombre;

    /**
     * Teléfono de contacto. No puede ser nulo.
     */
    @Column(nullable = false)
    private int telefono;

    /**
     * Ciudad donde se ubica el concesionario.
     */
    private String ciudad;

    /**
     * Hora de apertura diaria del concesionario.
     */
    private LocalTime apertura;

    /**
     * Hora de cierre diaria del concesionario.
     */
    private LocalTime cierre;

    /**
     * Conjunto de coches asociados a este concesionario.
     * Relación bidireccional One-to-Many con {@link Coche}.
     * <p>
     * {@code cascade = ALL} propaga operaciones de persistencia;
     * {@code orphanRemoval = true} elimina coches huérfanos;
     * {@code fetch = LAZY} retrasa la carga hasta que se soliciten.
     * </p>
     */
    @OneToMany(
        mappedBy      = "concesionario",
        cascade       = CascadeType.ALL,
        orphanRemoval = true,
        fetch         = FetchType.LAZY
    )

    @JsonIgnore
    private Set<Coche> listaCoches = new HashSet<>();

    /**
     * Constructor vacío requerido por JPA.
     */
    public Concesionario() {
        // Constructor para uso de JPA
    }

    /**
     * Constructor de conveniencia para crear un concesionario con datos básicos.
     * 
     * @param nombre   Nombre comercial. No nulo.
     * @param telefono Teléfono de contacto. No nulo.
     * @param ciudad   Ciudad de ubicación.
     * @param apertura Hora de apertura.
     * @param cierre   Hora de cierre.
     */
    public Concesionario(String nombre, int telefono, String ciudad,
                         LocalTime apertura, LocalTime cierre) {
        this.nombre   = nombre;
        this.telefono = telefono;
        this.ciudad   = ciudad;
        this.apertura = apertura;
        this.cierre   = cierre;
    }

    /**
     * Obtiene el identificador único de este concesionario.
     * 
     * @return el {@code id}, o {@code null} si aún no se ha persistido.
     */
    public Long getId() {
        return id;
    }

    /**
     * Obtiene la versión de concurrencia optimista.
     * 
     * @return el valor de {@code version}.
     */
    public Long getVersion() {
        return version;
    }

    /**
     * Obtiene el nombre comercial.
     * 
     * @return el {@code nombre}.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Modifica el nombre comercial.
     * 
     * @param nombre Nuevo nombre. No nulo.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene el teléfono de contacto.
     * 
     * @return el {@code telefono}.
     */
    public int getTelefono() {
        return telefono;
    }

    /**
     * Modifica el teléfono de contacto.
     * 
     * @param telefono Nuevo teléfono. No nulo.
     */
    public void setTelefono(int telefono) {
        this.telefono = telefono;
    }

    /**
     * Obtiene la ciudad de ubicación.
     * 
     * @return la {@code ciudad}.
     */
    public String getCiudad() {
        return ciudad;
    }

    /**
     * Modifica la ciudad de ubicación.
     * 
     * @param ciudad Nueva ciudad.
     */
    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    /**
     * Obtiene la hora de apertura diaria.
     * 
     * @return la {@code apertura}.
     */
    public LocalTime getApertura() {
        return apertura;
    }

    /**
     * Modifica la hora de apertura diaria.
     * 
     * @param apertura Nueva hora de apertura.
     */
    public void setApertura(LocalTime apertura) {
        this.apertura = apertura;
    }

    /**
     * Obtiene la hora de cierre diaria.
     * 
     * @return la {@code cierre}.
     */
    public LocalTime getCierre() {
        return cierre;
    }

    /**
     * Modifica la hora de cierre diaria.
     * 
     * @param cierre Nueva hora de cierre.
     */
    public void setCierre(LocalTime cierre) {
        this.cierre = cierre;
    }

    /**
     * Obtiene una vista inmutable de los coches asociados.
     * <p>
     * Se devuelve una copia de la colección interna para proteger el estado
     * de la entidad y mantener la inmutabilidad externa.
     * </p>
     * 
     * @return lista inmutable de {@link Coche} asociados.
     */
    public List<Coche> getListaCoches() {
        return List.copyOf(listaCoches);
    }

    /**
     * Añade un coche a este concesionario y sincroniza la relación bidireccional.
     * 
     * @param coche Instancia de {@link Coche} a agregar. No nula.
     */
    public void addCoche(Coche coche) {
        if (listaCoches.add(coche)) {
            coche.setConcesionario(this);
        }
    }

    /**
     * Elimina un coche de este concesionario y rompe la asociación bidireccional.
     * 
     * @param coche Instancia de {@link Coche} a remover. No nula.
     */
    public void removeCoche(Coche coche) {
        if (listaCoches.remove(coche)) {
            coche.setConcesionario(null);
        }
    }

    /**
     * Compara dos concesionarios por su identificador.
     * <p>
     * Dos instancias se consideran iguales si tienen el mismo {@code id} no nulo.
     * </p>
     * 
     * @param obj Objeto a comparar.
     * @return {@code true} si representan la misma entidad persistida.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)                   return true;
        if (!(obj instanceof Concesionario)) return false;
        Concesionario other = (Concesionario) obj;
        return id != null && Objects.equals(id, other.id);
    }

    /**
     * Calcula el código hash basado en el {@code id}.
     * <p>
     * Si el {@code id} es {@code null}, devuelve un valor constante para
     * mantener estabilidad durante toda la vida de la entidad.
     * </p>
     * 
     * @return código hash de la entidad.
     */
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 31;
    }

    /**
     * Representación en texto de la entidad, útil para logs y depuración.
     * Incluye {@code id}, {@code nombre} y {@code ciudad}.
     * 
     * @return cadena descriptiva del concesionario.
     */
    @Override
    public String toString() {
        return "Concesionario{" +
               "id=" + id +
               ", nombre='" + nombre + '\'' +
               ", ciudad='" + ciudad + '\'' +
               '}';
    }
}
