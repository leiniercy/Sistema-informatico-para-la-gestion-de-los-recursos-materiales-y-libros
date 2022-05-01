package trabajodediploma.views.libros;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.component.contextmenu.MenuItem;
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
public class LibroView extends VerticalLayout {

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

    private Html total;
    private HorizontalLayout toolbar;
    private HorizontalLayout buttons;

    public LibroView(@Autowired LibroService libroService) {
        this.libroService = libroService;
        addClassNames("libros-view");
        setSizeFull();
        configureGrid();
        configureForm();
        myFooter = new MyFooter();
        add(menuBar(), getContent(), myFooter);
        updateList();
        closeEditor();
    }

    /*Contenido de la vista*/
    private HorizontalLayout getContent() {
        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        content.addClassNames("content");
        content.setSizeFull();
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
        headerRow.getCell(tituloColumn).setComponent(FiltrarTitle());
        headerRow.getCell(autorColumn).setComponent(FiltrarAuthor());
        headerRow.getCell(volumenColumn).setComponent(FiltrarVolumen());
        headerRow.getCell(tomoColumn).setComponent(FiltrarTomo());
        headerRow.getCell(parteColumn).setComponent(FiltrarParte());
        headerRow.getCell(cantidadColumn).setComponent(FiltrarCantidad());
        headerRow.getCell(precioColumn).setComponent(FiltrarPrecio());

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
    private Component FiltrarAuthor() {

        TextField filterAuthor = new TextField();
        filterAuthor.setPlaceholder("Filtrar");
        filterAuthor.setPrefixComponent(VaadinIcon.SEARCH.create());
        filterAuthor.setClearButtonVisible(true);
        filterAuthor.setWidth("100%");
        filterAuthor.setValueChangeMode(ValueChangeMode.EAGER);
        filterAuthor.addValueChangeListener(
                event -> gridListDataView
                        .addFilter(book -> StringUtils.containsIgnoreCase(book.getAutor(), filterAuthor.getValue()))
        );

        return filterAuthor;
    }

    private Component FiltrarTitle() {

        TextField filterTitle = new TextField();
        filterTitle.setPlaceholder("Filtrar");
        filterTitle.setPrefixComponent(VaadinIcon.SEARCH.create());
        filterTitle.setClearButtonVisible(true);
        filterTitle.setWidth("100%");
        filterTitle.setValueChangeMode(ValueChangeMode.EAGER);
        filterTitle.addValueChangeListener(
                event -> gridListDataView
                        .addFilter(book -> StringUtils.containsIgnoreCase(book.getTitulo(), filterTitle.getValue()))
        );

        return filterTitle;
    }

    private Component FiltrarVolumen() {

        IntegerField volumenFilter = new IntegerField();
        volumenFilter.setPlaceholder("Filtrar");
        volumenFilter.setPrefixComponent(VaadinIcon.SEARCH.create());
        volumenFilter.setClearButtonVisible(true);
        volumenFilter.setWidth("100%");
        volumenFilter.setValueChangeMode(ValueChangeMode.LAZY);
        volumenFilter.addValueChangeListener(
                event -> gridListDataView
                        .addFilter(libro -> StringUtils.containsIgnoreCase(Integer.toString(libro.getVolumen()), Integer.toString(volumenFilter.getValue())))
        );
        return volumenFilter;

    }

    private Component FiltrarTomo() {
        IntegerField tomoFilter = new IntegerField();
        tomoFilter.setPlaceholder("Filtrar");
        tomoFilter.setPrefixComponent(VaadinIcon.SEARCH.create());
        tomoFilter.setClearButtonVisible(true);
        tomoFilter.setWidth("100%");
        tomoFilter.setValueChangeMode(ValueChangeMode.LAZY);
        tomoFilter.addValueChangeListener(
                event -> gridListDataView
                        .addFilter(libro -> StringUtils.containsIgnoreCase(Integer.toString(libro.getTomo()), Integer.toString(tomoFilter.getValue())))
        );
        return tomoFilter;
    }

    private Component FiltrarParte() {
        IntegerField parteFilter = new IntegerField();
        parteFilter.setPlaceholder("Filtrar");
        parteFilter.setPrefixComponent(VaadinIcon.SEARCH.create());
        parteFilter.setClearButtonVisible(true);
        parteFilter.setWidth("100%");
        parteFilter.setValueChangeMode(ValueChangeMode.LAZY);
        parteFilter.addValueChangeListener(
                event -> gridListDataView
                        .addFilter(libro -> StringUtils.containsIgnoreCase(Integer.toString(libro.getParte()), Integer.toString(parteFilter.getValue())))
        );
        return parteFilter;
    }

    private Component FiltrarCantidad() {
        IntegerField parteCantidad = new IntegerField();
        parteCantidad.setPlaceholder("Filtrar");
        parteCantidad.setPrefixComponent(VaadinIcon.SEARCH.create());
        parteCantidad.setClearButtonVisible(true);
        parteCantidad.setWidth("100%");
        parteCantidad.setValueChangeMode(ValueChangeMode.LAZY);
        parteCantidad.addValueChangeListener(
                event -> gridListDataView
                        .addFilter(libro -> StringUtils.containsIgnoreCase(Integer.toString(libro.getCantidad()), Integer.toString(parteCantidad.getValue())))
        );
        return parteCantidad;
    }

    private Component FiltrarPrecio() {
        NumberField precioFilter = new NumberField();
        precioFilter.setPlaceholder("Filtrar");
        precioFilter.setPrefixComponent(VaadinIcon.SEARCH.create());
        precioFilter.setClearButtonVisible(true);
        precioFilter.setWidth("100%");
        precioFilter.setValueChangeMode(ValueChangeMode.LAZY);
        precioFilter.addValueChangeListener(
                event -> gridListDataView
                        .addFilter(libro -> StringUtils.containsIgnoreCase(Double.toString(libro.getPrecio()), Double.toString(precioFilter.getValue())))
        );
        return precioFilter;
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
    }

    private void updateList() {
        grid.setItems(libroService.findAll());
    }

    /*Fin-Formulario*/
}
