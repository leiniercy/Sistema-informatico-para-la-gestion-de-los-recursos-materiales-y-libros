package trabajodediploma.views.recursosmateriales;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import trabajodediploma.data.entity.RecursoMaterial;
import trabajodediploma.data.service.RecursoMaterialService;
import trabajodediploma.views.MainLayout;
import trabajodediploma.views.footer.MyFooter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;

@org.springframework.stereotype.Component
@Scope("prototype")
@PageTitle("Recursos Materiales ")
@Route(value = "recurso-material-view", layout = MainLayout.class)
@RolesAllowed("VD_ADIMN_ECONOMIA")
public class RecursosMaterialesView extends Div {

    private Grid<RecursoMaterial> grid = new Grid<>(RecursoMaterial.class, false);

    RecursoMaterialService materialService;
    GridListDataView<RecursoMaterial> gridListDataView;
    Grid.Column<RecursoMaterial> codigoColumn;
    Grid.Column<RecursoMaterial> descripcionColumn;
    Grid.Column<RecursoMaterial> unidadMedidaColumn;
    Grid.Column<RecursoMaterial> cantidadColumn;
    Grid.Column<RecursoMaterial> editColumn;

    MyFooter myFooter;
    RecursosMaterialesForm form;
    private TextField filterCodigo;
    private TextField filterDescripcion;
    private TextField filterUnidadMedida;
    private IntegerField filterCantidad;

    private Dialog dialog;
    private Html total;
    private HorizontalLayout toolbar;
    private HorizontalLayout buttons;
    private Div header;

