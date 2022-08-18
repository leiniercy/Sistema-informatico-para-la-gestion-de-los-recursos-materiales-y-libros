/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabajodediploma.views.libros.estadisticas;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.charts.model.DataSeries;
import com.vaadin.flow.component.charts.model.DataSeriesItem;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import trabajodediploma.data.entity.Libro;
import trabajodediploma.data.entity.TarjetaPrestamo;
import trabajodediploma.data.service.LibroService;
import trabajodediploma.data.service.TarjetaPrestamoService;
import trabajodediploma.views.MainLayout;
import trabajodediploma.views.footer.MyFooter;

/**
 *
 * @author leinier
 */
@PageTitle("Estadísticas")
@Route(value = "estadisticas-view", layout = MainLayout.class)
@RolesAllowed("RESP_ALMACEN")
public class EstadisticasView extends Div {

    private LibroService libroService;
    private TarjetaPrestamoService prestamoService;
    private Div container;
    private MyFooter footer;
    private DatePicker endDate;
    private DatePicker initDate;
    private List<TarjetaPrestamo> tarjetas;
    private List<Libro> listLibros;
    
    public EstadisticasView(
            @Autowired LibroService libroService,
            @Autowired TarjetaPrestamoService prestamoService
    ) {
        addClassName("estadistica_view");
        this.libroService = libroService;
        this.prestamoService = prestamoService;
        
        container = new Div();
        container.addClassName("estadistica_view__container");
        
        footer = new MyFooter();
        add(getBarraDeMenu());
        if (initDate.getValue() == null || endDate.getValue() == null) {
            container.removeAll();
            updateList();
            container.add(getBoard(), getGraficosPasteles());
            add(container);
        } else {
            container.removeAll();
            updateList();
            container.add(getBoard(), getGraficosPasteles());
            add(container);
        }
        add(footer);
    }

    //actualizar listas
    private void updateList() {
        if (initDate.getValue() == null || endDate.getValue() == null) {
            listLibros = libroService.findAll();
            tarjetas = prestamoService.findAll();
        } else if (initDate.getValue() != null && endDate.getValue() != null) {
            listLibros = libroService.findAll();
            tarjetas = prestamoService.findAll();
            tarjetas = tarjetas.stream().filter(
                    //fecha_inicio <= x <= fecha_fin            
                    event -> event.getFechaPrestamo() != null
                    &&   (event.getFechaPrestamo().isEqual(initDate.getValue())
                    || event.getFechaPrestamo().isAfter(initDate.getValue()))
                    &&   event.getFechaDevolucion() != null      
                    && (event.getFechaDevolucion().isEqual(endDate.getValue())
                    || event.getFechaDevolucion().isBefore(endDate.getValue()))
            ).collect(Collectors.toList());
        }
    }

    private Component getBoard() {
        
        Board board = new Board();
        board.addClassName("estadistica_view__container__basic_board");
        board.addRow(
                createHighlight("Cantidad real de libros", new Span(String.format("%d", cantRealLibros()))),
                createHighlight("Cantidad de libros en el álmacen", new Span(String.format("%d", cantRealLibrosAlmacen()))),
                createHighlight("Cantidad de libros prestados", new Span(String.format("%d", cantRealLibrosPrestados())))
        );
        return board;
    }

    private Component createHighlight(String title, Span span) {

        H1 h1 = new H1(title);
        h1.addClassNames("font-normal", "m-0", "text-secondary", "text-xs");

        span.addClassNames("font-semibold", "text-3xl");

        VerticalLayout layout = new VerticalLayout(h1, span);
        layout.addClassName("p-l");
        layout.setPadding(false);
        layout.setSpacing(false);
        layout.getElement().getThemeList().add("spacing-l");
        layout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        return layout;
    }

