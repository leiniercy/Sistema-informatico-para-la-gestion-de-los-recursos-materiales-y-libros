/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabajodediploma.views.modeloPago;

import com.vaadin.flow.component.html.Div;
import javax.annotation.security.RolesAllowed;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import trabajodediploma.data.entity.DestinoFinal;
import trabajodediploma.data.entity.ModeloPago;
import trabajodediploma.data.service.DestinoFinalService;
import trabajodediploma.data.service.EstudianteService;
import trabajodediploma.data.service.LibroService;
import trabajodediploma.data.service.ModeloPagoService;
import trabajodediploma.data.service.ModuloService;
import trabajodediploma.data.service.TrabajadorService;
import trabajodediploma.views.MainLayout;
import trabajodediploma.views.footer.MyFooter;

/**
 *
 * @author leinier
 */

@PageTitle("Modelo de Pago")
@Route(value = "modelo-pago", layout = MainLayout.class)
@RolesAllowed("RESP_ALMACEN")
public class ModeloPagoView extends Div {

    private ModeloPago modelo;
    private ModeloPagoService modeloService;
    private EstudianteService estudianteService;
    private TrabajadorService trabajadorService;
    private LibroService libroService;
    private Div container;
    private Tab estudiante;
    private Tab trabajador;

    ModeloPagoEstudianteView estudianteView;
    ModeloPagoTrabajadorView trabajadorView;
    MyFooter myFooter;

    public ModeloPagoView(
            @Autowired ModeloPagoService modeloService,
            @Autowired EstudianteService estudianteService,
            @Autowired TrabajadorService trabajadorService,
            @Autowired LibroService libroService) {
        addClassNames("modelo_pago_view");
        this.modeloService = modeloService;
        this.estudianteService = estudianteService;
        this.trabajadorService = trabajadorService;
        this.libroService = libroService;

        myFooter = new MyFooter();
        estudianteView= new  ModeloPagoEstudianteView(modeloService, estudianteService, libroService);
        trabajadorView = new ModeloPagoTrabajadorView(modeloService, trabajadorService, libroService);
        container = new Div();
        container.addClassName("container");
        container.add(estudianteView);
        add(MenuBar(), container, myFooter);
    }

    // bara de menu
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
        tabs.addSelectedChangeListener(event -> setContent(event.getSelectedTab()));
        return tabs;
    }

    private void setContent(Tab tab) {
        container.removeAll();
        if (tab.equals(estudiante)) {
            container.add(estudianteView);
        } else if (tab.equals(trabajador)) {
            container.add(trabajadorView);
        }
    }

}
