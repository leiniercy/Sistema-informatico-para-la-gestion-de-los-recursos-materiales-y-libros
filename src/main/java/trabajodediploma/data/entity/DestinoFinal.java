package trabajodediploma.data.entity;

import java.time.LocalDate;
import javax.persistence.Entity;
import trabajodediploma.data.AbstractEntity;

@Entity
public class DestinoFinal extends AbstractEntity {

    private LocalDate fecha;
    private Integer cantidad;

    public LocalDate getFecha() {
        return fecha;
    }
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
    public Integer getCantidad() {
        return cantidad;
    }
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

}
