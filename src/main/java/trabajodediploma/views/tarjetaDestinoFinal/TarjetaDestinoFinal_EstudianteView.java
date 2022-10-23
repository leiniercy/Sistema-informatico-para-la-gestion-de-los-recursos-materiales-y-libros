package trabajodediploma.views.tarjetaDestinoFinal;

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
import trabajodediploma.data.entity.DestinoFinalEstudiante;
import trabajodediploma.data.entity.Estudiante;
import trabajodediploma.data.entity.Modulo;
import trabajodediploma.data.entity.RecursoMaterial;
import trabajodediploma.data.service.DestinoFinalService;
import trabajodediploma.data.service.EstudianteService;
import trabajodediploma.data.service.GrupoService;
import trabajodediploma.data.service.ModuloService;
import trabajodediploma.data.tools.EmailSenderService;

public class TarjetaDestinoFinal_EstudianteView extends Div {

    Grid<DestinoFinal> grid = new Grid<>(DestinoFinal.class, false);
    GridListDataView<DestinoFinal> gridListDataView;
    Grid.Column<DestinoFinal> estudianteColumn;
    Grid.Column<DestinoFinal> moduloColumn;
    Grid.Column<DestinoFinal> fechaEntregaColumn;
    private List<DestinoFinal> tarjetas;
    private ModuloService moduloService;
    private EstudianteService estudianteService;
    private DestinoFinalService destinoService;
    private GrupoService grupoService;
    private EmailSenderService senderService;
    private DestinoFinalEstudiante tarjetaEstudiante;
    private TarjetaDestinoFinal_EstudianteForm form;
    private TarjetaDestinoFinal_EstudianteForm_V2 form_V2;
    private ComboBox<Estudiante> estudianteFilter;
    private ComboBox<Modulo> moduloFilter;
    private DatePicker entregaFilter;
    private Dialog dialog;
    private Html total;
    private HorizontalLayout toolbar;
    private HorizontalLayout buttons;
    private Div header;
    private Div formContent;

