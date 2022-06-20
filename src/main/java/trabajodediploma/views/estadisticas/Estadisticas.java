/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabajodediploma.views.estadisticas;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import javax.annotation.security.RolesAllowed;
import trabajodediploma.views.MainLayout;

/**
 *
 * @author leinier
 */
@PageTitle("Estadisticas")
@Route(value = "estadisticas-view", layout = MainLayout.class)
@RolesAllowed("RESP_ALMACEN")
public class Estadisticas {

    public Estadisticas() {
    }
    
}
