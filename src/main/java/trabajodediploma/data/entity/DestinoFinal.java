package trabajodediploma.data.entity;

import java.time.LocalDate;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

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
public class DestinoFinal extends AbstractEntity {

    @EqualsAndHashCode.Include
    @ToString.Include
    
    @Column
    private LocalDate fecha;
    
    @ManyToOne(fetch = FetchType.EAGER)
    private Modulo modulo;

    @ManyToOne(fetch = FetchType.EAGER)
    private Trabajador trabajador;
    
    @ManyToOne(fetch = FetchType.EAGER)
    private Estudiante estudiante;




}
