/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabajodediploma.views.tarjetaprestamo.trabajador;

import trabajodediploma.views.tarjetaprestamo.trabajadorPrestamo.TarjetaPrestamoTrabajadorView;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import trabajodediploma.data.entity.Trabajador;
import trabajodediploma.data.service.TrabajadorService;
import trabajodediploma.data.tools.EmailSenderService;
import trabajodediploma.data.service.LibroService;
import trabajodediploma.data.service.TarjetaPrestamoService;

/**
 *
 * @author leinier
 */
public class TrabajadorGrid extends Div {

    private Grid<Trabajador> gridTrabajadors = new Grid<>(Trabajador.class, false);

    TarjetaPrestamoTrabajadorView tarjetaTrabajador;
    private TarjetaPrestamoService prestamoService;
    private TrabajadorService trabajadorService;
    private LibroService libroService;
    private EmailSenderService senderService;

    GridListDataView<Trabajador> gridListDataView;
    Grid.Column<Trabajador> nombreColumn;
    Grid.Column<Trabajador> tarjetaColumn;

    private TextField nombreFilter;
    private Div content;

    public TrabajadorGrid(
            TarjetaPrestamoService prestamoService,
            TrabajadorService trabajadorService,
            LibroService libroService, 
            EmailSenderService senderService) {
        addClassName("container___trabajador_grid");           
        this.prestamoService = prestamoService;
        this.trabajadorService = trabajadorService;
        this.libroService = libroService;
        this.senderService = senderService;
        configureGrid();
        content = new Div();
        content.addClassName("container___trabajador_grid__div");
        content.add(gridTrabajadors);

        add(content);

    }

    private void configureGrid() {

        gridTrabajadors.setClassName("container___trabajador_grid__div__table");
        nombreColumn = gridTrabajadors.addColumn(new ComponentRenderer<>(trab-> {
            HorizontalLayout hl = new HorizontalLayout();
            hl.getStyle().set("align-items","center");
            hl.setAlignItems(Alignment.CENTER);
            Avatar avatar = new Avatar(trab.getUser().getName(), trab.getUser().getProfilePictureUrl());
            VerticalLayout vl = new VerticalLayout();
            vl.getStyle().set("line-height","0");
            Span name = new Span();
            name.addClassNames("name");
            name.setText(trab.getUser().getName());
            Span email = new Span();
            email.addClassNames("text-s","text-secondary");
            email.setText(trab.getEmail());
            vl.add(name,email);
            hl.add(avatar,vl);
            return hl; 
        })).setHeader("Nombre").setAutoWidth(true).setSortable(true);
        tarjetaColumn = gridTrabajadors.addComponentColumn(event -> {
            Button cardButton = new Button("Tarjeta");
            cardButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            cardButton.addClickListener(e -> this.editCard(event));
            return cardButton;
        }).setAutoWidth(true);

        Filters();
        HeaderRow headerRow = gridTrabajadors.appendHeaderRow();
        headerRow.getCell(nombreColumn).setComponent(nombreFilter);
        
        gridListDataView = gridTrabajadors.setItems(trabajadorService.findAll());
        gridTrabajadors.setAllRowsVisible(true);
        gridTrabajadors.setSizeFull();
        gridTrabajadors.setWidthFull();
        gridTrabajadors.setHeightFull();
        gridTrabajadors.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
        gridTrabajadors.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        gridTrabajadors.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
    }

    private void Filters() {
        nombreFilter = new TextField();
        nombreFilter.setPlaceholder("Filtrar");
        nombreFilter.setPrefixComponent(VaadinIcon.SEARCH.create());
        nombreFilter.setClearButtonVisible(true);
        nombreFilter.setWidth("100%");
        nombreFilter.setValueChangeMode(ValueChangeMode.LAZY);
        nombreFilter.addValueChangeListener(
                event -> gridListDataView
                        .addFilter(trabajador -> StringUtils.containsIgnoreCase(trabajador.getUser().getName(), nombreFilter.getValue()))
        );
    }

    public void editCard(Trabajador e) {
        content.removeAll();
        tarjetaTrabajador = new TarjetaPrestamoTrabajadorView(e,prestamoService, trabajadorService,libroService,senderService);
        tarjetaTrabajador.setWidthFull();
        content.add(tarjetaTrabajador);
    }

}