    public TarjetaDestinoFinal_EstudianteView(
            ModuloService moduloService,
            EstudianteService estudianteService,
            DestinoFinalService destinoService,
            GrupoService grupoService,
            EmailSenderService senderService
    ) {
        addClassName("tarjeta_estudiante");
        this.moduloService = moduloService;
        this.estudianteService = estudianteService;
        this.destinoService = destinoService;
        this.grupoService = grupoService;
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
        gridContent.addClassName("tarjeta_estudiante__content__grid-content");
        Div content = new Div(gridContent);
        content.addClassName("tarjeta_estudiante__content");
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
        grid.setClassName("tarjeta_estudiante__content__grid-content__table");

        estudianteColumn = grid.addColumn(new ComponentRenderer<>(tarjeta -> {
            tarjetaEstudiante = (DestinoFinalEstudiante) tarjeta;
            List<Estudiante> estudiantes = new LinkedList<>(tarjetaEstudiante.getEstudiantes());
            VerticalLayout listEstudiantes = new VerticalLayout();
            for (int i = 0; i < estudiantes.size(); i++) {
                HorizontalLayout hl = new HorizontalLayout();
                hl.setAlignItems(Alignment.CENTER);
                Avatar avatar = new Avatar(estudiantes.get(i).getUser().getName(), estudiantes.get(i).getUser().getProfilePictureUrl());
                VerticalLayout vl = new VerticalLayout();
                vl.getStyle().set("line-height", "0");
                Span name = new Span();
                name.addClassNames("name");
                name.setText(estudiantes.get(i).getUser().getName());
                Span email = new Span();
                email.addClassNames("text-s", "text-secondary");
                email.setText(estudiantes.get(i).getEmail());
                vl.add(name, email);
                hl.add(avatar, vl);
                listEstudiantes.add(hl);
            }
            return listEstudiantes;
        })).setHeader("Estudiante").setFrozen(true).setAutoWidth(true).setSortable(true);

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
            tarjetaEstudiante = (DestinoFinalEstudiante) tarjeta;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/YYYY");
            String fecha = formatter.format(tarjetaEstudiante.getFecha()).toString();
            HorizontalLayout layout = new HorizontalLayout();
            Span span_fecha = new Span();
            span_fecha.add(fecha);
            span_fecha.getStyle()
                    .set("width","100%")
                    .set("display","flex")
                    .set("justify-content","center")
                    .set("align-items","end");
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
        headerRow.getCell(estudianteColumn).setComponent(estudianteFilter);
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

        estudianteFilter = new ComboBox<>();
        estudianteFilter.setItems(estudianteService.findAll());
        estudianteFilter.setItemLabelGenerator(estudiante -> estudiante.getUser().getName());
        estudianteFilter.setPlaceholder("Filtrar");
        estudianteFilter.setClearButtonVisible(true);
        estudianteFilter.setWidth("100%");
        estudianteFilter.setRenderer(new ComponentRenderer<>(event -> {
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
        estudianteFilter.addValueChangeListener(event -> {
            if (estudianteFilter.getValue() == null) {
                gridListDataView = grid.setItems(tarjetas);
            } else {
                gridListDataView.addFilter(tarjeta -> areEstudianteEqual(tarjeta, estudianteFilter));
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

    private boolean areEstudianteEqual(DestinoFinal tarjeta, ComboBox<Estudiante> estudianteFilter) {
        Estudiante estudianteFilterValue = estudianteFilter.getValue();
        tarjetaEstudiante = (DestinoFinalEstudiante) tarjeta;
        if (estudianteFilterValue != null) {
            List<Estudiante> list = new LinkedList<>(tarjetaEstudiante.getEstudiantes());
            return busquedaBinariaEstudiante(list, estudianteFilterValue);
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
                tarjetaEstudiante = (DestinoFinalEstudiante) tarjeta;
                List<Estudiante> listEstudiantes = new LinkedList<>(tarjetaEstudiante.getEstudiantes());
                if (listEstudiantes.size() == 1) {
                    editTarjeta(tarjetaEstudiante);
                } else if (listEstudiantes.size() > 1) {
                    editTarjeta_V2(tarjetaEstudiante);
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
        MenuItem porGrupo = createIconItem(shareSubMenu, VaadinIcon.USERS, "Por grupo", null, true);
        porGrupo.addClickListener(click -> {
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
            Notification notification = Notification.show("Ocurrió un problema al intentar eliminar el estudiante",
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
        form = new TarjetaDestinoFinal_EstudianteForm(estudianteService.findAll(), moduloService.findAll());
        form.setWidth("25em");
        form.addListener(TarjetaDestinoFinal_EstudianteForm.SaveEvent.class,
                this::saveTarjeta);
        form.addListener(TarjetaDestinoFinal_EstudianteForm.CloseEvent.class, e -> closeEditor());

        form_V2 = new TarjetaDestinoFinal_EstudianteForm_V2(grupoService.findAll(), estudianteService.findAll(), moduloService.findAll());
        form_V2.setWidth("25em");
        form_V2.addListener(TarjetaDestinoFinal_EstudianteForm_V2.SaveEvent.class,
                this::saveTarjeta_V2);
        form_V2.addListener(TarjetaDestinoFinal_EstudianteForm_V2.CloseEvent.class, e -> closeEditor());
    }

    private void saveTarjeta(TarjetaDestinoFinal_EstudianteForm.SaveEvent event) {

        tarjetas.clear();
        boolean band = false;
        for (int i = 0; i < destinoService.findAll().size() && band == false; i++) {
            if (destinoService.findAll().get(i) instanceof DestinoFinalEstudiante) {
                tarjetaEstudiante = (DestinoFinalEstudiante) destinoService.findAll().get(i);
                if (tarjetaEstudiante.getEstudiantes() != null) {
                    List<Estudiante> listTragetEstudiantes = new LinkedList<>(tarjetaEstudiante.getEstudiantes());
                    listTragetEstudiantes.sort(Comparator.comparing(Estudiante::getId));
                    List<Estudiante> listevent = new LinkedList<>(event.getDestinoFinal().getEstudiantes());
                    for (int j = 0; j < listevent.size() && band == false; j++) {
                        if (busquedaBinariaEstudiante(listTragetEstudiantes, listevent.get(j)) == true) {
                            if (tarjetaEstudiante.getModulo().getId().equals(event.getDestinoFinal().getModulo().getId())) {
                                band = true;
                                tarjetas.add(tarjetaEstudiante);
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
                    List<Estudiante> listTragetEstudiantes = new LinkedList<>(event.getDestinoFinal().getEstudiantes());
                    for (int i = 0; i < listTragetEstudiantes.size(); i++) {
                        senderService.sendSimpleEmail(
                                /* enviado a: */listTragetEstudiantes.get(i).getEmail(),
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
                    List<Estudiante> listTragetEstudiantes = new LinkedList<>(event.getDestinoFinal().getEstudiantes());
                    for (int i = 0; i < listTragetEstudiantes.size(); i++) {
                        senderService.sendSimpleEmail(
                                /* enviado a: */listTragetEstudiantes.get(i).getEmail(),
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

    private boolean busquedaBinariaEstudiante(List<Estudiante> list, Estudiante e) {
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

    private void saveTarjeta_V2(TarjetaDestinoFinal_EstudianteForm_V2.SaveEvent event) {

        tarjetas.clear();
        boolean band = false;
        for (int i = 0; i < destinoService.findAll().size() && band == false; i++) {
            if (destinoService.findAll().get(i) instanceof DestinoFinalEstudiante) {
                tarjetaEstudiante = (DestinoFinalEstudiante) destinoService.findAll().get(i);
                if (tarjetaEstudiante.getEstudiantes() != null) {
                    List<Estudiante> listTragetEstudiantes = new LinkedList<>(tarjetaEstudiante.getEstudiantes());
                    listTragetEstudiantes.sort(Comparator.comparing(Estudiante::getId));
                    List<Estudiante> listevent = new LinkedList<>(event.getDestinoFinal().getEstudiantes());
                    for (int j = 0; j < listevent.size() && band == false; j++) {
                        if (busquedaBinariaEstudiante(listTragetEstudiantes, listevent.get(j)) == true) {
                            if (tarjetaEstudiante.getModulo().getId().equals(event.getDestinoFinal().getModulo().getId())) {
                                band = true;
                                tarjetas.add(tarjetaEstudiante);
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
                    List<Estudiante> listTragetEstudiantes = new LinkedList<>(event.getDestinoFinal().getEstudiantes());
                    for (int i = 0; i < listTragetEstudiantes.size(); i++) {
                        senderService.sendSimpleEmail(
                                /* enviado a: */listTragetEstudiantes.get(i).getEmail(),
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
                    List<Estudiante> listTragetEstudiantes = new LinkedList<>(event.getDestinoFinal().getEstudiantes());
                    for (int i = 0; i < listTragetEstudiantes.size(); i++) {
                        senderService.sendSimpleEmail(
                                /* enviado a: */listTragetEstudiantes.get(i).getEmail(),
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
    void editTarjeta(DestinoFinalEstudiante tarjeta) {
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
        editTarjeta(new DestinoFinalEstudiante());
    }

    /*Fin - > Tarjeta de destino individual*/
 /*Tarjeta de destino por grupo*/
    void editTarjeta_V2(DestinoFinalEstudiante tarjeta) {
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
        editTarjeta_V2(new DestinoFinalEstudiante());
    }

    /*Fin -> Tarjeta de destino por grupo*/
    private void closeEditor() {
        form.setDestinoFinal(new DestinoFinalEstudiante());
        form.setVisible(false);
        removeClassName("editing");
        dialog.close();
    }

    private void updateList() {
        tarjetas.clear();
        for (int i = 0; i < destinoService.findAll().size(); i++) {
            if (destinoService.findAll().get(i) instanceof DestinoFinalEstudiante) {
                tarjetaEstudiante = (DestinoFinalEstudiante) destinoService.findAll().get(i);
                if (tarjetaEstudiante.getEstudiantes() != null) {
                    tarjetas.add(destinoService.findAll().get(i));
                }
            }
        }
        grid.setItems(tarjetas);
    }
    /* Fin-Barra de tarjetas */
}
