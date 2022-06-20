package trabajodediploma.views.destinofinal;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import javax.annotation.security.RolesAllowed;

import trabajodediploma.views.MainLayout;

@PageTitle("Destino Final")
@Route(value = "destino-final-view", layout = MainLayout.class)
@RolesAllowed("ASISTENTE_CONTROL")
public class DestinoFinalView extends Div {

    public DestinoFinalView() {

    }
}
