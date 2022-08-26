/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabajodediploma.views.usuarios;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import trabajodediploma.data.entity.User;
import trabajodediploma.data.service.UserService;
import trabajodediploma.views.MainLayout;
import trabajodediploma.views.footer.MyFooter;

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

    private GridListDataView<User> gridListDataView;
    Grid.Column<User> profilePictureUrlColumn;
    Grid.Column<User> userNameColumn;
    Grid.Column<User> rolColumn;
    Grid.Column<User> editColumn;

    private Dialog dialog;
    private TextField filterName;
    private TextField filterUserName;
    private TextField filterRol;
    private HorizontalLayout toolbar;
    private HorizontalLayout buttons;
    private Html total;
    private Div header;

    public UsuarioView(@Autowired UserService userService) {
        addClassNames("usuario-view");
        this.userService = userService;
        configureGrid();
        configureForm();
        Filtros();
        myFooter = new MyFooter();
        add(menuBar(), getContent(), myFooter);
    }

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
        Span title = new Span("Usuario");
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

        grid.setClassName("user-grid");

        LitRenderer<User> imagenRenderer = LitRenderer.<User>of(
                "<vaadin-horizontal-layout style=\"align-items: center;\" theme=\"spacing\">"
                        + "<vaadin-avatar img=\"${item.profilePictureUrl}\" name=\"${item.name}\" alt=\"User avatar\"></vaadin-avatar>"
                        + "  <vaadin-vertical-layout style=\"line-height: var(--lumo-line-height-m);\">"
                        + "    <span> ${item.name} </span>"
                        + "  </vaadin-vertical-layout>"
                        + "</vaadin-horizontal-layout>")
                .withProperty("profilePictureUrl", User::getProfilePictureUrl)
                .withProperty("name", User::getName);

        profilePictureUrlColumn = grid.addColumn(imagenRenderer).setHeader("Imagen").setAutoWidth(true);

        userNameColumn = grid.addColumn(User::getUsername).setHeader("Usuario").setAutoWidth(true);

        rolColumn = grid.addColumn(User::getRoles).setHeader("Rol").setAutoWidth(true).setSortable(true);

        editColumn = grid.addComponentColumn(user -> {
            Button editButton = new Button(VaadinIcon.EDIT.create());
            editButton.addClickListener(e -> this.editUser(user));
            return editButton;
        }).setFlexGrow(0);

        HeaderRow headerRow = grid.appendHeaderRow();
        headerRow.getCell(userNameColumn).setComponent(filterUserName);

        gridListDataView = grid.setItems(userService.findAll());
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
        filterName.addValueChangeListener(
                event -> gridListDataView
                        .addFilter(user -> StringUtils.containsIgnoreCase(user.getName(), filterName.getValue())));

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

        filterRol = new TextField();
        filterRol.setPlaceholder("Filtrar");
        filterRol.setPrefixComponent(VaadinIcon.SEARCH.create());
        filterRol.setClearButtonVisible(true);
        filterRol.setWidth("100%");
        filterRol.setValueChangeMode(ValueChangeMode.EAGER);
        filterRol.addValueChangeListener(
                event -> gridListDataView
                        .addFilter(user -> StringUtils.containsIgnoreCase(user.getName(), filterRol.getValue())));
    }
    /* Fin-Filtros */

    /* Barra de menu */
    private HorizontalLayout menuBar() {

        buttons = new HorizontalLayout();
        Button refreshButton = new Button(VaadinIcon.REFRESH.create());
        refreshButton.addClickListener(click -> refreshGrid());
        Button deleteButton = new Button(VaadinIcon.TRASH.create());
        deleteButton.addClickListener(click -> deleteLibro());

        buttons.add(refreshButton, deleteButton);

        total = new Html("<span>Total: <b>" + userService.count() + "</b> usuarios</span>");

        toolbar = new HorizontalLayout(buttons, total);
        toolbar.addClassName("toolbar");
        toolbar.setAlignItems(FlexComponent.Alignment.CENTER);
        toolbar.setWidth("100%");
        toolbar.setFlexGrow(1, buttons);
        toolbar.getStyle()
                .set("padding", "var(--lumo-space-wide-m)");
        return toolbar;
    }
    /* Barra de menu */

    private void refreshGrid() {
        grid.setVisible(true);
        grid.setItems(userService.findAll());
    }

    private void deleteLibro() {

        try {
            if (grid.asMultiSelect().isEmpty()) {
                Notification notification = Notification.show("Debe elegir al menos un campo", 5000,
                        Notification.Position.MIDDLE);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } else {
                deleteItems(grid.getSelectedItems().size(), grid.getSelectedItems());
                refreshGrid();
                toolbar.remove(total);
                total = new Html("<span>Total: <b>" + userService.count() + "</b> usuarios</span>");
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
            notification = Notification.show("El usuario ha sido eliminado", 5000, Notification.Position.BOTTOM_START);
        } else {
            notification = Notification.show("Han sido eliminados" + cantidad + " usuarios", 5000,
                    Notification.Position.BOTTOM_START);
        }
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    /* Formulario */
    private void configureForm() {
        form = new UsuarioForm();
        form.setWidth("25em");
        form.addListener(UsuarioForm.SaveEvent.class, this::saveUsuario);
        form.addListener(UsuarioForm.CloseEvent.class, e -> closeEditor());
    }

    private void saveUsuario(UsuarioForm.SaveEvent event) {
        List<User>listUsuarios = userService.findAll();
        listUsuarios = listUsuarios.parallelStream().filter(u-> event.getUsuario().getRoles().equals(u.getRoles())
        && event.getUsuario().getName().equals(u.getName())
        && event.getUsuario().getUsername().equals(u.getUsername())
        ).collect(Collectors.toList());
        if (listUsuarios.size() != 0) {
            Notification notification = Notification.show(
                    "El usuario ya existe",
                    5000,
                    Notification.Position.MIDDLE
            );
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        } else {
            userService.update(event.getUsuario());
            Notification notification = Notification.show(
                    "Usuario modificado",
                    5000,
                    Notification.Position.BOTTOM_START
            );
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
        grid.setItems(userService.findAll());
    }
    /* Formulario */
}
