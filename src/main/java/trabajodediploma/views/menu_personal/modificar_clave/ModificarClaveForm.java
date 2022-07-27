/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabajodediploma.views.menu_personal.modificar_clave;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import trabajodediploma.data.entity.User;

/**
 *
 * @author leinier
 */
public class ModificarClaveForm extends FormLayout {

    private PasswordEncoder passwordEncoder;

    User user;
    PasswordField initPassword = new PasswordField();
    PasswordField hashedPassword  = new PasswordField();
    PasswordField confirmPassword = new PasswordField();
    
    Button save = new Button("Añadir", VaadinIcon.PLUS.create());
    Button close = new Button("Cancelar", VaadinIcon.ERASER.create());

    BeanValidationBinder<User> binder = new BeanValidationBinder<>(User.class);

    public ModificarClaveForm(User user, PasswordEncoder passwordEncoder) {
        this.user = user;
        this.passwordEncoder = passwordEncoder;
        Configuracion();
        add(initPassword, hashedPassword, confirmPassword, createButtonsLayout());
    }

    private void Configuracion() {
        addClassName("modificar-clave-form");
        binder.bindInstanceFields(this);
        //contraseña actual
        initPassword.setPlaceholder("Contraseña actual...");
        //initPassword.setValue(user.getHashedPassword());
        
        //nueva contraseña 
        hashedPassword.setPlaceholder("Nueva contraseña...");
        hashedPassword.setMinLength(8);
        hashedPassword.setMaxLength(255);
        hashedPassword.setClearButtonVisible(true);
        hashedPassword.setErrorMessage("mínimo 8 caracteres y máximo 255");
        hashedPassword.addValueChangeListener(event -> {
            event.getSource().setHelperText(event.getValue().length() + "/" + 255);
        });
        
        //confirmacion de nueva contraseña
        confirmPassword.setPlaceholder("Confirmar contraseña...");
        confirmPassword.setMinLength(8);
        confirmPassword.setMaxLength(255);
        confirmPassword.setClearButtonVisible(true);
        confirmPassword.setErrorMessage("mínimo 8 caracteres y máximo 255");
        confirmPassword.addValueChangeListener(event -> {
            event.getSource().setHelperText(event.getValue().length() + "/" + 255);
        });
    }

    private HorizontalLayout createButtonsLayout() {
        HorizontalLayout buttonlayout = new HorizontalLayout();
        buttonlayout.addClassName("button-layout");
        save.addClickListener(event -> validateAndSave());
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickShortcut(Key.ENTER);
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        close.addClickShortcut(Key.ESCAPE);
        buttonlayout.add(save, close);
        return buttonlayout;
    }

    public void setUser(User user) {
        this.user = user;
        binder.readBean(user);
    }

    private void validateAndSave() {
        try {
            if (    !initPassword.getValue() .equals( hashedPassword.getValue() ) 
                    && initPassword.getValue().equals(user.getConfirmPassword())
                    && hashedPassword.getValue().equals(confirmPassword.getValue())) {
                binder.writeBean(user);
                this.user.setHashedPassword(passwordEncoder.encode(hashedPassword.getValue()));
                //this.user.setConfirmPassword(passwordEncoder.encode(confirmPassword.getValue()));
                this.user.setConfirmPassword(confirmPassword.getValue());
                fireEvent(new ModificarClaveForm.SaveEvent(this, user));
            } else {
                Notification notification = Notification.show(
                        "contraseña incorrecta",
                        5000,
                        Notification.Position.MIDDLE
                );
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        } catch (ValidationException e) {
            e.printStackTrace();
            Notification notification = Notification.show(
                    "Ocurrió un problema al intentar cambiar la clave",
                    5000,
                    Notification.Position.MIDDLE
            );
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
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
