package trabajodediploma.data.entity;

import javax.persistence.Entity;
import trabajodediploma.data.AbstractEntity;

@Entity
public class RecursoMaterial extends AbstractEntity {

    private String codigo;
    private String descripcion;
    private String unidadMedida;
    private Integer cantidad;

    public String getCodigo() {
        return codigo;
    }
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public String getUnidadMedida() {
        return unidadMedida;
    }
    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }
    public Integer getCantidad() {
        return cantidad;
    }
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

}
