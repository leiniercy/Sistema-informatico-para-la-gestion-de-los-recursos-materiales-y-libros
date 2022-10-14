package trabajodediploma.data.entity;

import java.time.LocalDate;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
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
public class TarjetaPrestamo extends AbstractEntity {

    @EqualsAndHashCode.Include
    @ToString.Include

    @NotNull(message = "El campo no debe estar vacío")
    @JoinColumn(name = "libro_id", nullable = false, updatable = true, unique = true)
    @ManyToOne(optional = false)
    protected Libro libro;

    @NotNull(message = "El campo no debe estar vacío")
    @Column(name = "fechaPrestamo")
    protected LocalDate fechaPrestamo;
    @Column(name = "fechaDevolucion")
    protected LocalDate fechaDevolucion;

}
