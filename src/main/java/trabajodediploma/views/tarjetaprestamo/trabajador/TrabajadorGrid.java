/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabajodediploma.views.tarjetaprestamo.trabajador;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Text;
import trabajodediploma.views.tarjetaprestamo.trabajadorPrestamo.TarjetaPrestamoTrabajadorView;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.contextmenu.HasMenuItems;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
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
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
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
import trabajodediploma.data.entity.Area;
import trabajodediploma.data.entity.TarjetaPrestamo;
import trabajodediploma.data.entity.TarjetaPrestamoTrabajador;
import trabajodediploma.data.service.TrabajadorService;
import trabajodediploma.data.service.AreaService;
import trabajodediploma.data.service.LibroService;
import trabajodediploma.data.service.TarjetaPrestamoService;
import trabajodediploma.data.tools.EmailSenderService;
import trabajodediploma.views.tarjetaprestamo.trabajadorPrestamo.TarjetaPrestamoTrabajadorForm;

/**
 *
 * @author leinier
 */
public class TrabajadorGrid extends Div {

    private Grid<Trabajador> gridTrabajadores = new Grid<>(Trabajador.class, false);
    TrabajadorForm form;
    TarjetaPrestamoTrabajadorView tarjetaPrestamoTrabajadorView;
    private TarjetaPrestamoTrabajador tarjetaTrabajador;
    private TarjetaPrestamoService prestamoService;
    private TrabajadorService trabajadorService;
    private AreaService areaService;
    private LibroService libroService;
    private List<TarjetaPrestamo> prestamos;
    private List<Trabajador>trabajadores;
    private int cantTrabajadores=0;
    GridListDataView<Trabajador> gridListDataView;
    Grid.Column<Trabajador> nombreColumn;
    Grid.Column<Trabajador> tarjetaColumn;

    private ComboBox<Area> areaFilter;
    private ComboBox<Trabajador> trabajadorFilter;
    private Div content;
    private EmailSenderService senderService;
    private HorizontalLayout barra_menu;
    private HorizontalLayout div_filtros;
    private Dialog dialog;
    private Div header;

    public TrabajadorGrid(
            TarjetaPrestamoService prestamoService,
            TrabajadorService trabajadorService,
            AreaService areaService,
            LibroService libroService,
            EmailSenderService senderService) {
        addClassName("container___trabajador_grid");
        this.prestamoService = prestamoService;
        this.trabajadorService = trabajadorService;
        this.libroService = libroService;
        this.areaService = areaService;
        this.senderService = senderService;
        prestamos = new LinkedList<>();
        trabajadores=trabajadorService.findAll();
        cantTrabajadores = trabajadores.size();
         Collections.sort(trabajadores, new Comparator<>() {
            @Override
            public int compare(Trabajador o1, Trabajador o2) {
                return new CompareToBuilder()
                        .append(o1.getArea().getNombre(), o2.getArea().getNombre())
                        .toComparison();
            }
        });
        configureGrid();
        menuBar();
        content = new Div();
        content.addClassName("container___trabajador_grid__div");
        content.add(barra_menu, div_filtros, gridTrabajadores);
        add(content);

    }

