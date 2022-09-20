package trabajodediploma.data.entity;

import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Entity
public class DestinoFinalTrabajador extends DestinoFinal {

    @EqualsAndHashCode.Include
    @ToString.Include

    @JoinColumn(name = "trabajador_id", updatable = true, unique = false)
    @ManyToOne()
    private Trabajador trabajador;

    public DestinoFinalTrabajador(LocalDate fecha, Modulo modulo, Trabajador trabajador) {
        super(fecha, modulo);
        this.trabajador = trabajador;
    }
}
