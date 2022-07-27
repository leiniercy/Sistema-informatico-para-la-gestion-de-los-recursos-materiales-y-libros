/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabajodediploma.views.menu_personal.modificar_perfil;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;
import java.util.List;
import trabajodediploma.data.entity.Area;
import trabajodediploma.data.entity.Trabajador;
import trabajodediploma.data.entity.Trabajador;
import trabajodediploma.data.entity.User;

/**
 *
 * @author leinier
 */
public class ModificarPerfilTrabajadorForm extends FormLayout {

    private User user;
    private List<Area> areas;
    private Trabajador trabajador;
    TextField name = new TextField();
    EmailField email = new EmailField();
    TextField solapin = new TextField();
    ComboBox<String> categoria = new ComboBox<>();
    ComboBox<Area> area = new ComboBox<>();

    Button save = new Button("Añadir", VaadinIcon.PLUS.create());
    Button close = new Button("Cancelar", VaadinIcon.ERASER.create());

    BeanValidationBinder<Trabajador> binderTrabajador = new BeanValidationBinder<>(Trabajador.class);
    BeanValidationBinder<User> binderUser = new BeanValidationBinder<>(User.class);

    public ModificarPerfilTrabajadorForm(List<Area> areas, User user, Trabajador trabajador) {
        this.user = user;
        this.areas = areas;
        this.trabajador = trabajador;
        Configuration();
        add(name, email, solapin, categoria, area, createButtonsLayout());
    }

    //Configuration
    private void Configuration() {
        binderTrabajador.bindInstanceFields(this);
        binderUser.bindInstanceFields(this);
        //nombre
        name.setLabel("Nombre");
        name.setPlaceholder("Nombre y  apellidos...");
        name.setClearButtonVisible(true);
        name.setValue(trabajador.getUser().getName());
        //email
        email.setLabel("Correo");
        email.setPlaceholder("usuario@trabajadors.uci.cu");
        email.setValue(trabajador.getEmail());
        email.setClearButtonVisible(true);
        email.setPattern("^[a-zA-Z][a-zA-Z0-9_\\.][a-zA-Z0-9]+(@trabajadors\\.uci\\.cu)$");
        email.setErrorMessage("Por favor escriba un correo válido");
        //solapin
        solapin.setLabel("Solapín");
        solapin.setPlaceholder("E1705587");
        solapin.setValue(trabajador.getSolapin());
        solapin.setClearButtonVisible(true);
        solapin.setMinLength(7);
        solapin.setMaxLength(7);
        solapin.setPattern("^[A-Z][0-9]+$");
        solapin.setErrorMessage("Una letra , mínimo 7 caracteres y máximo 7");
        solapin.addValueChangeListener(event -> {
            event.getSource().setHelperText(event.getValue().length() + "/" + 7);
        });
        //categoria
        categoria.setPlaceholder("Categoría");
        categoria.setItems("Tabajador", "otras");
        categoria.setValue(trabajador.getCategoria());
        //Area 
        area.setPlaceholder("Area");
        area.setItems(areas);
        area.setItemLabelGenerator(Area::getNombre);
        area.setValue(trabajador.getArea());
    }

    //Buttons 
    private HorizontalLayout createButtonsLayout() {
        HorizontalLayout buttonlayout = new HorizontalLayout();
        buttonlayout.addClassName("button-layout");
        save.addClickListener(event -> validateAndSave());
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickShortcut(Key.ENTER);
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        close.addClickShortcut(Key.ESCAPE);
        binderTrabajador.addStatusChangeListener(e -> save.setEnabled(binderTrabajador.isValid()));
        buttonlayout.add(save, close);

        return buttonlayout;
    }

    //Set Trabajador
    public void setTrabajador(Trabajador trabajador, User user) {
        this.trabajador = trabajador;
        this.user = user;
        binderTrabajador.readBean(trabajador);
        binderUser.readBean(user);
        this.trabajador.setUser(user);
    }

    //Validate and Save
    private void validateAndSave() {
        try {
            binderTrabajador.writeBean(trabajador);
            binderUser.writeBean(user);
            //user
            this.user.setName(name.getValue());
            //trabajador
            this.trabajador.setEmail(email.getValue());
            this.trabajador.setCategoria(categoria.getValue());
            this.trabajador.setArea(area.getValue());
            this.trabajador.setSolapin(solapin.getValue());
            this.trabajador.setUser(user);
            fireEvent(new SaveEvent(this, trabajador, user));
        } catch (ValidationException e) {
            e.printStackTrace();
            Notification notification = Notification.show(
                    "Ocurrió un problema al intentar guardar el trabajador",
                    5000,
                    Notification.Position.MIDDLE
            );
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    // Events
    public static abstract class TrabajadorFormEvent extends ComponentEvent<ModificarPerfilTrabajadorForm> {

        private Trabajador trabajador;
        private User user;

        protected TrabajadorFormEvent(ModificarPerfilTrabajadorForm source, Trabajador trabajador, User user) {
            super(source, false);
            this.trabajador = trabajador;
            this.user = user;
        }

        public Trabajador getTrabajador() {
            return trabajador;
        }

        public User getUser() {
            return user;
        }
    }

    //Save Event
    public static class SaveEvent extends TrabajadorFormEvent {

        SaveEvent(ModificarPerfilTrabajadorForm source, Trabajador trabajador, User user) {
            super(source, trabajador, user);
        }
    }

    //Delete Event
    public static class DeleteEvent extends TrabajadorFormEvent {

        DeleteEvent(ModificarPerfilTrabajadorForm source, Trabajador trabajador, User user) {
            super(source, trabajador, user);
        }

    }

    //Close Event
    public static class CloseEvent extends TrabajadorFormEvent {

        CloseEvent(ModificarPerfilTrabajadorForm source) {
            super(source, null, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
            ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

}
