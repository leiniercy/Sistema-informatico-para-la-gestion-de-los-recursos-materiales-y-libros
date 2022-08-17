package trabajodediploma.views.tarjetaprestamo;


import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import javax.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import trabajodediploma.data.service.EstudianteService;
import trabajodediploma.data.service.LibroService;
import trabajodediploma.data.service.TarjetaPrestamoService;
import trabajodediploma.data.service.TrabajadorService;
import trabajodediploma.views.MainLayout;
import trabajodediploma.views.footer.MyFooter;


@PageTitle("Tarjeta Prestamo")
@Route(value = "tarjeta-prestamo", layout = MainLayout.class)
@RolesAllowed("RESP_ALMACEN")
public class TarjetaPrestamoView extends Div {

    MyFooter myFooter;
    TarjetaPrestamoEstudianteForm form;
    EstudianteGrid estudianteGrid;
    TrabajadorGrid trabajadorGrid;
    private Div container;
    private Tab estudiante;
    private Tab trabajador;

    public TarjetaPrestamoView(
            @Autowired TarjetaPrestamoService prestamoService,
            @Autowired EstudianteService estudianteService,
            @Autowired LibroService libroService,
            @Autowired TrabajadorService trabajadorService
    ) {
        addClassNames("tarjeta-prestamo-view");
        myFooter = new MyFooter();
        estudianteGrid = new EstudianteGrid(prestamoService, estudianteService, libroService);
        trabajadorGrid = new TrabajadorGrid(prestamoService, trabajadorService, libroService);
        container = new Div();
        container.addClassName("container");
        container.add(estudianteGrid);

        add(MenuBar(), container, myFooter);

    }

    //barra de menu
    private Div MenuBar() {
        Div menu = new Div(getSecondaryNavigation());
        menu.addClassName("barra_navegacion");
        return menu;
    }

    private Tabs getSecondaryNavigation() {
        Tabs tabs = new Tabs();
        tabs.addClassName("barra_navegacion__tabs");
        estudiante = new Tab("Estudiante");
        estudiante.addClassName("barra_navegacion__tabs__tab1");
        trabajador = new Tab("Trabajador");
        trabajador.addClassName("barra_navegacion__tabs__tab2");
        tabs.add(estudiante, trabajador);
        tabs.addThemeVariants(TabsVariant.LUMO_MINIMAL);
        tabs.addSelectedChangeListener(event
                -> setContent(event.getSelectedTab())
        );
        return tabs;
    }

    private void setContent(Tab tab) {
        container.removeAll();
        if (tab.equals(estudiante)) {
            container.add(estudianteGrid);
        } else if (tab.equals(trabajador)) {
            container.add(trabajadorGrid);
        }
    }

}
