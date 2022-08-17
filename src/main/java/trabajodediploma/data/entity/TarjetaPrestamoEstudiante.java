package trabajodediploma.data.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Entity
public class TarjetaPrestamoEstudiante extends TarjetaPrestamo {
    
    @JoinColumn(name = "estudiante_id",  updatable = true, unique = false)
    @ManyToOne()
    private Estudiante estudiante;

    public TarjetaPrestamoEstudiante(Libro libro, LocalDate fechaPrestamo,LocalDate fechaDevolucion,Estudiante estudiante){
        super(libro, fechaPrestamo, fechaDevolucion);
        this.estudiante = estudiante;
    }


}
