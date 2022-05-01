package trabajodediploma.data.entity;

import java.time.LocalDate;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
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
    @OneToOne(optional = false)
    private Estudiante estudiante;

    @NotNull(message = "El campo no debe estar vacío")
    @JoinColumn(name = "libro_id", nullable = false, updatable = false)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Libro libro;

    @NotNull(message = "El campo no debe estar vacío")
    @Column(name = "fechaPrestamo")
    private LocalDate fechaPrestamo;
    @Column(name = "fechaDevolucion")
    private LocalDate fechaDevolucion;

//    @OneToOne
//    Trabajador trabajador;
}
