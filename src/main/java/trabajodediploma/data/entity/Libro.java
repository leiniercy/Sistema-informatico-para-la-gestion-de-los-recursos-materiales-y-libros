package trabajodediploma.data.entity;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
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
public class Libro extends AbstractEntity {

    @EqualsAndHashCode.Include
    @ToString.Include

    @Lob
    private String imagen;
    
    @NotBlank(message = "Campo requerido")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\u00f1\\u00d1]+(\\s*[ a-zA-Z0-9 À-ÿ\\u00f1\\u00d1 /#@$¿?!¡()-_,.]*)*[a-zA-Z0-9 À-ÿ\\u00f1\\u00d1 /#@$¿?!¡()-_,. ]+$", message = "El título es incorrecto, solo puede utlizar los siguientes caracteres especiales /#@$¿?!¡()-_,.")
    @Size(min = 2, max = 255, message = "Debe tener mínimo 2 caracteres")
    @Column(nullable=false)
    private String titulo;

    @NotBlank(message = "Campo requerido")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\u00f1\\u00d1]+(\\s*[a-zA-ZÀ-ÿ\\u00f1\\u00d1]*)*[a-zA-ZÀ-ÿ\\u00f1\\u00d1]+$", message = "El nombre del autor es incorrecto, use solo letras")
    @Size(min = 2, max = 255, message = "Debe tener mínimo 2 caracteres")
    @Column(nullable = false)
    private String autor;

//    @Max(message = "Máximo 10", value = 10)
//    @Min(message = "Mínimo 1", value = 1)
    @Column(nullable = true)
    private Integer volumen;

//    @Max(message = "Máximo 10", value = 10)
//    @Min(message = "Mínimo 1", value = 1)
    @Column(nullable = true)
    private Integer tomo;

//    @Max(message = "Máximo 10", value = 10)
//    @Min(message = "Mínimo 1", value = 1)
    @Column(nullable = true)
    private Integer parte;

    @NotNull(message = "Debe elegir una cantidad")
    @Min(message = "Mínimo 1", value = 1)
    @Column(nullable = false)
    private Integer cantidad;

    @NotNull(message = "Debe elegir un precio")
    @Min(message = "Mínimo 0", value = 0)
    @Column(nullable = false)
    private Double precio;
    
    @OneToMany(mappedBy = "libro",cascade = CascadeType.ALL)
    private List<TarjetaPrestamo> tarjetas;

    @ManyToMany(mappedBy = "libros", cascade = CascadeType.ALL)
    private Set<ModeloPago> modelos;

//    public Libro() {
//    }
//
//    public Libro(String imagen, String titulo, String autor, Integer volumen, Integer tomo, Integer parte, Integer cantidad, Double precio) {
//        this.imagen = imagen;
//        this.titulo = titulo;
//        this.autor = autor;
//        this.volumen = volumen;
//        this.tomo = tomo;
//        this.parte = parte;
//        this.cantidad = cantidad;
//        this.precio = precio;
//    }
//
//    public Libro(String imagen, String titulo, String autor, Integer volumen, Integer tomo, Integer parte, Integer cantidad, Double precio, List<TarjetaPrestamo> tarjetas, Set<ModeloPago> modelos) {
//        this.imagen = imagen;
//        this.titulo = titulo;
//        this.autor = autor;
//        this.volumen = volumen;
//        this.tomo = tomo;
//        this.parte = parte;
//        this.cantidad = cantidad;
//        this.precio = precio;
//        this.tarjetas = tarjetas;
//        this.modelos = modelos;
//    }
//
//    public String getImagen() {
//        return imagen;
//    }
//
//    public void setImagen(String imagen) {
//        this.imagen = imagen;
//    }
//
//    public String getTitulo() {
//        return titulo;
//    }
//
//    public void setTitulo(String titulo) {
//        this.titulo = titulo;
//    }
//
//    public String getAutor() {
//        return autor;
//    }
//
//    public void setAutor(String autor) {
//        this.autor = autor;
//    }
//
//    public Integer getVolumen() {
//        return volumen;
//    }
//
//    public void setVolumen(Integer volumen) {
//        this.volumen = volumen;
//    }
//
//    public Integer getTomo() {
//        return tomo;
//    }
//
//    public void setTomo(Integer tomo) {
//        this.tomo = tomo;
//    }
//
//    public Integer getParte() {
//        return parte;
//    }
//
//    public void setParte(Integer parte) {
//        this.parte = parte;
//    }
//
//    public Integer getCantidad() {
//        return cantidad;
//    }
//
//    public void setCantidad(Integer cantidad) {
//        this.cantidad = cantidad;
//    }
//
//    public Double getPrecio() {
//        return precio;
//    }
//
//    public void setPrecio(Double precio) {
//        this.precio = precio;
//    }
//
//    public List<TarjetaPrestamo> getTarjetas() {
//        return tarjetas;
//    }
//
//    public void setTarjetas(List<TarjetaPrestamo> tarjetas) {
//        this.tarjetas = tarjetas;
//    }
//
//    public Set<ModeloPago> getModelos() {
//        return modelos;
//    }
//
//    public void setModelos(Set<ModeloPago> modelos) {
//        this.modelos = modelos;
//    }
//
//    public Integer getId() {
//        return id;
//    }
//
//    public void setId(Integer id) {
//        this.id = id;
//    }
//
//    @Override
//    public int hashCode() {
//        int hash = 7;
//        hash = 79 * hash + Objects.hashCode(this.imagen);
//        hash = 79 * hash + Objects.hashCode(this.titulo);
//        hash = 79 * hash + Objects.hashCode(this.autor);
//        hash = 79 * hash + Objects.hashCode(this.volumen);
//        hash = 79 * hash + Objects.hashCode(this.tomo);
//        hash = 79 * hash + Objects.hashCode(this.parte);
//        hash = 79 * hash + Objects.hashCode(this.cantidad);
//        hash = 79 * hash + Objects.hashCode(this.precio);
//        hash = 79 * hash + Objects.hashCode(this.tarjetas);
//        hash = 79 * hash + Objects.hashCode(this.modelos);
//        return hash;
//    }
//
//    @Override
//    public boolean equals(Object obj) {
//        if (this == obj) {
//            return true;
//        }
//        if (obj == null) {
//            return false;
//        }
//        if (getClass() != obj.getClass()) {
//            return false;
//        }
//        final Libro other = (Libro) obj;
//        if (!Objects.equals(this.imagen, other.imagen)) {
//            return false;
//        }
//        if (!Objects.equals(this.titulo, other.titulo)) {
//            return false;
//        }
//        if (!Objects.equals(this.autor, other.autor)) {
//            return false;
//        }
//        if (!Objects.equals(this.volumen, other.volumen)) {
//            return false;
//        }
//        if (!Objects.equals(this.tomo, other.tomo)) {
//            return false;
//        }
//        if (!Objects.equals(this.parte, other.parte)) {
//            return false;
//        }
//        if (!Objects.equals(this.cantidad, other.cantidad)) {
//            return false;
//        }
//        if (!Objects.equals(this.precio, other.precio)) {
//            return false;
//        }
//        if (!Objects.equals(this.tarjetas, other.tarjetas)) {
//            return false;
//        }
//        if (!Objects.equals(this.modelos, other.modelos)) {
//            return false;
//        }
//        return true;
//    }
//
//    @Override
//    public String toString() {
//        return "Libro{" + "imagen=" + imagen + ", titulo=" + titulo + ", autor=" + autor + ", volumen=" + volumen + ", tomo=" + tomo + ", parte=" + parte + ", cantidad=" + cantidad + ", precio=" + precio + ", tarjetas=" + tarjetas + ", modelos=" + modelos + '}';
//    }
//    
}
