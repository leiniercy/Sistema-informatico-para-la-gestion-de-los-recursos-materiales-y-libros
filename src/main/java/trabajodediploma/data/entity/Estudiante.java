package trabajodediploma.data.entity;

import javax.persistence.Column;
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
    @Column
    private String nombre;
    @Column
    private String apellidos;
    @Column
    private String ci;
    @Column
    private String solapin;
    @Column
    private Integer anno_academico;
    @Column
    private String facultad;
    
    
    public String getNombreApellidos(){
        return nombre+" "+apellidos;
    }

    
}
