package trabajodediploma.views.tarjetaprestamo;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import javax.annotation.security.RolesAllowed;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import trabajodediploma.data.entity.Estudiante;
import trabajodediploma.data.service.EstudianteService;
import trabajodediploma.data.service.LibroService;
import trabajodediploma.data.service.TarjetaPrestamoService;
import trabajodediploma.data.service.TrabajadorService;
import trabajodediploma.views.MainLayout;
import trabajodediploma.views.footer.MyFooter;


@PageTitle("Tarjeta Prestamo")
@Route(value = "tarjeta-prestamo", layout = MainLayout.class)
@RolesAllowed("RESP_ALMACEN")
public class TarjetaPrestamoView extends VerticalLayout {

    MyFooter myFooter;

    TarjetaPrestamoEstudianteForm form;
    EstudianteGrid estudianteGrid;
    TabajadorGrid trabajadorGrid;

    private Div content;
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
        trabajadorGrid = new TabajadorGrid(prestamoService, trabajadorService, libroService);
        content = new Div();
        content.addClassName("content");
        content.add(estudianteGrid);

        add(MenuBar(), content, myFooter);

    }

    //barra de menu
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
        tabs.addSelectedChangeListener(event
                -> setContent(event.getSelectedTab())
        );
        return tabs;
    }

    private void setContent(Tab tab) {
        content.removeAll();
        if (tab.equals(estudiante)) {
            content.add(estudianteGrid);
        } else if (tab.equals(trabajador)) {
            content.add(trabajadorGrid);
        }
    }

}
