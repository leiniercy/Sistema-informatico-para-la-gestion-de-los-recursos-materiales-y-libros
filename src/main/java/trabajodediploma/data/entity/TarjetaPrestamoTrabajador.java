package trabajodediploma.data.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Entity
public class TarjetaPrestamoTrabajador  extends TarjetaPrestamo{
    
    @JoinColumn(name = "trabajador_id",  updatable = true, unique = false)
    @ManyToOne()
    private Trabajador trabajador;

    public TarjetaPrestamoTrabajador(Libro libro, LocalDate fechaPrestamo,LocalDate fechaDevolucion,Trabajador trabajador){
        super(libro, fechaPrestamo, fechaDevolucion);
        this.trabajador = trabajador;
    }
}
