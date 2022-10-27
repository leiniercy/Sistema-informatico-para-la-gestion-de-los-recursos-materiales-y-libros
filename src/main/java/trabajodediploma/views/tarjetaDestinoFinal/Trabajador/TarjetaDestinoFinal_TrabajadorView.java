package trabajodediploma.views.tarjetaDestinoFinal.Trabajador;

import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.contextmenu.HasMenuItems;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
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
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;
import trabajodediploma.data.entity.DestinoFinal;
import trabajodediploma.data.entity.DestinoFinalTrabajador;
import trabajodediploma.data.entity.Trabajador;
import trabajodediploma.data.entity.Modulo;
import trabajodediploma.data.entity.RecursoMaterial;
import trabajodediploma.data.service.DestinoFinalService;
import trabajodediploma.data.service.TrabajadorService;
import trabajodediploma.data.service.AreaService;
import trabajodediploma.data.service.ModuloService;
import trabajodediploma.data.tools.EmailSenderService;

public class TarjetaDestinoFinal_TrabajadorView extends Div {

    Grid<DestinoFinal> grid = new Grid<>(DestinoFinal.class, false);
    GridListDataView<DestinoFinal> gridListDataView;
    Grid.Column<DestinoFinal> trabajadorColumn;
    Grid.Column<DestinoFinal> moduloColumn;
    Grid.Column<DestinoFinal> fechaEntregaColumn;
    private List<DestinoFinal> tarjetas;
    private ModuloService moduloService;
    private TrabajadorService trabajadorService;
    private DestinoFinalService destinoService;
    private AreaService areaService;
    private EmailSenderService senderService;
    private DestinoFinalTrabajador tarjetaTrabajador;
    private TarjetaDestinoFinal_TrabajadorFrom_Individual form;
    private TarjetaDestinoFinal_TrabajadorForm_Grupo form_V2;
    private ComboBox<Trabajador> trabajadorFilter;
    private ComboBox<Modulo> moduloFilter;
    private DatePicker entregaFilter;
    private Dialog dialog;
    private Html total;
    private HorizontalLayout toolbar;
    private HorizontalLayout buttons;
    private Div header;
    private Div formContent;

    public TarjetaDestinoFinal_TrabajadorView(
            ModuloService moduloService,
            TrabajadorService trabajadorService,
            DestinoFinalService destinoService,
            AreaService areaService,
            EmailSenderService senderService
    ) {
        addClassName("tarjeta_trabajador");
        this.moduloService = moduloService;
        this.trabajadorService = trabajadorService;
        this.destinoService = destinoService;
        this.areaService = areaService;
        this.senderService = senderService;
        tarjetas = new LinkedList<>();
        updateList();
        configureGrid();
        configureForm();
        add(menuBar(), getContent());
    }

