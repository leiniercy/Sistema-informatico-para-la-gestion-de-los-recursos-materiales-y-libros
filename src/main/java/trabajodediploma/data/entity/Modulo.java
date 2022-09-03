package trabajodediploma.data.entity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
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
public class Modulo extends AbstractEntity {

    @EqualsAndHashCode.Include
    @ToString.Include

    @NotEmpty
    @NotBlank(message = "El campo no debe estar vacío")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ0-9\\u00f1\\u00d1]+(\\s*[a-zA-ZÀ-ÿ0-9\\u00f1\\u00d1]*)*[a-zA-ZÀ-ÿ0-9\\u00f1\\u00d1]+$", message = "Datos incorrectos, solo letras y números") // 0
    @Size(message = "Mínimo 1 caracteres y máximo 100", min = 1, max = 100)
    @Column(name = "nombre", nullable = false)
    private String nombre;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "modulo_recurosMateriales", 
    joinColumns = @JoinColumn(name = "modulo_id", nullable = false), 
    inverseJoinColumns = @JoinColumn(name = "material_id", unique = false, nullable = false, updatable = true))
    private Set<RecursoMaterial> recursosMateriales;

    public void addMaterial(RecursoMaterial material){
        if(this.recursosMateriales == null){
            this.recursosMateriales = new HashSet<>();
        }
        
        this.recursosMateriales.add(material);
    }

    @OneToMany(mappedBy = "modulo", cascade = CascadeType.ALL)
    List<DestinoFinal> destinosFinales;

}
