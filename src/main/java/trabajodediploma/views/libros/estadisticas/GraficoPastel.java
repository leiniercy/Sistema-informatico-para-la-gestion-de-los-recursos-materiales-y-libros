/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabajodediploma.views.libros.estadisticas;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;

import org.jfree.data.general.DefaultPieDataset;

/**
 *
 * @author leinier
 */
public class GraficoPastel extends Div {

    Image img;
    int cantRealLibrosAlmacen;
    int cantRealLibrosPrestados;
    int cantRealLibros;

    public GraficoPastel(int cantRealLibrosAlmacen, int cantRealLibrosPrestados, int cantRealLibros) {
        this.cantRealLibrosAlmacen = cantRealLibrosAlmacen;
        this.cantRealLibrosPrestados = cantRealLibrosPrestados;
        this.cantRealLibros = cantRealLibros;
        configuracionGraficoPastel();
        add(img);

    }

    protected void configuracionGraficoPastel() {
        DefaultPieDataset datos = new DefaultPieDataset();
        try {
            DecimalFormat df = new DecimalFormat("#.##");
            df.setRoundingMode(RoundingMode.CEILING);
            double prestados = 0;
            double almacenados = 0;

            if (cantRealLibros != 0) {
                prestados = (cantRealLibrosPrestados * 100 * 1.0) / cantRealLibros;
                almacenados = (cantRealLibrosAlmacen * 100 * 1.0) / cantRealLibros;
            }
            datos.setValue("Libros prestados " + df.format(prestados) + "%", cantRealLibrosPrestados);
            datos.setValue("Libros almacenados " + df.format(almacenados) + "%", cantRealLibrosAlmacen);

        } catch (ArithmeticException e) {

        }

        JFreeChart grafico_circular = ChartFactory.createPieChart(
                "Reparto de libros", // Nombre del Grafico
                datos, // datos
                true, // nombre de todas las categorias
                true, // herramientas
                false // generacion URL
        );

        JFreeChartWrapper wrapper = new JFreeChartWrapper(grafico_circular);
        img = new Image(wrapper.getStreamResource(), "Distribución de libros");
    }

}
