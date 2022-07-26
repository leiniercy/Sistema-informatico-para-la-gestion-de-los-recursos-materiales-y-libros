/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabajodediploma.views;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.shared.Registration;
import trabajodediploma.data.entity.User;

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
    
    
    /* Events*/
    public static abstract class ModificarClaveFormEvent extends ComponentEvent<ModificarClaveForm> {

        private User user;

        protected ModificarClaveFormEvent(ModificarClaveForm source, User user) {
            super(source, false);
            this.user = user;
        }

        public User getUser() {
            return user;
        }
    }
    //save  
    public static class SaveEvent extends ModificarClaveFormEvent {

        SaveEvent(ModificarClaveForm source, User user) {
            super(source, user);
        }
    }
    //delete    
    public static class DeleteEvent extends ModificarClaveFormEvent {

        DeleteEvent(ModificarClaveForm source, User user) {
            super(source, user);
        }

    }
    //close 
    public static class CloseEvent extends ModificarClaveFormEvent {

        CloseEvent(ModificarClaveForm source) {
            super(source, null);
        }
    }
    
    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
            ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

}