    /* Contenido de la vista */
    private Div getContent() {
        formContent = new Div();
        formContent.addClassName("form-content");
        Div gridContent = new Div(grid);
        gridContent.addClassName("tarjeta_trabajador__content__grid-content");
        Div content = new Div(gridContent);
        content.addClassName("tarjeta_trabajador__content");
        content.setSizeFull();
        /* Dialog Header */
        Button closeButton = new Button(new Icon("lumo", "cross"), (e) -> dialog.close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Span title = new Span("Módulo");
        Div titleDiv = new Div(title);
        titleDiv.addClassName("div_dialog_title");
        Div buttonDiv = new Div(closeButton);
        buttonDiv.addClassName("div_dialog_button");
        header = new Div(titleDiv, buttonDiv);
        header.addClassName("div_dialog_header");
        /* Dialog Header */
        dialog = new Dialog(header);
        return content;
    }

    /* Tabla */
 /* Configuracion de la tabla */
    private void configureGrid() {
        grid.setClassName("tarjeta_trabajador__content__grid-content__table");

        trabajadorColumn = grid.addColumn(new ComponentRenderer<>(tarjeta -> {
            tarjetaTrabajador = (DestinoFinalTrabajador) tarjeta;
            List<Trabajador> trabajadors = new LinkedList<>(tarjetaTrabajador.getTrabajadores());
            VerticalLayout listTrabajadors = new VerticalLayout();
            for (int i = 0; i < trabajadors.size(); i++) {
                HorizontalLayout hl = new HorizontalLayout();
                hl.setAlignItems(Alignment.CENTER);
                Avatar avatar = new Avatar(trabajadors.get(i).getUser().getName(), trabajadors.get(i).getUser().getProfilePictureUrl());
                VerticalLayout vl = new VerticalLayout();
                vl.getStyle().set("line-height", "0");
                Span name = new Span();
                name.addClassNames("name");
                name.setText(trabajadors.get(i).getUser().getName());
                Span email = new Span();
                email.addClassNames("text-s", "text-secondary");
                email.setText(trabajadors.get(i).getEmail());
                vl.add(name, email);
                hl.add(avatar, vl);
                listTrabajadors.add(hl);
            }
            return listTrabajadors;
        })).setHeader("Trabajador").setFrozen(true).setAutoWidth(true).setSortable(true);

        moduloColumn = grid.addColumn(new ComponentRenderer<>(tarjeta -> {
            VerticalLayout layout = new VerticalLayout();
            layout.getStyle().set("line-height", "0.5");
            Label nombreModulo = new Label(tarjeta.getModulo().getNombre());
            Span span_materiales = new Span();
            span_materiales.setWidth("100%");
            List<RecursoMaterial> materiales = new LinkedList<>(tarjeta.getModulo().getRecursosMateriales());
            String listMateriales = new String();
            if (materiales.size() != 0) {
                listMateriales += "" + materiales.get(0).getDescripcion();
                for (int i = 1; i < materiales.size(); i++) {
                    listMateriales += ", " + materiales.get(i).getDescripcion();
                }
            }
            span_materiales.setText(listMateriales);
            layout.add(nombreModulo, span_materiales);
            layout.setWidth("100%");
            return layout;
        })).setHeader("Modulo").setAutoWidth(true);

        fechaEntregaColumn = grid.addColumn(new ComponentRenderer<>(tarjeta -> {
            tarjetaTrabajador = (DestinoFinalTrabajador) tarjeta;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/YYYY");
            String fecha = formatter.format(tarjetaTrabajador.getFecha()).toString();
            HorizontalLayout layout = new HorizontalLayout();
            Span span_fecha = new Span();
            span_fecha.add(fecha);
            span_fecha.getStyle()
                    .set("width", "100%")
                    .set("display", "flex")
                    .set("justify-content", "center")
                    .set("align-items", "end");
            Icon icon = new Icon(VaadinIcon.CHECK_SQUARE_O);
            icon.getStyle()
                    .set("color", "var(--lumo-success-text-color)")
                    .set("margin-left", "10px");
            span_fecha.add(icon);
            layout.add(span_fecha);
            layout.setAlignItems(FlexComponent.Alignment.CENTER);
            return layout;
        })).setComparator(tarjeta -> tarjeta.getFecha()).setHeader("Fecha de Entrega").setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.CENTER)
                .setSortable(true);

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

    /* Filtros */
    private void Filtros() {

        trabajadorFilter = new ComboBox<>();
        trabajadorFilter.setItems(trabajadorService.findAll());
        trabajadorFilter.setItemLabelGenerator(trabajador -> trabajador.getUser().getName());
        trabajadorFilter.setPlaceholder("Filtrar");
        trabajadorFilter.setClearButtonVisible(true);
        trabajadorFilter.setWidth("100%");
        trabajadorFilter.setRenderer(new ComponentRenderer<>(event -> {
            HorizontalLayout hl = new HorizontalLayout();
            hl.setAlignItems(FlexComponent.Alignment.CENTER);
            Avatar avatar = new Avatar(event.getUser().getName(), event.getUser().getProfilePictureUrl());
            VerticalLayout vl = new VerticalLayout();
            vl.getStyle().set("line-height", "0");
            Span name = new Span();
            name.addClassNames("name");
            name.setText(event.getUser().getName());
            Span email = new Span();
            email.addClassNames("text-s", "text-secondary");
            email.setText(event.getEmail());
            vl.add(name, email);
            hl.add(avatar, vl);
            return hl;
        })
        );
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
        entregaFilter.setPlaceholder("Filtrar");
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
        Trabajador trabajadorFilterValue = trabajadorFilter.getValue();
        tarjetaTrabajador = (DestinoFinalTrabajador) tarjeta;
        if (trabajadorFilterValue != null) {
            List<Trabajador> list = new LinkedList<>(tarjetaTrabajador.getTrabajadores());
            list.sort(Comparator.comparing(Trabajador::getId));
            return busquedaBinariaTrabajador(list, trabajadorFilterValue);
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
        Button refreshButton = new Button(VaadinIcon.REFRESH.create(), click -> updateList());
        refreshButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button deleteButton = new Button(VaadinIcon.TRASH.create(), click -> deleteTarjeta());
        deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button editButton = new Button(VaadinIcon.EDIT.create());
        editButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        editButton.addClickListener(click -> {
            Notification notification;
            if (grid.getSelectedItems().size() == 0) {
                notification = Notification.show(
                        "Debe elegir al menos un elemento",
                        2000,
                        Notification.Position.MIDDLE);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } else if (grid.getSelectedItems().size() > 1) {
                notification = Notification.show(
                        "Seleccione solo un elemento",
                        2000,
                        Notification.Position.MIDDLE);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } else if (grid.getSelectedItems().size() == 1) {
                List<DestinoFinal> list = new LinkedList<>(grid.getSelectedItems());
                DestinoFinal tarjeta = list.get(0);
                tarjetaTrabajador = (DestinoFinalTrabajador) tarjeta;
                List<Trabajador> listTrabajadors = new LinkedList<>(tarjetaTrabajador.getTrabajadores());
                if (listTrabajadors.size() == 1) {
                    editTarjeta(tarjetaTrabajador);
                } else if (listTrabajadors.size() > 1) {
                    editTarjeta_V2(tarjetaTrabajador);
                }
            }

        });
        MenuBar anadirButton = new MenuBar();
        anadirButton.addThemeVariants(MenuBarVariant.LUMO_PRIMARY);
        MenuItem addAlls = createIconItem(anadirButton, VaadinIcon.PLUS, "Añadir",
                null);
        SubMenu shareSubMenu = addAlls.getSubMenu();
        MenuItem individual = createIconItem(shareSubMenu, VaadinIcon.USER, "Individual", null, true);
        individual.addClickListener(click -> {
            addTarjeta();
        });
        MenuItem porArea = createIconItem(shareSubMenu, VaadinIcon.USERS, "Por area", null, true);
        porArea.addClickListener(click -> {
            addTarjeta_V2();
        });
        buttons.add(refreshButton, deleteButton, editButton, anadirButton);
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

    private MenuItem createIconItem(HasMenuItems menu, VaadinIcon iconName,
            String label, String ariaLabel) {
        return createIconItem(menu, iconName, label, ariaLabel, false);
    }

    private MenuItem createIconItem(HasMenuItems menu, VaadinIcon iconName,
            String label, String ariaLabel, boolean isChild) {
        Icon icon = new Icon(iconName);

        if (isChild) {
            icon.getStyle().set("width", "var(--lumo-icon-size-s)");
            icon.getStyle().set("height", "var(--lumo-icon-size-s)");
            icon.getStyle().set("marginRight", "var(--lumo-space-s)");
        }

        MenuItem item = menu.addItem(icon, e -> {
        });

        if (ariaLabel != null) {
            item.getElement().setAttribute("aria-label", ariaLabel);
        }

        if (label != null) {
            item.add(new Text(label));
        }

        return item;
    }

    private void deleteTarjeta() {
        try {

            if (grid.asMultiSelect().isEmpty()) {
                Notification notification = Notification.show("Debe elegir al menos un elemento", 2000,
                        Notification.Position.MIDDLE);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } else {
                deleteItems(grid.getSelectedItems().size(), grid.getSelectedItems());
                updateList();
                toolbar.remove(total);
                total = new Html("<span>Total: <b>" + tarjetas.size() + "</b></span>");
                toolbar.addComponentAtIndex(1, total);
                toolbar.setFlexGrow(1, buttons);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Notification notification = Notification.show("Ocurrió un problema al intentar eliminar el trabajador",
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
            notification = Notification.show("Tarjeata ha sido eliminada", 5000, Notification.Position.BOTTOM_START);
        } else {
            notification = Notification.show("Han sido eliminados" + cantidad + " tarjetas", 5000,
                    Notification.Position.BOTTOM_START);
        }
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    // Configuracion del Formulario
    private void configureForm() {
        form = new TarjetaDestinoFinal_TrabajadorFrom_Individual(trabajadorService.findAll(), moduloService.findAll());
        form.setWidth("25em");
        form.addListener(TarjetaDestinoFinal_TrabajadorFrom_Individual.SaveEvent.class,
                this::saveTarjeta);
        form.addListener(TarjetaDestinoFinal_TrabajadorFrom_Individual.CloseEvent.class, e -> closeEditor());

        form_V2 = new TarjetaDestinoFinal_TrabajadorForm_Grupo(areaService.findAll(), trabajadorService.findAll(), moduloService.findAll());
        form_V2.setWidth("25em");
        form_V2.addListener(TarjetaDestinoFinal_TrabajadorForm_Grupo.SaveEvent.class,
                this::saveTarjeta_V2);
        form_V2.addListener(TarjetaDestinoFinal_TrabajadorForm_Grupo.CloseEvent.class, e -> closeEditor());
    }

    private void saveTarjeta(TarjetaDestinoFinal_TrabajadorFrom_Individual.SaveEvent event) {

        tarjetas.clear();
        boolean band = false;
        for (int i = 0; i < destinoService.findAll().size() && band == false; i++) {
            if (destinoService.findAll().get(i) instanceof DestinoFinalTrabajador) {
                tarjetaTrabajador = (DestinoFinalTrabajador) destinoService.findAll().get(i);
                if (tarjetaTrabajador.getTrabajadores() != null) {
                    List<Trabajador> listTragetTrabajadors = new LinkedList<>(tarjetaTrabajador.getTrabajadores());
                    listTragetTrabajadors.sort(Comparator.comparing(Trabajador::getId));
                    List<Trabajador> listevent = new LinkedList<>(event.getDestinoFinal().getTrabajadores());
                    for (int j = 0; j < listevent.size() && band == false; j++) {
                        if (busquedaBinariaTrabajador(listTragetTrabajadors, listevent.get(j)) == true) {
                            if (tarjetaTrabajador.getModulo().getId().equals(event.getDestinoFinal().getModulo().getId())) {
                                band = true;
                                tarjetas.add(tarjetaTrabajador);
                            }
                        }
                    }
                }
            }
        }

        if (tarjetas.size() > 0) {
            Notification notification = Notification.show(
                    "La tarjeta ya existe",
                    2000,
                    Notification.Position.MIDDLE);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/YYYY");
            if (event.getDestinoFinal().getId() == null) {
                try {
                    destinoService.save(event.getDestinoFinal());
                    List<Trabajador> listTragetTrabajadors = new LinkedList<>(event.getDestinoFinal().getTrabajadores());
                    for (int i = 0; i < listTragetTrabajadors.size(); i++) {
                        senderService.sendSimpleEmail(
                                /* enviado a: */listTragetTrabajadors.get(i).getEmail(),
                                /* asunto: */ "Entrega de Módulo",
                                /* mensaje: */ "Genius \n"
                                + "Sistema Informático para la gestión de la información de los recursos materiales y libros en la facultad 4. \n"
                                + "Usted ha recibido el Módulo: "
                                + event.getDestinoFinal().getModulo().getNombre()
                                + " el día: "
                                + formatter.format(event.getDestinoFinal().getFecha()).toString());

                    }
                    Notification notification = Notification.show(
                            "Tarjeta añadida",
                            2000,
                            Notification.Position.BOTTOM_START);
                    notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                } catch (Exception e) {
                    Notification notification = Notification.show(
                            "Error al enviar correo electrónico a la dirección de correo seleccionada",
                            2000,
                            Notification.Position.MIDDLE);
                    notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                }

            } else {
                try {
                    destinoService.update(event.getDestinoFinal());
                    List<Trabajador> listTragetTrabajadors = new LinkedList<>(event.getDestinoFinal().getTrabajadores());
                    for (int i = 0; i < listTragetTrabajadors.size(); i++) {
                        senderService.sendSimpleEmail(
                                /* enviado a: */listTragetTrabajadors.get(i).getEmail(),
                                /* asunto: */ "Entrega de Módulo",
                                /* mensaje: */ "Genius \n"
                                + "Sistema Informático para la gestión de la información de los recursos materiales y libros en la facultad 4. \n"
                                + "Usted ha recibido el Módulo: "
                                + event.getDestinoFinal().getModulo().getNombre()
                                + " el día: "
                                + formatter.format(event.getDestinoFinal().getFecha()).toString());

                    }
                    Notification notification = Notification.show(
                            "Tarjeta modificada",
                            2000,
                            Notification.Position.BOTTOM_START);
                    notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                } catch (Exception e) {
                    Notification notification = Notification.show(
                            "Error al enviar correo electrónico a la dirección de correo seleccionada",
                            2000,
                            Notification.Position.MIDDLE);
                    notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            }
            toolbar.remove(total);
            total = new Html("<span>Total: <b>" + tarjetas.size() + "</b></span>");
            toolbar.addComponentAtIndex(1, total);
            toolbar.setFlexGrow(1, buttons);
            updateList();
            closeEditor();
        }

    }
    //Busqueda Binaria

    private boolean busquedaBinariaTrabajador(List<Trabajador> list, Trabajador e) {
        int inicio = 0;
        int fin = list.size() - 1;
        while (inicio <= fin) {
            int mitad = (inicio + fin) / 2;
            if (e.getId().equals(list.get(mitad).getId())) {
                return true;
            }
            if (e.getId() > list.get(mitad).getId()) {
                inicio = mitad + 1;
            }
            if (e.getId() < list.get(mitad).getId()) {
                fin = mitad - 1;
            }
        }
        return false;
    }

    private void saveTarjeta_V2(TarjetaDestinoFinal_TrabajadorForm_Grupo.SaveEvent event) {

        tarjetas.clear();
        boolean band = false;
        for (int i = 0; i < destinoService.findAll().size() && band == false; i++) {
            if (destinoService.findAll().get(i) instanceof DestinoFinalTrabajador) {
                tarjetaTrabajador = (DestinoFinalTrabajador) destinoService.findAll().get(i);
                if (tarjetaTrabajador.getTrabajadores() != null) {
                    List<Trabajador> listTragetTrabajadors = new LinkedList<>(tarjetaTrabajador.getTrabajadores());
                    listTragetTrabajadors.sort(Comparator.comparing(Trabajador::getId));
                    List<Trabajador> listevent = new LinkedList<>(event.getDestinoFinal().getTrabajadores());
                    for (int j = 0; j < listevent.size() && band == false; j++) {
                        if (busquedaBinariaTrabajador(listTragetTrabajadors, listevent.get(j)) == true) {
                            if (tarjetaTrabajador.getModulo().getId().equals(event.getDestinoFinal().getModulo().getId())) {
                                band = true;
                                tarjetas.add(tarjetaTrabajador);
                            }
                        }
                    }
                }
            }
        }

        if (tarjetas.size() > 0) {
            Notification notification = Notification.show(
                    "La tarjeta ya existe",
                    2000,
                    Notification.Position.MIDDLE);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/YYYY");
            if (event.getDestinoFinal().getId() == null) {
                try {
                    destinoService.save(event.getDestinoFinal());
                    List<Trabajador> listTragetTrabajadors = new LinkedList<>(event.getDestinoFinal().getTrabajadores());
                    for (int i = 0; i < listTragetTrabajadors.size(); i++) {
                        senderService.sendSimpleEmail(
                                /* enviado a: */listTragetTrabajadors.get(i).getEmail(),
                                /* asunto: */ "Entrega de Módulo",
                                /* mensaje: */ "Genius \n"
                                + "Sistema Informático para la gestión de la información de los recursos materiales y libros en la facultad 4. \n"
                                + "Usted ha recibido el Módulo: "
                                + event.getDestinoFinal().getModulo().getNombre()
                                + " el día: "
                                + formatter.format(event.getDestinoFinal().getFecha()).toString());

                    }
                    Notification notification = Notification.show(
                            "Tarjeta añadida",
                            2000,
                            Notification.Position.BOTTOM_START);
                    notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                } catch (Exception e) {
                    Notification notification = Notification.show(
                            "Error al enviar correo electrónico a la dirección de correo seleccionada",
                            2000,
                            Notification.Position.MIDDLE);
                    notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                }

            } else {
                try {
                    destinoService.update(event.getDestinoFinal());
                    List<Trabajador> listTragetTrabajadors = new LinkedList<>(event.getDestinoFinal().getTrabajadores());
                    for (int i = 0; i < listTragetTrabajadors.size(); i++) {
                        senderService.sendSimpleEmail(
                                /* enviado a: */listTragetTrabajadors.get(i).getEmail(),
                                /* asunto: */ "Entrega de Módulo",
                                /* mensaje: */ "Genius \n"
                                + "Sistema Informático para la gestión de la información de los recursos materiales y libros en la facultad 4. \n"
                                + "Usted ha recibido el Módulo: "
                                + event.getDestinoFinal().getModulo().getNombre()
                                + " el día: "
                                + formatter.format(event.getDestinoFinal().getFecha()).toString());

                    }
                    Notification notification = Notification.show(
                            "Tarjeta modificada",
                            2000,
                            Notification.Position.BOTTOM_START);
                    notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                } catch (Exception e) {
                    Notification notification = Notification.show(
                            "Error al enviar correo electrónico a la dirección de correo seleccionada",
                            2000,
                            Notification.Position.MIDDLE);
                    notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            }
            toolbar.remove(total);
            total = new Html("<span>Total: <b>" + tarjetas.size() + "</b></span>");
            toolbar.addComponentAtIndex(1, total);
            toolbar.setFlexGrow(1, buttons);
            updateList();
            closeEditor();
        }

    }

    /*Tarjeta de destino individual*/
    void editTarjeta(DestinoFinalTrabajador tarjeta) {
        if (tarjeta == null) {
            closeEditor();
        } else {
            form.setDestinoFinal(tarjeta);
            form.setVisible(true);
            addClassName("editing");
            formContent.removeAll();
            formContent.add(form);
            dialog.removeAll();
            dialog.add(header, formContent);
            dialog.open();
        }
    }

    void addTarjeta() {
        grid.asMultiSelect().clear();
        editTarjeta(new DestinoFinalTrabajador());
    }

    /*Fin - > Tarjeta de destino individual*/
 /*Tarjeta de destino por area*/
    void editTarjeta_V2(DestinoFinalTrabajador tarjeta) {
        if (tarjeta == null) {
            closeEditor();
        } else {
            form_V2.setDestinoFinal(tarjeta);
            form_V2.setVisible(true);
            addClassName("editing");
            formContent.removeAll();
            formContent.add(form_V2);
            dialog.removeAll();
            dialog.add(header, formContent);
            dialog.open();
        }
    }

    void addTarjeta_V2() {
        grid.asMultiSelect().clear();
        editTarjeta_V2(new DestinoFinalTrabajador());
    }

    /*Fin -> Tarjeta de destino por area*/
    private void closeEditor() {
        form.setDestinoFinal(new DestinoFinalTrabajador());
        form.setVisible(false);
        removeClassName("editing");
        dialog.close();
    }

    private void updateList() {
        tarjetas.clear();
        for (int i = 0; i < destinoService.findAll().size(); i++) {
            if (destinoService.findAll().get(i) instanceof DestinoFinalTrabajador) {
                tarjetaTrabajador = (DestinoFinalTrabajador) destinoService.findAll().get(i);
                if (tarjetaTrabajador.getTrabajadores() != null) {
                    tarjetas.add(destinoService.findAll().get(i));
                }
            }
        }
        grid.setItems(tarjetas);
    }
    /* Fin-Barra de tarjetas */
}
