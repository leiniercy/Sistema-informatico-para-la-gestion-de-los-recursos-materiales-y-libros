package trabajodediploma.data.entity;

import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
    
    @NotBlank(message = "El campo no puede estar vacío")
    @Column(nullable = false)
    private String codigo;
    
    @NotBlank(message = "El campo no puede estar vacío")
    @Column(nullable = false)
    private String descripcion;
    
    @NotBlank(message = "El campo no puede estar vacío")
    @Column(nullable = false)
    private String unidadMedida;
    
    @NotNull
    @Column(nullable = false)
    @Min(message = "Mínimo 1", value = 1)
    private Integer cantidad;
    
    @ManyToMany(mappedBy = "recursosMateriales", cascade = CascadeType.ALL)
    Set<Modulo> modulos;

}
