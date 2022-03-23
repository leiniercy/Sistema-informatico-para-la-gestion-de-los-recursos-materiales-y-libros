package trabajodediploma.data.entity;

import javax.persistence.Entity;
import trabajodediploma.data.AbstractEntity;

@Entity
public class Estudiante extends AbstractEntity {

    private String nombre;
    private String apellidos;
    private String ci;
    private String solapin;
    private Integer anno_academico;
    private String facultad;

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getApellidos() {
        return apellidos;
    }
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }
    public String getCi() {
        return ci;
    }
    public void setCi(String ci) {
        this.ci = ci;
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

}