    public RecursosMaterialesView(@Autowired RecursoMaterialService materialService) {
        addClassNames("recurso-material-view");
        this.materialService = materialService;
        setSizeFull();
        Filtros();
        configureGrid();
        configureForm();
        myFooter = new MyFooter();
        add(menuBar(), getContent(), myFooter);
        updateList();
        closeEditor();
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
        Span title = new Span("Recuso Material");
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
        grid.setClassName("recursoMaterial-grid");

        codigoColumn = grid.addColumn(RecursoMaterial::getCodigo).setHeader("Código").setAutoWidth(true)
                .setSortable(true);

        descripcionColumn = grid.addColumn(RecursoMaterial::getDescripcion).setHeader("Descripción").setAutoWidth(true)
                .setSortable(true);

        unidadMedidaColumn = grid.addColumn(RecursoMaterial::getUnidadMedida).setHeader("Unidad de Medida")
                .setAutoWidth(true)
                .setSortable(true);

        cantidadColumn = grid.addColumn(RecursoMaterial::getCantidad).setHeader("Cantidad").setAutoWidth(true)
                .setSortable(true);
        editColumn = grid.addComponentColumn(material -> {
            Button editButton = new Button(VaadinIcon.EDIT.create());
            editButton.addClickListener(e -> this.editMaterial(material));
            return editButton;
        }).setFlexGrow(0);

        HeaderRow headerRow = grid.appendHeaderRow();
        headerRow.getCell(codigoColumn).setComponent(filterCodigo);
        headerRow.getCell(descripcionColumn).setComponent(filterDescripcion);
        headerRow.getCell(unidadMedidaColumn).setComponent(filterUnidadMedida);
        headerRow.getCell(cantidadColumn).setComponent(filterCantidad);

        gridListDataView = grid.setItems(materialService.findAll());
        grid.setAllRowsVisible(true);
        grid.setSizeFull();
        grid.setWidthFull();
        grid.setHeightFull();
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);

    }
    //Filtros
    private void Filtros() {

        filterCodigo = new TextField();
        filterCodigo.setPlaceholder("Filtrar");
        filterCodigo.setPrefixComponent(VaadinIcon.SEARCH.create());
        filterCodigo.setClearButtonVisible(true);
        filterCodigo.setWidth("100%");
        filterCodigo.setValueChangeMode(ValueChangeMode.EAGER);
        filterCodigo.addValueChangeListener(
                event -> gridListDataView
                        .addFilter(material -> StringUtils.containsIgnoreCase(material.getCodigo(),
                                filterCodigo.getValue())));

        filterDescripcion = new TextField();
        filterDescripcion.setPlaceholder("Filtrar");
        filterDescripcion.setPrefixComponent(VaadinIcon.SEARCH.create());
        filterDescripcion.setClearButtonVisible(true);
        filterDescripcion.setWidth("100%");
        filterDescripcion.setValueChangeMode(ValueChangeMode.EAGER);
        filterDescripcion.addValueChangeListener(
                event -> gridListDataView
                        .addFilter(material -> StringUtils.containsIgnoreCase(material.getDescripcion(),
                                filterDescripcion.getValue())));

        filterUnidadMedida = new TextField();
        filterUnidadMedida.setPlaceholder("Filtrar");
        filterUnidadMedida.setPrefixComponent(VaadinIcon.SEARCH.create());
        filterUnidadMedida.setClearButtonVisible(true);
        filterUnidadMedida.setWidth("100%");
        filterUnidadMedida.setValueChangeMode(ValueChangeMode.EAGER);
        filterUnidadMedida.addValueChangeListener(
                event -> gridListDataView
                        .addFilter(material -> StringUtils.containsIgnoreCase(material.getUnidadMedida(),
                                filterUnidadMedida.getValue())));

        filterCantidad = new IntegerField();
        filterCantidad.setPlaceholder("Filtrar");
        filterCantidad.setPrefixComponent(VaadinIcon.SEARCH.create());
        filterCantidad.setClearButtonVisible(true);
        filterCantidad.setWidth("100%");
        filterCantidad.setValueChangeMode(ValueChangeMode.EAGER);
        filterCantidad.addValueChangeListener(event -> {
            if (filterCantidad.getValue() == null) {
                gridListDataView = grid.setItems(materialService.findAll());
            } else {
                gridListDataView
                        .addFilter(material -> StringUtils.containsIgnoreCase(Integer.toString(material.getCantidad()),
                                Integer.toString(filterCantidad.getValue())));
            }
        });
    }

    private void refreshGrid() {
        grid.setVisible(true);
        grid.setItems(materialService.findAll());
    }

    private HorizontalLayout menuBar() {
        buttons = new HorizontalLayout();
        Button refreshButton = new Button(VaadinIcon.REFRESH.create());
        refreshButton.addClickListener(click -> refreshGrid());
        Button deleteButton = new Button(VaadinIcon.TRASH.create());
        deleteButton.addClickListener(click -> deleteMaterial());
        Button addButton = new Button(VaadinIcon.PLUS.create());
        addButton.addClickListener(click -> addMaterial());
        Button modelButton = new Button(VaadinIcon.FILE.create());
        buttons.add(refreshButton, watchColumns(), deleteButton, addButton, modelButton);
        if(materialService.count()==1){
            total = new Html("<span>Total: <b>" + materialService.count() + "</b> material</span>");
        
        }else if(materialService.count()==0 || materialService.count()>1){
            total = new Html("<span>Total: <b>" + materialService.count() + "</b> materiales</span>");
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

    private void deleteMaterial() {
        try {

            if (grid.asMultiSelect().isEmpty()) {
                Notification notification = Notification.show("Debe elegir al menos un campo", 5000,
                        Notification.Position.MIDDLE);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } else {
                deleteItems(grid.getSelectedItems().size(), grid.getSelectedItems());
                refreshGrid();
                toolbar.remove(total);
                if(materialService.count()==1){
                    total = new Html("<span>Total: <b>" + materialService.count() + "</b> material</span>");
                
                }else if(materialService.count()==0 || materialService.count()>1){
                    total = new Html("<span>Total: <b>" + materialService.count() + "</b> materiales</span>");
                }
                toolbar.addComponentAtIndex(1, total);
                toolbar.setFlexGrow(1, buttons);

            }

        } catch (Exception e) {
            e.printStackTrace();
            Notification notification = Notification.show("Ocurrió un problema al intentar eliminar el material", 5000,
                    Notification.Position.MIDDLE);
            ;
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void deleteItems(int cantidad, Set<RecursoMaterial> materiales) {
        Notification notification;
        materialService.deleteAll(materiales);
        if (cantidad == 1) {
            notification = Notification.show("El recuso material ha sido eliminado", 5000,
                    Notification.Position.BOTTOM_START);
        } else {
            notification = Notification.show("Han sido eliminados" + cantidad + " recursos materiales", 5000,
                    Notification.Position.BOTTOM_START);
        }
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    /* Menu de Columnas */
    private Button watchColumns() {
        Button menuButton = new Button(/* "Mostar/Ocultar Columnas" */VaadinIcon.EYE.create());

        ColumnToggleContextMenu columnToggleContextMenu = new ColumnToggleContextMenu(
                menuButton);
        columnToggleContextMenu.addColumnToggleItem("Código", codigoColumn);
        columnToggleContextMenu.addColumnToggleItem("Descripción", descripcionColumn);
        columnToggleContextMenu.addColumnToggleItem("Unidad de Medida", unidadMedidaColumn);
        columnToggleContextMenu.addColumnToggleItem("Cantidad", cantidadColumn);

        return menuButton;
    }

    private static class ColumnToggleContextMenu extends ContextMenu {

        public ColumnToggleContextMenu(Component target) {
            super(target);
            setOpenOnClick(true);
        }

        void addColumnToggleItem(String label, Grid.Column<RecursoMaterial> column) {
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
        form = new RecursosMaterialesForm();
        form.setWidth("25em");
        form.addListener(RecursosMaterialesForm.SaveEvent.class, this::saveMaterial);
        form.addListener(RecursosMaterialesForm.CloseEvent.class, e -> closeEditor());
    }

    private void saveMaterial(RecursosMaterialesForm.SaveEvent event) {
        List<RecursoMaterial> materiales = materialService.findAll();

        materiales = materiales.parallelStream().filter(
                mat -> event.getRecursoMaterial().getCodigo().equals(mat.getCodigo())
                        && event.getRecursoMaterial().getDescripcion().equals(mat.getDescripcion())
                        && event.getRecursoMaterial().getUnidadMedida().equals(mat.getUnidadMedida())
                        && event.getRecursoMaterial().getCantidad().equals(mat.getCantidad()))
                .collect(Collectors.toList());

        if (materiales.size() != 0) {
            Notification notification = Notification.show(
                    "El recurso material ya existe",
                    5000,
                    Notification.Position.MIDDLE);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        } else {
            if (event.getRecursoMaterial().getId() == null) {
                materialService.save(event.getRecursoMaterial());
                Notification notification = Notification.show(
                        "Recurso material añadido",
                        5000,
                        Notification.Position.BOTTOM_START);
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } else {
                materialService.update(event.getRecursoMaterial());
                Notification notification = Notification.show(
                        "Recurso material modificado",
                        5000,
                        Notification.Position.BOTTOM_START);
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            }
            toolbar.remove(total);
            if(materialService.count()==1){
                total = new Html("<span>Total: <b>" + materialService.count() + "</b> material</span>");
            
            }else if(materialService.count()==0 || materialService.count()>1){
                total = new Html("<span>Total: <b>" + materialService.count() + "</b> materiales</span>");
            }
            toolbar.addComponentAtIndex(1, total);
            toolbar.setFlexGrow(1, buttons);
            updateList();
            closeEditor();
        }

    }

    private void editMaterial(RecursoMaterial material) {
        if (material == null) {
            closeEditor();
        } else {
            form.setMaterial(material);
            form.setVisible(true);
            addClassName("editing");
            dialog.open();
        }
    }

    private void addMaterial() {
        grid.asMultiSelect().clear();
        RecursoMaterial r = new RecursoMaterial();
        editMaterial(r);
    }

    private void closeEditor() {
        form.setMaterial(null);
        form.setVisible(false);
        removeClassName("editing");
        dialog.close();
    }

    private void updateList() {
        grid.setItems(materialService.findAll());
    }
    /* Fin-Formulario */
}
