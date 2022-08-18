package trabajodediploma.data.entity;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

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
    
    @NotNull(message = "Debe elegir una fecha de entrega")
    @Column(nullable = false)
    protected LocalDate fecha;
    
    @JoinColumn(name = "modulo_id",  updatable = true, unique = false)
    @ManyToOne()
    protected Modulo modulo;

}
