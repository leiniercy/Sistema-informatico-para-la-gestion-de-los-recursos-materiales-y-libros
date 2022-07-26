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
import trabajodediploma.data.entity.Trabajador;
import trabajodediploma.data.service.LibroService;
import trabajodediploma.data.service.TarjetaPrestamoService;
import trabajodediploma.data.service.TrabajadorService;

/**
 *
 * @author leinier
 */
public class TabajadorGrid extends Div {

    private Grid<Trabajador> gridTrabajador = new Grid<>(Trabajador.class, false);

    TarjetaPrestamoTrabajadorView tarjetaTrabajador;
    TarjetaPrestamoService prestamoService;
    TrabajadorService trabajadorService;
    LibroService libroService;

    GridListDataView<Trabajador> gridListDataView;
    Grid.Column<Trabajador> nombreColumn;
    Grid.Column<Trabajador> tarjetaColumn;

    private TextField nombreFilter;
    private Div content;

    public TabajadorGrid(@Autowired TarjetaPrestamoService prestamoService, @Autowired TrabajadorService trabajadorService,
            @Autowired LibroService libroService) {
        this.prestamoService = prestamoService;
        this.trabajadorService = trabajadorService;
        this.libroService = libroService;
        configureGrid();
        content = new Div();
        content.add(gridTrabajador);

        add(content);

    }

    private void configureGrid() {

        gridTrabajador.setClassName("tarjera-prestamo-trabajador-grid");
        nombreColumn = gridTrabajador.addColumn(Trabajador::getNombreApellidos).setHeader("Nombre").setAutoWidth(true).setSortable(true);
        tarjetaColumn = gridTrabajador.addComponentColumn(event -> {
            Button cardButton = new Button("Tarjeta");
            cardButton.addClickListener(e -> this.editCard(event));
            return cardButton;
        }).setAutoWidth(true);

        Filters();
        HeaderRow headerRow = gridTrabajador.appendHeaderRow();
        headerRow.getCell(nombreColumn).setComponent(nombreFilter);

        gridListDataView = gridTrabajador.setItems(trabajadorService.findAll());
        gridTrabajador.setAllRowsVisible(true);
        gridTrabajador.setSizeFull();
        gridTrabajador.setWidthFull();
        gridTrabajador.setHeightFull();
        gridTrabajador.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
        gridTrabajador.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        gridTrabajador.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
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
                        .addFilter(trabajador -> StringUtils.containsIgnoreCase(trabajador.getNombreApellidos(), nombreFilter.getValue()))
        );
    }

    public void editCard(Trabajador trabajador) {
        content.removeAll();
        tarjetaTrabajador = new TarjetaPrestamoTrabajadorView(trabajador,prestamoService, trabajadorService,libroService);
        tarjetaTrabajador.setWidthFull();
        content.add(tarjetaTrabajador);
    }

}