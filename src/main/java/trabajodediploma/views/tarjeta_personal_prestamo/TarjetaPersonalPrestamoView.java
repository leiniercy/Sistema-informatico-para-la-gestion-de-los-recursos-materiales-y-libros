package trabajodediploma.views.tarjeta_personal_prestamo;

import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import javax.annotation.security.RolesAllowed;

import org.springframework.beans.factory.annotation.Autowired;
import org.apache.commons.lang3.StringUtils;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.util.Collections;
import java.util.Comparator;
import org.apache.commons.lang3.builder.CompareToBuilder;

import trabajodediploma.data.entity.Estudiante;
import trabajodediploma.data.entity.Libro;
import trabajodediploma.data.entity.TarjetaPrestamo;
import trabajodediploma.data.entity.TarjetaPrestamoEstudiante;
import trabajodediploma.data.entity.TarjetaPrestamoTrabajador;
import trabajodediploma.data.entity.Trabajador;
import trabajodediploma.data.entity.User;
import trabajodediploma.data.service.EstudianteService;
import trabajodediploma.data.service.LibroService;
import trabajodediploma.data.service.TarjetaPrestamoService;
import trabajodediploma.data.service.TrabajadorService;
import trabajodediploma.security.AuthenticatedUser;
import trabajodediploma.views.MainLayout;
import trabajodediploma.views.footer.MyFooter;

@PageTitle("Tarjeta Personal de Prestamo")
@Route(value = "tarjeta-personal-prestamo", layout = MainLayout.class)
@RolesAllowed("USER")
public class TarjetaPersonalPrestamoView extends Div {

    Grid<TarjetaPrestamo> grid = new Grid<>(TarjetaPrestamo.class, false);
    GridListDataView<TarjetaPrestamo> gridListDataView;
    Grid.Column<TarjetaPrestamo> libroColumn;
    Grid.Column<TarjetaPrestamo> fechaEntregaColumn;
    Grid.Column<TarjetaPrestamo> fechaDevolucionColumn;
    Grid.Column<TarjetaPrestamo> editColumn;
    private Estudiante estudiante;
    private Trabajador trabajador;
    private TarjetaPrestamoTrabajador tarjetaTrabajador;
    private TarjetaPrestamoEstudiante tarjetaEstudiante;
    private User user;
    private List<Libro> libros;
    private List<TarjetaPrestamo> prestamos;
    private TarjetaPrestamoService prestamoService;
    private EstudianteService estudiantService;
    private TrabajadorService trabajadorService;
    private LibroService libroService;
    private int cantPrestamos = 0;
    private MyFooter footer;
    private ComboBox<Libro> libroFilter;
    private DatePicker entregaFilter;
    private DatePicker devolucionFilter;
    private Html total;
    private Div info;
    private Div container;

