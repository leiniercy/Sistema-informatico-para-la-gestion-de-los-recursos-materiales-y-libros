package trabajodediploma.data.entity;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
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

    @NotEmpty
    @NotBlank(message = "El campo no debe estar vacío")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\u00f1\\u00d1]+(\\s*[a-zA-ZÀ-ÿ\\u00f1\\u00d1]*)*[a-zA-ZÀ-ÿ\\u00f1\\u00d1]+$", message = "Datos incorrectos, solo letras") //0 combinaciones de letras 0 o mas veces incluyendo espacios
    @Size(message = "Mínimo 2 caracteres y máximo 100", min = 2, max = 100)
    @Column(name = "nombre", nullable = false)
    private String nombre;

    @NotEmpty
    @NotBlank(message = "El campo no debe estar vacío")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\u00f1\\u00d1]+(\\s*[a-zA-ZÀ-ÿ\\u00f1\\u00d1]*)*[a-zA-ZÀ-ÿ\\u00f1\\u00d1]+$", message = "Datos incorrectos, solo letras") //0 combinaciones de letras 0 o mas veces incluyendo espacios
    @Size(message = "Mínimo 3 caracteres y máximo 100", min = 3, max = 100)
    @Column(name = "apellidos", nullable = false)
    private String apellidos;

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

    @Column
    private String categoria;

    @Column
    private String area;

    @ManyToMany(mappedBy = "trabajadores")
    List<Modulo> modulos;

    public String getNombreApellidos() {
        return nombre + " " + apellidos;
    }

}
