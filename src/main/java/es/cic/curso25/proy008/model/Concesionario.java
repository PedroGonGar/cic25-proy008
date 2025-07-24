package es.cic.curso25.proy008.model;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "concesionario")
public class Concesionario {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Version
    private Long version;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private int telefono;

    private String ciudad;
    private LocalTime apertura;
    private LocalTime cierre;

    @OneToMany(
        mappedBy        = "concesionario",
        cascade         = CascadeType.ALL,
        orphanRemoval   = true,
        fetch           = FetchType.LAZY
    )
    private Set<Coche> listaCoches = new HashSet<>();

    public Concesionario() {

    }

    public Concesionario(String nombre, int telefono, String ciudad, LocalTime apertura, LocalTime cierre) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.ciudad = ciudad;
        this.apertura = apertura;
        this.cierre = cierre;
    }

    public Long getId() {
        return id;
    }

    public Long getVersion() {
        return version;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getTelefono() {
        return telefono;
    }

    public void setTelefono(int telefono) {
        this.telefono = telefono;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public LocalTime getApertura() {
        return apertura;
    }

    public void setApertura(LocalTime apertura) {
        this.apertura = apertura;
    }

    public LocalTime getCierre() {
        return cierre;
    }

    public void setCierre(LocalTime cierre) {
        this.cierre = cierre;
    }

    public List<Coche> getListaCoches() {
        return List.copyOf(listaCoches);
    }

    public void addCoche(Coche coche) {
        if (listaCoches.add(coche)) {
            coche.setConcesionario(this);
        }
    }

    public void removeCoche(Coche coche) {
        if (listaCoches.remove(coche)) {
            coche.setConcesionario(null);
        }
    }

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
        Concesionario other = (Concesionario) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Concesionario [id=" + id + ", version=" + version + ", nombre=" + nombre + ", telefono=" + telefono
                + ", ciudad=" + ciudad + ", apertura=" + apertura + ", cierre=" + cierre + "]";
    }
}
