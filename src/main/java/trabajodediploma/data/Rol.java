package trabajodediploma.data;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 *
 * @author Leinier
 */

@AllArgsConstructor
@NoArgsConstructor
public enum Rol {
    
    ADMIN("admin"), VD_ADIMN_ECONOMIA("vicedecano"), ASISTENTE_CONTROL("asistente"), RESP_ALMACEN("responsable_almacen"), USER("usuario");

    private String rolname;

    public String getRolname() {
        return rolname;
    }

    public void setRolname(String rolname) {
        this.rolname = rolname;
    }
}
