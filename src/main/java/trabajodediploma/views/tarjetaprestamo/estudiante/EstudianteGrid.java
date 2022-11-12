/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabajodediploma.views.tarjetaprestamo.estudiante;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Text;
import trabajodediploma.views.tarjetaprestamo.estudiantePrestamo.TarjetaPrestamoEstudianteView;
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
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import trabajodediploma.data.entity.Estudiante;
import trabajodediploma.data.entity.Grupo;
import trabajodediploma.data.entity.TarjetaPrestamo;
import trabajodediploma.data.entity.TarjetaPrestamoEstudiante;
import trabajodediploma.data.service.EstudianteService;
import trabajodediploma.data.service.GrupoService;
import trabajodediploma.data.service.LibroService;
import trabajodediploma.data.service.TarjetaPrestamoService;
import trabajodediploma.data.tools.EmailSenderService;
import trabajodediploma.views.tarjetaprestamo.estudiantePrestamo.TarjetaPrestamoEstudianteForm;

/**
 *
 * @author leinier
 */
public class EstudianteGrid extends Div {

    private Grid<Estudiante> gridEstudiantes = new Grid<>(Estudiante.class, false);
    EstudianteForm form;
    TarjetaPrestamoEstudianteView tarjetaPrestamoEstudianteView;
    private TarjetaPrestamoEstudiante tarjetaEstudiante;
    private TarjetaPrestamoService prestamoService;
    private EstudianteService estudianteService;
    private GrupoService grupoService;
    private LibroService libroService;
    private List<TarjetaPrestamo> prestamos;

    GridListDataView<Estudiante> gridListDataView;
    Grid.Column<Estudiante> nombreColumn;
    Grid.Column<Estudiante> tarjetaColumn;

    private ComboBox<Grupo> grupoFilter;
    private ComboBox<Estudiante> estudianteFilter;
    private Div content;
    private EmailSenderService senderService;
    private HorizontalLayout barra_menu;
    private HorizontalLayout div_filtros;
    private Dialog dialog;
    private Div header;

