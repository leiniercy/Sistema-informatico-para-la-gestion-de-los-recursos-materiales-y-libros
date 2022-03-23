package trabajodediploma.data.entity;

import java.time.LocalDate;
import javax.persistence.Entity;
import trabajodediploma.data.AbstractEntity;

@Entity
public class TarjetaPrestamoEstudiante extends AbstractEntity {

    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucion;

    public LocalDate getFechaPrestamo() {
        return fechaPrestamo;
    }
    public void setFechaPrestamo(LocalDate fechaPrestamo) {
        this.fechaPrestamo = fechaPrestamo;
    }
    public LocalDate getFechaDevolucion() {
        return fechaDevolucion;
    }
    public void setFechaDevolucion(LocalDate fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

}
