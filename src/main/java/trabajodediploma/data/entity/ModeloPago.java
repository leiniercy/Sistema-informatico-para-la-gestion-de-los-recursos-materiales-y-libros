package trabajodediploma.data.entity;

import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.*;

import trabajodediploma.data.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Entity
public class ModeloPago extends AbstractEntity{
    
    @Lob
    private String imagen;

    @NotNull(message = "El campo no debe estar vacío")
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "modeloPago_libros", 
    joinColumns = @JoinColumn(name = "modelo_id", nullable = false), 
    inverseJoinColumns = @JoinColumn(name = "libro_id", unique = false, nullable = false, updatable = true))
    Set<Libro> libros;

}
