/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabajodediploma.views.footer;

import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.icon.VaadinIcon;
/**
 *
 * @author leinier
 */
public class MyFooter extends Footer {

    public MyFooter() {
        addClassName("footer");
        H6 content = new H6();
        content.add(VaadinIcon.ACADEMY_CAP.create());
        content.add("Universidad de Ciencias Informáticas");
        add(content);
    }

}
