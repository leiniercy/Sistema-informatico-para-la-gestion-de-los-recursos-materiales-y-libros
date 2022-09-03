package trabajodediploma.views.tarjetaDestinoFinal;

import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import trabajodediploma.data.entity.DestinoFinal;
import trabajodediploma.data.entity.DestinoFinalTrabajador;
import trabajodediploma.data.entity.Trabajador;
import trabajodediploma.data.entity.Modulo;
import trabajodediploma.data.entity.RecursoMaterial;
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
    private DestinoFinalTrabajador tarjetaTrabajador;
    private TarjetaDestinoFinal_TrabajadorFrom form;
    private ComboBox<Trabajador> trabajadorFilter;
    private ComboBox<Modulo> moduloFilter;
    private DatePicker entregaFilter;
    private Dialog dialog;
    private Html total;
    private HorizontalLayout toolbar;
    private HorizontalLayout buttons;
    private Div header;

    public TarjetaDestinoFinal_TrabajadorView(
            ModuloService moduloService,
            TrabajadorService trabajadorService,
            DestinoFinalService destinoService) {
        addClassName("tarjeta_trabajador");       
        this.moduloService = moduloService;
        this.trabajadorService = trabajadorService;
        this.destinoService = destinoService;
        tarjetas = new LinkedList<>();
        updateList();
        configureGrid();
        configureForm();
        add(menuBar(), getContent());
    }

    /* Contenido de la vista */
    private Div getContent() {
        Div formContent = new Div(form);
        formContent.addClassName("form-content");
        Div gridContent = new Div(grid);
        gridContent.addClassName("tarjeta_trabajador__content__grid-content");
        Div content = new Div(gridContent);
        content.addClassName("tarjeta_trabajador__content");
        content.setSizeFull();
        /* Dialog Header */
        Button closeButton = new Button(new Icon("lumo", "cross"), (e) -> dialog.close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Span title = new Span("Libro");
        Div titleDiv = new Div(title);
        titleDiv.addClassName("div_dialog_title");
        Div buttonDiv = new Div(closeButton);
        buttonDiv.addClassName("div_dialog_button");
        header = new Div(titleDiv, buttonDiv);
        header.addClassName("div_dialog_header");
        /* Dialog Header */
        dialog = new Dialog(header, formContent);
        return content;
    }

    /* Tabla */
    /* Configuracion de la tabla */
    private void configureGrid() {
        grid.setClassName("tarjeta_trabajador__content__grid-content__table");

        trabajadorColumn = grid.addColumn(new ComponentRenderer<>(tarjeta -> {
            tarjetaTrabajador = (DestinoFinalTrabajador) tarjeta;
            HorizontalLayout hl = new HorizontalLayout();
            hl.setAlignItems(Alignment.CENTER);
            Avatar avatar = new Avatar(tarjetaTrabajador.getTrabajador().getUser().getName(), tarjetaTrabajador.getTrabajador().getUser().getProfilePictureUrl());
            Span span = new Span();
            span.setClassName("name");
            span.setText(tarjetaTrabajador.getTrabajador().getUser().getName());
            hl.add(avatar, span);
            return hl;
        })).setHeader("Trabajador").setFrozen(true).setAutoWidth(true).setSortable(true);

        moduloColumn = grid.addColumn(new ComponentRenderer<>(tarjeta -> {
            VerticalLayout layout = new VerticalLayout();
            Label nombreModulo = new Label(tarjeta.getModulo().getNombre());
            Span span_materiales = new Span();
            span_materiales.setWidth("100%");
            List<RecursoMaterial> materiales = new LinkedList<>(tarjeta.getModulo().getRecursosMateriales());
            String listMateriales = new String();
            if (materiales.size() != 0) {
                listMateriales += "" + materiales.get(0);
                for (int i = 1; i < materiales.size(); i++) {
                    listMateriales += ", " + materiales.get(i);
                }
            }
            span_materiales.setText(listMateriales);
            layout.add(nombreModulo, span_materiales);
            layout.setWidth("100%");
            return layout;
        })).setHeader("Modulo").setAutoWidth(true);

        fechaEntregaColumn = grid
                .addColumn(new LocalDateRenderer<>(tarjeta -> tarjeta.getFecha(),
                        DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .setComparator(tarjeta -> tarjeta.getFecha()).setHeader("Fecha de Entrega").setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.CENTER)
                .setSortable(true);
        editColumn = grid.addComponentColumn(tarjeta -> {
            tarjetaTrabajador = (DestinoFinalTrabajador) tarjeta;
            Button editButton = new Button(VaadinIcon.EDIT.create());
            editButton.addClickListener(e -> this.editTarjeta(tarjetaTrabajador));
            editButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            return editButton;
        }).setTextAlign(ColumnTextAlign.CENTER).setFrozen(true).setFlexGrow(0);

        Filtros();

        HeaderRow headerRow = grid.appendHeaderRow();
        headerRow.getCell(trabajadorColumn).setComponent(trabajadorFilter);
        headerRow.getCell(moduloColumn).setComponent(moduloFilter);
        headerRow.getCell(fechaEntregaColumn).setComponent(entregaFilter);

        gridListDataView = grid.setItems(tarjetas);
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
        tarjetas.clear();
        destinoService.findAll().parallelStream().forEach((target) -> {
            if (target instanceof DestinoFinalTrabajador) {
                tarjetaTrabajador = (DestinoFinalTrabajador) target;
                if (tarjetaTrabajador.getTrabajador() != null) {
                    tarjetas.add(target);
                }
            }
        });
        grid.setItems(tarjetas);
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
                gridListDataView = grid.setItems(tarjetas);
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
                gridListDataView = grid.setItems(tarjetas);
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
                gridListDataView = grid.setItems(tarjetas);
            } else {
                gridListDataView.addFilter(tarjeta -> areFechaInicioEqual(tarjeta, entregaFilter));
            }
        });

    }

    private boolean areTrabajadorEqual(DestinoFinal tarjeta, ComboBox<Trabajador> trabajadorFilter) {
        String trabajadorFilterValue = trabajadorFilter.getValue().getUser().getName();
        tarjetaTrabajador = (DestinoFinalTrabajador) tarjeta;
        if (trabajadorFilterValue != null) {
            return StringUtils.equals(tarjetaTrabajador.getTrabajador().getUser().getName(), trabajadorFilterValue);
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
        refreshButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button deleteButton = new Button(VaadinIcon.TRASH.create(), click -> deleteTarjeta());
        deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button addButton = new Button(VaadinIcon.PLUS.create(), click -> addTarjeta());
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttons.add(refreshButton, deleteButton, addButton);

        total = new Html("<span>Total: <b>" + tarjetas.size() + "</b></span>");

        toolbar = new HorizontalLayout(buttons, total);
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
                total = new Html("<span>Total: <b>" + tarjetas.size() + "</b></span>");
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

        updateList();

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
            total = new Html("<span>Total: <b>" + tarjetas.size() + "</b></span>");
            toolbar.addComponentAtIndex(1, total);
            toolbar.setFlexGrow(1, buttons);
            updateList();
            closeEditor();
        }

    }

    void editTarjeta(DestinoFinalTrabajador tarjeta) {
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
        editTarjeta(new DestinoFinalTrabajador());
    }

    private void closeEditor() {
        form.setDestinoFinal(null);
        form.setVisible(false);
        removeClassName("editing");
        dialog.close();
    }

    private void updateList() {
        tarjetas.clear();
        destinoService.findAll().parallelStream().forEach((target) -> {
            if (target instanceof DestinoFinalTrabajador) {
                tarjetaTrabajador = (DestinoFinalTrabajador) target;
                if (tarjetaTrabajador.getTrabajador() != null) {
                    tarjetas.add(target);
                }
            }
        });
        grid.setItems(tarjetas);
    }
    /* Fin-Barra de tarjetas */
}