    //Cantidad real de libros
    public int cantRealLibros() {
        int cantReal = 0;
        for (int i = 0; i < listLibros.size(); i++) {
            cantReal += listLibros.get(i).getCantidad();
        }
        return cantReal;
    }

    //Cantidad real de libros en el almacen
    public int cantRealLibrosAlmacen() {
        int librosPrestados = 0;
        for (int i = 0; i < tarjetas.size(); i++) {
            if (tarjetas.get(i).getFechaDevolucion() == null) {
                librosPrestados++;
            }
        }

        return cantRealLibros() - librosPrestados;
    }

    //Cantidad de libros en manos de los estudiantes
    public int cantRealLibrosPrestados() {
        return cantRealLibros() - cantRealLibrosAlmacen();
    }

    //Graficos de pasteles
    private Component getGraficosPasteles() {
        Board board = new Board();
        board.addClassName("estadistica_view__container__grafics_key_board");
        board.addRow(
                getEstadisticasProrcientoLibros()
        );

        return board;
    }

    private Component getEstadisticasProrcientoLibros() {

        Chart chart = new Chart(ChartType.PIE);
        float promedioP = 0;
        float promedioA = 0;
        if (cantRealLibros() != 0) {
            promedioP = cantRealLibrosPrestados() * 100 / cantRealLibros();
            promedioA = cantRealLibrosAlmacen() * 100 / cantRealLibros();
        }

        DataSeries dataSeries = new DataSeries();
        dataSeries.add(new DataSeriesItem(String.format("Libros prestados: %d", cantRealLibrosPrestados()), promedioP));
        dataSeries.add(new DataSeriesItem(String.format("Libros en el álmacen: %d", cantRealLibrosAlmacen()), promedioA));

        chart.getConfiguration().setSeries(dataSeries);
        chart.getConfiguration().setTitle("Distribución de libros");
        chart.getConfiguration().setSubTitle(String.format("Cantidad real de libros: %d", cantRealLibros()));
        chart.addClassNames("text-xl", "mt-m");

        VerticalLayout serviceHealth = new VerticalLayout(chart);
        serviceHealth.addClassName("p-l");
        serviceHealth.setPadding(false);
        serviceHealth.setSpacing(false);
        serviceHealth.getElement().getThemeList().add("spacing-l");
        serviceHealth.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        return serviceHealth;
    }

    //Barra de menu
    private Component getBarraDeMenu() {

        Div buttons = new Div();
        buttons.addClassNames("estadistica_view___barra_menu__export");
        Button exportButton = new Button(VaadinIcon.FILE.create());
        buttons.add(exportButton);

        Div toolbar = new Div(DatePickerDateRange(), buttons);
        toolbar.addClassName("estadistica_view___barra_menu");
        return toolbar;
    }

    //Rango de las fechas
    private Component DatePickerDateRange() {

        DatePicker.DatePickerI18n singleFormatI18n = new DatePicker.DatePickerI18n();
        singleFormatI18n.setDateFormat("dd.MM.yyyy");

        initDate = new DatePicker("Fecha de inicio:");
        initDate.setI18n(singleFormatI18n);
        initDate.setClearButtonVisible(true);
        initDate.addValueChangeListener(event -> {
            container.removeAll();
            updateList();
            container.add(getBoard(), getGraficosPasteles());
        });
        endDate = new DatePicker("Fecha de fin:");
        endDate.setI18n(singleFormatI18n);
        endDate.setClearButtonVisible(true);
        endDate.setValue(LocalDate.now());
        endDate.addValueChangeListener(event -> {
            container.removeAll();
            updateList();
            container.add(getBoard(), getGraficosPasteles());
        });

        initDate.addValueChangeListener(e -> endDate.setMin(e.getValue()));
        endDate.addValueChangeListener(e -> initDate.setMax(e.getValue()));

        Span  s = new Span("-");

        Div layout = new Div(initDate, s ,endDate);
        layout.addClassNames("barra-menu-date");
        return layout;
    }

}
