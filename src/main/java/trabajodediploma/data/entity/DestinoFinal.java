package trabajodediploma.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import trabajodediploma.data.AbstractEntity;

@Entity
public class DestinoFinal extends AbstractEntity {

    @NotNull(message = "Debe elegir una fecha de entrega")
    @Column(nullable = false)
    protected LocalDate fecha;

    @JsonIgnoreProperties({"destinosFinales"})
    @JoinColumn(name = "modulo_id", updatable = true, unique = false)
    @ManyToOne()
    protected Modulo modulo;

    public DestinoFinal() {
    }

    public DestinoFinal(LocalDate fecha, Modulo modulo) {
        this.fecha = fecha;
        this.modulo = modulo;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Modulo getModulo() {
        return modulo;
    }

    public void setModulo(Modulo modulo) {
        this.modulo = modulo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}
