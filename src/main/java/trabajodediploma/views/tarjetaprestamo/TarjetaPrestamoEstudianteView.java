/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabajodediploma.views.tarjetaprestamo;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import trabajodediploma.data.entity.Estudiante;
import trabajodediploma.data.entity.Libro;
import trabajodediploma.data.entity.TarjetaPrestamo;
import trabajodediploma.data.service.TarjetaPrestamoService;
import trabajodediploma.views.footer.MyFooter;

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
    Grid.Column<TarjetaPrestamo> editColumn;

    Estudiante estudiante;
    List<Libro> libros;
    List<TarjetaPrestamo> prestamos;
    TarjetaPrestamoService prestamoService;

    MyFooter myFooter;
    TarjetaPrestamoEstudianteForm form;

    private ComboBox<Libro> libroFilter;
    private DatePicker entregaFilter;
    private DatePicker devolucionFilter;

    private Html total;
    private HorizontalLayout toolbar;
    private HorizontalLayout buttons;

    public TarjetaPrestamoEstudianteView(Estudiante estudiante, List<Libro> libros,@Autowired TarjetaPrestamoService prestamoService) {
        addClassName("tarjeta-estudiante");
        this.estudiante = estudiante;
        this.libros = libros;
        this.prestamoService = prestamoService;
        this.prestamos  = prestamoService.findAll();
        configureGrid();
        configureForm();
        myFooter = new MyFooter();

        add(menuBar(),getContent(), myFooter);
        updateList();
        closeEditor();
    }

    /*Contenido de la vista*/
    private HorizontalLayout getContent() {
        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        content.addClassNames("content");
        content.setSizeFull();
        return content;
    }

    /*Tabla*/
   /*Configuracion de la tabla*/
    private void configureGrid() {
        grid.setClassName("tarjeta-estudiante-grid");
        libroColumn = grid.addColumn(tarjeta -> tarjeta.getLibro().getTitulo()).setHeader("Libro").setAutoWidth(true).setSortable(true);
        fechaEntregaColumn = grid.addColumn(new LocalDateRenderer<>(tarjeta -> tarjeta.getFechaPrestamo(), DateTimeFormatter.ofPattern("dd/MM/yyyy"))).setComparator(tarjeta -> tarjeta.getFechaPrestamo()).setHeader("Fecha de Prestamo").setAutoWidth(true).setSortable(true);
        fechaDevolucionColumn = grid.addColumn(new LocalDateRenderer<>(tarjeta -> tarjeta.getFechaDevolucion(), DateTimeFormatter.ofPattern("dd/MM/yyyy"))).setComparator(tarjeta -> tarjeta.getFechaDevolucion()).setHeader("Fecha de Devolución").setAutoWidth(true).setSortable(true);
        editColumn = grid.addComponentColumn(libro -> {
            Button editButton = new Button(VaadinIcon.EDIT.create());
            editButton.addClickListener(e -> this.editLibro(libro));
            return editButton;
        }).setFlexGrow(0);

        Filtros();

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

    /*Filtros*/
    private void Filtros() {
        
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

    /*Fin-Filtros*/

 /*Barra de menu*/
    private HorizontalLayout menuBar() {
        buttons = new HorizontalLayout();
        Button refreshButton = new Button(VaadinIcon.REFRESH.create());
        refreshButton.addClickListener(click -> refreshGrid());
        Button deleteButton = new Button(VaadinIcon.TRASH.create());
        deleteButton.addClickListener(click -> deleteLibro());
        Button addButton = new Button(VaadinIcon.PLUS.create());
        addButton.addClickListener(click -> addLibro());
        buttons.add(refreshButton, deleteButton, addButton);

        total = new Html("<span>Total: <b>" + prestamos.size() + "</b> libros</span>");

        toolbar = new HorizontalLayout(buttons, total);
        toolbar.addClassName("toolbar");
        toolbar.setAlignItems(FlexComponent.Alignment.CENTER);
        toolbar.setWidth("100%");
        toolbar.setFlexGrow(1, buttons);
        toolbar.getStyle()
                .set("padding", "var(--lumo-space-wide-m)");

        return toolbar;
    }

    private void deleteLibro() {

        try {

            if (grid.asMultiSelect().isEmpty()) {
                Notification notification = Notification.show("Debe elegir al menos un campo", 5000, Notification.Position.MIDDLE);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } else {
                deleteItems(grid.getSelectedItems().size(), grid.getSelectedItems());
                refreshGrid();
                toolbar.remove(total);
                total = new Html("<span>Total: <b>" + libros.size() + "</b> libros</span>");
                toolbar.addComponentAtIndex(1, total);
                toolbar.setFlexGrow(1, buttons);

            }

        } catch (Exception e) {
            e.printStackTrace();
            Notification notification = Notification.show("Ocurrió un problema al intentar eliminar el libro", 5000, Notification.Position.MIDDLE);
            ;
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void deleteItems(int cantidad, Set<TarjetaPrestamo> tarjeta) {
        Notification notification;
        prestamoService.deleteAll(tarjeta);
        if (cantidad == 1) {
            notification = Notification.show("El libro ha sido eliminado", 5000, Notification.Position.BOTTOM_START);
        } else {
            notification = Notification.show("Han sido eliminados" + cantidad + " libros", 5000, Notification.Position.BOTTOM_START);
        }
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void configureForm() {
        form = new TarjetaPrestamoEstudianteForm(estudiante, libros);
        form.setWidth("25em");
        form.addListener(TarjetaPrestamoEstudianteForm.SaveEvent.class, this::saveLibro);
        form.addListener(TarjetaPrestamoEstudianteForm.CloseEvent.class, e -> closeEditor());
    }

    private void saveLibro(TarjetaPrestamoEstudianteForm.SaveEvent event) {

        prestamos = prestamos.parallelStream()
                .filter(lib -> event.getTarjetaPrestamo().getLibro().equals(lib.getLibro())
                && event.getTarjetaPrestamo().getEstudiante().equals(lib.getEstudiante())
                && event.getTarjetaPrestamo().getFechaPrestamo().equals(lib.getFechaPrestamo())
                && event.getTarjetaPrestamo().getFechaDevolucion().equals(lib.getFechaDevolucion())
                )
                .collect(Collectors.toList());

        if (prestamos.size() != 0) {
            Notification notification = Notification.show(
                    "El libro ya existe",
                    5000,
                    Notification.Position.MIDDLE
            );
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        } else {
            if (event.getTarjetaPrestamo().getId() == null) {
                prestamoService.save(event.getTarjetaPrestamo());
                Notification notification = Notification.show(
                        "Libro añadido",
                        5000,
                        Notification.Position.BOTTOM_START
                );
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } else {
                prestamoService.update(event.getTarjetaPrestamo());
                Notification notification = Notification.show(
                        "Libro modificado",
                        5000,
                        Notification.Position.BOTTOM_START
                );
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            }
            toolbar.remove(total);
            total = new Html("<span>Total: <b>" + libros.size() + "</b> libros</span>");
            toolbar.addComponentAtIndex(1, total);
            toolbar.setFlexGrow(1, buttons);
            updateList();
            closeEditor();
        }

    }

    public void editLibro(TarjetaPrestamo tarjeta) {
        if (tarjeta == null) {
            closeEditor();
        } else {
           // tarjeta.setEstudiante(estudiante);
            form.setTarjetaPrestamo(tarjeta);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    void addLibro() {
       grid.asMultiSelect().clear();
       editLibro( new TarjetaPrestamo());
    }

    private void closeEditor() {
        form.setTarjetaPrestamo(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    private void updateList() {
        grid.setItems(prestamoService.findAll().parallelStream().filter(e -> e.getEstudiante().equals(estudiante)).collect(Collectors.toList()));
    }
    /*Fin-Barra de menu*/

}
