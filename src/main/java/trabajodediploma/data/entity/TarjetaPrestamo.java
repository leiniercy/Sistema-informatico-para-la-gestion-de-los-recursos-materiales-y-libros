package trabajodediploma.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
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

@Entity
public class TarjetaPrestamo extends AbstractEntity {

    @NotNull(message = "El campo no debe estar vacío")
    @JoinColumn(name = "libro_id", nullable = false, updatable = true)
    @JsonIgnoreProperties({"tarjetas"})
    @ManyToOne(optional = false)
    protected Libro libro;

    @NotNull(message = "El campo no debe estar vacío")
    @Column(name = "fechaPrestamo",nullable = false)
    protected LocalDate fechaPrestamo;
    @Column(name = "fechaDevolucion" ,nullable = true)
    protected LocalDate fechaDevolucion;

    public TarjetaPrestamo() {
    }

    public TarjetaPrestamo(Libro libro, LocalDate fechaPrestamo, LocalDate fechaDevolucion) {
        this.libro = libro;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucion = fechaDevolucion;
    }

    public Libro getLibro() {
        return libro;
    }

    public void setLibro(Libro libro) {
        this.libro = libro;
    }

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    
}
