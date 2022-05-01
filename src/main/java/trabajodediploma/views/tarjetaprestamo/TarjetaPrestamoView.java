package trabajodediploma.views.tarjetaprestamo;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabVariant;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.shared.Registration;
import javax.annotation.security.RolesAllowed;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import trabajodediploma.data.entity.Estudiante;
import trabajodediploma.data.entity.TarjetaPrestamo;
import trabajodediploma.data.service.EstudianteService;
import trabajodediploma.data.service.LibroService;
import trabajodediploma.data.service.TarjetaPrestamoService;
import trabajodediploma.views.MainLayout;
import trabajodediploma.views.footer.MyFooter;
import trabajodediploma.views.inicio.InicioView;
import trabajodediploma.views.libros.LibroView;

@PageTitle("Tarjeta Prestamo")
@Route(value = "tarjeta-prestamo", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class TarjetaPrestamoView extends Div {

    private Grid<Estudiante> gridEstudiantes = new Grid<>(Estudiante.class, false);

    TarjetaPrestamoEstudianteView tarjetaEstudiante;
    TarjetaPrestamoService prestamoService;
    EstudianteService estudianteService;
    LibroService libroService;

    GridListDataView<Estudiante> gridListDataView;
    Grid.Column<Estudiante> nombreColumn;
    Grid.Column<Estudiante> apellidosColumn;
    Grid.Column<Estudiante> tarjetaColumn;

    MyFooter myFooter;

    TarjetaPrestamoEstudianteForm form;

    private Html total;
    private HorizontalLayout toolbar;
    private HorizontalLayout buttons;

    private Div content;
    private Tab estudiante;
    private Tab profesor;
    private Tab tarjeta;

    private TextField nombreFilter;
    private TextField apellidosFilter;

    public TarjetaPrestamoView(
            @Autowired TarjetaPrestamoService prestamoService,
            @Autowired EstudianteService estudianteService,
            @Autowired LibroService libroService
    ) {
        this.prestamoService = prestamoService;
        this.estudianteService = estudianteService;
        this.libroService = libroService;
        addClassNames("tarjeta-prestamo-view");
        configureGrid();
        content = new Div();
        content.add(gridEstudiantes);

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
        tabs.add(estudiante, profesor);
        tabs.addThemeVariants(TabsVariant.LUMO_MINIMAL);
        tabs.addSelectedChangeListener(event
                -> setContent(event.getSelectedTab())
        );
        return tabs;
    }

    private void setContent(Tab tab) {
        content.removeAll();
        if (tab.equals(estudiante)) {
            content.add(gridEstudiantes);
        } else if (tab.equals(profesor)) {
            content.add(new Paragraph("This is the profesor tab"));
        }
    }

    private void configureGrid() {

        gridEstudiantes.setClassName("tarjera-prestamo-grid");
        nombreColumn = gridEstudiantes.addColumn(Estudiante::getNombre).setHeader("Nombre").setAutoWidth(true).setSortable(true);
        apellidosColumn = gridEstudiantes.addColumn(Estudiante::getApellidos).setHeader("Apellidos").setAutoWidth(true).setSortable(true);
        tarjetaColumn = gridEstudiantes.addComponentColumn(event -> {
            Button cardButton = new Button("Tarjeta");
            cardButton.addClickListener(e -> this.editCard(event));
            return cardButton;
        }).setAutoWidth(true);

        Filters();
        HeaderRow headerRow = gridEstudiantes.appendHeaderRow();
        headerRow.getCell(nombreColumn).setComponent(nombreFilter);
        headerRow.getCell(apellidosColumn).setComponent(apellidosFilter);

        gridListDataView = gridEstudiantes.setItems(estudianteService.findAll());
        gridEstudiantes.setAllRowsVisible(true);
        gridEstudiantes.setSizeFull();
        gridEstudiantes.setWidthFull();
        gridEstudiantes.setHeightFull();
        gridEstudiantes.setSelectionMode(Grid.SelectionMode.MULTI);
        gridEstudiantes.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
        gridEstudiantes.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        gridEstudiantes.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
    }

    private void Filters() {
        nombreFilter = new TextField();
        nombreFilter.setPlaceholder("Filtrar");
        nombreFilter.setPrefixComponent(VaadinIcon.SEARCH.create());
        nombreFilter.setClearButtonVisible(true);
        nombreFilter.setWidth("100%");
        nombreFilter.setValueChangeMode(ValueChangeMode.LAZY);
        nombreFilter.addValueChangeListener(
                event -> gridListDataView
                        .addFilter(estudiante -> StringUtils.containsIgnoreCase(estudiante.getNombre(), nombreFilter.getValue()))
        );
        apellidosFilter = new TextField();
        apellidosFilter.setPlaceholder("Filtrar");
        apellidosFilter.setPrefixComponent(VaadinIcon.SEARCH.create());
        apellidosFilter.setClearButtonVisible(true);
        apellidosFilter.setWidth("100%");
        apellidosFilter.setValueChangeMode(ValueChangeMode.LAZY);
        apellidosFilter.addValueChangeListener(
                event -> gridListDataView
                        .addFilter(estudiante -> StringUtils.containsIgnoreCase(estudiante.getApellidos(), apellidosFilter.getValue()))
        );
    }

    private void editCard(Estudiante e) {
        content.removeAll();
        tarjetaEstudiante = new TarjetaPrestamoEstudianteView(e,libroService.findAll(),prestamoService);
        tarjetaEstudiante.setWidthFull();
        content.add(tarjetaEstudiante);
    }

}
