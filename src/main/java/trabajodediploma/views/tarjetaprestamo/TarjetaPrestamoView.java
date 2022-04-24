package trabajodediploma.views.tarjetaprestamo;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import javax.annotation.security.RolesAllowed;
import trabajodediploma.views.MainLayout;

@PageTitle("Tarjeta Prestamo")
@Route(value = "tarjeta-prestamo", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class TarjetaPrestamoView extends Div {

    private Div content;
    private Tab estudiante;
    private Tab profesor;

    public TarjetaPrestamoView() {
        addClassNames("tarjeta-prestamo-view");
        content = new Div();
        content.add(new Paragraph("This is the Estudiante tab"));
        
        add(MenuBar(), content);

    }

    private Div MenuBar() {
        Div menu = new Div(getSecondaryNavigation());
        menu.addClassName("barraNavegacion");

        return menu;
    }

    private Tabs getSecondaryNavigation() {
        Tabs tabs = new Tabs();
        estudiante = new Tab("Estudiante");
        profesor = new Tab("Profesor");
        tabs.add(estudiante,profesor);
        tabs.addThemeVariants(TabsVariant.LUMO_MINIMAL);
        tabs.addSelectedChangeListener(event
                -> setContent(event.getSelectedTab())
        );
        return tabs;
    }

    private void setContent(Tab tab) {
        content.removeAll();
        if (tab.equals(estudiante)) {
            content.add(new Paragraph("This is the Estudiante tab"));
        } else if (tab.equals(profesor)) {
            content.add(new Paragraph("This is the profesor tab"));
        }
    }

}
