package trabajodediploma.data.entity;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@EqualsAndHashCode(onlyExplicitlyIncluded = true)
//@ToString(onlyExplicitlyIncluded = true)
@Entity
public class ModeloPagoEstudiante extends ModeloPago {
    
    
    // @EqualsAndHashCode.Include
    // @ToString.Include
    
    @NotNull(message = "El campo no debe estar vacío")
   // @NotBlank(message = "Seleccione un estudiante")
    @JoinColumn(name = "estudiante_id", nullable = false, updatable = true, unique = false)
    @ManyToOne(optional = false)
    private Estudiante estudiante;

    //Constructor
    public ModeloPagoEstudiante() {
    }
    
    public ModeloPagoEstudiante(String imagen,Set<Libro> libros, Estudiante estudiante){
        super(imagen, libros);
        this.estudiante = estudiante;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public void setEstudiante(Estudiante estudiante) {
        this.estudiante = estudiante;
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
        int hash = 3;
        hash = 89 * hash + Objects.hashCode(this.estudiante);
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
        final ModeloPagoEstudiante other = (ModeloPagoEstudiante) obj;
        if (!Objects.equals(this.estudiante, other.estudiante)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ModeloPagoEstudiante{" + "estudiante=" + estudiante + '}';
    }

}
