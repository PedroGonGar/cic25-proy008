package es.cic.curso25.proy008.model;

import java.time.LocalTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity(name = "Concesionario")
public class Concesionario {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "nombre")
    private String nombre;
    @Column(name = "telefono")
    private int telefono;
    @Column(name = "ciudad")
    private String ciudad;
    @Column(name = "apertura")
    private LocalTime apertura;
    @Column(name = "cierre")
    private LocalTime cierre;
    @Column(name = "listaCoches")
    private List<Coche> listaCoches;

    @OneToOne(mappedBy = "coche")
    private Coche coche;

    public Concesionario() {

    }

    public Concesionario(Long id, String nombre, int telefono, String ciudad, LocalTime apertura, LocalTime cierre, List<Coche> listaCoches) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.ciudad = ciudad;
        this.apertura = apertura;
        this.cierre = cierre;
        this.listaCoches = listaCoches;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        return listaCoches;
    }

    public void setListaCoches(List<Coche> listaCoches) {
        this.listaCoches = listaCoches;
    }

    public Coche getCoche() {
        return coche;
    }

    public void setCoche(Coche coche) {
        this.coche = coche;
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
        return "Concesionario [id=" + id + ", nombre=" + nombre + ", telefono=" + telefono + ", ciudad=" + ciudad
                + ", apertura=" + apertura + ", cierre=" + cierre + "]";
    }
}
