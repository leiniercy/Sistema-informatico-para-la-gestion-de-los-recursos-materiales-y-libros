package trabajodediploma.data.entity;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Entity
public class ModeloPagoEstudiante extends ModeloPago {
    
    @NotNull(message = "El campo no debe estar vacío")
    @JoinColumn(name = "estudiante_id", nullable = false, updatable = true, unique = false)
    @ManyToOne(optional = false)
    Estudiante estudiante;
    
    public ModeloPagoEstudiante(String imagen,List<Libro> libros, Estudiante estudiante){
        super(imagen, libros);
        this.estudiante = estudiante;
    }

}
