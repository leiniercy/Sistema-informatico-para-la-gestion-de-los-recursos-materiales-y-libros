/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabajodediploma.views.login;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;

/**
 *
 * @author leinier
 */
public class CrearEstudianteForm extends FormLayout {

    TextField nombre;
    TextField apellidos;
    EmailField email;
    TextField solapin;
    ComboBox<Integer> anno_academico;
    ComboBox<String> facultad;

    Button save = new Button("Añadir", VaadinIcon.PLUS.create());
    Button close = new Button("Cancelar", VaadinIcon.ERASER.create());

    public CrearEstudianteForm() {
        Configuracion();
        add(nombre, apellidos,solapin,email, anno_academico, facultad, createButtonsLayout());
    }

    private void Configuracion() {

        nombre = new TextField();
        nombre.setPlaceholder("Nombre");

        apellidos = new TextField();
        apellidos.setPlaceholder("Apellidos");
        
        email = new EmailField();
        email.setPlaceholder("usuario@estudiantes.uci.cu");
        email.setValue("usuario@estudiantes.uci.cu");
        email.setClearButtonVisible(true);

        solapin = new TextField();
        solapin.setPlaceholder("Solapín");

        anno_academico = new ComboBox<>();
        anno_academico.setPlaceholder("Año académico");
        anno_academico.setItems(1, 2, 3, 4, 5);

        facultad = new ComboBox<>();
        facultad.setPlaceholder("Facultad");
        facultad.setItems("Facultad 1", "Facultad 2", "Facultad 3", "Facultad 4", "CITEC", "FTE");

    }

    private HorizontalLayout createButtonsLayout() {

        HorizontalLayout buttonlayout = new HorizontalLayout();
        buttonlayout.addClassName("button-layout");

        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickShortcut(Key.ENTER);

        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        close.addClickShortcut(Key.ESCAPE);

        buttonlayout.add(save, close);

        return buttonlayout;
    }

}
