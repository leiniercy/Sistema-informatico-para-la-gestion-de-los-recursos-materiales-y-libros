package trabajodediploma.views.modulo;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.contextmenu.MenuItem;
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

import trabajodediploma.data.entity.Modulo;
import trabajodediploma.data.entity.RecursoMaterial;
import trabajodediploma.data.service.ModuloService;
import trabajodediploma.data.service.RecursoMaterialService;
import trabajodediploma.views.MainLayout;
import trabajodediploma.views.footer.MyFooter;

@PageTitle("Módulo")
@Route(value = "modulo-view", layout = MainLayout.class)
@RolesAllowed("VD_ADIMN_ECONOMIA")
public class ModuloView extends Div {

    private Grid<Modulo> grid = new Grid<>(Modulo.class, false);
    private ModuloService moduloService;
    private RecursoMaterialService materialService;
    private Dialog dialog;
    private Html total;
    private HorizontalLayout toolbar;
    private HorizontalLayout buttons;
    private Div header;
    ModuloForm form;
    MyFooter footer;
    private TextField filterNombre;
    GridListDataView<Modulo> gridListDataView;
    Grid.Column<Modulo> nombreColumn;
    Grid.Column<Modulo> materialesColumn;
    Grid.Column<Modulo> editColumn;

    public ModuloView(
            @Autowired ModuloService moduloService,
            @Autowired RecursoMaterialService materialService) {

        addClassNames("modulo_view");
        this.moduloService = moduloService;
        this.materialService = materialService;
        Filtros();
        configureGrid();
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
        Span title = new Span("Modulo");
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

    // Filtros
    private void Filtros() {
        filterNombre = new TextField();
        filterNombre.setPlaceholder("Filtrar");
        filterNombre.setPrefixComponent(VaadinIcon.SEARCH.create());
        filterNombre.setClearButtonVisible(true);
        filterNombre.setWidth("100%");
        filterNombre.setValueChangeMode(ValueChangeMode.EAGER);
        filterNombre.addValueChangeListener(
                event -> gridListDataView
                        .addFilter(modulo -> StringUtils.containsIgnoreCase(modulo.getNombre(),
                                filterNombre.getValue())));

    }

    private void refreshGrid() {
        grid.setVisible(true);
        grid.setItems(moduloService.findAll());
    }

    private HorizontalLayout menuBar() {
        buttons = new HorizontalLayout();
        Button refreshButton = new Button(VaadinIcon.REFRESH.create());
        refreshButton.addClickListener(click -> refreshGrid());
        refreshButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button deleteButton = new Button(VaadinIcon.TRASH.create());
        deleteButton.addClickListener(click -> deleteModulo());
        deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button addButton = new Button(VaadinIcon.PLUS.create());
        addButton.addClickListener(click -> addModulo());
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttons.add(refreshButton, watchColumns(), deleteButton, addButton);
        if (moduloService.count() == 1) {
            total = new Html("<span>Total: <b>" + moduloService.count() + "</b> modulo</span>");
        } else if (moduloService.count() == 0 || moduloService.count() > 1) {
            total = new Html("<span>Total: <b>" + moduloService.count() + "</b> modulos</span>");
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

    private void deleteModulo() {

        try {

            if (grid.asMultiSelect().isEmpty()) {
                Notification notification = Notification.show("Debe elegir al menos un campo", 5000,
                        Notification.Position.MIDDLE);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } else {
                deleteItems(grid.getSelectedItems().size(), grid.getSelectedItems());
                refreshGrid();
                toolbar.remove(total);
                if (moduloService.count() == 1) {
                    total = new Html("<span>Total: <b>" + moduloService.count() + "</b> modulo</span>");
                } else if (moduloService.count() == 0 || moduloService.count() > 1) {
                    total = new Html("<span>Total: <b>" + moduloService.count() + "</b> modulos</span>");
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
            notification = Notification.show("Han sido eliminados" + cantidad + " modulos", 5000,
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
        List<Modulo> modulos = moduloService.findAll();

        modulos = modulos.parallelStream().filter(
                mat -> event.getModulo().getNombre().equals(mat.getNombre())
                && event.getModulo().getRecursosMateriales().equals(mat.getRecursosMateriales())
                )
                .collect(Collectors.toList());

        if (modulos.size() != 0) {
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
                total = new Html("<span>Total: <b>" + moduloService.count() + "</b> modulo</span>");
            } else if (moduloService.count() == 0 || moduloService.count() > 1) {
                total = new Html("<span>Total: <b>" + moduloService.count() + "</b> modulos</span>");
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
