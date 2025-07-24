package es.cic.curso25.proy008.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

/**
 * Clase Moto.
 * Contiene una entidad de moto como relacion OneToOne de la que es "inversa"
 * (NO DUEÑA),
 * ya que una moto puede existir sin un motorista pero no al reves
 */
@Entity
@Table(name = "moto") // Nombre exacto de la tabla
public class Moto {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Version
    private Long version; // Define el nombre de la columna en la base de datos

    @Column(name = "potencia")
    private double potencia;

    @Column(length = 20)
    private String marca;

    // Column no es necesario ya que solo lo tenemos que invocar
    // En caso de que queramos modificar algo
    private boolean encendido;

    private String tipo;

    // ONE TO ONE
    @JsonIgnore
    @OneToOne(mappedBy = "motorista")
    private Motorista motorista;

    // CONSTRUCTORES

    public Moto() {

    }

    public Moto(double potencia, String marca, String tipo) {
        this.potencia = potencia;
        this.marca = marca;
        this.tipo = tipo;
    }

    // GETTERS & SETTERS

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

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

    public Motorista geMotorista() {
        return motorista;
    }

    public void setMotorista(Motorista motorista) {
        this.motorista = motorista;
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
        Motorista other = (Motorista) obj;
        if (id == null) {
            if (other.getId() != null)
                return false;
        } else if (!id.equals(other.getId()))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Perro [id=" + id + ", potencia = " + potencia + ", marca = " + marca +
                ", esta encendida = " + encendido + ", tipo = " + tipo + "]";
    }
}