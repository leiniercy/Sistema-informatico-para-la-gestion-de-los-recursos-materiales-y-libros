package trabajodediploma.views.tarjetaDestinoFinal.Estudiante;

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
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.contextmenu.HasMenuItems;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dialog.DialogVariant;
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
import java.util.Comparator;
import trabajodediploma.data.entity.DestinoFinal;
import trabajodediploma.data.entity.DestinoFinalEstudiante;
import trabajodediploma.data.entity.Estudiante;
import trabajodediploma.data.entity.Grupo;
import trabajodediploma.data.entity.Modulo;
import trabajodediploma.data.entity.RecursoMaterial;
import trabajodediploma.data.service.DestinoFinalService;
import trabajodediploma.data.service.EstudianteService;
import trabajodediploma.data.service.GrupoService;
import trabajodediploma.data.service.ModuloService;
import trabajodediploma.data.tools.EmailSenderService;

public class TarjetaDestinoFinal_EstudianteView extends Div {

    Grid<DestinoFinal> gridDestinoFinal = new Grid<>(DestinoFinal.class, false);
    GridListDataView<DestinoFinal> gridListDataViewDestinoFinal;
    Grid.Column<DestinoFinal> estudianteColumn;
    Grid.Column<DestinoFinal> moduloColumn;
    Grid.Column<DestinoFinal> fechaEntregaColumn;
    Grid.Column<DestinoFinal> editColumn;

    Grid<Estudiante> gridEstudiantes = new Grid<>(Estudiante.class, false);
    GridListDataView<Estudiante> gridListDataViewEstudiante;
    Grid.Column<Estudiante> columnaEstudiante;

    private List<DestinoFinal> tarjetas;
    private ModuloService moduloService;
    private EstudianteService estudianteService;
    private GrupoService grupoService;
    private DestinoFinalService destinoService;
    private EmailSenderService senderService;
    private DestinoFinalEstudiante tarjetaEstudiante;
    private TarjetaDestinoFinal_EstudianteForm_Individual formIndividual;
    private TarjetaDestinoFinal_EstudianteForm_Grupo formPorGrupo;
    private ComboBox<Estudiante> estudianteFilter;
    private ComboBox<Modulo> moduloFilter;
    private DatePicker entregaFilter;
    private Dialog dialogIndividual;
    private Dialog dialogPorGrupo;
    private Html total;
    private HorizontalLayout toolbar;
    private HorizontalLayout buttons;
    private Div headerDialogDestino;
    private Div headerDialogEstudiante;
    private VerticalLayout filtrosContainer;
    private HorizontalLayout buttonsDialogEst;

    private ComboBox<Estudiante> filtrarEstudiante;
    private ComboBox<Grupo> filtrarGrupo;

    public TarjetaDestinoFinal_EstudianteView(
            ModuloService moduloService,
            EstudianteService estudianteService,
            GrupoService grupoService,
            DestinoFinalService destinoService,
            EmailSenderService senderService) {
        addClassName("tarjeta_estudiante");
        this.moduloService = moduloService;
        this.estudianteService = estudianteService;
        this.grupoService = grupoService;
        this.destinoService = destinoService;
        this.senderService = senderService;
        tarjetas = new LinkedList<>();
        updateList();
        configurarGridTarjetaDestino();
        configurarGridEstudiante();
        confiuracionDialogPorGrupo();
        configureFormIndividual();
        add(menuBar(), getContent());
    }

