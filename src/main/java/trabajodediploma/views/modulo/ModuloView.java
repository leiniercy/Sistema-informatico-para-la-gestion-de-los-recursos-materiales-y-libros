package trabajodediploma.views.modulo;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.contextmenu.HasMenuItems;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dialog.DialogVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import trabajodediploma.data.entity.Area;
import trabajodediploma.data.entity.Estudiante;
import trabajodediploma.data.entity.Grupo;

import trabajodediploma.data.entity.Modulo;
import trabajodediploma.data.entity.RecursoMaterial;
import trabajodediploma.data.entity.Trabajador;
import trabajodediploma.data.service.AreaService;
import trabajodediploma.data.service.EstudianteService;
import trabajodediploma.data.service.GrupoService;
import trabajodediploma.data.service.ModuloService;
import trabajodediploma.data.service.RecursoMaterialService;
import trabajodediploma.data.service.TrabajadorService;
import trabajodediploma.data.tools.EmailSenderService;
import trabajodediploma.views.MainLayout;
import trabajodediploma.views.footer.MyFooter;

@PageTitle("Módulo")
@Route(value = "modulo-view", layout = MainLayout.class)
@RolesAllowed("VD_ADIMN_ECONOMIA")
public class ModuloView extends Div {

    private Grid<Modulo> grid = new Grid<>(Modulo.class, false);
    GridListDataView<Modulo> gridListDataView;
    Grid.Column<Modulo> nombreColumn;
    Grid.Column<Modulo> materialesColumn;
    Grid.Column<Modulo> editColumn;
    private ComboBox<Modulo> filterNombre;

    private ModuloService moduloService;
    private RecursoMaterialService materialService;
    private EstudianteService estudianteService;
    private TrabajadorService trabajadorService;
    private EmailSenderService emailSenderService;
    private GrupoService grupoService;
    private AreaService areaService;
    private Dialog dialog;
    private Html total;
    private HorizontalLayout toolbar;
    private HorizontalLayout buttons;
    private Div header;
    ModuloForm form;
    MyFooter footer;

    Grid<Estudiante> gridEstudiantes = new Grid<>(Estudiante.class, false);
    GridListDataView<Estudiante> gridListDataViewEstudiante;
    Grid.Column<Estudiante> columnaEstudiante;
    private ComboBox<Estudiante> filtrarEstudiante;
    private ComboBox<Grupo> filtrarGrupo;
    private IntegerField filtrarAnno;
    private Dialog dialogEstudiante;
    private VerticalLayout filtrosEstudianteContainer;
    private Div headerDialogEstudiante;
    private HorizontalLayout buttonsDialogEst;
    private List<Estudiante> listEstudiantes;

    Grid<Trabajador> gridTrabajadores = new Grid<>(Trabajador.class, false);
    GridListDataView<Trabajador> gridListDataViewTrabajador;
    Grid.Column<Trabajador> columnaTrabajador;
    private ComboBox<Trabajador> filtrarTrabajador;
    private ComboBox<Area> filtrarArea;
    private Dialog dialogTrabajador;
    private VerticalLayout filtrosTrabajadorContainer;
    private Div headerDialogTrabajador;
    private List<Trabajador> listTrabajadores;

    public ModuloView(
            @Autowired ModuloService moduloService,
            @Autowired RecursoMaterialService materialService,
            @Autowired EstudianteService estudianteService,
            @Autowired TrabajadorService trabajadorService,
            @Autowired EmailSenderService senderService,
            @Autowired GrupoService grupoService,
            @Autowired AreaService areaService
    ) {

        addClassNames("modulo_view");
        this.moduloService = moduloService;
        this.materialService = materialService;
        this.estudianteService = estudianteService;
        this.trabajadorService = trabajadorService;
        this.emailSenderService = emailSenderService;
        this.grupoService = grupoService;
        this.areaService = areaService;
        listEstudiantes = new LinkedList<>();
        listTrabajadores = new LinkedList<>();
        Filtros();
        configureGrid();
        configurarGridEstudiante();
        configurarGridTrabajador();
        confiuracionDialogEstudiante();
        confiuracionDialogTrabajador();
        configureForm();
        footer = new MyFooter();
        add(menuBar(), getContent(), footer);
        updateList();
        closeEditor();
    }

