/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabajodediploma.views.libros.estadisticas;

import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.color.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.export.ExportOptions;
import com.vaadin.flow.component.charts.export.SVGGenerator;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.charts.model.Configuration;
import com.vaadin.flow.component.charts.model.DataSeries;
import com.vaadin.flow.component.charts.model.DataSeriesItem;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
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
    private List<TarjetaPrestamo> tarjetas;
    private List<Libro> listLibros;
    private Configuration configuration;
    private Chart chart;

    public EstadisticasView(
            @Autowired LibroService libroService,
            @Autowired TarjetaPrestamoService prestamoService) {
        addClassName("estadistica_view");
        this.libroService = libroService;
        this.prestamoService = prestamoService;

        container = new Div();
        container.addClassName("estadistica_view__container");
        footer = new MyFooter();

        updateList();
        container.add(getBoard(), getGraficosPasteles());
        add(getBarraDeMenu(), container, footer);
    }

    // actualizar listas
    private void updateList() {
        listLibros = libroService.findAll();
        tarjetas = prestamoService.findAll();
    }

    // Barra de menu
    private Component getBarraDeMenu() {

        Div buttons = new Div();
        buttons.addClassNames("estadistica_view___barra_menu__export");

        Anchor linkExport = new Anchor();
        linkExport.addClassName("estadistica_view___barra_menu__export__link");
        linkExport.add(VaadinIcon.FILE.create());
        linkExport.add(new Label("Exportar"));
        linkExport.setHref(exportChart());
        linkExport.setTarget("_BLANK");

        buttons.add(linkExport);
        Div toolbar = new Div(buttons);
        toolbar.addClassName("estadistica_view___barra_menu");
        return toolbar;
    }

    private Component getBoard() {

        Board board = new Board();
        board.addClassName("estadistica_view__container__basic_board");
        board.addRow(
                createHighlight("Cantidad real de libros", new Span(String.format("%d", cantRealLibros()))),
                createHighlight("Cantidad de libros en el álmacen",
                        new Span(String.format("%d", cantRealLibrosAlmacen()))),
                createHighlight("Cantidad de libros prestados",
                        new Span(String.format("%d", cantRealLibrosPrestados()))));
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

    // Cantidad real de libros
    public int cantRealLibros() {
        int cantReal = 0;
        for (int i = 0; i < listLibros.size(); i++) {
            cantReal += listLibros.get(i).getCantidad();
        }
        return cantReal;
    }

    // Cantidad real de libros en el almacen
    public int cantRealLibrosAlmacen() {
        int librosPrestados = 0;
        for (int i = 0; i < tarjetas.size(); i++) {
            if (tarjetas.get(i).getFechaDevolucion() == null) {
                librosPrestados++;
            }
        }

        return cantRealLibros() - librosPrestados;
    }

    // Cantidad de libros en manos de los estudiantes
    public int cantRealLibrosPrestados() {
        return cantRealLibros() - cantRealLibrosAlmacen();
    }

    // Graficos de pasteles
    private Component getGraficosPasteles() {
        Board board = new Board();
        board.addClassName("estadistica_view__container__grafics_key_board");
        board.addRow(
                getEstadisticasProrcientoLibros());

        return board;
    }

    private Component getEstadisticasProrcientoLibros() {
        configuration = new Configuration();
        chart = new Chart(ChartType.PIE);

        float promedioP = 0;
        float promedioA = 0;
        if (cantRealLibros() != 0) {
            promedioP = cantRealLibrosPrestados() * 100 / cantRealLibros();
            promedioA = cantRealLibrosAlmacen() * 100 / cantRealLibros();
        }

        DataSeries dataSeries = new DataSeries();
        dataSeries.add(new DataSeriesItem(String.format("Libros prestados: %.0f", promedioP ) + "%", promedioP));
        dataSeries
                .add(new DataSeriesItem(String.format("Libros en el álmacen: %.0f", promedioA ) + "%", promedioA));

        chart.getConfiguration().setSeries(dataSeries);
        chart.getConfiguration().setTitle("Distribución de libros");
        chart.getConfiguration().setSubTitle(String.format("Cantidad real de libros: %d", cantRealLibros()));
        configuration = chart.getConfiguration();
        chart.addClassNames("text-xl", "mt-m");

        VerticalLayout serviceHealth = new VerticalLayout(chart);
        serviceHealth.addClassName("p-l");
        serviceHealth.setPadding(false);
        serviceHealth.setSpacing(false);
        serviceHealth.getElement().getThemeList().add("spacing-l");
        serviceHealth.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        return serviceHealth;
    }

    private StreamResource exportChart() {

        /*
         * ExportOptions options = new ExportOptions();
         * options.setWidth(800);
         * options.setHeight(600);
         * 
         * try (SVGGenerator generator = new SVGGenerator()) {
         * String svg = generator.generate(configuration);
         * SvgWrapper styleSvg = new SvgWrapper(svg);
         * 
         * // container.add(styleSvg);
         * } catch (IOException | InterruptedException ex) {
         * // handle exceptions accordingly
         * System.out.print("Error al exportar csv");
         * }
         */

        StreamResource source = new StreamResource("ReporteEvaluaciones.pdf", () -> {
            String path = "src/main/resources/META-INF/resources/archivos/RecurosMateriales.pdf";

            try {
                PdfWriter pdfWriter = new PdfWriter(path);
                PdfDocument pdfDocument = new PdfDocument(pdfWriter);
                Document document = new Document(pdfDocument);

                document.add(infoPdf());
                document.close();

                File initialFile = new File(path);
                InputStream targetStream = new FileInputStream(initialFile);
                return targetStream;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Notification notification = Notification.show("Ocurrió al exportar el documento", 5000,
                        Notification.Position.MIDDLE);
                ;
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                return null;
            }

        });
        return source;

    }
    
    private Table infoPdf() {
        float firstGrow_columnWidth[] = { 600 };

        Table firstGrow = new Table(firstGrow_columnWidth);

        firstGrow.addCell(new Cell().add("UNIVERSIDAD DE LAS CIENCIAS INFORMÁTICAS")
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setMarginTop(30f)
                .setMarginBottom(30f)
                .setFontSize(14f)
                .setBold()
                .setBorder(Border.NO_BORDER));

        firstGrow.addCell(new Cell().add("Estadísticas: Álmacen Facultad 4")
                .setTextAlignment(TextAlignment.LEFT)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setMarginTop(20f)
                .setMarginBottom(20f)
                .setFontSize(14f)
                .setBold()
                .setBorder(Border.NO_BORDER));

        firstGrow.addCell(new Cell().add("CANTIDAD REAL DE LIBROS: " + Integer.toString(cantRealLibros()))
                .setTextAlignment(TextAlignment.LEFT)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setMarginTop(10f)
                .setMarginBottom(10f)
                .setFontSize(10f)
                .setBorder(Border.NO_BORDER));

        firstGrow.addCell(
                new Cell().add("CANTIDAD DE LIBROS EN EL ÁLMACEN: " + Integer.toString(cantRealLibrosAlmacen()))
                        .setTextAlignment(TextAlignment.LEFT)
                        .setVerticalAlignment(VerticalAlignment.MIDDLE)
                        .setMarginTop(10f)
                        .setMarginBottom(10f)
                        .setFontSize(10f)
                        .setBorder(Border.NO_BORDER));

        firstGrow.addCell(new Cell().add("CANTIDAD DE LIBROS PRESTADOS: " + cantRealLibrosPrestados())
                .setTextAlignment(TextAlignment.LEFT)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setMarginTop(10f)
                .setMarginBottom(10f)
                .setFontSize(10f)
                .setBorder(Border.NO_BORDER));

        return firstGrow;
    }

}
