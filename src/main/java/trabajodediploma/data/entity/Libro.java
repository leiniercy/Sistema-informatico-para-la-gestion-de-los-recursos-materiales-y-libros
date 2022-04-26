package trabajodediploma.data.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
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
    
    @NotBlank(message = "Campo requerido")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\u00f1\\u00d1]+(\\s*[a-zA-ZÀ-ÿ\\u00f1\\u00d1]*)*[a-zA-ZÀ-ÿ\\u00f1\\u00d1]+$", message = "El título es incorrecto, use solo letras")
    @Size(min = 2, max = 255, message = "Debe tener mínimo 2 caracteres")
    @Column(unique = true, nullable=false)
    private String titulo;

    @NotBlank(message = "Campo requerido")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\u00f1\\u00d1]+(\\s*[a-zA-ZÀ-ÿ\\u00f1\\u00d1]*)*[a-zA-ZÀ-ÿ\\u00f1\\u00d1]+$", message = "El nombre del autor es incorrecto, use solo letras")
    @Size(min = 2, max = 255, message = "Debe tener mínimo 2 caracteres")
    @Column(nullable = false)
    private String autor;

    @Max(message = "Máximo 10", value = 10)
    @Min(message = "Mínimo 1", value = 1)
    @Column
    private Integer volumen;

    @Max(message = "Máximo 10", value = 10)
    @Min(message = "Mínimo 1", value = 1)
    @Column
    private Integer tomo;


    @Max(message = "Máximo 10", value = 10)
    @Min(message = "Mínimo 1", value = 1)
    @Column
    private Integer parte;

    @Min(message = "Mínimo 1", value = 1)
    @Column
    private Integer cantidad;

    @Min(message = "Mínimo 0", value = 0)
    @Column
    private Double precio;
    
    @NotNull(message = "debe elegir un campo")
    @JoinColumn(name = "tarjeta_id",nullable = false, updatable = false )
    @ManyToOne(optional = false,fetch = FetchType.EAGER)
    private TarjetaPrestamo tarjeta;

}
