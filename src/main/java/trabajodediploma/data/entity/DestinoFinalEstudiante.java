package trabajodediploma.data.entity;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
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
public class DestinoFinalEstudiante extends DestinoFinal {

    @EqualsAndHashCode.Include
    @ToString.Include

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "tarjetaDestino_estudiantes", 
    joinColumns = @JoinColumn(name = "tarjetaDestino_id", nullable = false), 
    inverseJoinColumns = @JoinColumn(name = "estudiante_id", unique = false, nullable = false, updatable = true))
    private Set<Estudiante> estudiantes;

    public void addMaterial(Estudiante estudiante){
        if(this.estudiantes == null){
            this.estudiantes = new HashSet<>();
        }
        
        this.estudiantes.add(estudiante);
    }

    public DestinoFinalEstudiante(LocalDate fecha, Modulo modulo, Set<Estudiante> estudiantes) {
        super(fecha, modulo);
        this.estudiantes = estudiantes;
    }

}
