package trabajodediploma.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

@Entity
public class ModeloPagoEstudiante extends ModeloPago {
    
    @NotNull(message = "El campo no debe estar vacío")
    @JsonIgnoreProperties({"modeloPagos"})
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

}
