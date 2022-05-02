/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabajodediploma.views.tarjetaprestamo;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import trabajodediploma.data.service.LibroService;
import trabajodediploma.data.service.TarjetaPrestamoService;
import trabajodediploma.data.service.TrabajadorService;
import trabajodediploma.data.entity.Trabajador;
import trabajodediploma.data.entity.Libro;
import trabajodediploma.data.entity.TarjetaPrestamo;

/**
 *
 * @author leinier
 */
public class TarjetaPrestamoTrabajadorView extends Div {

    Grid<TarjetaPrestamo> grid = new Grid<>(TarjetaPrestamo.class, false);
    GridListDataView<TarjetaPrestamo> gridListDataView;
    Grid.Column<TarjetaPrestamo> libroColumn;
    Grid.Column<TarjetaPrestamo> fechaEntregaColumn;
    Grid.Column<TarjetaPrestamo> fechaDevolucionColumn;
    Grid.Column<TarjetaPrestamo> editColumn;

    TabajadorGrid trabajadorGrid;
    Trabajador trabajador;
    List<Libro> libros;
    List<TarjetaPrestamo> prestamos;
    TarjetaPrestamoService prestamoService;
    TrabajadorService trabajadorService;
    LibroService libroService;

    TarjetaPrestamoTrabajadorForm form;

    private ComboBox<Libro> libroFilter;
    private DatePicker entregaFilter;
    private DatePicker devolucionFilter;

    private Html total;
    private HorizontalLayout info;
    private Div content;

    public TarjetaPrestamoTrabajadorView(
            Trabajador trabajador,
            @Autowired TarjetaPrestamoService prestamoService,
            @Autowired TrabajadorService trabajadorService,
            @Autowired LibroService libroService) {

        addClassName("tarjeta-estudiante");
        this.trabajador = trabajador;
        this.prestamoService = prestamoService;
        this.trabajadorService = trabajadorService;
        this.libroService = libroService;
        this.libros = libroService.findAll();
        this.prestamos = prestamoService.findAll();

        configureGrid();
        configureForm();
        content = new Div(getGridContent());

        add(content);
        updateList();
        closeEditor();
    }

    /*Contenido de la vista*/
    private HorizontalLayout getContent() {
        HorizontalLayout contenedor = new HorizontalLayout(grid, form);
        contenedor.setFlexGrow(2, grid);
        contenedor.setFlexGrow(1, form);
        contenedor.addClassNames("content");
        contenedor.setSizeFull();
        return contenedor;
    }

    private HorizontalLayout informacionTabla() {
        info = new HorizontalLayout();
        total = new Html("<span>Total: <b>" + prestamos.size() + "</b> libros</span>");
        info.add(total);
        return info;
    }

    private VerticalLayout getGridContent() {
        VerticalLayout nuevo = new VerticalLayout();
        nuevo.add(menuBar(), getContent(), informacionTabla());
        return nuevo;
    }

    /*Tabla*/
 /*Configuracion de la tabla*/
    private void configureGrid() {
        grid.setClassName("tarjeta-estudiante-grid");
        libroColumn = grid.addColumn(new ComponentRenderer<>(tarjeta -> {
            HorizontalLayout hl = new HorizontalLayout();
            hl.setAlignItems(FlexComponent.Alignment.CENTER);
            Image img = new Image(tarjeta.getLibro().getImagen(), "");
            img.setHeight("3.5rem");
            Span span = new Span();
            span.setClassName("name");
            span.setText(tarjeta.getLibro().getTitulo());
            hl.add(img, span);
            return hl;
        })).setHeader("Libro").setAutoWidth(true).setSortable(true);

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
                prestamos.parallelStream().filter(event -> event.getTrabajador().equals(trabajador)).collect(Collectors.toList())
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
        grid.setItems(prestamos.parallelStream().filter(event -> event.getTrabajador().equals(trabajador)).collect(Collectors.toList()));
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
                gridListDataView = grid.setItems(prestamos.parallelStream().filter(e -> e.getTrabajador().equals(trabajador)).collect(Collectors.toList()));
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
                gridListDataView = grid.setItems(prestamos.parallelStream().filter(e -> e.getTrabajador().equals(trabajador)).collect(Collectors.toList()));
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
                gridListDataView = grid.setItems(prestamos.parallelStream().filter(e -> e.getTrabajador().equals(trabajador)).collect(Collectors.toList()));
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
        HorizontalLayout buttons = new HorizontalLayout();
        Button refreshButton = new Button(VaadinIcon.REFRESH.create(), click -> refreshGrid());
        refreshButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        Button deleteButton = new Button(VaadinIcon.TRASH.create(), click -> deleteLibro());
        deleteButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        Button addButton = new Button(VaadinIcon.PLUS.create(), click -> addLibro());
        addButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        buttons.add(refreshButton, deleteButton, addButton);

        Button salirButton = new Button(trabajador.getNombreApellidos(), new Icon(VaadinIcon.ARROW_RIGHT), click -> volverAtras());
        salirButton.setIconAfterText(true);
        salirButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        HorizontalLayout personalInfo = new HorizontalLayout(salirButton);
        personalInfo.setAlignItems(FlexComponent.Alignment.CENTER);

        HorizontalLayout toolbar = new HorizontalLayout(buttons, personalInfo);
        toolbar.addClassName("toolbar");
        toolbar.setAlignItems(FlexComponent.Alignment.CENTER);
        toolbar.setWidth("100%");
        toolbar.setFlexGrow(1, buttons);
        toolbar.getStyle()
                .set("padding", "var(--lumo-space-wide-m)");

        return toolbar;
    }

    public void volverAtras() {
        trabajadorGrid = new TabajadorGrid(prestamoService, trabajadorService, libroService);
        content.removeAll();
        content.add(trabajadorGrid);
    }

    private void deleteLibro() {

        try {

            if (grid.asMultiSelect().isEmpty()) {
                Notification notification = Notification.show("Debe elegir al menos un campo", 5000, Notification.Position.MIDDLE);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } else {
                deleteItems(grid.getSelectedItems().size(), grid.getSelectedItems());
                refreshGrid();
                info.remove(total);
                total = new Html("<span>Total: <b>" + libros.size() + "</b> libros</span>");
                info.add(total);
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
        form = new TarjetaPrestamoTrabajadorForm(trabajador, libros);
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
            info.remove(total);
            total = new Html("<span>Total: <b>" + libros.size() + "</b> libros</span>");
            info.add(total);
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
        editLibro(new TarjetaPrestamo());
    }

    private void closeEditor() {
        form.setTarjetaPrestamo(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    private void updateList() {
        grid.setItems(prestamoService.findAll().parallelStream().filter(e -> e.getTrabajador().equals(trabajador)).collect(Collectors.toList()));
    }
    /*Fin-Barra de menu*/

}
