/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabajodediploma.views.login;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;

/**
 *
 * @author leinier
 */
public class CrearInformacionPerfilView extends VerticalLayout {

    Select<String> selection;
    CrearEstudianteForm form_estudiante;
    CrearTrabajadorForm form_trabajador;
    Div container;

    public CrearInformacionPerfilView() {
        Configuracion();

        add(selection);
        selection.addValueChangeListener(event -> {

            if (event.getValue() == "Estudiante") {
                container.removeAll();
                container.add(form_estudiante);
                add(container);
            } else if (event.getValue() == "Trabajador") {
                container.removeAll();
                container.add(form_trabajador);
                add(container);
            }
        });

    }

    private void Configuracion() {
        form_estudiante = new CrearEstudianteForm();
        form_trabajador = new CrearTrabajadorForm();
        container = new Div();
        container.addClassName("container-form");

        selection = new Select<>();
        selection.setLabel("Categoría");
        selection.setPlaceholder("Seleccione una categoría ");
        selection.setItems("Estudiante", "Trabajador");
       
    }

}
