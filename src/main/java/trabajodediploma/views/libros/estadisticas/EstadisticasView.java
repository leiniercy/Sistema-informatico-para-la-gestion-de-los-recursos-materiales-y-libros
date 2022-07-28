/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabajodediploma.views.libros.estadisticas;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.charts.model.DataSeries;
import com.vaadin.flow.component.charts.model.DataSeriesItem;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.util.List;
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
    private MyFooter footer;

    public EstadisticasView(
            @Autowired LibroService libroService,
            @Autowired TarjetaPrestamoService prestamoService
    ) {
        this.libroService = libroService;
        this.prestamoService = prestamoService;
        footer = new MyFooter();
        add(getBoard(), getGraficosPasteles(),footer);
    }

    private Component getBoard() {
        addClassName("basic-board");
        Board board = new Board();

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
        List<Libro> listLibros = libroService.findAll();
        int cantReal = 0;
        for (int i = 0; i < listLibros.size(); i++) {
            cantReal += listLibros.get(i).getCantidad();
        }
        return cantReal;
    }

    //Cantidad real de libros en el almacen
    public int cantRealLibrosAlmacen() {
        List<TarjetaPrestamo> tarjetas = prestamoService.findAll();
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
        addClassName("grafics_key-board");
        Board board = new Board();
        board.addRow(
                getEstadisticasProrcientoLibros()
        );

        return board;
    }

    private Component getEstadisticasProrcientoLibros() {

        Chart chart = new Chart(ChartType.PIE);

        float promedioP = cantRealLibrosPrestados() * 100 / cantRealLibros();
        float promedioA = cantRealLibrosAlmacen() * 100 / cantRealLibros();

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

}