    /* Contenido de la vista */
    private Div getContent() {
        Div formContent = new Div(formIndividual);
        formContent.addClassName("form-content");
        Div gridContent = new Div(gridDestinoFinal);
        gridContent.addClassName("tarjeta_estudiante__content__grid-content");
        Div content = new Div(gridContent);
        content.addClassName("tarjeta_estudiante__content");
        content.setSizeFull();
        /* Dialog Header */
        Button closeButton = new Button(new Icon("lumo", "cross"), (e) -> dialogIndividual.close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Span title = new Span("Libro");
        Div titleDiv = new Div(title);
        titleDiv.addClassName("div_dialog_title");
        Div buttonDiv = new Div(closeButton);
        buttonDiv.addClassName("div_dialog_button");
        headerDialogDestino = new Div(titleDiv, buttonDiv);
        headerDialogDestino.addClassName("div_dialog_header");
        /* Dialog Header */
        dialogIndividual = new Dialog(headerDialogDestino, formContent);
        return content;
    }

    /* Tabla */
 /* Configuracion de la tabla */
    private void configurarGridTarjetaDestino() {
        gridDestinoFinal.setClassName("tarjeta_estudiante__content__grid-content__table");

        estudianteColumn = gridDestinoFinal.addColumn(new ComponentRenderer<>(tarjeta -> {
            tarjetaEstudiante = (DestinoFinalEstudiante) tarjeta;
            HorizontalLayout hl = new HorizontalLayout();
            hl.setAlignItems(Alignment.CENTER);
            Avatar avatar = new Avatar(tarjetaEstudiante.getEstudiante().getUser().getName(), tarjetaEstudiante.getEstudiante().getUser().getProfilePictureUrl());
            VerticalLayout vl = new VerticalLayout();
            vl.getStyle().set("line-height", "0");
            Span name = new Span();
            name.addClassNames("name");
            name.setText(tarjetaEstudiante.getEstudiante().getUser().getName());
            Span email = new Span();
            email.addClassNames("text-s", "text-secondary");
            email.setText(tarjetaEstudiante.getEstudiante().getEmail());
            vl.add(name, email);
            hl.add(avatar, vl);
            return hl;
        })).setHeader("Estudiante").setFrozen(true).setAutoWidth(true).setSortable(true);

        moduloColumn = gridDestinoFinal.addColumn(new ComponentRenderer<>(tarjeta -> {
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
        })).setHeader("Modulo").setAutoWidth(true).setSortable(true);

        fechaEntregaColumn = gridDestinoFinal
                .addColumn(new ComponentRenderer<>(tarjeta -> {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/YYYY");
                    String fecha = formatter.format(tarjeta.getFecha()).toString();
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
                            .set("color", "var(--lumo-success-color)")
                            .set("margin-left", "10px");
                    span_fecha.add(icon);
                    layout.add(span_fecha);
                    layout.setAlignItems(FlexComponent.Alignment.CENTER);
                    return layout;
                })).setComparator(tarjeta -> tarjeta.getFecha()).setHeader("Fecha de Entrega").setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.CENTER)
                .setSortable(true)
                .setAutoWidth(true);

        editColumn = gridDestinoFinal.addComponentColumn(tarjeta -> {
            tarjetaEstudiante = (DestinoFinalEstudiante) tarjeta;
            Button editButton = new Button(VaadinIcon.EDIT.create());
            editButton.addClickListener(e -> this.editTarjetaIndividual(tarjetaEstudiante));
            editButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            return editButton;
        }).setTextAlign(ColumnTextAlign.CENTER).setFrozen(true).setFlexGrow(0);

        FiltrosTarjetaDestino();

        HeaderRow headerRow = gridDestinoFinal.appendHeaderRow();
        headerRow.getCell(estudianteColumn).setComponent(estudianteFilter);
        headerRow.getCell(moduloColumn).setComponent(moduloFilter);
        headerRow.getCell(fechaEntregaColumn).setComponent(entregaFilter);

        gridListDataViewDestinoFinal = gridDestinoFinal.setItems(tarjetas);
        gridDestinoFinal.setAllRowsVisible(true);
        gridDestinoFinal.setSizeFull();
        gridDestinoFinal.setWidthFull();
        gridDestinoFinal.setHeightFull();
        gridDestinoFinal.setSelectionMode(Grid.SelectionMode.MULTI);
        gridDestinoFinal.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
        gridDestinoFinal.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        gridDestinoFinal.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);

    }

    private void configurarGridEstudiante() {

        gridEstudiantes.addColumn(new ComponentRenderer<>(est -> {
            HorizontalLayout hl = new HorizontalLayout();
            hl.getStyle().set("align-items", "center");
            hl.setAlignItems(Alignment.CENTER);
            Avatar avatar = new Avatar(est.getUser().getName(), est.getUser().getProfilePictureUrl());
            VerticalLayout vl = new VerticalLayout();
            vl.getStyle().set("line-height", "0");
            Span name = new Span();
            name.addClassNames("name");
            name.setText(est.getUser().getName());
            Span email = new Span();
            email.addClassNames("text-s", "text-secondary");
            email.setText(est.getEmail());
            vl.add(name, email);
            hl.add(avatar, vl);
            return hl;
        })).setHeader("Nombre").setSortable(true);

        FiltrosGridEstudiante();

        gridEstudiantes.setItems(estudianteService.findAll());
        gridEstudiantes.setSelectionMode(Grid.SelectionMode.MULTI);
        gridEstudiantes.getStyle().set("width", "500px").set("max-width", "100%");
    }

    private void confiuracionDialogPorGrupo() {
        dialogPorGrupo = new Dialog();
        dialogPorGrupo.addThemeVariants(DialogVariant.LUMO_NO_PADDING);
        filtrosContainer = new VerticalLayout();
        /* Dialog Header */
        Button closeButton = new Button(new Icon("lumo", "cross"), (e) -> dialogPorGrupo.close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        /*Menu Filtros*/
        MenuBar barraMenu = new MenuBar();
        barraMenu.addThemeVariants(MenuBarVariant.LUMO_PRIMARY);
        MenuItem filtros = createMenuIconItem(barraMenu, VaadinIcon.FILTER, "Filtros", null, false);
        SubMenu filtrosSubMenu = filtros.getSubMenu();
        Checkbox estudianteCheckBox = new Checkbox();
        estudianteCheckBox.addClickListener(event -> {
            if (estudianteCheckBox.getValue()) {
                filtrosContainer.add(filtrarEstudiante);
            } else {
                filtrosContainer.remove(filtrarEstudiante);
                filtrarEstudiante.setValue(null);
            }
        });
        MenuItem estudiante = createSubMenuEstudianteIconItem(filtrosSubMenu, estudianteCheckBox, VaadinIcon.USER, "Estudiante", null, true);
        Checkbox grupoCheckBox = new Checkbox();
        grupoCheckBox.addClickListener(event -> {
            if (grupoCheckBox.getValue()) {
                filtrosContainer.add(filtrarGrupo);
            } else {
                filtrosContainer.remove(filtrarGrupo);
                filtrarGrupo.setValue(null);
            }
        });
        MenuItem grupo = createSubMenuEstudianteIconItem(filtrosSubMenu, grupoCheckBox, VaadinIcon.USERS, "Grupo", null, true);
        /*FIN -> Menu Filtros*/
        Div filter = new Div(barraMenu);
        filter.addClassName("div_dialog_title");
        Div buttonDiv = new Div(closeButton);
        buttonDiv.addClassName("div_dialog_button");
        headerDialogEstudiante = new Div(filter, buttonDiv);
        headerDialogEstudiante.addClassName("div_dialog_header");
        /* Dialog Header */

        Button addForm = new Button("Seleccionar", VaadinIcon.PLUS.create());
        addForm.addClickListener(click -> {
            if (gridEstudiantes.getSelectedItems().size() > 0) {
                configureFormPorGrupo();
                addTarjetaPorGrupo();
            } else {
                Notification notification = Notification.show(
                        "Seleccione uno o más estudiantes",
                        2000,
                        Notification.Position.MIDDLE);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        addForm.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addForm.getStyle().set("margin-right", "10px");

        buttonsDialogEst = new HorizontalLayout(addForm);
        buttonsDialogEst.getStyle()
                .set("width", "100%")
                .set("align-items", "flex-end")
                .set("justify-content", "end");
        dialogPorGrupo.add(headerDialogEstudiante, filtrosContainer, gridEstudiantes, buttonsDialogEst);

    }

    //Crear Submenu Estudiante Item
    private MenuItem createSubMenuEstudianteIconItem(HasMenuItems menu, Checkbox checkBox, VaadinIcon iconName,
            String label, String ariaLabel, boolean isChild) {
        Icon icon = new Icon(iconName);

        if (isChild) {
            icon.getStyle().set("width", "var(--lumo-icon-size-s)");
            icon.getStyle().set("height", "var(--lumo-icon-size-s)");
            icon.getStyle().set("marginRight", "var(--lumo-space-s)");
        }
        MenuItem item = menu.addItem(checkBox, e -> {
        });
        item.add(icon);

        if (ariaLabel != null) {
            item.getElement().setAttribute("aria-label", ariaLabel);
        }

        if (label != null) {
            item.add(new Text(label));
        }

        return item;
    }

    private void refreshGrid() {
        gridDestinoFinal.setVisible(true);
        tarjetas.clear();
        destinoService.findAll().parallelStream().forEach((target) -> {
            if (target instanceof DestinoFinalEstudiante) {
                tarjetaEstudiante = (DestinoFinalEstudiante) target;
                if (tarjetaEstudiante.getEstudiante() != null) {
                    tarjetas.add(target);
                }
            }
        });
        gridDestinoFinal.setItems(tarjetas);
    }

    /* Filtros */
    private void FiltrosTarjetaDestino() {

        estudianteFilter = new ComboBox<>();
        estudianteFilter.setItems(estudianteService.findAll());
        estudianteFilter.setItemLabelGenerator(estudiante -> estudiante.getUser().getName());
        estudianteFilter.setPlaceholder("Filtrar");
        estudianteFilter.setClearButtonVisible(true);
        estudianteFilter.setWidth("100%");
        estudianteFilter.addValueChangeListener(event -> {
            if (estudianteFilter.getValue() == null) {
                gridListDataViewDestinoFinal = gridDestinoFinal.setItems(tarjetas);
            } else {
                gridListDataViewDestinoFinal.addFilter(tarjeta -> areEstudianteTarjetaDestinoEqual(tarjeta, estudianteFilter));
            }
        });
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

        moduloFilter = new ComboBox<>();
        moduloFilter.setItems(moduloService.findAll());
        moduloFilter.setItemLabelGenerator(Modulo::getNombre);
        moduloFilter.setPlaceholder("Filtrar");
        moduloFilter.setClearButtonVisible(true);
        moduloFilter.setWidth("100%");
        moduloFilter.addValueChangeListener(event -> {
            if (moduloFilter.getValue() == null) {
                gridListDataViewDestinoFinal = gridDestinoFinal.setItems(tarjetas);
            } else {
                gridListDataViewDestinoFinal.addFilter(des -> StringUtils.containsIgnoreCase(des.getModulo().getNombre(),
                        moduloFilter.getValue().getNombre()));
            }
        });

        entregaFilter = new DatePicker();
        entregaFilter.setPlaceholder("Filter");
        entregaFilter.setClearButtonVisible(true);
        entregaFilter.setWidth("100%");
        entregaFilter.addValueChangeListener(event -> {
            if (entregaFilter.getValue() == null) {
                gridListDataViewDestinoFinal = gridDestinoFinal.setItems(tarjetas);
            } else {
                gridListDataViewDestinoFinal.addFilter(tarjeta -> areFechaInicioEqual(tarjeta, entregaFilter));
            }
        });

    }

    private void FiltrosGridEstudiante() {

        filtrarEstudiante = new ComboBox<>();
        filtrarEstudiante.setItems(estudianteService.findAll());
        filtrarEstudiante.setItemLabelGenerator(estudiante -> estudiante.getUser().getName());
        filtrarEstudiante.setPlaceholder("Filtrar");
        filtrarEstudiante.setClearButtonVisible(true);
        filtrarEstudiante.setWidth("100%");
        filtrarEstudiante.addValueChangeListener(event -> {
            if (filtrarEstudiante.getValue() == null) {
                gridListDataViewEstudiante = gridEstudiantes.setItems(estudianteService.findAll());
            } else {
                gridListDataViewEstudiante.addFilter(estudiante -> areEstudianteEqual(estudiante, filtrarEstudiante));
            }
        });
        filtrarEstudiante.setRenderer(new ComponentRenderer<>(event -> {
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

        filtrarGrupo = new ComboBox<>();
        filtrarGrupo.setItems(grupoService.findAll());
        filtrarGrupo.setItemLabelGenerator(grupo -> grupo.getNumero());
        filtrarGrupo.setPlaceholder("Filtrar");
        filtrarGrupo.setClearButtonVisible(true);
        filtrarGrupo.setWidth("100%");
        filtrarGrupo.addValueChangeListener(event -> {
            if (filtrarGrupo.getValue() == null) {
                gridListDataViewDestinoFinal = gridDestinoFinal.setItems(tarjetas);
            } else {
                gridListDataViewEstudiante.addFilter(estudiante -> areGrupoEqual(estudiante, filtrarGrupo));
            }
        });

    }

    private boolean areEstudianteTarjetaDestinoEqual(DestinoFinal tarjeta, ComboBox<Estudiante> estudianteFilter) {
        String estudianteFilterValue = estudianteFilter.getValue().getUser().getName();
        if (estudianteFilterValue != null) {
            tarjetaEstudiante = (DestinoFinalEstudiante) tarjeta;
            return StringUtils.equals(tarjetaEstudiante.getEstudiante().getUser().getName(), estudianteFilterValue);
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

    private boolean areEstudianteEqual(Estudiante estudiante, ComboBox<Estudiante> estudianteFilter) {
        String estudianteFilterValue = estudianteFilter.getValue().getUser().getName();
        if (estudianteFilterValue != null) {
            return StringUtils.equals(estudiante.getUser().getName(), estudianteFilterValue);
        }
        return true;
    }

    private boolean areGrupoEqual(Estudiante estudiante, ComboBox<Grupo> grupoFilter) {
        String grupoFilterValue = grupoFilter.getValue().getNumero();
        if (grupoFilterValue != null) {
            return StringUtils.equals(estudiante.getGrupo().getNumero(), grupoFilterValue);
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

        MenuBar barraMenu = new MenuBar();
        barraMenu.addThemeVariants(MenuBarVariant.LUMO_PRIMARY);
        MenuItem addButton = createMenuIconItem(barraMenu, VaadinIcon.PLUS, "Añadir", null, false);
        SubMenu addButtonSubMenu = addButton.getSubMenu();
        MenuItem individual = createSubMenuIconItem(addButtonSubMenu, VaadinIcon.USER, "Individual", null, true);
        individual.addClickListener(event -> addTarjetaIndividual());
        MenuItem porGrupo = createSubMenuIconItem(addButtonSubMenu, VaadinIcon.USERS, "Por Grupo", null, true);
        porGrupo.addClickListener(event -> {
            dialogPorGrupo.removeAll();
            dialogPorGrupo.add(headerDialogEstudiante, filtrosContainer, gridEstudiantes, buttonsDialogEst);
            dialogPorGrupo.open();
        });
        buttons.add(refreshButton, deleteButton/*, addButton*/, barraMenu);

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

    //Crear MenuItem
    private MenuItem createMenuIconItem(HasMenuItems menu, VaadinIcon iconName,
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

    //Crear Submenu Item
    private MenuItem createSubMenuIconItem(HasMenuItems menu, VaadinIcon iconName,
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

            if (gridDestinoFinal.asMultiSelect().isEmpty()) {
                Notification notification = Notification.show("Debe elegir al menos un campo", 5000,
                        Notification.Position.MIDDLE);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } else {
                deleteItems(gridDestinoFinal.getSelectedItems().size(), gridDestinoFinal.getSelectedItems());
                refreshGrid();
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
            notification = Notification.show("Tarjeata ha eliminada", 5000, Notification.Position.BOTTOM_START);
        } else {
            notification = Notification.show("Han sido eliminados" + cantidad + " tarjetas", 5000,
                    Notification.Position.BOTTOM_START);
        }
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    // Configuracion del Formulario
    private void configureFormIndividual() {
        formIndividual = new TarjetaDestinoFinal_EstudianteForm_Individual(estudianteService.findAll(), moduloService.findAll());
        formIndividual.setWidth("25em");
        formIndividual.addListener(TarjetaDestinoFinal_EstudianteForm_Individual.SaveEvent.class,
                this::saveTarjetaIndividual);
        formIndividual.addListener(TarjetaDestinoFinal_EstudianteForm_Individual.CloseEvent.class, e -> closeEditorIndividual());
    }

    private void configureFormPorGrupo() {

        List<Estudiante> listEstudianteSeleccionados = new LinkedList<>(gridEstudiantes.getSelectedItems());
        listEstudianteSeleccionados.sort(Comparator.comparing(Estudiante::getId));

        formPorGrupo = new TarjetaDestinoFinal_EstudianteForm_Grupo(listEstudianteSeleccionados, moduloService.findAll());
        formPorGrupo.setWidth("25em");
        formPorGrupo.addListener(TarjetaDestinoFinal_EstudianteForm_Grupo.SaveEvent.class, this::saveTarjetaPorGrupo);
        formPorGrupo.addListener(TarjetaDestinoFinal_EstudianteForm_Grupo.CloseEvent.class, e -> closeEditorPorGrupo());
    }

    private void saveTarjetaIndividual(TarjetaDestinoFinal_EstudianteForm_Individual.SaveEvent event) {

        tarjetas.clear();
        boolean band = false;
        for (int i = 0; i < destinoService.findAll().size() && band == false; i++) {
            if (destinoService.findAll().get(i) instanceof DestinoFinalEstudiante) {
                tarjetaEstudiante = (DestinoFinalEstudiante) destinoService.findAll().get(i);
                if (event.getDestinoFinal().getId() != null) {
                    if (event.getDestinoFinal().getEstudiante().getUser().getUsername().equals(tarjetaEstudiante.getEstudiante().getUser().getUsername())) {
                        if (event.getDestinoFinal().getModulo().getId().equals(tarjetaEstudiante.getModulo().getId())) {
                            if (event.getDestinoFinal().getFecha().equals(tarjetaEstudiante.getFecha())) {
                                band = true;
                                tarjetas.add(tarjetaEstudiante);
                            }
                        }
                    }

                } else {
                    if (event.getDestinoFinal().getEstudiante().getUser().getUsername().equals(tarjetaEstudiante.getEstudiante().getUser().getUsername())) {
                        if (event.getDestinoFinal().getModulo().getId().equals(tarjetaEstudiante.getModulo().getId())) {
                            band = true;
                            tarjetas.add(tarjetaEstudiante);
                        }
                    }
                }
            }
        }
        if (tarjetas.size() > 0) {
            Notification notification = Notification.show(
                    "La módulo ya existe",
                    2000,
                    Notification.Position.MIDDLE);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/YYYY");
            if (event.getDestinoFinal().getId() == null) {
                try {
                    destinoService.save(event.getDestinoFinal());
                    senderService.sendSimpleEmail(
                            /* enviado a: */event.getDestinoFinal().getEstudiante().getEmail(),
                            /* asunto: */ "Entrega de Módulo",
                            /* mensaje: */ "Genius \n"
                            + "Sistema Informático para la gestión de la información de los recursos materiales y libros en la facultad 4. \n"
                            + "Usted ha recibido el Módulo: "
                            + event.getDestinoFinal().getModulo().getNombre()
                            + " el día: "
                            + formatter.format(event.getDestinoFinal().getFecha()).toString()
                    );
                    Notification notification = Notification.show(
                            "Módulo añadido",
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
                destinoService.update(event.getDestinoFinal());
                Notification notification = Notification.show(
                        "Módulo modificado",
                        2000,
                        Notification.Position.BOTTOM_START);
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            }
            toolbar.remove(total);
            total = new Html("<span>Total: <b>" + tarjetas.size() + "</b></span>");
            toolbar.addComponentAtIndex(1, total);
            toolbar.setFlexGrow(1, buttons);
            updateList();
            closeEditorIndividual();
        }

    }

    private void saveTarjetaPorGrupo(TarjetaDestinoFinal_EstudianteForm_Grupo.SaveEvent event) {

        tarjetas.clear();
        boolean band = false;
        for (int i = 0; i < destinoService.findAll().size() && band == false; i++) {
            if (destinoService.findAll().get(i) instanceof DestinoFinalEstudiante) {
                tarjetaEstudiante = (DestinoFinalEstudiante) destinoService.findAll().get(i);
                for (int j = 0; j < event.getDestinoFinal().size() && band == false; j++) {
                    if (event.getDestinoFinal().get(j).getId() != null) {
                        if (event.getDestinoFinal().get(j).getEstudiante().getUser().getUsername().equals(tarjetaEstudiante.getEstudiante().getUser().getUsername())) {
                            if (event.getDestinoFinal().get(j).getModulo().getId().equals(tarjetaEstudiante.getModulo().getId())) {
                                if (event.getDestinoFinal().get(j).getFecha().equals(tarjetaEstudiante.getFecha())) {
                                    band = true;
                                    tarjetas.add(tarjetaEstudiante);
                                }
                            }
                        }

                    } else {
                        if (event.getDestinoFinal().get(j).getEstudiante().getUser().getUsername().equals(tarjetaEstudiante.getEstudiante().getUser().getUsername())) {
                            if (event.getDestinoFinal().get(j).getModulo().getId().equals(tarjetaEstudiante.getModulo().getId())) {
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
                    "El módulo ya existe",
                    2000,
                    Notification.Position.MIDDLE);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/YYYY");
            try {
                for (int i = 0; i < event.getDestinoFinal().size(); i++) {
                    destinoService.save(event.getDestinoFinal().get(i));
                    senderService.sendSimpleEmail(
                            /* enviado a: */event.getDestinoFinal().get(i).getEstudiante().getEmail(),
                            /* asunto: */ "Entrega de Módulo",
                            /* mensaje: */ "Genius \n"
                            + "Sistema Informático para la gestión de la información de los recursos materiales y libros en la facultad 4. \n"
                            + "Usted ha recibido el Módulo: "
                            + event.getDestinoFinal().get(i).getModulo().getNombre()
                            + " el día: "
                            + formatter.format(event.getDestinoFinal().get(i).getFecha()).toString()
                    );
                }
                Notification notification = Notification.show(
                        "Módulo añadido",
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
            toolbar.remove(total);
            total = new Html("<span>Total: <b>" + tarjetas.size() + "</b></span>");
            toolbar.addComponentAtIndex(1, total);
            toolbar.setFlexGrow(1, buttons);
            updateList();
            closeEditorPorGrupo();
        }
    }

    private void editTarjetaIndividual(DestinoFinalEstudiante tarjeta) {
        if (tarjeta == null) {
            closeEditorIndividual();
        } else {
            formIndividual.setDestinoFinal(tarjeta);
            formIndividual.setVisible(true);
            addClassName("editing");
            dialogIndividual.open();
        }
    }

    private void addTarjetaIndividual() {
        gridDestinoFinal.asMultiSelect().clear();
        editTarjetaIndividual(new DestinoFinalEstudiante());
    }

    private void addTarjetaPorGrupo() {
        List<DestinoFinalEstudiante> tarjetasDestino = new LinkedList<>();
        for (int i = 0; i < gridEstudiantes.getSelectedItems().size(); i++) {
            DestinoFinalEstudiante destinoFinal = new DestinoFinalEstudiante();
            tarjetasDestino.add(destinoFinal);
        }
        formPorGrupo.setDestinoFinal(tarjetasDestino);
        formPorGrupo.setVisible(true);
        addClassName("editing");
        dialogPorGrupo.removeAll();
        /* Dialog Header */
        Button closeButton = new Button(new Icon("lumo", "cross"), (e) -> dialogPorGrupo.close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Span title = new Span("Módulo");
        Div titleDiv = new Div(title);
        titleDiv.addClassName("div_dialog_title");
        Div buttonDiv = new Div(closeButton);
        buttonDiv.addClassName("div_dialog_button");
        Div headerDialogDestinoPorGrupo = new Div(titleDiv, buttonDiv);
        headerDialogDestinoPorGrupo.addClassName("div_dialog_header");
        /* Dialog Header */
        dialogPorGrupo.add(headerDialogDestinoPorGrupo, formPorGrupo);
    }

    private void closeEditorIndividual() {
        formIndividual.setDestinoFinal(new DestinoFinalEstudiante());
        formIndividual.setVisible(false);
        removeClassName("editing");
        dialogIndividual.close();
    }

    private void closeEditorPorGrupo() {
        formPorGrupo.setDestinoFinal(new LinkedList<DestinoFinalEstudiante>());
        formIndividual.setVisible(false);
        removeClassName("editing");
        gridEstudiantes.deselectAll();
        dialogPorGrupo.close();
    }

    private void updateList() {
        tarjetas.clear();
        for (int i = 0; i < destinoService.findAll().size(); i++) {
            if (destinoService.findAll().get(i) instanceof DestinoFinalEstudiante) {
                tarjetaEstudiante = (DestinoFinalEstudiante) destinoService.findAll().get(i);
                if (tarjetaEstudiante.getEstudiante() != null) {
                    tarjetas.add(destinoService.findAll().get(i));
                }
            }
        }
        gridDestinoFinal.setItems(tarjetas);
    }
    /* Fin-Barra de tarjetas */
}
