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

// @Data
// @Getter
// @Setter
// @NoArgsConstructor
// @AllArgsConstructor
// @EqualsAndHashCode(onlyExplicitlyIncluded = true)
// @ToString(onlyExplicitlyIncluded = true)
@Entity
public class ModeloPago extends AbstractEntity {

    // @EqualsAndHashCode.Include
    // @ToString.Include

    @Lob
    @NotNull(message = "Seleccione una imagen")
    @Column(nullable = false)
    protected String imagen;

    // @NotBlank(message = "Seleccione al menos un libro")
    @NotNull(message = "El campo no debe estar vacío")
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "modeloPago_libros", joinColumns = @JoinColumn(name = "modelo_id", nullable = false), inverseJoinColumns = @JoinColumn(name = "libro_id", unique = false, nullable = false, updatable = true))
    protected Set<Libro> libros;
    
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

    @Override
    public int hashCode() {
    int hash = 5;
    hash = 79 * hash + Objects.hashCode(this.imagen);
    hash = 79 * hash + Objects.hashCode(this.libros);
    return hash;
    }

    @Override
    public boolean equals(Object obj) {
    if (this == obj) {
    return true;
    }
    if (obj == null) {
    return false;
    }
    if (getClass() != obj.getClass()) {
    return false;
    }
    final ModeloPago other = (ModeloPago) obj;
    if (!Objects.equals(this.imagen, other.imagen)) {
    return false;
    }
    if (!Objects.equals(this.libros, other.libros)) {
    return false;
    }
    return true;
    }

    @Override
    public String toString() {
        return "ModeloPago{" + "imagen=" + imagen + ", libros=" + libros + '}';
    }

}