    private void getContent() {
        Div formContent = new Div(form);
        formContent.addClassName("form-content");
        /* Dialog Header */
        Button closeButton = new Button(new Icon("lumo", "cross"), (e) -> dialog.close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Span title = new Span("Préstamo");
        Div titleDiv = new Div(title);
        titleDiv.addClassName("div-dialog-title");
        Div buttonDiv = new Div(closeButton);
        buttonDiv.addClassName("div-dialog-button");
        header = new Div(titleDiv, buttonDiv);
        header.addClassName("div-dialog-header");
        /* Dialog Header */
        dialog = new Dialog(header, formContent);

    }

    private void configureGrid() {
        gridTrabajadores.setClassName("container___trabajador_grid__div__table");
        gridTrabajadores.getStyle().set("max-height", "550px");
        
        nombreColumn = gridTrabajadores.addColumn(new ComponentRenderer<>(est -> {
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
        })).setHeader("Nombre").setAutoWidth(true).setSortable(true);

        tarjetaColumn = gridTrabajadores.addComponentColumn(event -> {
            Button cardButton = new Button("Tarjeta", VaadinIcon.FILE_TABLE.create(), e -> this.editCard(event));
            cardButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            return cardButton;
        }).setAutoWidth(true).setTextAlign(ColumnTextAlign.END);

        Filtros();

        gridListDataView = gridTrabajadores.setItems(trabajadores);
       
        if (cantTrabajadores < 50) {
          gridTrabajadores.setPageSize(50);
        } else {
          gridTrabajadores.setPageSize(cantTrabajadores);
        }
        
        gridTrabajadores.setAllRowsVisible(true);
        gridTrabajadores.setSelectionMode(Grid.SelectionMode.MULTI);
        gridTrabajadores.setSizeFull();
        gridTrabajadores.setWidthFull();
        gridTrabajadores.setHeightFull();
        gridTrabajadores.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
        gridTrabajadores.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        gridTrabajadores.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
    }

    //Barra de Menu
    private void menuBar() {
        barra_menu = new HorizontalLayout();

        Button anadirPor = new Button("Añadir", VaadinIcon.PLUS.create(), click -> {
            if (gridTrabajadores.getSelectedItems().size() == 0) {
                Notification notification = Notification.show(
                        "Debe elegir al menos un elemento",
                        2000,
                        Notification.Position.MIDDLE);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } else if (gridTrabajadores.getSelectedItems().size() > 0) {
                configureForm();
                getContent();
                addLibro(gridTrabajadores.getSelectedItems());
            }
        });
        anadirPor.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

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
                div_filtros.add(trabajadorFilter);
            } else {
                div_filtros.remove(trabajadorFilter);
                trabajadorFilter.setValue(null);
                trabajadorCheckBox.setValue(Boolean.FALSE);
            }
        });
        MenuItem trabajador = createSubMenuIconItem(filtrosSubMenu, trabajadorCheckBox, VaadinIcon.USER, "Trabajador", null, true);
        trabajador.addClickListener(event -> {
            if (!trabajadorCheckBox.getValue()) {
                trabajadorCheckBox.setValue(Boolean.TRUE);
                div_filtros.add(trabajadorFilter);
            } else {
                div_filtros.remove(trabajadorFilter);
                trabajadorFilter.setValue(null);
                trabajadorCheckBox.setValue(Boolean.FALSE);
            }
        });
        //Fin ->Trabajador
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
        MenuItem area = createSubMenuIconItem(filtrosSubMenu, areaCheckBox, VaadinIcon.USERS, "Area", null, true);
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

        barra_menu.add(barraMenu, anadirPor);
        barra_menu.setWidth("100%");
        barra_menu.getStyle().set("justify-content", "end");
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

    //Filtros
    private void Filtros() {

        div_filtros = new HorizontalLayout();

        trabajadorFilter = new ComboBox<>();
        trabajadorFilter.setItems(trabajadores);
        trabajadorFilter.setItemLabelGenerator(trabajador -> trabajador.getUser().getName());
        trabajadorFilter.setPlaceholder("Trabajador");
        trabajadorFilter.setClearButtonVisible(true);
        trabajadorFilter.setWidth("100%");
        trabajadorFilter.setRenderer(new ComponentRenderer<>(event -> {
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
        trabajadorFilter.addValueChangeListener(event -> {
            if (trabajadorFilter.getValue() == null) {
                gridListDataView = gridTrabajadores.setItems(trabajadores);
            } else {
                gridListDataView.addFilter(trabajador -> areTrabajadorEqual(trabajador, trabajadorFilter));
            }
        });

        areaFilter = new ComboBox<>();
        areaFilter.setItems(areaService.findAll());
        areaFilter.setItemLabelGenerator(Area::getNombre);
        areaFilter.setPlaceholder("Area");
        areaFilter.setWidth("100%");
        areaFilter.addValueChangeListener(event -> {
            if (areaFilter.getValue() == null) {
                gridListDataView = gridTrabajadores.setItems(trabajadores);
            } else {
                gridListDataView.addFilter(trabajador -> areAreaEqual(trabajador, areaFilter));
            }
        });
    }

    private boolean areTrabajadorEqual(Trabajador trabajador, ComboBox<Trabajador> trabajadorFilter) {
        String trabajadorFilterValue = trabajadorFilter.getValue().getUser().getName();
        if (trabajadorFilterValue != null) {
            return StringUtils.equals(trabajador.getUser().getName(), trabajadorFilterValue);
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

    public void editCard(Trabajador e) {
        content.removeAll();
        tarjetaPrestamoTrabajadorView = new TarjetaPrestamoTrabajadorView(e, prestamoService, trabajadorService, areaService, libroService, senderService);
        tarjetaPrestamoTrabajadorView.setWidthFull();
        content.add(tarjetaPrestamoTrabajadorView);
    }

    // Configuracion del Formulario
    private void configureForm() {

        List<Trabajador> listTrabajadorSeleccionados = new LinkedList<>(gridTrabajadores.getSelectedItems());
        listTrabajadorSeleccionados.sort(Comparator.comparing(Trabajador::getId));

        form = new TrabajadorForm(listTrabajadorSeleccionados, libroService.findAll());
        form.addListener(TrabajadorForm.SaveEvent.class, this::saveLibro);
        form.addListener(TrabajadorForm.CloseEvent.class, e -> closeEditor());
        form.setWidth("25em");
    }

    private void saveLibro(TrabajadorForm.SaveEvent event) {

        prestamos.clear();
        List<TarjetaPrestamo> listTarjetas = prestamoService.findAll();
        boolean band = false;
        List<Trabajador> listTrabajadorSeleccionados = new LinkedList<>(gridTrabajadores.getSelectedItems());
        for (int i = 0; i < listTarjetas.size() && band == false; i++) {
            if (listTarjetas.get(i) instanceof TarjetaPrestamoTrabajador) {
                tarjetaTrabajador = (TarjetaPrestamoTrabajador) listTarjetas.get(i);
                if (busquedaBinariaTrabajador(listTrabajadorSeleccionados, tarjetaTrabajador.getTrabajador())) {
                    for (int j = 0; j < event.getTarjetaPrestamo().size() && band == false; j++) {
                        //anadir
                        if (event.getTarjetaPrestamo().get(j).getId() == null && event.getTarjetaPrestamo().get(j).getFechaDevolucion() == null) {
                            if (event.getTarjetaPrestamo().get(j).getLibro().getId() == tarjetaTrabajador.getLibro().getId()
                                    && event.getTarjetaPrestamo().get(j).getFechaPrestamo().equals(tarjetaTrabajador.getFechaPrestamo())) {
                                prestamos.add(tarjetaTrabajador);
                                band = true;
                            }
                            //modificar
                        } else if (event.getTarjetaPrestamo().get(j).getId() != null && event.getTarjetaPrestamo().get(j).getFechaDevolucion() == null) {
                            if (event.getTarjetaPrestamo().get(j).getLibro().getId() == tarjetaTrabajador.getLibro().getId()
                                    && event.getTarjetaPrestamo().get(j).getFechaPrestamo().equals(tarjetaTrabajador.getFechaPrestamo())) {
                                prestamos.add(tarjetaTrabajador);
                                band = true;

                            }
                            //modificar
                        } else if (event.getTarjetaPrestamo().get(j).getId() != null && event.getTarjetaPrestamo().get(j).getFechaDevolucion() != null) {
                            if (event.getTarjetaPrestamo().get(j).getLibro().getId() == tarjetaTrabajador.getLibro().getId()
                                    && event.getTarjetaPrestamo().get(j).getFechaPrestamo().equals(tarjetaTrabajador.getFechaPrestamo())
                                    && event.getTarjetaPrestamo().get(j).getFechaDevolucion().equals(tarjetaTrabajador.getFechaDevolucion())) {
                                prestamos.add(tarjetaTrabajador);
                                band = true;
                            }
                        }
                    }
                }
            }
        }

        if (prestamos.size() > 0) {
            Notification notification = Notification.show(
                    "El libro ya existe",
                    2000,
                    Notification.Position.MIDDLE);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/YYYY");

            try {

                for (int i = 0; i < event.getTarjetaPrestamo().size(); i++) {
                    //salvar tarjeta
                    prestamoService.save(event.getTarjetaPrestamo().get(i));
                    //confirmar al correo
                    AceptAllSSLCertificate();
                    senderService.sendSimpleEmail(
                            /* enviado a: */event.getTarjetaPrestamo().get(i).getTrabajador().getEmail(),
                            /* asunto: */ "Entrega de libros",
                            /* mensaje: */ "Genius\n"
                            + "Sistema Informático para la gestión de información de los recursos materiales y libros en la facultad 4.\n"
                            + "Usted ha recibido el libro: "
                            + event.getTarjetaPrestamo().get(i).getLibro().getTitulo()
                            + " el día: "
                            + formatter.format(event.getTarjetaPrestamo().get(i).getFechaPrestamo()).toString());
                }

                Notification notification = Notification.show(
                        "Libro añadido",
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

    private boolean busquedaBinariaTrabajador(List<Trabajador> list, Trabajador e) {
        int inicio = 0;
        int fin = list.size() - 1;
        while (inicio <= fin) {
            int mitad = (inicio + fin) / 2;
            if (e.getId().equals(list.get(mitad).getId())) {
                return true;
            }
            if (e.getId() > list.get(mitad).getId()) {
                inicio = mitad + 1;
            }
            if (e.getId() < list.get(mitad).getId()) {
                fin = mitad - 1;
            }
        }
        return false;
    }

    private void addLibro(Set<Trabajador> setTrabajadors) {

        List<TarjetaPrestamoTrabajador> tarjetasPrestamo = new LinkedList<>();
        for (int i = 0; i < setTrabajadors.size(); i++) {
            TarjetaPrestamoTrabajador tarjeta = new TarjetaPrestamoTrabajador();
            tarjetasPrestamo.add(tarjeta);
        }

//        if (listTarjetas.size() == 0) {
//            closeEditor();
//        } else {
        form.setTarjetaPrestamo(tarjetasPrestamo);
        form.setVisible(true);
        addClassName("editing");
        dialog.open();
        //}
    }

    private void closeEditor() {
        List<TarjetaPrestamoTrabajador> tarjetasPrestamo = new LinkedList<>();
        form.setTarjetaPrestamo(tarjetasPrestamo);
        form.setVisible(false);
        removeClassName("editing");
        dialog.close();
        gridTrabajadores.deselectAll();
    }

}
