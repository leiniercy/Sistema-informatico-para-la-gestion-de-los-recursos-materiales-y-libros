package trabajodediploma.views.tarjetaDestinoFinal;

import javax.annotation.security.RolesAllowed;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import trabajodediploma.data.entity.DestinoFinal;
import trabajodediploma.data.service.DestinoFinalService;
import trabajodediploma.data.service.EstudianteService;
import trabajodediploma.data.service.ModuloService;
import trabajodediploma.data.service.TrabajadorService;
import trabajodediploma.views.MainLayout;
import trabajodediploma.views.footer.MyFooter;

@PageTitle("Tarjeta de Destino Final")
@Route(value = "tarjeta-destino-final", layout = MainLayout.class)
@RolesAllowed("ASISTENTE_CONTROL")
public class TarjetaDestinoFinalView extends Div {

    private DestinoFinal tarjeta;
    private DestinoFinalService destinoService;
    private EstudianteService estudianteService;
    private TrabajadorService trabajadorService;
    private ModuloService moduloService;
    private Div content;
    private Tab estudiante;
    private Tab trabajador;

    TarjetaDestinoFinal_EstudianteView estudianteView;
    TarjetaDestinoFinal_TrabajadorView trabajadorView;
    MyFooter myFooter;

    public TarjetaDestinoFinalView(
            @Autowired DestinoFinalService destinoService,
            @Autowired EstudianteService estudianteService,
            @Autowired TrabajadorService trabajadorService,
            @Autowired ModuloService moduloService) {
        addClassNames("tarjeta_destino_final_view");
        this.destinoService = destinoService;
        this.estudianteService = estudianteService;
        this.trabajadorService = trabajadorService;
        this.moduloService = moduloService;
        myFooter = new MyFooter();
        trabajadorView = new TarjetaDestinoFinal_TrabajadorView(moduloService, trabajadorService, destinoService);
        estudianteView = new TarjetaDestinoFinal_EstudianteView(moduloService, estudianteService, destinoService);
        content = new Div();
        content.addClassName("tarjeta_destino_final_view__container");
        content.add(estudianteView);
        add(MenuBar(), content, myFooter);
    }

    // bara de menu
    private Div MenuBar() {
        Div menu = new Div(getSecondaryNavigation());
        menu.addClassName("barraNavegacion");
        return menu;
    }

    private Tabs getSecondaryNavigation() {
        Tabs tabs = new Tabs();
        estudiante = new Tab("Estudiante");
        trabajador = new Tab("Trabajador");
        tabs.add(estudiante, trabajador);
        tabs.addThemeVariants(TabsVariant.LUMO_MINIMAL);
        tabs.addSelectedChangeListener(event -> setContent(event.getSelectedTab()));
        return tabs;
    }

    private void setContent(Tab tab) {
        content.removeAll();
        if (tab.equals(estudiante)) {
            content.add(estudianteView);
        } else if (tab.equals(trabajador)) {
            content.add(trabajadorView);
        }
    }

}
