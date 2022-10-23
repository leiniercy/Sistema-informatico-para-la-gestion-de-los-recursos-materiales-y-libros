package trabajodediploma.data.entity;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.*;

import trabajodediploma.data.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Entity
public class Libro extends AbstractEntity {

    @EqualsAndHashCode.Include
    @ToString.Include

    @Lob
    private String imagen;

    @Lob
    private String libroDigital;

    @NotBlank(message = "Campo requerido")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\u00f1\\u00d1]+(\\s*[ a-zA-Z0-9 À-ÿ\\u00f1\\u00d1 /#@$¿?!¡()-_,.]*)*[a-zA-Z0-9 À-ÿ\\u00f1\\u00d1 /#@$¿?!¡()-_,. ]+$", message = "El título es incorrecto, solo puede utlizar los siguientes caracteres especiales /#@$¿?!¡()-_,.")
    @Size(min = 2, max = 255, message = "Debe tener mínimo 2 caracteres")
    @Column(nullable = false)
    private String titulo;

    @NotBlank(message = "Campo requerido")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\u00f1\\u00d1]+(\\s*[a-zA-ZÀ-ÿ\\u00f1\\u00d1]*)*[a-zA-ZÀ-ÿ\\u00f1\\u00d1]+$", message = "El nombre del autor es incorrecto, use solo letras")
    @Size(min = 2, max = 255, message = "Debe tener mínimo 2 caracteres")
    @Column(nullable = false)
    private String autor;

    @Column(nullable = true)
    private Integer volumen;

    @Column(nullable = true)
    private Integer tomo;

    @Column(nullable = true)
    private Integer parte;

    @NotNull(message = "Debe elegir una cantidad")
    @Min(message = "Mínimo 1", value = 1)
    @Column(nullable = false)
    private Integer cantidad;

    @NotNull(message = "Debe elegir un precio")
    @Min(message = "Mínimo 0", value = 0)
    @Column(nullable = false)
    private Double precio;

    @NotNull(message = "Debe elegir un año")
    @Max(message = "Máximo 5", value = 5)
    @Min(message = "Mínimo 0", value = 0)
    @Column(nullable = false)
    private int anno_academico;

    @NotNull(message = "El campo no debe estar vacío")
    @JoinColumn(name = "asignatura_id", nullable = false, updatable = true, unique = true)
    @ManyToOne(optional = false)
    protected Asignatura asignatura;

    @OneToMany(mappedBy = "libro", cascade = CascadeType.ALL)
    private List<TarjetaPrestamo> tarjetas;

    @ManyToMany(mappedBy = "libros", cascade = CascadeType.ALL)
    private Set<ModeloPago> modelos;

}