    public TarjetaPersonalPrestamoView(
            @Autowired AuthenticatedUser authenticatedUser,
            @Autowired TarjetaPrestamoService prestamoService,
            @Autowired EstudianteService estudianteService,
            @Autowired TrabajadorService trabajadorService,
            @Autowired LibroService libroService) {

        addClassName("tarjeta-personal-perstamo");
        this.prestamoService = prestamoService;
        this.estudiantService = estudianteService;
        this.trabajadorService = trabajadorService;
        this.libroService = libroService;
        this.libros = libroService.findAll();
        prestamos = new LinkedList<>();
        footer = new MyFooter();

        try {
            Optional<User> maybeUser = authenticatedUser.get();
            if (maybeUser.isPresent()) {
                user = maybeUser.get();

                Optional<Estudiante> e = estudianteService.findAll().stream()
                        .filter(event -> event.getUser().equals(user))
                        .findFirst();
                Optional<Trabajador> t = trabajadorService.findAll().stream()
                        .filter(event -> event.getUser().equals(user))
                        .findFirst();
                if (e.isEmpty() && t.isEmpty()) {
                    Image imageError = new Image("images/error.png", "Error 401");
                    imageError.addClassName("container__img");
                    H1 info = new H1("Información personal no disponible");
                    info.addClassName("container__h1");
                    container = new Div();
                    container.addClassName("container");
                    container.add(imageError, info);
                } else if (!e.isEmpty()) {
                    estudiante = e.get();
                    updateListEstudiante();
                    configureGrid();
                    getContent();
                } else if (!t.isEmpty()) {
                    trabajador = t.get();
                    updateListTrabajador();
                    configureGrid();
                    getContent();
                }
                add(container, footer);
            }

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

    }

    private void getContent() {
        Div gridContent = new Div(grid);
        gridContent.addClassName("container__div_grid");
        container = new Div();
        container.addClassName("container");
        container.add(informacionTabla(), gridContent);
    }

    private Div informacionTabla() {
        info = new Div();
        info.addClassName("container__div_info");
        if (cantPrestamos == 1) {
            total = new Html("<span>Total: <b>" + cantPrestamos + "</b> libro</span>");
        } else if (cantPrestamos == 0 || cantPrestamos > 1) {
            total = new Html("<span>Total: <b>" + cantPrestamos + "</b> libros</span>");
        }
        info.add(total);
        return info;
    }

    private void configureGrid() {
        grid.setClassName("container__div_grid__tabla");
        grid.getStyle().set("max-height", "550px");

        libroColumn = grid.addColumn(new ComponentRenderer<>(tarjeta -> {
            HorizontalLayout hl = new HorizontalLayout();
            hl.setAlignItems(Alignment.CENTER);
            Image img = new Image(tarjeta.getLibro().getImagen(), "");
            img.setHeight("3.5rem");
            Span span = new Span();
            span.setClassName("name");
            span.setText(tarjeta.getLibro().getTitulo());
            hl.add(img, span);
            return hl;
        })).setHeader("Libro").setAutoWidth(true).setSortable(true);

        fechaEntregaColumn = grid.addColumn(new ComponentRenderer<>(tarjeta -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/YYYY");
            String fecha = formatter.format(tarjeta.getFechaPrestamo()).toString();
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
                    .set("color", "var(--lumo-primary-color)")
                    .set("margin-left", "10px");
            span_fecha.add(icon);
            layout.add(span_fecha);
            layout.setAlignItems(FlexComponent.Alignment.CENTER);
            return layout;
        })).setComparator(tarjeta -> tarjeta.getFechaPrestamo()).setHeader("Fecha de Préstamo").setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.CENTER)
                .setSortable(true);

