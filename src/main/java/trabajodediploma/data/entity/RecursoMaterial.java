package trabajodediploma.data.entity;

import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import trabajodediploma.data.AbstractEntity;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Entity
public class RecursoMaterial extends AbstractEntity {

    @EqualsAndHashCode.Include
    @ToString.Include
    
    @Column
    private String codigo;
    
    @Column
    private String descripcion;
    
    @Column
    private String unidadMedida;
    
    @Column
    @Min(message = "Mínimo 1", value = 1)
    private Integer cantidad;
    
    @ManyToMany(mappedBy = "recursosMateriales", cascade = CascadeType.ALL)
    Set<Modulo> modulos;

}
