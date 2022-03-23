package trabajodediploma.data.entity;

import javax.persistence.Entity;
import javax.persistence.Lob;
import trabajodediploma.data.AbstractEntity;

@Entity
public class Libro extends AbstractEntity {

    @Lob
    private String imagen;
    private String titulo;
    private String autor;
    private Integer volumen;
    private Integer tomo;
    private Integer parte;
    private Integer cantidad;
    private Double precio;

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

}
