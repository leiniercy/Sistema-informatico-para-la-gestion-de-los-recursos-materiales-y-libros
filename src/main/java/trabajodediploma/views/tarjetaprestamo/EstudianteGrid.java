/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabajodediploma.views.tarjetaprestamo;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import trabajodediploma.data.entity.Estudiante;
import trabajodediploma.data.service.EstudianteService;
import trabajodediploma.data.service.LibroService;
import trabajodediploma.data.service.TarjetaPrestamoService;

/**
 *
 * @author leinier
 */
public class EstudianteGrid extends Div {

    private Grid<Estudiante> gridEstudiantes = new Grid<>(Estudiante.class, false);

    TarjetaPrestamoEstudianteView tarjetaEstudiante;
    TarjetaPrestamoService prestamoService;
    EstudianteService estudianteService;
    LibroService libroService;

    GridListDataView<Estudiante> gridListDataView;
    Grid.Column<Estudiante> nombreColumn;
    Grid.Column<Estudiante> apellidosColumn;
    Grid.Column<Estudiante> tarjetaColumn;

    private TextField nombreFilter;
    private TextField apellidosFilter;
    private Div content;

    public EstudianteGrid(@Autowired TarjetaPrestamoService prestamoService, @Autowired EstudianteService estudianteService,
            @Autowired LibroService libroService) {
        this.prestamoService = prestamoService;
        this.estudianteService = estudianteService;
        this.libroService = libroService;
        configureGrid();
        content = new Div();
        content.add(gridEstudiantes);

        add(content);

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

    public void editCard(Estudiante e) {
        content.removeAll();
        tarjetaEstudiante = new TarjetaPrestamoEstudianteView(e,prestamoService, estudianteService,libroService);
        tarjetaEstudiante.setWidthFull();
        content.add(tarjetaEstudiante);
    }

}
