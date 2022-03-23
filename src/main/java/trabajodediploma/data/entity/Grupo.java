package trabajodediploma.data.entity;

import javax.persistence.Entity;
import trabajodediploma.data.AbstractEntity;

@Entity
public class Grupo extends AbstractEntity {

    private String numero;

    public String getNumero() {
        return numero;
    }
    public void setNumero(String numero) {
        this.numero = numero;
    }

}
