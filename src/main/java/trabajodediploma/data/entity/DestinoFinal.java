package trabajodediploma.data.entity;

import java.time.LocalDate;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
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
    
    @ManyToMany
    @JoinTable(
            name = "destinoFinal_modulo",
            joinColumns = @JoinColumn(name = "modulo_id"),
            inverseJoinColumns = @JoinColumn(name = "destinoF_id"))
    @ElementCollection(fetch = FetchType.EAGER)
    private List<Modulo> modulos;

}
