package trabajodediploma.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.HashSet;
import java.util.LinkedList;
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

@Entity
public class Libro extends AbstractEntity {

    @Lob
    private String imagen;
    
    @NotBlank(message = "Campo requerido")
    @Pattern(regexp = "^[0-9]+$", message = "El código es incorrecto, solo puede utlizar números.")
    @Size( max = 10, message = "Debe tener máximo 10 caracteres")
    @Column(nullable = false)
    private String codigo;

    @NotBlank(message = "Campo requerido")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\u00f1\\u00d1]+(\\s*[ a-zA-Z0-9 À-ÿ\\u00f1\\u00d1 /#@$¿?!¡()-_,.]*)*[a-zA-Z0-9 À-ÿ\\u00f1\\u00d1 /#@$¿?!¡()-_,. ]+$", message = "El título es incorrecto, solo puede utlizar los siguientes caracteres especiales /#@$¿?!¡()-_,.")
    @Size(min = 2, max = 255, message = "Debe tener mínimo 2 caracteres")
    @Column(nullable = false)
    private String titulo;

    @NotBlank(message = "Campo requerido")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\u00f1\\u00d1]+(\\s*[a-zA-ZÀ-ÿ\\u00f1\\u00d1]*)*[a-zA-ZÀ-ÿ\\u00f1\\u00d1]+$", message = "El nombre del autor es incorrecto, use solo letras")
    @Size(min = 2, max = 255, message = "Debe tener mínimo 2 caracteres")
    @Column(nullable = false)
    private String autor;

    @Column(nullable = true)
    private Integer volumen;

    @Column(nullable = true)
    private Integer tomo;

    @Column(nullable = true)
    private Integer parte;

    @NotNull(message = "Campo requerido")
    @Min(message = "Mínimo 1", value = 1)
    @Column(nullable = false)
    private Integer cantidad;

    @NotNull(message = "Campo requerido")
    @Min(message = "Mínimo 0", value = 0)
    @Column(nullable = false)
    private Double precio;

    @NotNull(message = "Campo requerido")
    @Max(message = "Máximo 5", value = 5)
    @Min(message = "Mínimo 1", value = 1)
    @Column(nullable = false)
    private int anno_academico;

    @NotNull(message = "Campo requerido")
    @JsonIgnoreProperties({"libros"})
    @JoinColumn(name = "asignatura_id", nullable = false, updatable = true)
    @ManyToOne(optional = false)
    private Asignatura asignatura;

    @OneToMany(mappedBy = "libro", cascade = CascadeType.ALL)
    private List<TarjetaPrestamo> tarjetas = new LinkedList<>();

    @ManyToMany(mappedBy = "libros", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<ModeloPago> modelos = new HashSet<>();

    public Libro() {
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public Integer getVolumen() {
        return volumen;
    }

    public void setVolumen(Integer volumen) {
        this.volumen = volumen;
    }

    public Integer getTomo() {
        return tomo;
    }

    public void setTomo(Integer tomo) {
        this.tomo = tomo;
    }

    public Integer getParte() {
        return parte;
    }

    public void setParte(Integer parte) {
        this.parte = parte;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public int getAnno_academico() {
        return anno_academico;
    }

    public void setAnno_academico(int anno_academico) {
        this.anno_academico = anno_academico;
    }

    public Asignatura getAsignatura() {
        return asignatura;
    }

    public void setAsignatura(Asignatura asignatura) {
        this.asignatura = asignatura;
    }

    public List<TarjetaPrestamo> getTarjetas() {
        return tarjetas;
    }

    public void setTarjetas(List<TarjetaPrestamo> tarjetas) {
        this.tarjetas = tarjetas;
    }

    public Set<ModeloPago> getModelos() {
        return modelos;
    }

    public void setModelos(Set<ModeloPago> modelos) {
        this.modelos = modelos;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
    
}
