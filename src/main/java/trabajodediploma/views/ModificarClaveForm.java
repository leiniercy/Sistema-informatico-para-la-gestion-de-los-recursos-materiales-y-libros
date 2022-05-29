/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabajodediploma.views;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.PasswordField;

/**
 *
 * @author leinier
 */
public class ModificarClaveForm extends FormLayout {

    PasswordField initPassword;
    PasswordField hashedPassword;
    PasswordField confirmPassword;

    Button save = new Button("Añadir", VaadinIcon.PLUS.create());
    Button close = new Button("Cancelar", VaadinIcon.ERASER.create());

    public ModificarClaveForm() {
        Configuracion();
        add(initPassword,hashedPassword,confirmPassword,createButtonsLayout());
    }

    private void Configuracion() {
        initPassword = new PasswordField();
        initPassword.setPlaceholder("Contraseña actual...");
        hashedPassword = new PasswordField();
        hashedPassword.setPlaceholder("Nueva contraseña...");
        confirmPassword = new PasswordField();
        confirmPassword.setPlaceholder("Confirmar contraseña...");
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
