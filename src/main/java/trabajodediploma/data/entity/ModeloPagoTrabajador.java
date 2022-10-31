package trabajodediploma.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity
public class ModeloPagoTrabajador extends ModeloPago {
    
    @NotNull(message = "El campo no debe estar vacío")
    @JsonIgnoreProperties({"modeloPagos"})
    @JoinColumn(name = "trabajador_id",  updatable = true, unique = false)
    @ManyToOne()
    private Trabajador trabajador;

    //Constructor

    public ModeloPagoTrabajador() {
    }
 
    public ModeloPagoTrabajador (String imagen, Set<Libro> libros,Trabajador trabajador){
        super(imagen, libros);
        this.trabajador = trabajador; 
    }

    public Trabajador getTrabajador() {
        return trabajador;
    }

    public void setTrabajador(Trabajador trabajador) {
        this.trabajador = trabajador;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public Set<Libro> getLibros() {
        return libros;
    }

    public void setLibros(Set<Libro> libros) {
        this.libros = libros;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    
}
