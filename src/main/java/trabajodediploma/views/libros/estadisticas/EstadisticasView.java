/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabajodediploma.views.libros.estadisticas;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.color.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Image;
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
import java.net.MalformedURLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

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
    private GraficoPastel graficoPastel;

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
        getGraficoCirciular();
        container.add(getEstadisticas(), graficoPastel);
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

    //Estaidisticas
    private HorizontalLayout getEstadisticas() {

        HorizontalLayout barra_info = new HorizontalLayout();
        barra_info.addClassName("barra_info");

        VerticalLayout cantLibrosReal_div = new VerticalLayout();
        cantLibrosReal_div.addClassName("barra_info__vl");
        H1 cantLibrosReal_h1 = new H1("Cantidad real de libros");
        Span cantLibrosReal_span = new Span(String.format("%d", cantRealLibros()));
        cantLibrosReal_div.add(cantLibrosReal_h1, cantLibrosReal_span);

        VerticalLayout cantLibrosAlmacen_div = new VerticalLayout();
        cantLibrosAlmacen_div.addClassName("barra_info__vl");
        H1 cantLibrosAlmacen_h1 = new H1("Cantidad de libros en el álmacen");
        Span cantLibrosAlmacen_span = new Span(String.format("%d", cantRealLibrosAlmacen()));
        cantLibrosAlmacen_div.add(cantLibrosAlmacen_h1, cantLibrosAlmacen_span);

        VerticalLayout cantLibrosPrestados_div = new VerticalLayout();
        cantLibrosPrestados_div.addClassName("barra_info__vl");
        H1 cantLibrosPrestados_h1 = new H1("Cantidad de libros prestados");
        Span cantLibrosPrestados_span = new Span(String.format("%d", cantRealLibrosPrestados()));
        cantLibrosPrestados_div.add(cantLibrosPrestados_h1, cantLibrosPrestados_span);

        barra_info.add(cantLibrosReal_div, cantLibrosAlmacen_div, cantLibrosPrestados_div);

        return barra_info;
    }

    // Graficos de pasteles
    private void getGraficoCirciular() {
        graficoPastel = new GraficoPastel(cantRealLibrosAlmacen(), cantRealLibrosPrestados(), cantRealLibros());
    }

    //exportar informacion
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

//                ImageData imageData = ImageDataFactory.create("src/main/resources/META-INF/resources/images/logo_pag_2.png");
//                Image img = new Image(imageData);
                
                document.add(infoPdf());
               // document.add(img);
                document.close();

                File initialFile = new File(path);
                InputStream targetStream = new FileInputStream(initialFile);
                return targetStream;
            } catch (FileNotFoundException /*| MalformedURLException*/ e) {
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

    //informacion del pdf
    private Table infoPdf() {
        float firstGrow_columnWidth[] = {600};

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
