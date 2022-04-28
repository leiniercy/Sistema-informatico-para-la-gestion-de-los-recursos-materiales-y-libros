/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabajodediploma.views.tarjetaprestamo;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.security.RolesAllowed;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import trabajodediploma.data.entity.Estudiante;
import trabajodediploma.data.entity.Libro;
import trabajodediploma.data.entity.TarjetaPrestamo;
import trabajodediploma.data.service.EstudianteService;
import trabajodediploma.data.service.LibroService;
import trabajodediploma.data.service.TarjetaPrestamoService;
import trabajodediploma.views.MainLayout;

/**
 *
 * @author leinier
 */
public class TarjetaPrestamoEstudianteView extends Div {

    Grid<TarjetaPrestamo> grid = new Grid<>(TarjetaPrestamo.class, false);
    GridListDataView<TarjetaPrestamo> gridListDataView;
    Grid.Column<TarjetaPrestamo> libroColumn;
    Grid.Column<TarjetaPrestamo> fechaEntregaColumn;
    Grid.Column<TarjetaPrestamo> fechaDevolucionColumn;

    Estudiante estudiante;
    List<Libro>libros;
    List<TarjetaPrestamo> prestamos;
    

    private ComboBox<Libro> libroFilter;
    private DatePicker entregaFilter;
    private DatePicker devolucionFilter;

    public TarjetaPrestamoEstudianteView(Estudiante estudiante, List<Libro> libros, List<TarjetaPrestamo> prestamos) {
        addClassName("tarjeta-estudiante");
        this.estudiante = estudiante;
        this.libros = libros;
        this.prestamos = prestamos;
        configureGrid();
        add(grid);
    }

    private void configureGrid() {
        grid.setClassName("tarjeta-estudiante-grid");
        libroColumn = grid.addColumn(tarjeta -> tarjeta.getLibro().getTitulo()).setHeader("Libro").setAutoWidth(true).setSortable(true);
        fechaEntregaColumn = grid.addColumn(new LocalDateRenderer<>(tarjeta -> tarjeta.getFechaPrestamo(),  DateTimeFormatter.ofPattern("dd/MM/yyyy"))).setComparator(tarjeta -> tarjeta.getFechaPrestamo()).setHeader("Fecha de Prestamo").setAutoWidth(true).setSortable(true);
        fechaDevolucionColumn = grid.addColumn(new LocalDateRenderer<>(tarjeta -> tarjeta.getFechaDevolucion(),  DateTimeFormatter.ofPattern("dd/MM/yyyy"))).setComparator(tarjeta -> tarjeta.getFechaDevolucion()).setHeader("Fecha de Devolución").setAutoWidth(true).setSortable(true);
        
        Filters();
        
        HeaderRow headerRow = grid.appendHeaderRow();
        headerRow.getCell(libroColumn).setComponent(libroFilter);
        headerRow.getCell(fechaEntregaColumn).setComponent(entregaFilter);
        headerRow.getCell(fechaDevolucionColumn).setComponent(devolucionFilter);

        gridListDataView = grid.setItems(
                prestamos.parallelStream().filter(e -> e.getEstudiante().equals(estudiante)).collect(Collectors.toList())
        );
        grid.setAllRowsVisible(true);
        grid.setSizeFull();
        grid.setWidthFull();
        grid.setHeightFull();
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);

    }

    private void refreshGrid() {
        grid.setVisible(true);
        grid.setItems(prestamos.parallelStream().filter(e -> e.getEstudiante().equals(estudiante)).collect(Collectors.toList()));
    }

    private void Filters() {
        libroFilter = new ComboBox<>();
        libroFilter.setItems(libros);
        libroFilter.setItemLabelGenerator(Libro::getTitulo);
        libroFilter.setPlaceholder("Filtrar");
        libroFilter.setClearButtonVisible(true);
        libroFilter.setWidth("100%");
        libroFilter.addValueChangeListener(event -> {
            if (libroFilter.getValue() == null) {
                gridListDataView = grid.setItems(prestamos.parallelStream().filter(e -> e.getEstudiante().equals(estudiante)).collect(Collectors.toList()));
            } else {
                gridListDataView.addFilter(tarjeta -> areLibroEqual(tarjeta, libroFilter));
            }
        });

        entregaFilter = new DatePicker();
        entregaFilter.setPlaceholder("Filter");
        entregaFilter.setClearButtonVisible(true);
        entregaFilter.setWidth("100%");
        entregaFilter.addValueChangeListener(event -> {
            if (entregaFilter.getValue() == null) {
                gridListDataView = grid.setItems(prestamos.parallelStream().filter(e -> e.getEstudiante().equals(estudiante)).collect(Collectors.toList()));
            } else {
                gridListDataView.addFilter(tarjeta -> areFechaInicioEqual(tarjeta, entregaFilter));
            }
        });

        devolucionFilter = new DatePicker();
        devolucionFilter.setPlaceholder("Filter");
        devolucionFilter.setClearButtonVisible(true);
        devolucionFilter.setWidth("100%");
        devolucionFilter.addValueChangeListener(event -> {
            if (devolucionFilter.getValue() == null) {
                gridListDataView = grid.setItems(prestamos.parallelStream().filter(e -> e.getEstudiante().equals(estudiante)).collect(Collectors.toList()));
            } else {
                gridListDataView.addFilter(tarjeta -> areFechaFinEqual(tarjeta, devolucionFilter));
            }
        });

    }

    private boolean areLibroEqual(TarjetaPrestamo tarjeta, ComboBox<Libro> libroFilter) {
        String libroFilterValue = libroFilter.getValue().getTitulo();
       if (libroFilterValue != null) {
            return StringUtils.equals(tarjeta.getLibro().getTitulo(), libroFilterValue);
        }
        return true;
    }

    private boolean areFechaInicioEqual(TarjetaPrestamo tarjeta, DatePicker dateFilter) {
        String dateFilterValue = dateFilter.getValue().toString();
        String tareaDate = tarjeta.getFechaPrestamo().toString();
        if (dateFilterValue != null) {
            return StringUtils.equals(dateFilterValue, tareaDate);
        }
        return true;
    }
    
     private boolean areFechaFinEqual(TarjetaPrestamo tarjeta, DatePicker dateFilter) {
        String dateFilterValue = dateFilter.getValue().toString();
        String tareaDate = tarjeta.getFechaDevolucion().toString();
        if (dateFilterValue != null) {
            return StringUtils.equals(dateFilterValue, tareaDate);
        }
        return true;
    }

}
