package trabajodediploma.data.entity;

import javax.persistence.Entity;
import trabajodediploma.data.AbstractEntity;

@Entity
public class Modulo extends AbstractEntity {

    private Integer anno_academico;

    public Integer getAnno_academico() {
        return anno_academico;
    }
    public void setAnno_academico(Integer anno_academico) {
        this.anno_academico = anno_academico;
    }

}
