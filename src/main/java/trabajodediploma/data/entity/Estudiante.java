package trabajodediploma.data.entity;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
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
public class Estudiante extends AbstractEntity {

    @EqualsAndHashCode.Include
    @ToString.Include

    @OneToOne()
    private User user;

    
    @Email
    @NotEmpty
    @NotBlank(message = "El correo proporcionado no es correcto")
    @Pattern(regexp = "^([a-zA-Z]+[a-zA-Z0-9_\\.]+)*[a-zA-Z0-9]+(@estudiantes\\.uci\\.cu)$", message = "Por favor escriba un correo válido")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotEmpty
    @NotBlank(message = "El campo no debe estar vacío")
    @Pattern(regexp = "^[A-Z][0-9]+$", message = "Solapín incorrecto")
    @Size(message = "Mínimo 7 caracteres y máximo 7 ", min = 7, max = 7)
    @Column(name = "solapin", nullable = false, unique = true)
    private String solapin;

    @NotNull(message = "campo vacío")
    @Column(name = "anno_academico")
    @Max(message = "Máximo 5", value = 5)
    @Min(message = "Mínimo 1", value = 1)
    private Integer anno_academico;

    @Column()
    private String facultad;
    
    @NotNull(message = "debe elegir un campo")
    @JoinColumn(name = "grupo_id",nullable = false, updatable = false )
    @ManyToOne(optional = false,fetch = FetchType.EAGER)
    private Grupo grupo;

    @OneToMany(mappedBy = "estudiante")
    List<DestinoFinal> destinoFinal;

    @OneToMany(mappedBy = "estudiante")
    List<TarjetaPrestamo> tarjetaPrestamo;
    
    public String getNombreApellidos() {
        return user.getName();
    }

}
