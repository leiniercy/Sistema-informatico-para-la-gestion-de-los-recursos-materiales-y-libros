/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabajodediploma.views.tarjetaprestamo;

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
import trabajodediploma.data.entity.Estudiante;
import trabajodediploma.data.service.EstudianteService;
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
    TarjetaPrestamoService prestamoService;
    EstudianteService estudianteService;
    LibroService libroService;

    GridListDataView<Estudiante> gridListDataView;
    Grid.Column<Estudiante> nombreColumn;
    Grid.Column<Estudiante> tarjetaColumn;

    private TextField nombreFilter;
    private Div content;
    private EmailSenderService senderService;

    public EstudianteGrid( 
        TarjetaPrestamoService prestamoService, 
        EstudianteService estudianteService,
        LibroService libroService, 
        EmailSenderService senderService) {
        addClassName("container___estudiante_grid");           
        this.prestamoService = prestamoService;
        this.estudianteService = estudianteService;
        this.libroService = libroService;
        this.senderService = senderService;
        configureGrid();
        content = new Div();
        content.addClassName("container___estudiante_grid__div");
        content.add(gridEstudiantes);

        add(content);

    }

    private void configureGrid() {

        gridEstudiantes.setClassName("container___estudiante_grid__div__table");
        nombreColumn = gridEstudiantes.addColumn(new ComponentRenderer<>(est-> {
            HorizontalLayout hl = new HorizontalLayout();
            hl.getStyle().set("align-items","center");
            hl.setAlignItems(Alignment.CENTER);
            Avatar avatar = new Avatar(est.getUser().getName(), est.getUser().getProfilePictureUrl());
            VerticalLayout vl = new VerticalLayout();
            vl.getStyle().set("line-height","0");
            Span name = new Span();
            name.addClassNames("name");
            name.setText(est.getUser().getName());
            Span email = new Span();
            email.addClassNames("text-s","text-secondary");
            email.setText(est.getEmail());
            vl.add(name,email);
            hl.add(avatar,vl);
            return hl;
        })).setHeader("Nombre").setAutoWidth(true).setSortable(true);
        tarjetaColumn = gridEstudiantes.addComponentColumn(event -> {
            Button cardButton = new Button("Tarjeta");
            cardButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            cardButton.addClickListener(e -> this.editCard(event));
            return cardButton;
        }).setAutoWidth(true);

        Filters();
        HeaderRow headerRow = gridEstudiantes.appendHeaderRow();
        headerRow.getCell(nombreColumn).setComponent(nombreFilter);
        
        gridListDataView = gridEstudiantes.setItems(estudianteService.findAll());
        gridEstudiantes.setAllRowsVisible(true);
        gridEstudiantes.setSizeFull();
        gridEstudiantes.setWidthFull();
        gridEstudiantes.setHeightFull();
        gridEstudiantes.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
        gridEstudiantes.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        gridEstudiantes.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
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
                        .addFilter(estudiante -> StringUtils.containsIgnoreCase(estudiante.getUser().getName(), nombreFilter.getValue()))
        );
    }

    public void editCard(Estudiante e) {
        content.removeAll();
        tarjetaEstudiante = new TarjetaPrestamoEstudianteView(e,prestamoService, estudianteService,libroService,senderService);
        tarjetaEstudiante.setWidthFull();
        content.add(tarjetaEstudiante);
    }

}
