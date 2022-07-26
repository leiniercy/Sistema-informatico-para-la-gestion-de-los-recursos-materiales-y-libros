package trabajodediploma.data.entity;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
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
public class Trabajador extends AbstractEntity {

    @EqualsAndHashCode.Include
    @ToString.Include

    @OneToOne()
    private User user;

    @Email
    @NotEmpty
    @NotBlank(message = "El correo proporcionado no es correcto")
    @Pattern(regexp = "^([a-zA-Z]+[a-zA-Z0-9_\\.]+)*[a-zA-Z0-9]+(@uci\\.cu)$", message = "Por favor escriba un correo válido")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotEmpty
    @NotBlank(message = "El campo no debe estar vacío")
    @Pattern(regexp = "^[A-Z][0-9]+$", message = "Solapín incorrecto")
    @Size(message = "Mínimo 7 caracteres y máximo 7 ", min = 7, max = 7)
    @Column(name = "solapin", nullable = false, unique = true)
    private String solapin;

    @NotEmpty
    @NotBlank(message = "debe elegir un campo")
    @Column(name = "categoria", nullable = false)
    private String categoria;
    
    @NotNull(message = "debe elegir un campo de área")
    @JoinColumn(name = "area_id",nullable = false, updatable = false)
    @ManyToOne()
    private Area area;
    
    @ManyToMany(mappedBy = "trabajadores")
    List<DestinoFinal> destinoFinal;

    @ManyToMany(mappedBy = "trabajadores")
    List<TarjetaPrestamo> tarjetaPrestamo;

    public String getNombreApellidos() {
        return user.getName();
    }
}
