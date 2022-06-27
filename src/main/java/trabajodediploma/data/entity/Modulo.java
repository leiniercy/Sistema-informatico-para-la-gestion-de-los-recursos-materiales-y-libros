package trabajodediploma.data.entity;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;

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
public class Modulo extends AbstractEntity {

    @EqualsAndHashCode.Include
    @ToString.Include

    @Column
    @Max(message = "Máximo 5", value = 5)
    @Min(message = "Mínimo 1", value = 1)
    private Integer anno_academico;

    @ManyToMany
    @JoinTable(
            name = "modulo_recurosMateriales",
            joinColumns = @JoinColumn(name = "modulo_id"),
            inverseJoinColumns = @JoinColumn(name = "material_id"))
    @ElementCollection(fetch = FetchType.EAGER)
    private List<RecursoMaterial> recursosMateriales;

    @OneToOne
    private Asignatura asignatura;

    @ManyToMany(mappedBy = "modulos")
    List<DestinoFinal> destinosFinales;

}
