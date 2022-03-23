package trabajodediploma.data.entity;

import javax.persistence.Entity;
import trabajodediploma.data.AbstractEntity;

@Entity
public class Trabajador extends AbstractEntity {

    private String nombre;
    private String apellidos;
    private String ci;
    private String solapin;
    private String categoria;
    private String area;

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
    public String getCategoria() {
        return categoria;
    }
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
    public String getArea() {
        return area;
    }
    public void setArea(String area) {
        this.area = area;
    }

}
