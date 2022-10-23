package trabajodediploma.data.entity;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
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

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "tarjetaDestino_trabajadores",
            joinColumns = @JoinColumn(name = "tarjetaDestino_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "trabajador_id", unique = false, nullable = false, updatable = true))
    private Set<Trabajador> trabajadores;

    public void addMaterial(Trabajador trabajador) {
        if (this.trabajadores == null) {
            this.trabajadores = new HashSet<>();
        }

        this.trabajadores.add(trabajador);
    }

    public DestinoFinalTrabajador(LocalDate fecha, Modulo modulo, Set<Trabajador> trabajadores) {
        super(fecha, modulo);
        this.trabajadores = trabajadores;
    }
}
