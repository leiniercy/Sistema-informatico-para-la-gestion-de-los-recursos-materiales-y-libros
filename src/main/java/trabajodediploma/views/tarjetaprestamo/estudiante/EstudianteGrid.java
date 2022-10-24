/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabajodediploma.views.tarjetaprestamo.estudiante;

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
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import trabajodediploma.data.entity.Estudiante;
import trabajodediploma.data.entity.Grupo;
import trabajodediploma.data.service.EstudianteService;
import trabajodediploma.data.service.GrupoService;
import trabajodediploma.data.service.LibroService;
import trabajodediploma.data.service.TarjetaPrestamoService;
import trabajodediploma.data.tools.EmailSenderService;

/**
 *
 * @author leinier
 */
public class EstudianteGrid extends Div {

    private Grid<Estudiante> gridEstudiantes = new Grid<>(Estudiante.class, false);

    TarjetaPrestamoEstudianteView tarjetaEstudiante;
    private TarjetaPrestamoService prestamoService;
    private EstudianteService estudianteService;
    private GrupoService grupoService;
    private LibroService libroService;

    GridListDataView<Estudiante> gridListDataView;
    Grid.Column<Estudiante> nombreColumn;
    Grid.Column<Estudiante> tarjetaColumn;

    private ComboBox<Grupo> grupoFilter;
    private Div content;
    private EmailSenderService senderService;
    private HorizontalLayout barra_menu;
    private HorizontalLayout div_filtros;
    private ComboBox<Estudiante> estudianteFilter;

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
        configureGrid();
        menuBar();
        content = new Div();
        content.addClassName("container___estudiante_grid__div");
        content.add(barra_menu, div_filtros, gridEstudiantes);

        add(content);

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

        Button anadirPor = new Button("Añadir", VaadinIcon.PLUS.create());
        anadirPor.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        /*Menu Filtros*/
        MenuBar barraMenu = new MenuBar();
        barraMenu.addThemeVariants(MenuBarVariant.LUMO_PRIMARY);
        MenuItem filtros = createMenuIconItem(barraMenu, VaadinIcon.FILTER, "Filtros", null, false);
        SubMenu filtrosSubMenu = filtros.getSubMenu();
        Checkbox estudianteCheckBox = new Checkbox();
        estudianteCheckBox.addClickListener(event -> {
            if (estudianteCheckBox.getValue()) {
                div_filtros.add(estudianteFilter);
            } else {
                div_filtros.remove(estudianteFilter);
            }
        });
        MenuItem estudiante = createSubMenuIconItem(filtrosSubMenu, estudianteCheckBox, VaadinIcon.USER, "Estudiante", null, true);
        Checkbox grupoCheckBox = new Checkbox();
        grupoCheckBox.addClickListener(event -> {
            if (grupoCheckBox.getValue()) {
                div_filtros.add(grupoFilter);
            } else {
                div_filtros.remove(grupoFilter);
            }
        });
        MenuItem grupo = createSubMenuIconItem(filtrosSubMenu, grupoCheckBox, VaadinIcon.USERS, "Grupo", null, true);
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
        tarjetaEstudiante = new TarjetaPrestamoEstudianteView(e, prestamoService, estudianteService, grupoService, libroService, senderService);
        tarjetaEstudiante.setWidthFull();
        content.add(tarjetaEstudiante);
    }

}
