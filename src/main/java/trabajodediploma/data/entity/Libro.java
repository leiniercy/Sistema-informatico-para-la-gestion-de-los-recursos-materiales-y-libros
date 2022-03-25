package trabajodediploma.data.entity;

import javax.persistence.Entity;
import javax.persistence.Lob;
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
    private String titulo;
    private String autor;
    private Integer volumen;
    private Integer tomo;
    private Integer parte;
    private Integer cantidad;
    private Double precio;

}
