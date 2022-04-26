package trabajodediploma.data.entity;

import javax.persistence.Entity;
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
public class Estudiante extends AbstractEntity {

    @EqualsAndHashCode.Include
    @ToString.Include
    private String nombre;
    private String apellidos;
    private String ci;
    private String solapin;
    private Integer anno_academico;
    private String facultad;
    
    
    public String getNombreApellidos(){
        return nombre+" "+apellidos;
    }

    
}
