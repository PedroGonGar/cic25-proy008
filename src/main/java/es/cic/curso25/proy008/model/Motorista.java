package es.cic.curso25.proy008.model;

import es.cic.curso25.proy008.enums.TipoCarnet;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

/**
 * Clase motorista.
 * Contiene una moto, en una relacion OneToOne, ya que para que
 * exista un motorista, tiene que existir una moto, y por como funcionan estas
 * relaciones, al crear una instancia motorista, se persiste (se crea XD)
 * automaticamente
 * su Moto (mucho texto, que si hay motorista hay moto, fin)
 */
@Entity // Marcamos que es una entidad de una bbdd
@Table(name = "motorista") // le decimos el nombre de la tabla
public class Motorista {

    @Id // Campo en la BBD ID
    @GeneratedValue(strategy = GenerationType.AUTO) // El valor se generará automáticamente
    private Long id;

    @Version
    private Long version; // Define el nombre de la columna en la BBDD

    @Column(name = "nombre", length = 20) // Definimos que es una columna en la BBDD
    private String nombre;

    @Column(name = "apellidos", length = 40)
    private String apellidos;

    @Column(name = "edad")
    private int edad;

    @Column(name = "esMayorDeEdad")
    private boolean esMayorDeEdad;

    @Column(name = "tipo_de_carnet")
    // Le decimos que es un enum, pero que lo maneje en la bbdd como un string
    // Este metodo es mas legible y mas manipulable si cambia la lista
    @Enumerated(EnumType.STRING)
    private TipoCarnet tipoCarnet;

    // ONE TO ONE
    @OneToOne(cascade = CascadeType.PERSIST)
    private Moto moto;

    // CONSTRUCTORES

    // public Motorista() {
    // }

    // public Motorista(String nombre, String apellidos, int edad, boolean
    // esMayorDeEdad, TipoCarnet tipoCarnet) {
    // this.nombre = nombre;
    // this.apellidos = apellidos;
    // this.edad = edad;
    // this.esMayorDeEdad = esMayorDeEdad;
    // this.tipoCarnet = tipoCarnet;
    // }

    // Getters&Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public boolean isEsMayorDeEdad() {
        return esMayorDeEdad;
    }

    public void setEsMayorDeEdad(boolean esMayorDeEdad) {
        this.esMayorDeEdad = esMayorDeEdad;
    }

    public TipoCarnet getTipoCarnet() {
        return tipoCarnet;
    }

    public void setTipoCarnet(TipoCarnet tipoCarnet) {
        this.tipoCarnet = tipoCarnet;
    }

    public Moto geMoto() {
        return moto;
    }

    public void seMoto(Moto moto) {
        this.moto = moto;
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
        Moto other = (Moto) obj;
        if (id == null) {
            if (other.getId() != null)
                return false;
        } else if (!id.equals(other.getId()))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Perro [id = " + id + ", nombre = " + nombre + ", apellidos = " + apellidos +
                ", edad =" + edad + ", Es mayor de Edad = " + esMayorDeEdad +
                ", Tipo de Carnet = " + tipoCarnet + " ]";
    }

}
