/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabajodediploma.views.usuarios;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.contextmenu.HasMenuItems;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.dialog.Dialog;
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
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import java.util.Collections;
import java.util.Comparator;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import trabajodediploma.data.Rol;
import trabajodediploma.data.entity.Area;
import trabajodediploma.data.entity.Estudiante;
import trabajodediploma.data.entity.Grupo;
import trabajodediploma.data.entity.Trabajador;
import trabajodediploma.data.entity.User;
import trabajodediploma.data.service.AreaService;
import trabajodediploma.data.service.EstudianteService;
import trabajodediploma.data.service.GrupoService;
import trabajodediploma.data.service.TrabajadorService;
import trabajodediploma.data.service.UserService;
import trabajodediploma.views.MainLayout;
import trabajodediploma.views.footer.MyFooter;
import org.apache.commons.lang3.builder.CompareToBuilder;

/**
 *
 * @author leinier
 */
@PageTitle("Usuarios")
@Route(value = "usuarios-view", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class UsuarioView extends Div {

    private Grid<User> grid = new Grid<>(User.class, false);
    private final MyFooter myFooter;
    private UsuarioForm form;
    private UserService userService;
    private EstudianteService estudianteService;
    private GrupoService grupoService;
    private TrabajadorService trabajadorService;
    private AreaService areaService;

    private GridListDataView<User> gridListDataView;
    Grid.Column<User> profilePictureUrlColumn;
    Grid.Column<User> userNameColumn;
    Grid.Column<User> rolColumn;
    Grid.Column<User> editColumn;

    private Dialog dialog;
    private ComboBox<Grupo> grupoFilter;
    private ComboBox<Area> areaFilter;
    private IntegerField annoAcademicoFilter;
    private TextField filterName;
    private TextField filterUserName;
    private ComboBox<Rol> filterRol;
    private HorizontalLayout toolbar;
    private HorizontalLayout buttons;
    private Html total;
    private Div header;
    private HorizontalLayout div_filtros;
    private List<Estudiante> estudiantes;
    private List<Trabajador> trabajadores;
    private List<User> usuarios;
    private int cantUsuarios = 0;

    public UsuarioView(
            @Autowired UserService userService,
            @Autowired EstudianteService estudianteService,
            @Autowired GrupoService grupoService,
            @Autowired TrabajadorService trabajadorService,
            @Autowired AreaService areaService
    ) {
        addClassNames("usuario_view");
        this.userService = userService;
        this.estudianteService = estudianteService;
        this.trabajadorService = trabajadorService;
        this.grupoService = grupoService;
        this.areaService = areaService;
        updateList();
        configureGrid();
        configureForm();
        myFooter = new MyFooter();
        div_filtros = new HorizontalLayout();
        add(menuBar(), div_filtros, getContent(), myFooter);
    }

    /* Contenido de la vista */
    private Div getContent() {

        Div formContent = new Div(form);
        formContent.addClassName("form_content");
        Div gridContent = new Div(grid);
        gridContent.addClassName("usuario_view__container__div_grid");
        Div container = new Div(gridContent);
        container.addClassName("usuario_view__container");
        container.setSizeFull();
        /* Dialog Header */
        Button closeButton = new Button(new Icon("lumo", "cross"), (e) -> dialog.close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Span title = new Span("Usuario");
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
        grid.setClassName("usuario_view__container__div_grid");
        grid.getStyle().set("max-height", "550px");

        LitRenderer<User> imagenRenderer = LitRenderer.<User>of(
                "<vaadin-horizontal-layout style=\"align-items: center;\" theme=\"spacing\">"
                + "<vaadin-avatar img=\"${item.profilePictureUrl}\" name=\"${item.name}\" alt=\"User avatar\"></vaadin-avatar>"
                + "  <vaadin-vertical-layout style=\"line-height: var(--lumo-line-height-m);\">"
                + "    <span> ${item.name} </span>"
                + "  </vaadin-vertical-layout>"
                + "</vaadin-horizontal-layout>")
                .withProperty("profilePictureUrl", User::getProfilePictureUrl)
                .withProperty("name", User::getName);

        profilePictureUrlColumn = grid.addColumn(imagenRenderer).setHeader("Nombre").setAutoWidth(true);

        userNameColumn = grid.addColumn(User::getUsername).setHeader("Usuario").setAutoWidth(true);

        rolColumn = grid.addColumn(new ComponentRenderer<>(Span::new, (span, rol) -> {
            span.setWidth("100%");
            List<Rol> rols = new LinkedList<>(rol.getRoles());
            String listRoles = new String();
            if (rols.size() != 0) {
                listRoles += "" + rols.get(0).getRolname();
                for (int i = 1; i < rols.size(); i++) {
                    listRoles += ", " + rols.get(i).getRolname();
                }
            }
            span.setText(listRoles);
        })).setHeader("Rol").setAutoWidth(true);

        editColumn = grid.addComponentColumn(user -> {
            Button editButton = new Button(VaadinIcon.EDIT.create());
            editButton.addClickListener(e -> this.editUser(user));
            editButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            return editButton;
        }).setFlexGrow(0);

        Filtros();
        HeaderRow headerRow = grid.appendHeaderRow();
        headerRow.getCell(userNameColumn).setComponent(filterUserName);
        headerRow.getCell(profilePictureUrlColumn).setComponent(filterName);
        headerRow.getCell(rolColumn).setComponent(filterRol);

//        gridListDataView = grid.setItems(userService.findAll());
//        if (userService.findAll().size() < 50) {
//            grid.setPageSize(50);
//        } else {
//            grid.setPageSize(userService.findAll().size());
//        }
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

        // nombre
        filterName = new TextField();
        filterName.setPlaceholder("Filtrar");
        filterName.setPrefixComponent(VaadinIcon.SEARCH.create());
        filterName.setClearButtonVisible(true);
        filterName.setWidth("100%");
        filterName.setValueChangeMode(ValueChangeMode.EAGER);
        filterName.addValueChangeListener(event -> {
            gridListDataView.addFilter(user -> StringUtils.containsIgnoreCase(user.getName(), filterName.getValue()));
        });

        // usuario
        filterUserName = new TextField();
        filterUserName.setPlaceholder("Filtrar");
        filterUserName.setPrefixComponent(VaadinIcon.SEARCH.create());
        filterUserName.setClearButtonVisible(true);
        filterName.setWidth("100%");
        filterUserName.setValueChangeMode(ValueChangeMode.EAGER);
        filterUserName.addValueChangeListener(
                event -> gridListDataView
                        .addFilter(
                                user -> StringUtils.containsIgnoreCase(user.getUsername(), filterUserName.getValue())));

        filterRol = new ComboBox<>();
        filterRol.setPlaceholder("Filtrar");
        filterRol.setItems(Rol.getVD_ADIMN_ECONOMIA(),
                Rol.getASISTENTE_CONTROL(),
                Rol.getRESP_ALMACEN());
        filterRol.setItemLabelGenerator(Rol::getRolname);
        filterRol.setClearButtonVisible(true);
        filterRol.setWidth("100%");
        filterRol.addValueChangeListener(event -> {
            if (filterRol.getValue() == null) {
                gridListDataView = grid.setItems(usuarios);
            } else {
                gridListDataView.addFilter(user -> areRolEqual(user.getRoles(), filterRol));
            }
        });

        grupoFilter = new ComboBox<>();
        grupoFilter.setItems(grupoService.findAll());
        grupoFilter.setItemLabelGenerator(Grupo::getNumero);
        grupoFilter.setPlaceholder("Grupo");
        grupoFilter.setWidth("100%");
        grupoFilter.addValueChangeListener(event -> {
            if (grupoFilter.getValue() == null) {
                gridListDataView = grid.setItems(usuarios);
            } else {
                //buscar estudiantes por grupo
                List<User> listUser = new LinkedList<>();
                for (int i = 0; i < estudiantes.size(); i++) {
                    if (areGrupoEqual(estudiantes.get(i), grupoFilter)) {
                        listUser.add(estudiantes.get(i).getUser());
                    }
                }
                gridListDataView = grid.setItems(listUser);
                if (listUser.size() < 50) {
                    grid.setPageSize(50);
                } else {
                    grid.setPageSize(listUser.size());
                }
            }
        });

        areaFilter = new ComboBox<>();
        areaFilter.setItems(areaService.findAll());
        areaFilter.setItemLabelGenerator(Area::getNombre);
        areaFilter.setPlaceholder("Área");
        areaFilter.setWidth("100%");
        areaFilter.addValueChangeListener(event -> {
            if (areaFilter.getValue() == null) {
                gridListDataView = grid.setItems(usuarios);
            } else {
                //buscar trabajadores
                List<User> listUser = new LinkedList<>();
                for (int i = 0; i < trabajadores.size(); i++) {
                    if (areAreaEqual(trabajadores.get(i), areaFilter)) {
                        listUser.add(trabajadores.get(i).getUser());
                    }
                }
                gridListDataView = grid.setItems(listUser);
                if (listUser.size() < 50) {
                    grid.setPageSize(50);
                } else {
                    grid.setPageSize(listUser.size());
                }
            }
        });

        annoAcademicoFilter = new IntegerField();
        annoAcademicoFilter.setPlaceholder("Año Académico");
        annoAcademicoFilter.setClearButtonVisible(true);
        annoAcademicoFilter.setHasControls(true);
        annoAcademicoFilter.setMin(1);
        annoAcademicoFilter.setMax(5);
        annoAcademicoFilter.setWidth("100%");
        annoAcademicoFilter.setValueChangeMode(ValueChangeMode.LAZY);
        annoAcademicoFilter.addValueChangeListener(event -> {
            if (annoAcademicoFilter.getValue() == null) {
                gridListDataView = grid.setItems(usuarios);
            } else {
                //buscar estudiantes por año academico
                List<User> listUser = new LinkedList<>();
                for (int i = 0; i < estudiantes.size(); i++) {
                    if (annoAcademicoFilter.getValue() == estudiantes.get(i).getAnno_academico()) {
                        listUser.add(estudiantes.get(i).getUser());
                    }
                }
                gridListDataView = grid.setItems(listUser);
                if (listUser.size() < 50) {
                    grid.setPageSize(50);
                } else {
                    grid.setPageSize(listUser.size());
                }

            }
        });

    }

    private boolean areGrupoEqual(Estudiante estudiante, ComboBox<Grupo> grupoFilter) {
        String grupoFilterValue = grupoFilter.getValue().getNumero();
        if (grupoFilterValue != null) {
            return StringUtils.equals(estudiante.getGrupo().getNumero(), grupoFilterValue);
        }
        return true;
    }

    private boolean areAreaEqual(Trabajador trabajador, ComboBox<Area> areaFilter) {
        String areaFilterValue = areaFilter.getValue().getNombre();
        if (areaFilterValue != null) {
            return StringUtils.equals(trabajador.getArea().getNombre(), areaFilterValue);
        }
        return true;
    }

    private boolean areRolEqual(Set<Rol> roles, ComboBox<Rol> rolFilter) {
        String rolFilterValue = rolFilter.getValue().getRolname();
        if (rolFilterValue != null) {
            List<Rol> list = new LinkedList<>(roles);
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getRolname().equals(rolFilterValue)) {
                    return true;
                }
            }
        }
        return false;
    }

    /* Fin-Filtros */

 /* Barra de menu */
    private HorizontalLayout menuBar() {

        buttons = new HorizontalLayout();
        Button refreshButton = new Button(VaadinIcon.REFRESH.create());
        refreshButton.addClickListener(click -> updateList());
        refreshButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button deleteButton = new Button(VaadinIcon.TRASH.create());
        deleteButton.addClickListener(click -> deleteLibro());
        deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        /*Menu Filtros*/
        MenuBar barraMenu = new MenuBar();
        barraMenu.addThemeVariants(MenuBarVariant.LUMO_PRIMARY);
        MenuItem filtros = createMenuIconItem(barraMenu, VaadinIcon.FILTER, "Filtros", null, false);
        SubMenu filtrosSubMenu = filtros.getSubMenu();

        /*Año academico*/
        Checkbox annoAcademicoCheckBox = new Checkbox();
        annoAcademicoCheckBox.addClickListener(event -> {
            if (!annoAcademicoCheckBox.getValue()) {
                annoAcademicoCheckBox.setValue(Boolean.TRUE);
                div_filtros.add(annoAcademicoFilter);
            } else {
                div_filtros.remove(annoAcademicoFilter);
                annoAcademicoFilter.setValue(null);
                annoAcademicoCheckBox.setValue(Boolean.FALSE);
            }
        });
        MenuItem anno_academico = createSubMenuIconItem(filtrosSubMenu, annoAcademicoCheckBox, VaadinIcon.USERS, "Año académico", null, true);
        anno_academico.addClickListener(event -> {
            if (!annoAcademicoCheckBox.getValue()) {
                annoAcademicoCheckBox.setValue(Boolean.TRUE);
                div_filtros.add(annoAcademicoFilter);
            } else {
                div_filtros.remove(annoAcademicoFilter);
                annoAcademicoFilter.setValue(null);
                annoAcademicoCheckBox.setValue(Boolean.FALSE);
            }
        });
        //FIN -> Año academico
        /*Grupo*/
        Checkbox grupoCheckBox = new Checkbox();
        grupoCheckBox.addClickListener(event -> {
            if (!grupoCheckBox.getValue()) {
                grupoCheckBox.setValue(Boolean.TRUE);
                div_filtros.add(grupoFilter);
            } else {
                div_filtros.remove(grupoFilter);
                grupoFilter.setValue(null);
                grupoCheckBox.setValue(Boolean.FALSE);
            }
        });
        MenuItem grupo = createSubMenuIconItem(filtrosSubMenu, grupoCheckBox, VaadinIcon.USERS, "Grupo", null, true);
        grupo.addClickListener(event -> {
            if (!grupoCheckBox.getValue()) {
                grupoCheckBox.setValue(Boolean.TRUE);
                div_filtros.add(grupoFilter);
            } else {
                div_filtros.remove(grupoFilter);
                grupoFilter.setValue(null);
                grupoCheckBox.setValue(Boolean.FALSE);
            }
        });
        //FIN -> Grupo
        /*Area*/
        Checkbox areaCheckBox = new Checkbox();
        areaCheckBox.addClickListener(event -> {
            if (!areaCheckBox.getValue()) {
                areaCheckBox.setValue(Boolean.TRUE);
                div_filtros.add(areaFilter);
            } else {
                div_filtros.remove(areaFilter);
                areaFilter.setValue(null);
                areaCheckBox.setValue(Boolean.FALSE);
            }
        });
        MenuItem area = createSubMenuIconItem(filtrosSubMenu, areaCheckBox, VaadinIcon.USERS, "Área", null, true);
        area.addClickListener(event -> {
            if (!areaCheckBox.getValue()) {
                areaCheckBox.setValue(Boolean.TRUE);
                div_filtros.add(areaFilter);
            } else {
                div_filtros.remove(areaFilter);
                areaFilter.setValue(null);
                areaCheckBox.setValue(Boolean.FALSE);
            }
        });
        //FIN -> Area
        /*FIN -> Menu Filtros*/
        buttons.add(refreshButton, deleteButton, barraMenu);

        total = new Html("<span>Total: <b>" + cantUsuarios + "</b> usuarios</span>");

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

    /* Barra de menu */
    private void deleteLibro() {

        try {
            if (grid.asMultiSelect().isEmpty()) {
                Notification notification = Notification.show("Debe elegir al menos un campo", 5000,
                        Notification.Position.MIDDLE);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } else {
                deleteItems(grid.getSelectedItems().size(), grid.getSelectedItems());
                updateList();
                toolbar.remove(total);
                total = new Html("<span>Total: <b>" + cantUsuarios + "</b> usuarios</span>");
                toolbar.addComponentAtIndex(1, total);
                toolbar.setFlexGrow(1, buttons);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Notification notification = Notification.show("Ocurrió un problema al intentar eliminar el usuario", 5000,
                    Notification.Position.MIDDLE);
            ;
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }

    }

    private void deleteItems(int cantidad, Set<User> users) {
        Notification notification;
        userService.deleteAll(users);
        if (cantidad == 1) {
            notification = Notification.show("El usuario ha sido eliminado", 2000, Notification.Position.BOTTOM_START);
        } else {
            notification = Notification.show("Han sido eliminados" + cantidad + " usuarios", 2000,
                    Notification.Position.BOTTOM_START);
        }
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    /* Formulario */
    private void configureForm() {
        form = new UsuarioForm();
        form.setWidth("25em");
        form.addListener(UsuarioForm.SaveEvent.class,
                this::saveUsuario);
        form.addListener(UsuarioForm.CloseEvent.class,
                e -> closeEditor());
    }

    private void saveUsuario(UsuarioForm.SaveEvent event) {
        List<User> listUsuarios = userService.findAll();
        listUsuarios = listUsuarios.parallelStream().filter(u -> event.getUsuario().getRoles().equals(u.getRoles())
                && event.getUsuario().getName().equals(u.getName())
                && event.getUsuario().getUsername().equals(u.getUsername())).collect(Collectors.toList());
        if (listUsuarios.size() != 0) {
            Notification notification = Notification.show(
                    "El usuario ya existe",
                    2000,
                    Notification.Position.MIDDLE);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        } else {
            userService.update(event.getUsuario());
            Notification notification = Notification.show(
                    "Usuario modificado",
                    2000,
                    Notification.Position.BOTTOM_START);
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        }
        toolbar.remove(total);
        total = new Html("<span>Total: <b>" + userService.count() + "</b> usaurios</span>");
        toolbar.addComponentAtIndex(1, total);
        toolbar.setFlexGrow(1, buttons);
        updateList();
        closeEditor();
    }

    private void editUser(User user) {
        if (user == null) {
            closeEditor();
        } else {
            form.setUser(user);
            form.setVisible(true);
            addClassName("editing");
            dialog.open();
        }
    }

    private void closeEditor() {
        form.setUser(null);
        form.setVisible(false);
        removeClassName("editing");
        dialog.close();
    }

    private void updateList() {
        sortList();
        cantUsuarios = usuarios.size();
        gridListDataView = grid.setItems(usuarios);
        if (cantUsuarios < 50) {
            grid.setPageSize(50);
        } else {
            grid.setPageSize(cantUsuarios);
        }
        grid.deselectAll();
    }

    private void sortList() {
//        lista de usuarios
        usuarios = userService.findAll();
        Collections.sort(usuarios, new Comparator<>() {
            @Override
            public int compare(User o1, User o2) {
                return new CompareToBuilder()
                        .append(o1.getName(), o2.getName())
                        .append(o1.getUsername(), o2.getUsername())
                        .toComparison();
            }
        });
//        lista de estudiantes
        estudiantes = estudianteService.findAll();
        Collections.sort(estudiantes, new Comparator<>() {
            @Override
            public int compare(Estudiante o1, Estudiante o2) {
                return new CompareToBuilder()
                        .append(o1.getAnno_academico(), o2.getAnno_academico())
                        .append(o1.getGrupo().getNumero(), o2.getGrupo().getNumero())
                        .toComparison();
            }
        });
//        lista de trabajadores
        trabajadores = trabajadorService.findAll();
        Collections.sort(trabajadores, new Comparator<>() {
            @Override
            public int compare(Trabajador o1, Trabajador o2) {
                return new CompareToBuilder()
                        .append(o1.getArea().getNombre(), o2.getArea().getNombre())
                        //.compare(o1.getGrupo().getNumero(), o2.getGrupo().getNumero())
                        .toComparison();
            }
        });
    }
    /* Formulario */
}
