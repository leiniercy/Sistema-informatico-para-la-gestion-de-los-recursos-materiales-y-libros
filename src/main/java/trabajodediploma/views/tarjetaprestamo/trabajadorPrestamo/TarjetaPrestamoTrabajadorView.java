/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabajodediploma.views.tarjetaprestamo.trabajadorPrestamo;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import java.security.cert.X509Certificate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import trabajodediploma.data.entity.Trabajador;
import trabajodediploma.data.entity.Libro;
import trabajodediploma.data.entity.TarjetaPrestamo;
import trabajodediploma.data.entity.TarjetaPrestamoTrabajador;
import trabajodediploma.data.service.AreaService;
import trabajodediploma.data.service.TrabajadorService;
import trabajodediploma.data.tools.EmailSenderService;
import trabajodediploma.data.service.LibroService;
import trabajodediploma.data.service.TarjetaPrestamoService;
import trabajodediploma.views.tarjetaprestamo.trabajador.TrabajadorGrid;

/**
 *
 * @author leinier
 */
public class TarjetaPrestamoTrabajadorView extends Div {

    Grid<TarjetaPrestamo> grid = new Grid<>(TarjetaPrestamo.class, false);
    GridListDataView<TarjetaPrestamo> gridListDataView;
    Grid.Column<TarjetaPrestamo> libroColumn;
    Grid.Column<TarjetaPrestamo> fechaEntregaColumn;
    Grid.Column<TarjetaPrestamo> fechaDevolucionColumn;
    Grid.Column<TarjetaPrestamo> editColumn;
    TrabajadorGrid trabajadorGrid;
    private Trabajador trabajador;
    private List<Libro> libros;
    private List<TarjetaPrestamo> prestamos;
    private TarjetaPrestamoService prestamoService;
    private TrabajadorService trabajadorService;
    private LibroService libroService;
    private EmailSenderService senderService;
    private AreaService areaService;
    TarjetaPrestamoTrabajadorForm form;
    private ComboBox<Libro> libroFilter;
    private DatePicker entregaFilter;
    private DatePicker devolucionFilter;
    private Html total;
    private HorizontalLayout info;
    private Div content;
    private Div header;
    private Dialog dialog;
    private TarjetaPrestamoTrabajador tarjetaTrabajador;
    private int cantPrestamos = 0;

    public TarjetaPrestamoTrabajadorView(
            Trabajador trabajador,
            TarjetaPrestamoService prestamoService,
            TrabajadorService trabajadorService,
            AreaService areaService,
            LibroService libroService,
            EmailSenderService senderService) {

        addClassName("container__modelo");
        this.trabajador = trabajador;
        this.prestamoService = prestamoService;
        this.trabajadorService = trabajadorService;
        this.areaService = areaService;
        this.libroService = libroService;
        this.senderService = senderService;
        this.libros = libroService.findAll();
        prestamos = new LinkedList<>();
        updateList();
        configureForm();
        configureGrid();
        getContent();
        add(content);
    }

