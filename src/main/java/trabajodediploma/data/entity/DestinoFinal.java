package trabajodediploma.data.entity;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.Min;
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
public class DestinoFinal extends AbstractEntity {

    @EqualsAndHashCode.Include
    @ToString.Include
    
    @Column
    private LocalDate fecha;
    
    @Column
    @Min(message = "Mínimo 0", value = 0)
    private Integer cantidad;

}
