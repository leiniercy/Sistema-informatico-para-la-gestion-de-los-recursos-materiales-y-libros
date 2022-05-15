package trabajodediploma.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import trabajodediploma.data.AbstractEntity;
import trabajodediploma.data.Rol;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "Users")

public class User extends AbstractEntity {

    @EqualsAndHashCode.Include
    @ToString.Include
    
    @Column()
    private String name;
    
    @Column(unique = true)
    private String username;
    
    @JsonIgnore
    private String hashedPassword;
    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Rol> roles;
    @Lob
    private String profilePictureUrl;

}
