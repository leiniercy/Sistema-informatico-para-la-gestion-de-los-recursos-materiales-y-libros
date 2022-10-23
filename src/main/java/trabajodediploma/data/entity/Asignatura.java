/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabajodediploma.data.entity;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import trabajodediploma.data.AbstractEntity;

/**
 *
 * @author leinier
 */
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Asignatura extends AbstractEntity {

    @NotBlank(message = "Campo requerido")
    @Column(nullable = false)
    private String nombre;
    
    @OneToMany(mappedBy = "asignatura", cascade = CascadeType.ALL)
    private List<Libro> libros;

}
