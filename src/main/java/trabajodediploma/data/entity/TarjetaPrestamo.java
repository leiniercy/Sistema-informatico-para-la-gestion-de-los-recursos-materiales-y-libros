package trabajodediploma.data.entity;

import java.time.LocalDate;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import trabajodediploma.data.AbstractEntity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Entity
public class TarjetaPrestamo extends AbstractEntity {

    @EqualsAndHashCode.Include
    @ToString.Include
    
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucion;

    @OneToMany(mappedBy = "tarjeta",cascade = CascadeType.ALL)
    @ElementCollection(fetch = FetchType.EAGER)
    List<Libro> libros;
    
    @OneToOne
    Estudiante estudiante;
    @OneToOne
    Trabajador trabajador;
    

}
