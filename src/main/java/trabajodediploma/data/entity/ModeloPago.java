package trabajodediploma.data.entity;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.*;

import trabajodediploma.data.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity
public class ModeloPago extends AbstractEntity {

    @Lob
    @NotNull(message = "Seleccione una imagen")
    @Column(nullable = false)
    protected String imagen;

    @NotNull(message = "El campo no debe estar vacío")
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "modeloPago_libros", joinColumns = @JoinColumn(name = "modelo_id", nullable = false), inverseJoinColumns = @JoinColumn(name = "libro_id", unique = false, nullable = false, updatable = true))
    protected Set<Libro> libros = new HashSet<>();
    
    public void addLibro(Libro libro){
        if(this.libros == null){
            this.libros = new HashSet<>();
        }
        this.libros.add(libro);
    }

    public ModeloPago() {
    }

    public ModeloPago(String imagen, Set<Libro> libros) {
    this.imagen = imagen;
    this.libros = libros;
    }

    public String getImagen() {
    return imagen;
    }

    public void setImagen(String imagen) {
    this.imagen = imagen;
    }

    public Set<Libro> getLibros() {
    return libros;
    }

    public void setLibros(Set<Libro> libros) {
    this.libros = libros;
    }

    public Integer getId() {
    return id;
    }

    public void setId(Integer id) {
    this.id = id;
    }

}
