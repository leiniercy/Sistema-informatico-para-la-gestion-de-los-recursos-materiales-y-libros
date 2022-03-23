package trabajodediploma.data.entity;

import javax.persistence.Entity;
import javax.persistence.Lob;
import trabajodediploma.data.AbstractEntity;

@Entity
public class Libro extends AbstractEntity {

    @Lob
    private String image;
    private String titulo;
    private String autor;
    private Integer volumen;
    private Integer tomo;
    private Integer parte;
    private Integer cantidad;
    private Integer precio;

    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
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
    public Integer getPrecio() {
        return precio;
    }
    public void setPrecio(Integer precio) {
        this.precio = precio;
    }

}