    /* Contenido de la vista */
    private Div getContent() {
        Div formContent = new Div(form);
        formContent.addClassName("form_content");
        Div gridContent = new Div(grid);
        gridContent.addClassName("container__grid_content");
        Div container = new Div(gridContent);
        container.addClassName("container");
        container.setSizeFull();
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
        dialog = new Dialog(header, formContent);
        return container;
    }

    /* Tabla */
 /* Configuracion de la tabla */
    private void configureGrid() {
        grid.setClassName("container__grid_content__table");

        nombreColumn = grid.addColumn(Modulo::getNombre).setHeader("Nombre").setAutoWidth(true)
                .setSortable(true);

        materialesColumn = grid.addColumn(new ComponentRenderer<>(Span::new, (span, modulo) -> {
            span.setWidth("100%");
            List<RecursoMaterial> materiales = new LinkedList<>(modulo.getRecursosMateriales());
            String listMateriales = new String();
            if (materiales.size() != 0) {
                listMateriales += "" + materiales.get(0).getDescripcion();
                for (int i = 1; i < materiales.size(); i++) {
                    listMateriales += ", " + materiales.get(i).getDescripcion();
                }
            }
            span.setText(listMateriales);

        })).setHeader("Materiales").setAutoWidth(true);

        editColumn = grid.addComponentColumn(modulo -> {
            Button editButton = new Button(VaadinIcon.EDIT.create());
            editButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            editButton.addClickListener(e -> this.editModulo(modulo));
            return editButton;
        }).setFlexGrow(0);

        HeaderRow headerRow = grid.appendHeaderRow();
        headerRow.getCell(nombreColumn).setComponent(filterNombre);

        gridListDataView = grid.setItems(moduloService.findAll());
        grid.setAllRowsVisible(true);
        grid.setSizeFull();
        grid.setWidthFull();
        grid.setHeightFull();
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);

    }

    /* Configuracion de la tabla Estudiante */
    private void configurarGridEstudiante() {

        gridEstudiantes.addColumn(new ComponentRenderer<>(est -> {
            HorizontalLayout hl = new HorizontalLayout();
            hl.getStyle().set("align-items", "center");
            hl.setAlignItems(FlexComponent.Alignment.CENTER);
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

        gridListDataViewEstudiante = gridEstudiantes.setItems(estudianteService.findAll());
        gridEstudiantes.setSelectionMode(Grid.SelectionMode.MULTI);
        gridEstudiantes.getStyle().set("width", "500px").set("max-width", "100%");
    }

    /* Configuracion de la tabla Trabajador */
    private void configurarGridTrabajador() {

        gridTrabajadores.addColumn(new ComponentRenderer<>(est -> {
            HorizontalLayout hl = new HorizontalLayout();
            hl.getStyle().set("align-items", "center");
            hl.setAlignItems(FlexComponent.Alignment.CENTER);
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

        FiltrosGridTrabajador();

        gridListDataViewTrabajador = gridTrabajadores.setItems(trabajadorService.findAll());
        gridTrabajadores.setSelectionMode(Grid.SelectionMode.MULTI);
        gridTrabajadores.getStyle().set("width", "500px").set("max-width", "100%");
    }

    // Filtros
    private void Filtros() {
        filterNombre = new ComboBox<>();
        filterNombre.setPlaceholder("Filtrar");
        filterNombre.setClearButtonVisible(true);
        filterNombre.setItems(moduloService.findAll());
        filterNombre.setItemLabelGenerator(Modulo::getNombre);
        filterNombre.setWidth("100%");
        filterNombre.addValueChangeListener(event -> {
            if (filterNombre.getValue() == null) {
                gridListDataView = grid.setItems(moduloService.findAll());
            } else {
                gridListDataView.addFilter(modulo -> StringUtils.containsIgnoreCase(modulo.getNombre(),
                        filterNombre.getValue().getNombre()));
            }
        });

    }

    /* Filtros tabla de estudiantes*/
    private void FiltrosGridEstudiante() {

        filtrarEstudiante = new ComboBox<>();
        filtrarEstudiante.setItems(estudianteService.findAll());
        filtrarEstudiante.setItemLabelGenerator(estudiante -> estudiante.getUser().getName());
        filtrarEstudiante.setPlaceholder("Estudiante");
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
        filtrarGrupo.setPlaceholder("Grupo");
        filtrarGrupo.setClearButtonVisible(true);
        filtrarGrupo.setWidth("100%");
        filtrarGrupo.addValueChangeListener(event -> {
            if (filtrarGrupo.getValue() == null) {
                gridListDataViewEstudiante = gridEstudiantes.setItems(estudianteService.findAll());
            } else {
                gridListDataViewEstudiante.addFilter(estudiante -> areGrupoEqual(estudiante, filtrarGrupo));
            }
        });

        filtrarAnno = new IntegerField();
        filtrarAnno.setPlaceholder("Año académico");
        filtrarAnno.setWidth("100%");
        filtrarAnno.setHasControls(true);
        filtrarAnno.setMin(1);
        filtrarAnno.setMax(5);
        filtrarAnno.addValueChangeListener(event -> {
            if (filtrarAnno.getValue() == null) {
                gridListDataViewEstudiante = gridEstudiantes.setItems(estudianteService.findAll());
            } else {
                gridListDataViewEstudiante
                        .addFilter(estudiante -> StringUtils.containsIgnoreCase(Integer.toString(estudiante.getAnno_academico()),
                        Integer.toString(filtrarAnno.getValue())));
            }
        });

    }

    /* Filtros tabla de estudiantes*/
    private void FiltrosGridTrabajador() {

        filtrarTrabajador = new ComboBox<>();
        filtrarTrabajador.setItems(trabajadorService.findAll());
        filtrarTrabajador.setItemLabelGenerator(trabajador -> trabajador.getUser().getName());
        filtrarTrabajador.setPlaceholder("Trabajador");
        filtrarTrabajador.setClearButtonVisible(true);
        filtrarTrabajador.setWidth("100%");
        filtrarTrabajador.addValueChangeListener(event -> {
            if (filtrarTrabajador.getValue() == null) {
                gridListDataViewTrabajador = gridTrabajadores.setItems(trabajadorService.findAll());
            } else {
                gridListDataViewTrabajador.addFilter(trabajador -> areTrabajadorEqual(trabajador, filtrarTrabajador));
            }
        });
        filtrarTrabajador.setRenderer(new ComponentRenderer<>(event -> {
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

        filtrarArea = new ComboBox<>();
        filtrarArea.setItems(areaService.findAll());
        filtrarArea.setItemLabelGenerator(Area -> Area.getNombre());
        filtrarArea.setPlaceholder("Área");
        filtrarArea.setClearButtonVisible(true);
        filtrarArea.setWidth("100%");
        filtrarArea.addValueChangeListener(event -> {
            if (filtrarArea.getValue() == null) {
                gridListDataViewTrabajador = gridTrabajadores.setItems(trabajadorService.findAll());
            } else {
                gridListDataViewTrabajador.addFilter(trabajador -> areAreaEqual(trabajador, filtrarArea));
            }
        });

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

    private boolean areTrabajadorEqual(Trabajador trabajador, ComboBox<Trabajador> trabajadorFilter) {
        String trabajadorFilterValue = trabajadorFilter.getValue().getUser().getName();
        if (trabajadorFilterValue != null) {
            return StringUtils.equals(trabajador.getUser().getName(), trabajadorFilterValue);
        }
        return true;
    }

    private boolean areAreaEqual(Trabajador trabajador, ComboBox<Area> AreaFilter) {
        String AreaFilterValue = AreaFilter.getValue().getNombre();
        if (AreaFilterValue != null) {
            return StringUtils.equals(trabajador.getArea().getNombre(), AreaFilterValue);
        }
        return true;
    }

    /* Configuracion de la Dialog para seleccionar los estudiantes por grupo */
    private void confiuracionDialogEstudiante() {
        dialogEstudiante = new Dialog();
        dialogEstudiante.addThemeVariants(DialogVariant.LUMO_NO_PADDING);
        filtrosEstudianteContainer = new VerticalLayout();
        /* Dialog Header */
        Button closeButton = new Button(new Icon("lumo", "cross"), (e) -> {
            dialogEstudiante.close();
            gridEstudiantes.deselectAll();
            grid.deselectAll();
        });
        closeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        /*Menu Filtros*/
        MenuBar barraMenu = new MenuBar();
        barraMenu.addThemeVariants(MenuBarVariant.LUMO_PRIMARY);
        MenuItem filtros = createMenuIconItem(barraMenu, VaadinIcon.FILTER, "Filtros", null, false);
        SubMenu filtrosSubMenu = filtros.getSubMenu();
        //Estudiante
        Checkbox estudianteCheckBox = new Checkbox();
        estudianteCheckBox.addClickListener(event -> {
            if (!estudianteCheckBox.getValue()) {
                estudianteCheckBox.setValue(Boolean.TRUE);
                filtrosEstudianteContainer.add(filtrarEstudiante);
            } else {
                filtrosEstudianteContainer.remove(filtrarEstudiante);
                filtrarEstudiante.setValue(null);
                estudianteCheckBox.setValue(Boolean.FALSE);
            }
        });
        MenuItem estudiante = createSubMenuIconItem(filtrosSubMenu, estudianteCheckBox, VaadinIcon.USER, "Estudiante", null, true);
        estudiante.addClickListener(event -> {
            if (!estudianteCheckBox.getValue()) {
                estudianteCheckBox.setValue(Boolean.TRUE);
                filtrosEstudianteContainer.add(filtrarEstudiante);
            } else {
                filtrosEstudianteContainer.remove(filtrarEstudiante);
                filtrarEstudiante.setValue(null);
                estudianteCheckBox.setValue(Boolean.FALSE);
            }
        });
        // FIN -> Estudiante
        /*Grupo*/
        Checkbox grupoCheckBox = new Checkbox();
        grupoCheckBox.addClickListener(event -> {
            if (!grupoCheckBox.getValue()) {
                grupoCheckBox.setValue(Boolean.TRUE);
                filtrosEstudianteContainer.add(filtrarGrupo);
            } else {
                filtrosEstudianteContainer.remove(filtrarGrupo);
                filtrarGrupo.setValue(null);
                grupoCheckBox.setValue(Boolean.FALSE);
            }
        });
        MenuItem grupo = createSubMenuIconItem(filtrosSubMenu, grupoCheckBox, VaadinIcon.USERS, "Grupo", null, true);
        grupo.addClickListener(event -> {
            if (!grupoCheckBox.getValue()) {
                grupoCheckBox.setValue(Boolean.TRUE);
                filtrosEstudianteContainer.add(filtrarGrupo);
            } else {
                filtrosEstudianteContainer.remove(filtrarGrupo);
                filtrarGrupo.setValue(null);
                grupoCheckBox.setValue(Boolean.FALSE);
            }
        });
        //FIN -> Grupo
        /*Año*/
        Checkbox annoCheckBox = new Checkbox();
        annoCheckBox.addClickListener(event -> {
            if (!annoCheckBox.getValue()) {
                annoCheckBox.setValue(Boolean.TRUE);
                filtrosEstudianteContainer.add(filtrarAnno);
            } else {
                filtrosEstudianteContainer.remove(filtrarAnno);
                filtrarAnno.setValue(null);
                annoCheckBox.setValue(Boolean.FALSE);
            }
        });
        MenuItem anno = createSubMenuIconItem(filtrosSubMenu, annoCheckBox, VaadinIcon.USERS, "Año Académico", null, true);
        anno.addClickListener(event -> {
            if (!annoCheckBox.getValue()) {
                annoCheckBox.setValue(Boolean.TRUE);
                filtrosEstudianteContainer.add(filtrarAnno);
            } else {
                filtrosEstudianteContainer.remove(filtrarAnno);
                filtrarAnno.setValue(null);
                annoCheckBox.setValue(Boolean.FALSE);
            }
        });
        //FIN -> Año
        /*FIN -> Menu Filtros*/
        Div filter = new Div(barraMenu);
        filter.addClassName("div_dialog_title");
        Div buttonDiv = new Div(closeButton);
        buttonDiv.addClassName("div_dialog_button");
        headerDialogEstudiante = new Div(filter, buttonDiv);
        headerDialogEstudiante.addClassName("div_dialog_header");
        /* Dialog Header */

        Button addForm = new Button("Notificar", VaadinIcon.BELL_O.create());
        addForm.addClickListener(click -> {
            if (gridEstudiantes.getSelectedItems().size() > 0) {
                listEstudiantes = new LinkedList<>(gridEstudiantes.getSelectedItems());
                List<Modulo> listModulo = new LinkedList<>(grid.getSelectedItems());
                List<RecursoMaterial> listMateriales = new LinkedList<>(listModulo.get(0).getRecursosMateriales());
                String materiales = listMateriales.get(0).getDescripcion();
                for (int i = 0; i < listMateriales.size(); i++) {
                    materiales += ("," + listMateriales.get(i).getDescripcion());
                }
                for (int i = 0; i < listEstudiantes.size(); i++) {
                    emailSenderService.sendSimpleEmail(
                            /* enviado a: */listEstudiantes.get(i).getEmail(),
                            /* asunto: */ "Entrega de módulo",
                            /* mensaje: */ "Sistema Informático para la gestión de la información de los recursos materiales y libros en la facultad 4. \n"
                            + "Buenas, el módulo "
                            + listModulo.get(0).getNombre()
                            + "(" + materiales + ")"
                            + " se encuentra disponible."
                    );
                }
                Notification notification = Notification.show(
                        "Recogida de módulo notificada",
                        2000,
                        Notification.Position.BOTTOM_START);
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                dialogEstudiante.close();
                gridEstudiantes.deselectAll();
                grid.deselectAll();
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
        dialogEstudiante.add(headerDialogEstudiante, filtrosEstudianteContainer, gridEstudiantes, buttonsDialogEst);
    }

    /* Configuracion de la Dialog para seleccionar los Trabajadores por Area*/
    private void confiuracionDialogTrabajador() {
        dialogTrabajador = new Dialog();
        dialogTrabajador.addThemeVariants(DialogVariant.LUMO_NO_PADDING);
        filtrosTrabajadorContainer = new VerticalLayout();
        /* Dialog Header */
        Button closeButton = new Button(new Icon("lumo", "cross"), (e) -> {
            dialogTrabajador.close();
            gridTrabajadores.deselectAll();
            grid.deselectAll();
        });
        closeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        /*Menu Filtros*/
        MenuBar barraMenu = new MenuBar();
        barraMenu.addThemeVariants(MenuBarVariant.LUMO_PRIMARY);
        MenuItem filtros = createMenuIconItem(barraMenu, VaadinIcon.FILTER, "Filtros", null, false);
        SubMenu filtrosSubMenu = filtros.getSubMenu();
        /*Trabajador*/
        Checkbox trabajadorCheckBox = new Checkbox();
        trabajadorCheckBox.addClickListener(event -> {
            if (!trabajadorCheckBox.getValue()) {
                trabajadorCheckBox.setValue(Boolean.TRUE);
                filtrosTrabajadorContainer.add(filtrarTrabajador);
            } else {
                filtrosTrabajadorContainer.remove(filtrarTrabajador);
                filtrarTrabajador.setValue(null);
                trabajadorCheckBox.setValue(Boolean.FALSE);
            }
        });
        MenuItem trabajador = createSubMenuIconItem(filtrosSubMenu, trabajadorCheckBox, VaadinIcon.USER, "Trabajador", null, true);
        trabajador.addClickListener(event -> {
            if (!trabajadorCheckBox.getValue()) {
                trabajadorCheckBox.setValue(Boolean.TRUE);
                filtrosTrabajadorContainer.add(filtrarTrabajador);
            } else {
                filtrosTrabajadorContainer.remove(filtrarTrabajador);
                filtrarTrabajador.setValue(null);
                trabajadorCheckBox.setValue(Boolean.FALSE);
            }
        });
        //Fin ->Trabajador
        /*Area*/
        Checkbox areaCheckBox = new Checkbox();
        areaCheckBox.addClickListener(event -> {
            if (!areaCheckBox.getValue()) {
                areaCheckBox.setValue(Boolean.TRUE);
                filtrosTrabajadorContainer.add(filtrarArea);
            } else {
                filtrosTrabajadorContainer.remove(filtrarArea);
                filtrarArea.setValue(null);
                areaCheckBox.setValue(Boolean.FALSE);
            }
        });
        MenuItem area = createSubMenuIconItem(filtrosSubMenu, areaCheckBox, VaadinIcon.USERS, "Area", null, true);
        area.addClickListener(event -> {
            if (!areaCheckBox.getValue()) {
                areaCheckBox.setValue(Boolean.TRUE);
                filtrosTrabajadorContainer.add(filtrarArea);
            } else {
                filtrosTrabajadorContainer.remove(filtrarArea);
                filtrarArea.setValue(null);
                areaCheckBox.setValue(Boolean.FALSE);
            }
        });
        //FIN -> Area
        /*FIN -> Menu Filtros*/
        Div filter = new Div(barraMenu);
        filter.addClassName("div_dialog_title");
        Div buttonDiv = new Div(closeButton);
        buttonDiv.addClassName("div_dialog_button");
        headerDialogTrabajador = new Div(filter, buttonDiv);
        headerDialogTrabajador.addClassName("div_dialog_header");
        /* Dialog Header */

        Button addForm = new Button("Notificar", VaadinIcon.BELL_O.create());
        addForm.addClickListener(click -> {
            if (gridTrabajadores.getSelectedItems().size() > 0) {
                listTrabajadores = new LinkedList<>(gridTrabajadores.getSelectedItems());
                List<Modulo> listModulo = new LinkedList<>(grid.getSelectedItems());
                List<RecursoMaterial> listMateriales = new LinkedList<>(listModulo.get(0).getRecursosMateriales());
                String materiales = listMateriales.get(0).getDescripcion();
                for (int i = 0; i < listMateriales.size(); i++) {
                    materiales += ("," + listMateriales.get(i).getDescripcion());
                }
                for (int i = 0; i < listTrabajadores.size(); i++) {
                    emailSenderService.sendSimpleEmail(
                            /* enviado a: */listTrabajadores.get(i).getEmail(),
                            /* asunto: */ "Entrega de módulo",
                            /* mensaje: */ "Sistema Informático para la gestión de la información de los recursos materiales y libros en la facultad 4. \n"
                            + "Buenas, el módulo "
                            + listModulo.get(0).getNombre()
                            + "(" + materiales + ")"
                            + " se encuentra disponible."
                    );
                }
                Notification notification = Notification.show(
                        "Recogida de módulo notificada",
                        2000,
                        Notification.Position.BOTTOM_START);
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                dialogTrabajador.close();
                gridTrabajadores.deselectAll();
                grid.deselectAll();
            } else {
                Notification notification = Notification.show(
                        "Seleccione uno o más trabajadors",
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
        dialogTrabajador.add(headerDialogTrabajador, filtrosTrabajadorContainer, gridTrabajadores, buttonsDialogEst);

    }

    //Crear Submenu Estudiante Item
    private MenuItem createSubMenuIconItem(HasMenuItems menu, Checkbox checkBox, VaadinIcon iconName,
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

    /* Barra de menu con las Opciones de añadir, eleiminar, actualizar, notificar*/
    private HorizontalLayout menuBar() {
        buttons = new HorizontalLayout();
        Button refreshButton = new Button(VaadinIcon.REFRESH.create());
        refreshButton.addClickListener(click -> updateList());
        refreshButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button deleteButton = new Button(VaadinIcon.TRASH.create());
        deleteButton.addClickListener(click -> deleteModulo());
        deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button addButton = new Button(VaadinIcon.PLUS.create());
        addButton.addClickListener(click -> addModulo());
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        /*Menu Filtros*/
        MenuBar barraMenu = new MenuBar();
        barraMenu.addThemeVariants(MenuBarVariant.LUMO_PRIMARY);
        MenuItem filtros = createMenuIconItem(barraMenu, VaadinIcon.BELL, "Notificar", null, false);
        SubMenu filtrosSubMenu = filtros.getSubMenu();
        MenuItem itemEstudiante = createMenuIconItem(filtrosSubMenu, VaadinIcon.USERS, "Estudiantes", null, true);
        itemEstudiante.addClickListener(event -> {
            if (grid.getSelectedItems().size() > 1) {
                Notification notification = Notification.show(
                        "Debe elegir solo un Módulo",
                        2000,
                        Notification.Position.MIDDLE);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } else if (grid.getSelectedItems().size() == 0) {
                Notification notification = Notification.show(
                        "Debe elegir un Módulo",
                        2000,
                        Notification.Position.MIDDLE);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } else if (grid.getSelectedItems().size() == 1) {
                dialogEstudiante.open();
            }
        });
        MenuItem itemTrabajador = createMenuIconItem(filtrosSubMenu, VaadinIcon.USERS, "Trabajadores", null, true);
        itemTrabajador.addClickListener(event -> {
            if (grid.getSelectedItems().size() > 1) {
                Notification notification = Notification.show(
                        "Debe elegir solo un Módulo",
                        2000,
                        Notification.Position.MIDDLE);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } else if (grid.getSelectedItems().size() == 0) {
                Notification notification = Notification.show(
                        "Debe elegir un Módulo",
                        2000,
                        Notification.Position.MIDDLE);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } else if (grid.getSelectedItems().size() == 1) {
                dialogTrabajador.open();
            }
        });

        buttons.add(refreshButton, watchColumns(), deleteButton, addButton, barraMenu);
        if (moduloService.count() == 1) {
            total = new Html("<span>Total: <b>" + moduloService.count() + "</b> módulo</span>");
        } else if (moduloService.count() == 0 || moduloService.count() > 1) {
            total = new Html("<span>Total: <b>" + moduloService.count() + "</b> módulos</span>");
        }
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

    private void deleteModulo() {

        try {

            if (grid.asMultiSelect().isEmpty()) {
                Notification notification = Notification.show("Debe elegir al menos un campo", 5000,
                        Notification.Position.MIDDLE);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } else {
                deleteItems(grid.getSelectedItems().size(), grid.getSelectedItems());
                updateList();
                toolbar.remove(total);
                if (moduloService.count() == 1) {
                    total = new Html("<span>Total: <b>" + moduloService.count() + "</b> módulo</span>");
                } else if (moduloService.count() == 0 || moduloService.count() > 1) {
                    total = new Html("<span>Total: <b>" + moduloService.count() + "</b> módulos</span>");
                }
                toolbar.addComponentAtIndex(1, total);
                toolbar.setFlexGrow(1, buttons);

            }

        } catch (Exception e) {
            e.printStackTrace();
            Notification notification = Notification.show("Ocurrió un problema al intentar eliminar el modulo", 5000,
                    Notification.Position.MIDDLE);
            ;
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void deleteItems(int cantidad, Set<Modulo> modulo) {
        Notification notification;
        moduloService.deleteAll(modulo);
        if (cantidad == 1) {
            notification = Notification.show("El modulo ha sido eliminado", 5000,
                    Notification.Position.BOTTOM_START);
        } else {
            notification = Notification.show("Han sido eliminados" + cantidad + " módulos", 5000,
                    Notification.Position.BOTTOM_START);
        }
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    /* Menu de Columnas */
    private Button watchColumns() {
        Button menuButton = new Button(/* "Mostar/Ocultar Columnas" */VaadinIcon.EYE.create());
        menuButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        ColumnToggleContextMenu columnToggleContextMenu = new ColumnToggleContextMenu(
                menuButton);
        columnToggleContextMenu.addColumnToggleItem("Nombre", nombreColumn);
        columnToggleContextMenu.addColumnToggleItem("Materiales", materialesColumn);
        return menuButton;
    }

    private static class ColumnToggleContextMenu extends ContextMenu {

        public ColumnToggleContextMenu(Component target) {
            super(target);
            setOpenOnClick(true);
        }

        void addColumnToggleItem(String label, Grid.Column<Modulo> column) {
            MenuItem menuItem = this.addItem(label, e -> {
                column.setAutoWidth(true).setSortable(true).setVisible(e.getSource().isChecked());
            });
            menuItem.setCheckable(true);
            menuItem.setChecked(column.isVisible());
        }
    }

    /* Fin-Menu de Columnas */
 /* Fin-Tabla */
 /* Formulario */
    private void configureForm() {
        form = new ModuloForm(materialService.findAll());
        form.setWidth("25em");
        form.addListener(ModuloForm.SaveEvent.class, this::saveModulo);
        form.addListener(ModuloForm.CloseEvent.class, e -> closeEditor());
    }

    private void saveModulo(ModuloForm.SaveEvent event) {
        List<Modulo> módulos = moduloService.findAll();

        módulos = módulos.parallelStream().filter(
                mat -> event.getModulo().getNombre().equals(mat.getNombre())
                && event.getModulo().getRecursosMateriales().equals(mat.getRecursosMateriales())
        )
                .collect(Collectors.toList());

        if (módulos.size() != 0) {
            Notification notification = Notification.show(
                    "El modulo ya existe",
                    5000,
                    Notification.Position.MIDDLE);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        } else {
            if (event.getModulo().getId() == null) {
                moduloService.save(event.getModulo());
                Notification notification = Notification.show(
                        "Modulo añadido",
                        5000,
                        Notification.Position.BOTTOM_START);
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } else {
                moduloService.update(event.getModulo());
                Notification notification = Notification.show(
                        "Modulo modificado",
                        5000,
                        Notification.Position.BOTTOM_START);
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            }
            toolbar.remove(total);
            if (moduloService.count() == 1) {
                total = new Html("<span>Total: <b>" + moduloService.count() + "</b> módulo</span>");
            } else if (moduloService.count() == 0 || moduloService.count() > 1) {
                total = new Html("<span>Total: <b>" + moduloService.count() + "</b> módulos</span>");
            }
            toolbar.addComponentAtIndex(1, total);
            toolbar.setFlexGrow(1, buttons);
            updateList();
            closeEditor();
        }

    }

    private void editModulo(Modulo modulo) {
        if (modulo == null) {
            closeEditor();
        } else {
            form.setModulo(modulo);
            form.setVisible(true);
            addClassName("editing");
            dialog.open();
        }
    }

    private void addModulo() {
        grid.asMultiSelect().clear();
        editModulo(new Modulo());
    }

    private void closeEditor() {
        form.setModulo(null);
        form.setVisible(false);
        removeClassName("editing");
        dialog.close();
    }

    private void updateList() {
        grid.setItems(moduloService.findAll());
    }
    /* Fin-Formulario */

}
