package trabajodediploma.data.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
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
    @Max(message = "Máximo 5", value = 5)
    @Min(message = "Mínimo 1", value = 1)
    private Integer cantidad;

}
