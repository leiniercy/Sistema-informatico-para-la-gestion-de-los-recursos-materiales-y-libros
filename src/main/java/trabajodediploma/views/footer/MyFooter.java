/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabajodediploma.views.footer;

import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;

/**
 *
 * @author leinier
 */
public class MyFooter extends HorizontalLayout {

    public MyFooter() {
        setWidthFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultVerticalComponentAlignment(Alignment.CENTER);
        
        HorizontalLayout ly = new HorizontalLayout(new Span(VaadinIcon.ACADEMY_CAP.create()), new H6("Universidad de Ciencias Informáticas"));
        ly.setAlignItems(Alignment.BASELINE);

        add(ly);
    }

}
