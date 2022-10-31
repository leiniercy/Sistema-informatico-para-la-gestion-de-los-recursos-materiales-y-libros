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
    
    ADMIN("admin"), VD_ADIMN_ECONOMIA("vicedecano"), ASISTENTE_CONTROL("asistente"), RESP_ALMACEN("responsable de almacén"), USER("usuario");

    private String rolname;

    public String getRolname() {
        return rolname;
    }

    public void setRolname(String rolname) {
        this.rolname = rolname;
    }

    public static Rol getADMIN() {
        return ADMIN;
    }

    public static Rol getVD_ADIMN_ECONOMIA() {
        return VD_ADIMN_ECONOMIA;
    }

    public static Rol getASISTENTE_CONTROL() {
        return ASISTENTE_CONTROL;
    }

    public static Rol getRESP_ALMACEN() {
        return RESP_ALMACEN;
    }

    public static Rol getUSER() {
        return USER;
    }
    
}
