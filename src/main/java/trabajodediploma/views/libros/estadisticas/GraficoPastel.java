/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabajodediploma.views.libros.estadisticas;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinRequest;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.annotation.security.RolesAllowed;
import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import org.jfree.data.general.DefaultPieDataset;

import trabajodediploma.views.MainLayout;

/**
 *
 * @author leinier
 */
@PageTitle("Estadísticas")
@Route(value = "estadisticas-view", layout = MainLayout.class)
@RolesAllowed("RESP_ALMACEN")
public class GraficoPastel extends Div {

    Image img;

    public GraficoPastel() {
        init();
        add(img);

    }

    protected void init() {

        int n1 = 50;
        int n2 = 100;
        int n3 = 150;
        int n4 = 200;

        DefaultPieDataset datos = new DefaultPieDataset();
        datos.setValue("Videojuegos", n1);
        datos.setValue("Deportes", n2);
        datos.setValue("Internet", n3);
        datos.setValue("Libros", n4);

        JFreeChart grafico_circular = ChartFactory.createPieChart(
                "Grafico circular", // Nombre del Grafico
                datos, // datos
                true, // nombre de todas las categorias
                true, // herramientas
                false // generacion URL
        );

        JFreeChartWrapper wrapper = new JFreeChartWrapper(grafico_circular);


        img = new Image(wrapper.getStreamResource(), "Gráfica");

    }

}
