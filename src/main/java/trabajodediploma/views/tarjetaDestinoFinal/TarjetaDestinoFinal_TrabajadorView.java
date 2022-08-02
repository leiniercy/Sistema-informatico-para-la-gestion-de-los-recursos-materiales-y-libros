package trabajodediploma.views.tarjetaDestinoFinal;

import com.vaadin.flow.component.html.Div;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import trabajodediploma.data.entity.DestinoFinal;
import trabajodediploma.data.entity.Trabajador;
import trabajodediploma.data.entity.Modulo;
import trabajodediploma.data.service.DestinoFinalService;
import trabajodediploma.data.service.TrabajadorService;
import trabajodediploma.data.service.ModuloService;

public class TarjetaDestinoFinal_TrabajadorView extends Div {

    Grid<DestinoFinal> grid = new Grid<>(DestinoFinal.class, false);
    GridListDataView<DestinoFinal> gridListDataView;
    Grid.Column<DestinoFinal> trabajadorColumn;
    Grid.Column<DestinoFinal> moduloColumn;
    Grid.Column<DestinoFinal> fechaEntregaColumn;
    Grid.Column<DestinoFinal> editColumn;
    private List<DestinoFinal> tarjetas;
    private ModuloService moduloService;
    private TrabajadorService trabajadorService;
    private DestinoFinalService destinoService;
    private TarjetaDestinoFinal_TrabajadorFrom form;
    private ComboBox<Trabajador> trabajadorFilter;
    private ComboBox<Modulo> moduloFilter;
    private DatePicker entregaFilter;
    private Dialog dialog;
    private Html total;
    private Div header;
    private HorizontalLayout buttons;
    private HorizontalLayout toolbar;

    public TarjetaDestinoFinal_TrabajadorView(
            ModuloService moduloService,
            TrabajadorService trabajadorService,
            DestinoFinalService destinoService) {
        this.moduloService = moduloService;
        this.trabajadorService = trabajadorService;
        this.destinoService = destinoService;
        this.tarjetas = destinoService.findAll();
        configureGrid();
        configureForm();
        add(menuBar(), getContent());
        updateList();
        closeEditor();

    }