    public EstudianteGrid(
            TarjetaPrestamoService prestamoService,
            EstudianteService estudianteService,
            GrupoService grupoService,
            LibroService libroService,
            EmailSenderService senderService) {
        addClassName("container___estudiante_grid");
        this.prestamoService = prestamoService;
        this.estudianteService = estudianteService;
        this.libroService = libroService;
        this.grupoService = grupoService;
        this.senderService = senderService;
        prestamos = new LinkedList<>();
        configureGrid();
        menuBar();
        content = new Div();
        content.addClassName("container___estudiante_grid__div");
        content.add(barra_menu, div_filtros, gridEstudiantes);
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

        gridEstudiantes.setClassName("container___estudiante_grid__div__table");
        nombreColumn = gridEstudiantes.addColumn(new ComponentRenderer<>(est -> {
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

        tarjetaColumn = gridEstudiantes.addComponentColumn(event -> {
            Button cardButton = new Button("Tarjeta", VaadinIcon.FILE_TABLE.create(), e -> this.editCard(event));
            cardButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            return cardButton;
        }).setAutoWidth(true).setTextAlign(ColumnTextAlign.END);

        Filtros();

        gridListDataView = gridEstudiantes.setItems(estudianteService.findAll());
        gridEstudiantes.setAllRowsVisible(true);
        gridEstudiantes.setSelectionMode(Grid.SelectionMode.MULTI);
        gridEstudiantes.setSizeFull();
        gridEstudiantes.setWidthFull();
        gridEstudiantes.setHeightFull();
        gridEstudiantes.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
        gridEstudiantes.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        gridEstudiantes.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
    }

    //Barra de Menu
    private void menuBar() {
        barra_menu = new HorizontalLayout();

        Button anadirPor = new Button("Añadir", VaadinIcon.PLUS.create(), click -> {
            if (gridEstudiantes.getSelectedItems().size() == 0) {
                Notification notification = Notification.show(
                        "Debe elegir al menos un elemento",
                        2000,
                        Notification.Position.MIDDLE);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } else if (gridEstudiantes.getSelectedItems().size() > 0) {
                configureForm();
                getContent();
                addLibro(gridEstudiantes.getSelectedItems());
            }
        });
        anadirPor.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        /*Menu Filtros*/
        MenuBar barraMenu = new MenuBar();
        barraMenu.addThemeVariants(MenuBarVariant.LUMO_PRIMARY);
        MenuItem filtros = createMenuIconItem(barraMenu, VaadinIcon.FILTER, "Filtros", null, false);
        SubMenu filtrosSubMenu = filtros.getSubMenu();
        /*Estudiante*/
        Checkbox estudianteCheckBox = new Checkbox();
        estudianteCheckBox.addClickListener(event -> {
            if (!estudianteCheckBox.getValue()) {
                estudianteCheckBox.setValue(Boolean.TRUE);
                div_filtros.add(estudianteFilter);
            } else {
                div_filtros.remove(estudianteFilter);
                estudianteFilter.setValue(null);
                estudianteCheckBox.setValue(Boolean.FALSE);
            }
        });
        MenuItem estudiante = createSubMenuIconItem(filtrosSubMenu, estudianteCheckBox, VaadinIcon.USER, "Estudiante", null, true);
        estudiante.addClickListener(event -> {
            if (!estudianteCheckBox.getValue()) {
                estudianteCheckBox.setValue(Boolean.TRUE);
                div_filtros.add(estudianteFilter);
            } else {
                div_filtros.remove(estudianteFilter);
                estudianteFilter.setValue(null);
                estudianteCheckBox.setValue(Boolean.FALSE);
            }
        });
        //FIN -> estudiante
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

        estudianteFilter = new ComboBox<>();
        estudianteFilter.setItems(estudianteService.findAll());
        estudianteFilter.setItemLabelGenerator(estudiante -> estudiante.getUser().getName());
        estudianteFilter.setPlaceholder("Estudiante");
        estudianteFilter.setClearButtonVisible(true);
        estudianteFilter.setWidth("100%");
        estudianteFilter.setRenderer(new ComponentRenderer<>(event -> {
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
        estudianteFilter.addValueChangeListener(event -> {
            if (estudianteFilter.getValue() == null) {
                gridListDataView = gridEstudiantes.setItems(estudianteService.findAll());
            } else {
                gridListDataView.addFilter(estudiante -> areEstudianteEqual(estudiante, estudianteFilter));
            }
        });

        grupoFilter = new ComboBox<>();
        grupoFilter.setItems(grupoService.findAll());
        grupoFilter.setItemLabelGenerator(Grupo::getNumero);
        grupoFilter.setPlaceholder("Grupo");
        grupoFilter.setWidth("100%");
        grupoFilter.addValueChangeListener(event -> {
            if (grupoFilter.getValue() == null) {
                gridListDataView = gridEstudiantes.setItems(estudianteService.findAll());
            } else {
                gridListDataView.addFilter(estudiante -> areGrupoEqual(estudiante, grupoFilter));
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

    public void editCard(Estudiante e) {
        content.removeAll();
        tarjetaPrestamoEstudianteView = new TarjetaPrestamoEstudianteView(e, prestamoService, estudianteService, grupoService, libroService, senderService);
        tarjetaPrestamoEstudianteView.setWidthFull();
        content.add(tarjetaPrestamoEstudianteView);
    }

    // Configuracion del Formulario
    private void configureForm() {

        List<Estudiante> listEstudianteSeleccionados = new LinkedList<>(gridEstudiantes.getSelectedItems());
        listEstudianteSeleccionados.sort(Comparator.comparing(Estudiante::getId));

        form = new EstudianteForm(listEstudianteSeleccionados, libroService.findAll());
        form.addListener(EstudianteForm.SaveEvent.class, this::saveLibro);
        form.addListener(EstudianteForm.CloseEvent.class, e -> closeEditor());
        form.setWidth("25em");
    }

    private void saveLibro(EstudianteForm.SaveEvent event) {

        prestamos.clear();
        List<TarjetaPrestamo> listTarjetas = prestamoService.findAll();
        boolean band = false;
        List<Estudiante> listEstudianteSeleccionados = new LinkedList<>(gridEstudiantes.getSelectedItems());
        for (int i = 0; i < listTarjetas.size() && band == false; i++) {
            if (listTarjetas.get(i) instanceof TarjetaPrestamoEstudiante) {
                tarjetaEstudiante = (TarjetaPrestamoEstudiante) listTarjetas.get(i);
                if (busquedaBinariaEstudiante(listEstudianteSeleccionados, tarjetaEstudiante.getEstudiante())) {
                    for (int j = 0; j < event.getTarjetaPrestamo().size() && band == false; j++) {
                        //anadir
                        if (event.getTarjetaPrestamo().get(j).getId() == null && event.getTarjetaPrestamo().get(j).getFechaDevolucion() == null) {
                            if (event.getTarjetaPrestamo().get(j).getLibro().getId() == tarjetaEstudiante.getLibro().getId()
                                    && event.getTarjetaPrestamo().get(j).getFechaPrestamo().equals(tarjetaEstudiante.getFechaPrestamo())) {
                                prestamos.add(tarjetaEstudiante);
                                band = true;
                            }
                            //modificar
                        } else if (event.getTarjetaPrestamo().get(j).getId() != null && event.getTarjetaPrestamo().get(j).getFechaDevolucion() == null) {
                            if (event.getTarjetaPrestamo().get(j).getLibro().getId() == tarjetaEstudiante.getLibro().getId()
                                    && event.getTarjetaPrestamo().get(j).getFechaPrestamo().equals(tarjetaEstudiante.getFechaPrestamo())) {
                                prestamos.add(tarjetaEstudiante);
                                band = true;

                            }
                            //modificar
                        } else if (event.getTarjetaPrestamo().get(j).getId() != null && event.getTarjetaPrestamo().get(j).getFechaDevolucion() != null) {
                            if (event.getTarjetaPrestamo().get(j).getLibro().getId() == tarjetaEstudiante.getLibro().getId()
                                    && event.getTarjetaPrestamo().get(j).getFechaPrestamo().equals(tarjetaEstudiante.getFechaPrestamo())
                                    && event.getTarjetaPrestamo().get(j).getFechaDevolucion().equals(tarjetaEstudiante.getFechaDevolucion())) {
                                prestamos.add(tarjetaEstudiante);
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
                    senderService.sendSimpleEmail(
                            /* enviado a: */event.getTarjetaPrestamo().get(i).getEstudiante().getEmail(),
                            /* asunto: */ "Entrega de libros",
                            /* mensaje: */ "Genius\n"
                            + "Sistema Informático para la gestión de información de los recursos materiales y libros en la facultad 4.  \n"
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

    private boolean busquedaBinariaEstudiante(List<Estudiante> list, Estudiante e) {
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

    private void addLibro(Set<Estudiante> setEstudiantes) {

        List<TarjetaPrestamoEstudiante> tarjetasPrestamo = new LinkedList<>();
        for (int i = 0; i < setEstudiantes.size(); i++) {
            TarjetaPrestamoEstudiante tarjeta = new TarjetaPrestamoEstudiante();
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
        List<TarjetaPrestamoEstudiante> tarjetasPrestamo = new LinkedList<>();
        form.setTarjetaPrestamo(tarjetasPrestamo);
        form.setVisible(false);
        removeClassName("editing");
        dialog.close();
        gridEstudiantes.deselectAll();
    }

}
