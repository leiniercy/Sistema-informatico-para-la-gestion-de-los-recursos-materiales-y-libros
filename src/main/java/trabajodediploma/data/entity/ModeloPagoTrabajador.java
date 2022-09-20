package trabajodediploma.data.entity;

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

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Entity
public class ModeloPagoTrabajador extends ModeloPago {
    
    
    @EqualsAndHashCode.Include
    @ToString.Include
    
    @NotNull(message = "El campo no debe estar vacío")
    //@NotBlank(message = "Seleccione un trabajador")
    @JoinColumn(name = "trabajador_id",  updatable = true, unique = false)
    @ManyToOne()
    private Trabajador trabajador;

    //Constructor
    public ModeloPagoTrabajador (String imagen, Set<Libro> libros,Trabajador trabajador){
        super(imagen, libros);
        this.trabajador = trabajador; 
    }
}
