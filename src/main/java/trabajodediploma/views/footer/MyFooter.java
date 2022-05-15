/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabajodediploma.views.footer;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;

/**
 *
 * @author leinier
 */
public class MyFooter extends Div {

    public MyFooter() {
        addClassName("div-footer");
        setWidthFull();
        
        Span icon =new Span(VaadinIcon.ACADEMY_CAP.create());
        icon.addClassName("icon");
        H6 title = new H6("Universidad de Ciencias Informáticas");
        title.addClassName("title");
        Div footer = new Div(icon,title);
        footer.addClassName("footer");

        add(footer);
    }

}
