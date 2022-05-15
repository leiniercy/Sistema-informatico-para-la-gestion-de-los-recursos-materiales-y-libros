package trabajodediploma.data.entity;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
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
@Table(name = "Grupos")
public class Grupo extends AbstractEntity {

    @EqualsAndHashCode.Include
    @ToString.Include

    @NotEmpty
    @NotBlank(message = "no puede estar vacio")
    @Pattern(regexp = "^[0-9]+$", message = "Solo numeros")
    @Column(name = "numero", unique = true)
    @Size(message = "solo puede contener 4 numeros ", max = 4, min = 4)
    private String numero;

    @OneToMany(mappedBy = "grupo")
    @ElementCollection(fetch = FetchType.EAGER)
    List<Estudiante> estudiantes;
}
