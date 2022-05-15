package trabajodediploma.views.libros;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.security.RolesAllowed;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import trabajodediploma.data.entity.Libro;
import trabajodediploma.data.service.LibroService;
import trabajodediploma.views.MainLayout;
import trabajodediploma.views.footer.MyFooter;

@PageTitle("Libros")
@Route(value = "libro-view", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class LibroView extends Div {

    private Grid<Libro> grid = new Grid<>(Libro.class, false);

    LibroService libroService;

    GridListDataView<Libro> gridListDataView;
    Grid.Column<Libro> imagenColumn;
    Grid.Column<Libro> tituloColumn;
    Grid.Column<Libro> autorColumn;
    Grid.Column<Libro> volumenColumn;
    Grid.Column<Libro> tomoColumn;
    Grid.Column<Libro> parteColumn;
    Grid.Column<Libro> cantidadColumn;
    Grid.Column<Libro> precioColumn;
    Grid.Column<Libro> editColumn;

    MyFooter myFooter;
    LibroForm form;
    
    private Dialog dialog;
    private Html total;
    private HorizontalLayout toolbar;
    private HorizontalLayout buttons;
    private TextField filterAuthor;
    private TextField filterTitle;
    private IntegerField filterVolumen;
    private IntegerField filterTomo;
    private IntegerField filterParte;
    private IntegerField filterCantidad;
    private NumberField filterPrecio;
    private Div header;
    
    public LibroView(@Autowired LibroService libroService) {
        this.libroService = libroService;
        addClassNames("libros-view");
        setSizeFull();
        configureGrid();
        configureForm();
        Filtros();
        myFooter = new MyFooter();
        add(menuBar(), getContent(), myFooter);
        updateList();
        closeEditor();
    }

    /*Contenido de la vista*/
    private Div getContent() {

        Div formContent = new Div(form);
        formContent.addClassName("form-content");
        Div gridContent = new Div(grid);
        gridContent.addClassName("grid-content");

        Div content = new Div(gridContent);
        content.addClassName("content");
        content.setSizeFull();
        
             
        /*Dialog Header*/
        Button closeButton = new Button(new Icon("lumo", "cross"), (e) -> dialog.close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Span title = new Span("Libro");
        Div titleDiv = new Div(title);
        titleDiv.addClassName("div-dialog-title");
        Div buttonDiv = new Div(closeButton);
        buttonDiv.addClassName("div-dialog-button");
        header = new Div(titleDiv,buttonDiv);
        header.addClassName("div-dialog-header");
        /*Dialog Header*/
        
        dialog = new Dialog(header,formContent);   
        
        return content;
    }

    /*Tabla*/
 /*Configuracion de la tabla*/
    private void configureGrid() {
        grid.setClassName("libro-grid");
        LitRenderer<Libro> imagenRenderer = LitRenderer.<Libro>of("<img style='height: 64px' src=${item.imagen} />")
                .withProperty("imagen", Libro::getImagen);
        imagenColumn = grid.addColumn(imagenRenderer).setHeader("Imagen").setAutoWidth(true);
        tituloColumn = grid.addColumn(Libro::getTitulo).setHeader("Título").setAutoWidth(true).setSortable(true);
        autorColumn = grid.addColumn(Libro::getAutor).setHeader("Autor").setAutoWidth(true).setSortable(true);
        volumenColumn = grid.addColumn(Libro::getVolumen).setHeader("Vólumen").setAutoWidth(true).setSortable(true);
        tomoColumn = grid.addColumn(Libro::getTomo).setHeader("Tomo").setAutoWidth(true).setSortable(true);
        parteColumn = grid.addColumn(Libro::getParte).setHeader("Parte").setAutoWidth(true).setSortable(true);
        cantidadColumn = grid.addColumn(Libro::getCantidad).setHeader("Cantidad").setAutoWidth(true).setSortable(true);
        precioColumn = grid.addColumn(Libro::getPrecio).setHeader("Precio").setAutoWidth(true).setSortable(true);
        editColumn = grid.addComponentColumn(libro -> {
            Button editButton = new Button(VaadinIcon.EDIT.create());
            editButton.addClickListener(e -> this.editLibro(libro));
            return editButton;
        }).setFlexGrow(0);

        volumenColumn.setVisible(false);
        tomoColumn.setVisible(false);
        parteColumn.setVisible(false);

        HeaderRow headerRow = grid.appendHeaderRow();
        headerRow.getCell(tituloColumn).setComponent(filterTitle);
        headerRow.getCell(autorColumn).setComponent(filterAuthor);
        headerRow.getCell(volumenColumn).setComponent(filterVolumen);
        headerRow.getCell(tomoColumn).setComponent(filterTomo);
        headerRow.getCell(parteColumn).setComponent(filterParte);
        headerRow.getCell(cantidadColumn).setComponent(filterCantidad);
        headerRow.getCell(precioColumn).setComponent(filterPrecio);

        gridListDataView = grid.setItems(libroService.findAll());
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
        grid.setItems(libroService.findAll());
    }

    /*Filtros*/
    private void Filtros() {

        //autor
        filterAuthor = new TextField();
        filterAuthor.setPlaceholder("Filtrar");
        filterAuthor.setPrefixComponent(VaadinIcon.SEARCH.create());
        filterAuthor.setClearButtonVisible(true);
        filterAuthor.setWidth("100%");
        filterAuthor.setValueChangeMode(ValueChangeMode.EAGER);
        filterAuthor.addValueChangeListener(
                event -> gridListDataView
                        .addFilter(book -> StringUtils.containsIgnoreCase(book.getAutor(), filterAuthor.getValue()))
        );

        //titulo
        filterTitle = new TextField();
        filterTitle.setPlaceholder("Filtrar");
        filterTitle.setPrefixComponent(VaadinIcon.SEARCH.create());
        filterTitle.setClearButtonVisible(true);
        filterTitle.setWidth("100%");
        filterTitle.setValueChangeMode(ValueChangeMode.EAGER);
        filterTitle.addValueChangeListener(
                event -> gridListDataView
                        .addFilter(book -> StringUtils.containsIgnoreCase(book.getTitulo(), filterTitle.getValue()))
        );

        //voulmen
        filterVolumen = new IntegerField();
        filterVolumen.setPlaceholder("Filtrar");
        filterVolumen.setPrefixComponent(VaadinIcon.SEARCH.create());
        filterVolumen.setClearButtonVisible(true);
        filterVolumen.setWidth("100%");
        filterVolumen.setValueChangeMode(ValueChangeMode.LAZY);
        filterVolumen.addValueChangeListener(
                event -> gridListDataView
                        .addFilter(libro -> StringUtils.containsIgnoreCase(Integer.toString(libro.getVolumen()), Integer.toString(filterVolumen.getValue())))
        );

        filterTomo = new IntegerField();
        filterTomo.setPlaceholder("Filtrar");
        filterTomo.setPrefixComponent(VaadinIcon.SEARCH.create());
        filterTomo.setClearButtonVisible(true);
        filterTomo.setWidth("100%");
        filterTomo.setValueChangeMode(ValueChangeMode.LAZY);
        filterTomo.addValueChangeListener(
                event -> gridListDataView
                        .addFilter(libro -> StringUtils.containsIgnoreCase(Integer.toString(libro.getTomo()), Integer.toString(filterTomo.getValue())))
        );

        //Parte
        filterParte = new IntegerField();
        filterParte.setPlaceholder("Filtrar");
        filterParte.setPrefixComponent(VaadinIcon.SEARCH.create());
        filterParte.setClearButtonVisible(true);
        filterParte.setWidth("100%");
        filterParte.setValueChangeMode(ValueChangeMode.LAZY);
        filterParte.addValueChangeListener(
                event -> gridListDataView
                        .addFilter(libro -> StringUtils.containsIgnoreCase(Integer.toString(libro.getParte()), Integer.toString(filterParte.getValue())))
        );

        //Cantidad
        filterCantidad = new IntegerField();
        filterCantidad.setPlaceholder("Filtrar");
        filterCantidad.setPrefixComponent(VaadinIcon.SEARCH.create());
        filterCantidad.setClearButtonVisible(true);
        filterCantidad.setWidth("100%");
        filterCantidad.setValueChangeMode(ValueChangeMode.LAZY);
        filterCantidad.addValueChangeListener(
                event -> gridListDataView
                        .addFilter(libro -> StringUtils.containsIgnoreCase(Integer.toString(libro.getCantidad()), Integer.toString(filterCantidad.getValue())))
        );

        //Precio
        filterPrecio = new NumberField();
        filterPrecio.setPlaceholder("Filtrar");
        filterPrecio.setPrefixComponent(VaadinIcon.SEARCH.create());
        filterPrecio.setClearButtonVisible(true);
        filterPrecio.setWidth("100%");
        filterPrecio.setValueChangeMode(ValueChangeMode.LAZY);
        filterPrecio.addValueChangeListener(
                event -> gridListDataView
                        .addFilter(libro -> StringUtils.containsIgnoreCase(Double.toString(libro.getPrecio()), Double.toString(filterPrecio.getValue())))
        );

    }
    /*Fin-Filtros*/

 /*Barra de menu*/
    private HorizontalLayout menuBar() {
        buttons = new HorizontalLayout();
        Button refreshButton = new Button(VaadinIcon.REFRESH.create());
        refreshButton.addClickListener(click -> refreshGrid());
        Button deleteButton = new Button(VaadinIcon.TRASH.create());
        deleteButton.addClickListener(click -> deleteLibro());
        Button addButton = new Button(VaadinIcon.PLUS.create());
        addButton.addClickListener(click -> addLibro());
        buttons.add(refreshButton, watchColumns(), deleteButton, addButton);

        total = new Html("<span>Total: <b>" + libroService.count() + "</b> libros</span>");

        toolbar = new HorizontalLayout(buttons, total);
        toolbar.addClassName("toolbar");
        toolbar.setAlignItems(FlexComponent.Alignment.CENTER);
        toolbar.setWidth("100%");
        toolbar.setFlexGrow(1, buttons);
        toolbar.getStyle()
                .set("padding", "var(--lumo-space-wide-m)");

        return toolbar;
    }

    /*Fin-Barra de menu*/
    private void deleteLibro() {

        try {

            if (grid.asMultiSelect().isEmpty()) {
                Notification notification = Notification.show("Debe elegir al menos un campo", 5000, Notification.Position.MIDDLE);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } else {
                deleteItems(grid.getSelectedItems().size(), grid.getSelectedItems());
                refreshGrid();
                toolbar.remove(total);
                total = new Html("<span>Total: <b>" + libroService.count() + "</b> libros</span>");
                toolbar.addComponentAtIndex(1, total);
                toolbar.setFlexGrow(1, buttons);

            }

        } catch (Exception e) {
            e.printStackTrace();
            Notification notification = Notification.show("Ocurrió un problema al intentar eliminar el libro", 5000, Notification.Position.MIDDLE);
            ;
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void deleteItems(int cantidad, Set<Libro> libros) {
        Notification notification;
        libroService.deleteAll(libros);
        if (cantidad == 1) {
            notification = Notification.show("El libro ha sido eliminado", 5000, Notification.Position.BOTTOM_START);
        } else {
            notification = Notification.show("Han sido eliminados" + cantidad + " libros", 5000, Notification.Position.BOTTOM_START);
        }
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    /*Menu de Columnas*/
    private Button watchColumns() {
        Button menuButton = new Button(/*"Mostar/Ocultar Columnas"*/VaadinIcon.EYE.create());

        ColumnToggleContextMenu columnToggleContextMenu = new ColumnToggleContextMenu(
                menuButton);
        columnToggleContextMenu.addColumnToggleItem("Autor", autorColumn);
        columnToggleContextMenu.addColumnToggleItem("Título", tituloColumn);
        columnToggleContextMenu.addColumnToggleItem("Vólumen", volumenColumn);
        columnToggleContextMenu.addColumnToggleItem("Tomo", tomoColumn);
        columnToggleContextMenu.addColumnToggleItem("Parte", parteColumn);
        columnToggleContextMenu.addColumnToggleItem("Cantidad", cantidadColumn);
        columnToggleContextMenu.addColumnToggleItem("Precio", precioColumn);

        return menuButton;
    }

    private static class ColumnToggleContextMenu extends ContextMenu {

        public ColumnToggleContextMenu(Component target) {
            super(target);
            setOpenOnClick(true);
        }

        void addColumnToggleItem(String label, Grid.Column<Libro> column) {
            MenuItem menuItem = this.addItem(label, e -> {
                column.setAutoWidth(true).setSortable(true).setVisible(e.getSource().isChecked());
            });
            menuItem.setCheckable(true);
            menuItem.setChecked(column.isVisible());

        }
    }

    /*Fin-Menu de Columnas*/
 /*Fin-Tabla*/
    
 /*Formulario*/
    private void configureForm() {
        form = new LibroForm();
        form.setWidth("25em");
        form.addListener(LibroForm.SaveEvent.class, this::saveLibro);
        form.addListener(LibroForm.CloseEvent.class, e -> closeEditor());
    }

    private void saveLibro(LibroForm.SaveEvent event) {

        List<Libro> listLibros = libroService.findAll();

        listLibros = listLibros.parallelStream()
                .filter(lib -> event.getLibro().getImagen().equals(lib.getImagen())
                && event.getLibro().getTitulo().equals(lib.getTitulo())
                && event.getLibro().getAutor().equals(lib.getAutor())
                && event.getLibro().getVolumen().equals(lib.getVolumen())
                && event.getLibro().getTomo().equals(lib.getTomo())
                && event.getLibro().getParte().equals(lib.getParte())
                && event.getLibro().getCantidad().equals(lib.getCantidad())
                && event.getLibro().getPrecio().equals(lib.getPrecio())
                )
                .collect(Collectors.toList());

        if (listLibros.size() != 0) {
            Notification notification = Notification.show(
                    "El libro ya existe",
                    5000,
                    Notification.Position.MIDDLE
            );
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        } else {
            if (event.getLibro().getId() == null) {
                libroService.save(event.getLibro());
                Notification notification = Notification.show(
                        "Libro añadido",
                        5000,
                        Notification.Position.BOTTOM_START
                );
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } else {
                libroService.update(event.getLibro());
                Notification notification = Notification.show(
                        "Libro modificado",
                        5000,
                        Notification.Position.BOTTOM_START
                );
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            }
            toolbar.remove(total);
            total = new Html("<span>Total: <b>" + libroService.count() + "</b> libros</span>");
            toolbar.addComponentAtIndex(1, total);
            toolbar.setFlexGrow(1, buttons);
            updateList();
            closeEditor();
        }

    }

    public void editLibro(Libro libro) {
        if (libro == null) {
            closeEditor();
        } else {
            form.setLibro(libro);
            form.setVisible(true);
            addClassName("editing");
            dialog.open();
        }
    }

    void addLibro() {
        grid.asMultiSelect().clear();
        Libro b = new Libro();
        b.setImagen("");
        editLibro(b);
    }

    private void closeEditor() {
        form.setLibro(null);
        form.setVisible(false);
        removeClassName("editing");
        dialog.close();
    }

    private void updateList() {
        grid.setItems(libroService.findAll());
    }

    /*Fin-Formulario*/
}
