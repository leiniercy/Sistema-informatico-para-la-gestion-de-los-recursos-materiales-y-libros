package trabajodediploma.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
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
public class Estudiante extends AbstractEntity {

    @JsonIgnoreProperties({"estudiante"})
    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    @Email
    @NotEmpty
    @NotBlank(message = "El correo proporcionado no es correcto")
    @Pattern(regexp = "^([a-zA-Z]+[a-zA-Z0-9_\\.]+)*[a-zA-Z0-9]+(@estudiantes\\.uci\\.cu)$", message = "Por favor escriba un correo válido")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotEmpty
    @NotBlank(message = "El campo no debe estar vacío")
    @Pattern(regexp = "^[A-Z][0-9]+$", message = "Solapín incorrecto")
    @Size(message = "Mínimo 7 caracteres y máximo 7 ", min = 7, max = 7)
    @Column(name = "solapin", nullable = false, unique = true)
    private String solapin;

    @NotNull(message = "campo vacío")
    @Column(name = "anno_academico")
    @Max(message = "Máximo 5", value = 5)
    @Min(message = "Mínimo 1", value = 1)
    private Integer anno_academico;

    @Column()
    private String facultad;

//    @NotNull(message = "debe elegir un campo")
    @JsonIgnoreProperties({"estudiantes"})
    @JoinColumn(name = "grupo_id", nullable = false, updatable = false)
    @ManyToOne(optional = false)
    private Grupo grupo;

    @OneToMany(mappedBy = "estudiante", cascade = CascadeType.ALL)
    List<DestinoFinalEstudiante> destinoFinal = new LinkedList<>();

    @OneToMany(mappedBy = "estudiante", cascade = CascadeType.ALL)
    List<TarjetaPrestamoEstudiante> tarjetaPrestamo = new LinkedList<>();

    @OneToMany(mappedBy = "estudiante", cascade = CascadeType.ALL)
    List<ModeloPagoEstudiante> modeloPagos = new LinkedList<>();

    public Estudiante() {
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

    public Integer getAnno_academico() {
        return anno_academico;
    }

    public void setAnno_academico(Integer anno_academico) {
        this.anno_academico = anno_academico;
    }

    public String getFacultad() {
        return facultad;
    }

    public void setFacultad(String facultad) {
        this.facultad = facultad;
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }

    public List<DestinoFinalEstudiante> getDestinoFinal() {
        return destinoFinal;
    }

    public void setDestinoFinal(List<DestinoFinalEstudiante> destinoFinal) {
        this.destinoFinal = destinoFinal;
    }

    public List<TarjetaPrestamoEstudiante> getTarjetaPrestamo() {
        return tarjetaPrestamo;
    }

    public void setTarjetaPrestamo(List<TarjetaPrestamoEstudiante> tarjetaPrestamo) {
        this.tarjetaPrestamo = tarjetaPrestamo;
    }

    public List<ModeloPagoEstudiante> getModeloPagos() {
        return modeloPagos;
    }

    public void setModeloPagos(List<ModeloPagoEstudiante> modeloPagos) {
        this.modeloPagos = modeloPagos;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    
}
