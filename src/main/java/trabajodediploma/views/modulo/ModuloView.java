package trabajodediploma.views.modulo;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import javax.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import trabajodediploma.data.service.ModuloService;
import trabajodediploma.views.MainLayout;

@PageTitle("Módulo")
@Route(value = "modulo-view", layout = MainLayout.class)
@RolesAllowed("ASISTENTE_CONTROL")
public class ModuloView extends Div {

    public ModuloView(@Autowired ModuloService moduloService) {
    }
}