    /* Contenido de la vista */
    /* Contenido de la vista */
    private Div getContent() {
        Div formContent = new Div(form);
        formContent.addClassName("form-content");
        Div gridContent = new Div(grid);
        gridContent.addClassName("grid-content");
        Div content = new Div(gridContent);
        content.addClassName("content");
        content.setSizeFull();
        /* Dialog Header */
        Button closeButton = new Button(new Icon("lumo", "cross"), (e) -> dialog.close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Span title = new Span("Libro");
        Div titleDiv = new Div(title);
        titleDiv.addClassName("div-dialog-title");
        Div buttonDiv = new Div(closeButton);
        buttonDiv.addClassName("div-dialog-button");
        header = new Div(titleDiv, buttonDiv);
        header.addClassName("div-dialog-header");
        /* Dialog Header */
        dialog = new Dialog(header, formContent);
        return content;
    }

    /* Tabla */
    /* Configuracion de la tabla */
    private void configureGrid() {
        grid.setClassName("tarjetaDestinoFinal-Trabajador-grid");

        trabajadorColumn = grid.addColumn(new ComponentRenderer<>(tarjeta -> {
            HorizontalLayout hl = new HorizontalLayout();
            hl.setAlignItems(Alignment.CENTER);
            Image img = new Image(tarjeta.getTrabajador().getUser().getProfilePictureUrl(), "");
            img.setHeight("3.5rem");
            Span span = new Span();
            span.setClassName("name");
            span.setText(tarjeta.getTrabajador().getUser().getName());
            hl.add(img, span);
            return hl;
        })).setHeader("Trabajador").setAutoWidth(true).setSortable(true);

        moduloColumn = grid.addColumn(tarjeta -> tarjeta.getModulo().getRecursosMateriales())
                .setHeader("Modulo")
                .setAutoWidth(true);

        fechaEntregaColumn = grid
                .addColumn(new LocalDateRenderer<>(tarjeta -> tarjeta.getFecha(),
                        DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .setComparator(tarjeta -> tarjeta.getFecha()).setHeader("Fecha de Entrega").setAutoWidth(true)
                .setSortable(true);
        editColumn = grid.addComponentColumn(tarjeta -> {
            Button editButton = new Button(VaadinIcon.EDIT.create());
            editButton.addClickListener(e -> this.editTarjeta(tarjeta));
            return editButton;
        }).setFlexGrow(0);

        Filtros();

        HeaderRow headerRow = grid.appendHeaderRow();
        headerRow.getCell(trabajadorColumn).setComponent(trabajadorFilter);
        headerRow.getCell(moduloColumn).setComponent(moduloFilter);
        headerRow.getCell(fechaEntregaColumn).setComponent(entregaFilter);

        gridListDataView = grid.setItems(

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
        grid.setItems(destinoService.findAll().parallelStream().filter(e -> e.getTrabajador() != null)
                .collect(Collectors.toList()));
    }

    /* Filtros */
    private void Filtros() {

        trabajadorFilter = new ComboBox<>();
        trabajadorFilter.setItems(trabajadorService.findAll());
        trabajadorFilter.setItemLabelGenerator(Trabajador -> Trabajador.getUser().getName());
        trabajadorFilter.setPlaceholder("Filtrar");
        trabajadorFilter.setClearButtonVisible(true);
        trabajadorFilter.setWidth("100%");
        trabajadorFilter.addValueChangeListener(event -> {
            if (trabajadorFilter.getValue() == null) {
                gridListDataView = grid
                        .setItems(destinoService.findAll().parallelStream().filter(e -> e.getTrabajador() != null)
                                .collect(Collectors.toList()));
            } else {
                gridListDataView.addFilter(tarjeta -> areTrabajadorEqual(tarjeta, trabajadorFilter));
            }
        });

        moduloFilter = new ComboBox<>();
        moduloFilter.setItems(moduloService.findAll());
        moduloFilter.setItemLabelGenerator(Modulo::getNombre);
        moduloFilter.setPlaceholder("Filtrar");
        moduloFilter.setClearButtonVisible(true);
        moduloFilter.setWidth("100%");
        moduloFilter.addValueChangeListener(event -> {
            if (moduloFilter.getValue() == null) {
                gridListDataView = grid
                        .setItems(destinoService.findAll().parallelStream().filter(e -> e.getTrabajador() != null)
                                .collect(Collectors.toList()));
            } else {
                gridListDataView.addFilter(des -> StringUtils.containsIgnoreCase(des.getModulo().getNombre(),
                        moduloFilter.getValue().getNombre()));
            }
        });

        entregaFilter = new DatePicker();
        entregaFilter.setPlaceholder("Filter");
        entregaFilter.setClearButtonVisible(true);
        entregaFilter.setWidth("100%");
        entregaFilter.addValueChangeListener(event -> {
            if (entregaFilter.getValue() == null) {
                gridListDataView = grid
                        .setItems(destinoService.findAll().parallelStream().filter(e -> e.getTrabajador() != null)
                                .collect(Collectors.toList()));
            } else {
                gridListDataView.addFilter(tarjeta -> areFechaInicioEqual(tarjeta, entregaFilter));
            }
        });

    }

    private boolean areTrabajadorEqual(DestinoFinal tarjeta, ComboBox<Trabajador> trabajadorFilter) {
        String trabajadorFilterValue = trabajadorFilter.getValue().getUser().getName();
        if (trabajadorFilterValue != null) {
            return StringUtils.equals(tarjeta.getTrabajador().getUser().getName(), trabajadorFilterValue);
        }
        return true;
    }

    private boolean areFechaInicioEqual(DestinoFinal tarjeta, DatePicker dateFilter) {
        String dateFilterValue = dateFilter.getValue().toString();
        String tareaDate = tarjeta.getFecha().toString();
        if (dateFilterValue != null) {
            return StringUtils.equals(dateFilterValue, tareaDate);
        }
        return true;
    }
    /* Fin-Filtros */

    /* Barra de menu */
    private HorizontalLayout menuBar() {
        buttons = new HorizontalLayout();
        Button refreshButton = new Button(VaadinIcon.REFRESH.create(), click -> refreshGrid());
        refreshButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        Button deleteButton = new Button(VaadinIcon.TRASH.create(), click -> deleteTarjeta());
        deleteButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        Button addButton = new Button(VaadinIcon.PLUS.create(), click -> addTarjeta());
        addButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        buttons.add(refreshButton, deleteButton, addButton);

        total = new Html("<span>Total: <b>"
        + destinoService.findAll().parallelStream().filter(e -> e.getTrabajador() != null)
                .collect(Collectors.toList()).size()
        + "</b></span>");

        toolbar = new HorizontalLayout(buttons,total);
        toolbar.addClassName("toolbar");
        toolbar.setAlignItems(FlexComponent.Alignment.CENTER);
        toolbar.setWidth("100%");
        toolbar.setFlexGrow(1, buttons);
        toolbar.getStyle()
                .set("padding", "var(--lumo-space-wide-m)");

        return toolbar;
    }

    private void deleteTarjeta() {

        try {

            if (grid.asMultiSelect().isEmpty()) {
                Notification notification = Notification.show("Debe elegir al menos un campo", 5000,
                        Notification.Position.MIDDLE);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } else {
                deleteItems(grid.getSelectedItems().size(), grid.getSelectedItems());
                refreshGrid();
                toolbar.remove(total);
                total = new Html("<span>Total: <b>"
                        + destinoService.findAll().parallelStream().filter(e -> e.getTrabajador() != null)
                                .collect(Collectors.toList()).size()
                        + "</b></span>");
                toolbar.addComponentAtIndex(1, total);
                toolbar.setFlexGrow(1, buttons);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Notification notification = Notification.show("Ocurrió un problema al intentar eliminar el Trabajador",
                    5000,
                    Notification.Position.MIDDLE);
            ;
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void deleteItems(int cantidad, Set<DestinoFinal> tarjeta) {
        Notification notification;
        destinoService.deleteAll(tarjeta);
        if (cantidad == 1) {
            notification = Notification.show("Tarjeata ha eliminada", 5000, Notification.Position.BOTTOM_START);
        } else {
            notification = Notification.show("Han sido eliminados" + cantidad + " tarjetas", 5000,
                    Notification.Position.BOTTOM_START);
        }
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    // Configuracion del Formulario
    private void configureForm() {
        form = new TarjetaDestinoFinal_TrabajadorFrom(trabajadorService.findAll(), moduloService.findAll());
        form.setWidth("25em");
        form.addListener(TarjetaDestinoFinal_TrabajadorFrom.SaveEvent.class,
                this::saveTarjeta);
        form.addListener(TarjetaDestinoFinal_TrabajadorFrom.CloseEvent.class, e -> closeEditor());
    }

    private void saveTarjeta(TarjetaDestinoFinal_TrabajadorFrom.SaveEvent event) {

        tarjetas = tarjetas.parallelStream()
                .filter(tar -> event.getDestinoFinal().getTrabajador().equals(tar.getTrabajador())
                        && event.getDestinoFinal().getModulo().equals(tar.getModulo())
                        && event.getDestinoFinal().getFecha().equals(tar.getFecha()))
                .collect(Collectors.toList());

        if (tarjetas.size() != 0) {
            Notification notification = Notification.show(
                    "La tarjeta ya existe",
                    5000,
                    Notification.Position.MIDDLE);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        } else {
            if (event.getDestinoFinal().getId() == null) {
                destinoService.save(event.getDestinoFinal());
                Notification notification = Notification.show(
                        "Tarjeta añadida",
                        5000,
                        Notification.Position.BOTTOM_START);
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } else {
                destinoService.update(event.getDestinoFinal());
                Notification notification = Notification.show(
                        "Tarjeta modificada",
                        5000,
                        Notification.Position.BOTTOM_START);
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            }
            toolbar.remove(total);
                total = new Html("<span>Total: <b>"
                        + destinoService.findAll().parallelStream().filter(e -> e.getTrabajador() != null)
                                .collect(Collectors.toList()).size()
                        + "</b></span>");
                toolbar.addComponentAtIndex(1, total);
                toolbar.setFlexGrow(1, buttons);
            updateList();
            closeEditor();
        }

    }

    void editTarjeta(DestinoFinal tarjeta) {
        if (tarjeta == null) {
            closeEditor();
        } else {
            form.setDestinoFinal(tarjeta);
            form.setVisible(true);
            addClassName("editing");
            dialog.open();
        }
    }

    void addTarjeta() {
        grid.asMultiSelect().clear();
        editTarjeta(new DestinoFinal());
    }

    private void closeEditor() {
        form.setDestinoFinal(null);
        form.setVisible(false);
        removeClassName("editing");
        dialog.close();
    }

    private void updateList() {
        grid.setItems(destinoService.findAll().parallelStream().filter(e -> e.getTrabajador() != null)
                .collect(Collectors.toList()));
    }
    /* Fin-Barra de tarjetas */
}
