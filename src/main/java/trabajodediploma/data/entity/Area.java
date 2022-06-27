package trabajodediploma.data.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import trabajodediploma.data.AbstractEntity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Entity
public class Area extends AbstractEntity {

    
    @EqualsAndHashCode.Include
    @ToString.Include
    
    @Column
    private String nombre;

    @OneToMany(mappedBy = "area", cascade = CascadeType.ALL)
    @ElementCollection(fetch = FetchType.EAGER)
    private List<Trabajador> trabajadores;

}