        fechaDevolucionColumn = grid.addColumn(new ComponentRenderer<>(tarjeta -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/YYYY");
            String fecha = "";
            Icon icon = new Icon(VaadinIcon.CHECK_SQUARE_O);
            if (tarjeta.getFechaDevolucion() == null) {
                fecha = "";
                icon.getStyle()
                        .set("color", "var(--lumo-error-color)")
                        .set("margin-left", "10px");

            } else {
                fecha = formatter.format(tarjeta.getFechaDevolucion()).toString();
                icon.getStyle()
                        .set("color", "var(--lumo-success-text-color)")
                        .set("margin-left", "10px");
            }
            HorizontalLayout layout = new HorizontalLayout();
            Span span_fecha = new Span();
            span_fecha.add(fecha);
            span_fecha.getStyle()
                    .set("width", "100%")
                    .set("display", "flex")
                    .set("justify-content", "center")
                    .set("align-items", "end");
            span_fecha.add(icon);
            layout.add(span_fecha);
            layout.setAlignItems(FlexComponent.Alignment.CENTER);
            return layout;
        })).setComparator(tarjeta -> tarjeta.getFechaDevolucion()).setHeader("Fecha de Devolución").setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.CENTER)
                .setSortable(true);

        Filtros();

        HeaderRow headerRow = grid.appendHeaderRow();
        headerRow.getCell(libroColumn).setComponent(libroFilter);
        headerRow.getCell(fechaEntregaColumn).setComponent(entregaFilter);
        headerRow.getCell(fechaDevolucionColumn).setComponent(devolucionFilter);

        grid.setSizeFull();
        grid.setWidthFull();
        grid.setHeightFull();
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);

    }

    private void Filtros() {

        libroFilter = new ComboBox<>();
        libroFilter.setItems(libros);
        libroFilter.setItemLabelGenerator(Libro::getTitulo);
        libroFilter.setPlaceholder("Filtrar");
        libroFilter.setClearButtonVisible(true);
        libroFilter.setWidth("100%");
        libroFilter.addValueChangeListener(event -> {
            if (libroFilter.getValue() == null) {
                gridListDataView = grid
                        .setItems(prestamos);
            } else {
                gridListDataView.addFilter(tarjeta -> areLibroEqual(tarjeta, libroFilter));
            }
        });

        entregaFilter = new DatePicker();
        entregaFilter.setPlaceholder("Filtrar");
        entregaFilter.setClearButtonVisible(true);
        entregaFilter.setWidth("100%");
        entregaFilter.addValueChangeListener(event -> {
            if (entregaFilter.getValue() == null) {
                gridListDataView = grid
                        .setItems(prestamos);
            } else {
                gridListDataView.addFilter(tarjeta -> areFechaInicioEqual(tarjeta, entregaFilter));
            }
        });

        devolucionFilter = new DatePicker();
        devolucionFilter.setPlaceholder("Filtrar");
        devolucionFilter.setClearButtonVisible(true);
        devolucionFilter.setWidth("100%");
        devolucionFilter.addValueChangeListener(event -> {
            if (devolucionFilter.getValue() == null) {
                gridListDataView = grid
                        .setItems(prestamos);
            } else {
                gridListDataView.addFilter(tarjeta -> areFechaFinEqual(tarjeta, devolucionFilter));
            }
        });

    }

    private boolean areLibroEqual(TarjetaPrestamo tarjeta, ComboBox<Libro> libroFilter) {
        String libroFilterValue = libroFilter.getValue().getTitulo();
        if (libroFilterValue != null) {
            return StringUtils.equals(tarjeta.getLibro().getTitulo(), libroFilterValue);
        }
        return true;
    }

    private boolean areFechaInicioEqual(TarjetaPrestamo tarjeta, DatePicker dateFilter) {
        String dateFilterValue = dateFilter.getValue().toString();
        String tareaDate = tarjeta.getFechaPrestamo().toString();
        if (dateFilterValue != null) {
            return StringUtils.equals(dateFilterValue, tareaDate);
        }
        return true;
    }

    private boolean areFechaFinEqual(TarjetaPrestamo tarjeta, DatePicker dateFilter) {
        String dateFilterValue = dateFilter.getValue().toString();
        String tareaDate = tarjeta.getFechaDevolucion().toString();
        if (dateFilterValue != null) {
            return StringUtils.equals(dateFilterValue, tareaDate);
        }
        return true;
    }

    /* Trabajador */
    private void updateListTrabajador() {
        prestamos.clear();
        prestamoService.findAll().parallelStream().forEach((tarjeta) -> {
            if (tarjeta instanceof TarjetaPrestamoTrabajador) {
                tarjetaTrabajador = (TarjetaPrestamoTrabajador) tarjeta;
                if (tarjetaTrabajador.getTrabajador().equals(trabajador)) {
                    prestamos.add(tarjetaTrabajador);
                }
            }
        });
        cantPrestamos = prestamos.size();
        Collections.sort(prestamos, new Comparator<>() {
            @Override
            public int compare(TarjetaPrestamo o1, TarjetaPrestamo o2) {
                return new CompareToBuilder()
                        .append(o1.getFechaPrestamo(), o2.getFechaPrestamo())
                        .toComparison();
            }
        });
        gridListDataView = grid.setItems(prestamos);
        if (cantPrestamos < 50) {
            grid.setPageSize(50);
        } else {
            grid.setPageSize(cantPrestamos);
        }
    }

    /* Fin -> Trabajador */
 /* Estudiante */
    private void updateListEstudiante() {
        prestamos.clear();
        prestamoService.findAll().parallelStream().forEach((tarjeta) -> {
            if (tarjeta instanceof TarjetaPrestamoEstudiante) {
                tarjetaEstudiante = (TarjetaPrestamoEstudiante) tarjeta;
                if (tarjetaEstudiante.getEstudiante().equals(estudiante)) {
                    prestamos.add(tarjetaEstudiante);
                }
            }
        });
        cantPrestamos = prestamos.size();
        Collections.sort(prestamos, new Comparator<>() {
            @Override
            public int compare(TarjetaPrestamo o1, TarjetaPrestamo o2) {
                return new CompareToBuilder()
                        .append(o1.getFechaPrestamo(), o2.getFechaPrestamo())
                        .toComparison();
            }
        });
        gridListDataView = grid.setItems(prestamos);
        if (cantPrestamos < 50) {
            grid.setPageSize(50);
        } else {
            grid.setPageSize(cantPrestamos);
        }
    }
    /* Fin -> Estudiante */
}
