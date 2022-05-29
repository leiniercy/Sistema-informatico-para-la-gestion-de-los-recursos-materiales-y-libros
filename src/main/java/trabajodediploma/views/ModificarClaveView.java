/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabajodediploma.views;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import trabajodediploma.data.entity.User;

/**
 *
 * @author leinier
 */
public class ModificarClaveView extends Div {

    ModificarClaveForm form;
    
    public ModificarClaveView() {
        addClassName("modificar-clave-view");
        form = new ModificarClaveForm();
        add(form);            
    }

}