    /* Contenido de la vista */
    private void getContent() {
        Div formContent = new Div(form);
        formContent.addClassName("form-content");
        Div gridContent = new Div(grid);
        gridContent.addClassName("grid-content");
        content = new Div();
        content.addClassName("content");
        content.setSizeFull();
        /* Dialog Header */
        Button closeButton = new Button(new Icon("lumo", "cross"), (e) -> dialog.close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Span title = new Span("Libro");
        Div titleDiv = new Div(title);
        titleDiv.addClassName("div-dialog-title");
        Div buttonDiv = new Div(closeButton);
        buttonDiv.addClassName("div-dialog-button");
        header = new Div(titleDiv, buttonDiv);
        header.addClassName("div-dialog-header");
        /* Dialog Header */
        dialog = new Dialog(header, formContent);
        content.add(menuBar(), gridContent, informacionTabla());
    }

    private HorizontalLayout informacionTabla() {
        info = new HorizontalLayout();
        info.addClassName("table_info");
        if (cantPrestamos == 1) {
            total = new Html("<span>Total: <b>" + cantPrestamos + "</b> libro</span>");
        } else if (cantPrestamos == 0 || cantPrestamos > 1) {
            total = new Html("<span>Total: <b>" + cantPrestamos + "</b> libros</span>");
        }
        info.add(total);
        return info;
    }

    /* Tabla */
 /* Configuracion de la tabla */
    private void configureGrid() {
        grid.setClassName("container__modelo__grid");
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
        editColumn = grid.addComponentColumn(target -> {
            Button editButton = new Button(VaadinIcon.EDIT.create());
            editButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            editButton.addClickListener(e -> this.editLibro((TarjetaPrestamoTrabajador) target));
            return editButton;
        }).setFlexGrow(0);

        Filtros();

        HeaderRow headerRow = grid.appendHeaderRow();
        headerRow.getCell(libroColumn).setComponent(libroFilter);
        headerRow.getCell(fechaEntregaColumn).setComponent(entregaFilter);
        headerRow.getCell(fechaDevolucionColumn).setComponent(devolucionFilter);

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

    /* Fin-Filtros */

 /* Barra de menu */
    private HorizontalLayout menuBar() {
        HorizontalLayout buttons = new HorizontalLayout();
        Button refreshButton = new Button(VaadinIcon.REFRESH.create(), click -> updateList());
        refreshButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button deleteButton = new Button(VaadinIcon.TRASH.create(), click -> deleteLibro());
        deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button addButton = new Button(VaadinIcon.PLUS.create(), click -> addLibro());
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttons.add(refreshButton, deleteButton, addButton);

        H6 nombreTrabajador = new H6();
        nombreTrabajador.add(trabajador.getNombreApellidos());
        Button salirButton = new Button(new Icon(VaadinIcon.ARROW_RIGHT),
                click -> volverAtras());
        salirButton.setIconAfterText(true);
        salirButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        nombreTrabajador.add(salirButton);
        HorizontalLayout personalInfo = new HorizontalLayout(nombreTrabajador);
        personalInfo.setAlignItems(FlexComponent.Alignment.CENTER);

        HorizontalLayout toolbar = new HorizontalLayout(buttons, personalInfo);
        toolbar.addClassName("toolbar");
        toolbar.setAlignItems(FlexComponent.Alignment.CENTER);
        toolbar.setWidth("100%");
        toolbar.setFlexGrow(1, buttons);
        toolbar.getStyle()
                .set("padding", "var(--lumo-space-wide-m)");

        return toolbar;
    }

    public void volverAtras() {
        trabajadorGrid = new TrabajadorGrid(prestamoService, trabajadorService, areaService, libroService, senderService);
        content.removeAll();
        content.add(trabajadorGrid);
    }

    private void deleteLibro() {

        try {

            if (grid.asMultiSelect().isEmpty()) {
                Notification notification = Notification.show("Debe elegir al menos un campo", 5000,
                        Notification.Position.MIDDLE);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } else {
                deleteItems(grid.getSelectedItems().size(), grid.getSelectedItems());
                updateList();
                info.remove(total);
                if (cantPrestamos == 1) {
                    total = new Html("<span>Total: <b>" + cantPrestamos + "</b> libro</span>");
                } else if (cantPrestamos == 0 || cantPrestamos > 1) {
                    total = new Html("<span>Total: <b>" + cantPrestamos + "</b> libros</span>");
                }
                info.add(total);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Notification notification = Notification.show("Ocurrió un problema al intentar eliminar el libro", 5000,
                    Notification.Position.MIDDLE);
            ;
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void deleteItems(int cantidad, Set<TarjetaPrestamo> tarjeta) {
        Notification notification;
        prestamoService.deleteAll(tarjeta);
        if (cantidad == 1) {
            notification = Notification.show("El libro ha sido eliminado", 5000, Notification.Position.BOTTOM_START);
        } else {
            notification = Notification.show("Han sido eliminados" + cantidad + " libros", 5000,
                    Notification.Position.BOTTOM_START);
        }
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    // Configuracion del Formulario
    private void configureForm() {
        form = new TarjetaPrestamoTrabajadorForm(trabajador, libros);
        form.setWidth("25em");
        form.addListener(TarjetaPrestamoTrabajadorForm.SaveEvent.class, this::saveLibro);
        form.addListener(TarjetaPrestamoTrabajadorForm.CloseEvent.class, e -> closeEditor());
    }

    private void saveLibro(TarjetaPrestamoTrabajadorForm.SaveEvent event) {
        prestamos.clear();
        List<TarjetaPrestamo> listTarjetas = prestamoService.findAll();
        boolean band = false;
        for (int i = 0; i < listTarjetas.size() && band == false; i++) {
            if (listTarjetas.get(i) instanceof TarjetaPrestamoTrabajador) {
                tarjetaTrabajador = (TarjetaPrestamoTrabajador) listTarjetas.get(i);
                if (tarjetaTrabajador.getTrabajador().getId() == trabajador.getId()
                        && event.getTarjetaPrestamo().getTrabajador().getId() == trabajador.getId()) {
                    //anadir
                    if (event.getTarjetaPrestamo().getId() == null && event.getTarjetaPrestamo().getFechaDevolucion() == null) {
                        if (event.getTarjetaPrestamo().getLibro().getId() == tarjetaTrabajador.getLibro().getId()
                                && event.getTarjetaPrestamo().getFechaPrestamo().equals(tarjetaTrabajador.getFechaPrestamo())) {
                            prestamos.add(tarjetaTrabajador);
                            band = true;
                        }
                        //modificar
                    } else if (event.getTarjetaPrestamo().getId() != null && event.getTarjetaPrestamo().getFechaDevolucion() == null) {
                        if (event.getTarjetaPrestamo().getLibro().getId() == tarjetaTrabajador.getLibro().getId()
                                && event.getTarjetaPrestamo().getFechaPrestamo().equals(tarjetaTrabajador.getFechaPrestamo())) {
                            prestamos.add(tarjetaTrabajador);
                            band = true;

                        }
                        //modificar
                    } else if (event.getTarjetaPrestamo().getId() != null && event.getTarjetaPrestamo().getFechaDevolucion() != null) {
                        if (event.getTarjetaPrestamo().getLibro().getId() == tarjetaTrabajador.getLibro().getId()
                                && event.getTarjetaPrestamo().getFechaPrestamo().equals(tarjetaTrabajador.getFechaPrestamo())
                                && event.getTarjetaPrestamo().getFechaDevolucion().equals(tarjetaTrabajador.getFechaDevolucion())) {
                            prestamos.add(tarjetaTrabajador);
                            band = true;
                        }
                    }
                }
            }
        }

        if (prestamos.size() > 0) {
            Notification notification = Notification.show(
                    "El libro ya existe",
                    5000,
                    Notification.Position.MIDDLE);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/YYYY");
            if (event.getTarjetaPrestamo().getId() == null) {

                try {
                    prestamoService.save(event.getTarjetaPrestamo());
                    AceptAllSSLCertificate();
                    senderService.sendSimpleEmail(
                            /* enviado a: */trabajador.getEmail(),
                            /* asunto: */ "Entrega de libros",
                            /* mensaje: */ "Genius\n"
                            + "Sistema Informático para la gestión de información de los recursos materiales y libros en la facultad 4.\n"
                            + "Usted ha recibido el libro: "
                            + event.getTarjetaPrestamo().getLibro().getTitulo()
                            + " el día: "
                            + formatter.format(event.getTarjetaPrestamo().getFechaPrestamo()).toString());
                    if (event.getTarjetaPrestamo().getFechaDevolucion() != null) {
                        AceptAllSSLCertificate();
                        senderService.sendSimpleEmail(
                                /* enviado a: */trabajador.getEmail(),
                                /* asunto: */ "Devolución de libros",
                                /* mensaje: */ "Genius\n"
                                + "Sistema de Gestión Académica Genius \n"
                                + "Usted ha entregado el libro: "
                                + event.getTarjetaPrestamo().getLibro().getTitulo()
                                + " el día: "
                                + formatter.format(event.getTarjetaPrestamo().getFechaDevolucion()).toString());
                    }
                    Notification notification = Notification.show(
                            "Libro añadido",
                            2000,
                            Notification.Position.BOTTOM_START);
                    notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                } catch (Exception e) {
                    // TODO: handle exception
                    Notification notification = Notification.show(
                            "Error al enviar correo electrónico a la dirección de correo seleccionada",
                            2000,
                            Notification.Position.MIDDLE);
                    notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                }

            } else {

                try {
                    prestamoService.update(event.getTarjetaPrestamo());
                    if (event.getTarjetaPrestamo().getFechaDevolucion() != null) {
                        AceptAllSSLCertificate();
                        senderService.sendSimpleEmail(
                                /* enviado a: */trabajador.getEmail(),
                                /* asunto: */ "Devolución de libros",
                                /* mensaje: */ "Genius\n"
                                + "Sistema Informático para la gestión de información de los recursos materiales y libros en la facultad 4.\n"
                                + "Usted ha entregado el libro: "
                                + event.getTarjetaPrestamo().getLibro().getTitulo()
                                + " el día: "
                                + formatter.format(event.getTarjetaPrestamo().getFechaDevolucion()).toString());
                    }
                    Notification notification = Notification.show(
                            "Libro modificado",
                            2000,
                            Notification.Position.BOTTOM_START);
                    notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    form.remove(form.fechaDevolucion);
                } catch (Exception e) {
                    // TODO: handle exception
                    Notification notification = Notification.show(
                            "Error al enviar correo electrónico a la dirección de correo seleccionada",
                            2000,
                            Notification.Position.MIDDLE);
                    notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            }
            updateList();
            info.remove(total);
            if (cantPrestamos == 1) {
                total = new Html("<span>Total: <b>" + cantPrestamos + "</b> libro</span>");
            } else if (cantPrestamos == 0 || cantPrestamos > 1) {
                total = new Html("<span>Total: <b>" + cantPrestamos + "</b> libros</span>");
            }
            info.add(total);
            closeEditor();
        }

    }

    private static void AceptAllSSLCertificate() {
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkClientTrusted(
                        java.security.cert.X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(
                        java.security.cert.X509Certificate[] certs, String authType) {
                }
            }
        };

// Install the all-trusting trust manager
        try {

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

// Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public void editLibro(TarjetaPrestamoTrabajador tarjeta) {
        if (tarjeta == null) {
            closeEditor();
        } else {
            form.setTarjetaPrestamo(tarjeta);
            form.setVisible(true);
            addClassName("editing");
            dialog.open();
        }
    }

    void addLibro() {
        grid.asMultiSelect().clear();
        editLibro(new TarjetaPrestamoTrabajador());
    }

    private void closeEditor() {
        form.setTarjetaPrestamo(new TarjetaPrestamoTrabajador());
        form.setVisible(false);
        removeClassName("editing");
        dialog.close();
    }

    private void updateList() {
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
        grid.deselectAll();
    }
    /* Fin-Barra de menu */

}
