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
public class RecursoMaterial extends AbstractEntity {

    @EqualsAndHashCode.Include
    @ToString.Include

    @NotBlank(message = "El campo no puede estar vacío")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\u00f1\\u00d1]+(\\s*[a-zA-ZÀ-ÿ0-9\\u00f1\\u00d1]*)*[a-zA-ZÀ-ÿ0-9\\u00f1\\u00d1]+$", message = "El nombre del código es incorrecto, use solo letras y números")
    @Column(nullable = false)
    private String codigo;

    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\u00f1\\u00d1]+(\\s*[a-zA-ZÀ-ÿ\\u00f1\\u00d1]*)*[a-zA-ZÀ-ÿ\\u00f1\\u00d1]+$", message = "El nombre del material es incorrecto, use solo letras")
    @NotBlank(message = "El campo no puede estar vacío")
    @Size(min = 2, max = 255, message = "Debe tener mínimo 2 caracteres")
    @Column(nullable = false)
    private String descripcion;

    @ManyToMany(mappedBy = "recursosMateriales", cascade = CascadeType.ALL)
    Set<Modulo> modulos;
}
