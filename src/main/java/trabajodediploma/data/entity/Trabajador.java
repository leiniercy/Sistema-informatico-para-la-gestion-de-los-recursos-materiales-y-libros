package trabajodediploma.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import trabajodediploma.data.AbstractEntity;

@Entity
public class Trabajador extends AbstractEntity {

    @JsonIgnoreProperties({"trabajador"})
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    @OneToOne(optional = false)
    private User user;

    @Email
    @NotEmpty
    @NotBlank(message = "El correo proporcionado no es correcto")
    @Pattern(regexp = "^([a-zA-Z]+[a-zA-Z0-9_\\.]+)*[a-zA-Z0-9]+(@uci\\.cu)$", message = "Por favor escriba un correo válido")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotEmpty
    @NotBlank(message = "El campo no debe estar vacío")
    @Pattern(regexp = "^[A-Z][0-9]+$", message = "Solapín incorrecto")
    @Size(message = "Mínimo 7 caracteres y máximo 7 ", min = 7, max = 7)
    @Column(name = "solapin", nullable = false, unique = true)
    private String solapin;

    @NotEmpty
    @NotBlank(message = "debe elegir un campo")
    @Column(name = "categoria", nullable = false)
    private String categoria;

    @NotNull(message = "debe elegir un campo de área")
    @JsonIgnoreProperties({"trabajadores"})
    @JoinColumn(name = "area_id", nullable = false, updatable = false)
    @ManyToOne()
    private Area area;

    @OneToMany(mappedBy = "trabajador", cascade = CascadeType.ALL)
    List<DestinoFinalTrabajador> destinoFinal = new LinkedList<>();

    @OneToMany(mappedBy = "trabajador", cascade = CascadeType.ALL)
    List<TarjetaPrestamoTrabajador> tarjetaPrestamo = new LinkedList<>();

    @OneToMany(mappedBy = "trabajador", cascade = CascadeType.ALL)
    List<ModeloPagoTrabajador> modeloPagos = new LinkedList<>();

    public Trabajador() {
    }

    public String getNombreApellidos() {
        return user.getName();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSolapin() {
        return solapin;
    }

    public void setSolapin(String solapin) {
        this.solapin = solapin;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public List<DestinoFinalTrabajador> getDestinoFinal() {
        return destinoFinal;
    }

    public void setDestinoFinal(List<DestinoFinalTrabajador> destinoFinal) {
        this.destinoFinal = destinoFinal;
    }

    public List<TarjetaPrestamoTrabajador> getTarjetaPrestamo() {
        return tarjetaPrestamo;
    }

    public void setTarjetaPrestamo(List<TarjetaPrestamoTrabajador> tarjetaPrestamo) {
        this.tarjetaPrestamo = tarjetaPrestamo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}
