/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabajodediploma.views.login;

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
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;
import java.util.List;
import trabajodediploma.data.entity.Area;
import trabajodediploma.data.entity.Trabajador;
import trabajodediploma.data.entity.User;

/**
 *
 * @author leinier
 */
public class CrearTrabajadorForm extends FormLayout {

    Trabajador trabajador;
    User user;
    List<Area> listArea;
    TextField nombre = new TextField();
    TextField apellidos = new TextField();
    EmailField email = new EmailField();
    TextField solapin = new TextField();
    ComboBox<String> categoria = new ComboBox<>();
    ComboBox<Area> area = new ComboBox<>();

    BeanValidationBinder<Trabajador> binder = new BeanValidationBinder<>(Trabajador.class);

    Button save = new Button("Añadir", VaadinIcon.PLUS.create());

    public CrearTrabajadorForm(List<Area> listArea, User user) {
        this.listArea = listArea;
        this.user = user;
        Configuration();
        add(nombre, apellidos, solapin, email, categoria, area, createButtonsLayout());
    }

    //Configuration
    private void Configuration() {
        addClassName("crear-trabajdor-form");
        binder.bindInstanceFields(this);
        //nombre
        nombre.setPlaceholder("Nombre");
        nombre.getElement().setAttribute("nombre", "Ejemplo: Daniel");
        nombre.setAutofocus(true);
        nombre.setMinLength(2);
        nombre.setMaxLength(100);
        //  nombre.setPattern("^[a-zA-ZÀ-ÿ\\u00f1\\u00d1]+(\\s*[a-zA-ZÀ-ÿ\\u00f1\\u00d1]*)*[a-zA-ZÀ-ÿ\\u00f1\\u00d1]+$");
        nombre.setErrorMessage("Solo letras, mínimo 2 caracteres y máximo 100");
        nombre.addValueChangeListener(event -> {
            event.getSource().setHelperText(event.getValue().length() + "/" + 100);
        });
        //apellidos
        apellidos.setPlaceholder("Apellidos");
        apellidos.getElement().setAttribute("apellidos", "Ejemplo:Perez Diaz");
        apellidos.setAutofocus(true);
        apellidos.setRequired(true);
        apellidos.setMinLength(3);
        apellidos.setMaxLength(100);
        //    apellidos.setPattern("^[a-zA-ZÀ-ÿ\\u00f1\\u00d1]+(\\s*[a-zA-ZÀ-ÿ\\u00f1\\u00d1]*)*[a-zA-ZÀ-ÿ\\u00f1\\u00d1]+$");
        apellidos.setErrorMessage("Solo letras, mínimo 3 caracteres y máximo 100");
        apellidos.addValueChangeListener(event -> {
            event.getSource().setHelperText(event.getValue().length() + "/" + 100);
        });
        //email
        email.setLabel("Correo");
        email.setPlaceholder("usuario@uci.cu");
        email.setValue("usuario@uci.cu");
        email.setClearButtonVisible(true);
        email.setPattern("^[a-zA-Z][a-zA-Z0-9_\\.][a-zA-Z0-9]+(@uci\\.cu)$");
        email.setErrorMessage("Por favor escriba un correo válido");
        //solapin
        solapin.setLabel("Solapín");
        solapin.setPlaceholder("E1705587");
        solapin.getElement().setAttribute("solapin", "E1705587");
        solapin.setAutofocus(true);
        solapin.setMinLength(7);
        solapin.setMaxLength(7);
        solapin.setErrorMessage("Una letra , mínimo 7 caracteres y máximo 7");
        solapin.addValueChangeListener(event -> {
            event.getSource().setHelperText(event.getValue().length() + "/" + 7);
        });
        //categoria
        categoria.setPlaceholder("Categoría");
        categoria.setItems("Tabajador", "otras");
        //area
        area.setPlaceholder("Área");
        area.setItems(listArea);
        area.setItemLabelGenerator(Area::getNombre);
    }

    //Buttons 
    private HorizontalLayout createButtonsLayout() {
        HorizontalLayout buttonlayout = new HorizontalLayout();
        buttonlayout.addClassName("button-layout");
        save.addClickListener(event -> validateAndSave());
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickShortcut(Key.ENTER);
        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
        buttonlayout.add(save);
        return buttonlayout;
    }

    //Set Trabajador
    public void setTrabajador(Trabajador trabajador) {
        this.trabajador = trabajador;
        binder.readBean(trabajador);
        if (user == null) {
            this.trabajador.setUser(null);
        } else {
            this.trabajador.setUser(user);;
        }
    }

    //Validate and Save
    private void validateAndSave() {
        try {
            binder.writeBean(trabajador);
            this.trabajador.setUser(user);
            this.trabajador.setNombre(nombre.getValue());
            this.trabajador.setApellidos(apellidos.getValue());
            this.trabajador.setSolapin(solapin.getValue());
            this.trabajador.setEmail(email.getValue());
            this.trabajador.setCategoria(categoria.getValue());
            this.trabajador.setArea(area.getValue());
            fireEvent(new CrearTrabajadorForm.SaveEvent(this, trabajador));
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
    public static abstract class TrabajadorFormEvent extends ComponentEvent<CrearTrabajadorForm> {

        private Trabajador trabajador;

        protected TrabajadorFormEvent(CrearTrabajadorForm source, Trabajador trabajador) {
            super(source, false);
            this.trabajador = trabajador;
        }

        public Trabajador getTrabajador() {
            return trabajador;
        }
    }

    //Save Event
    public static class SaveEvent extends TrabajadorFormEvent {

        SaveEvent(CrearTrabajadorForm source, Trabajador trabajador) {
            super(source, trabajador);
        }
    }

    //Delete Event
    public static class DeleteEvent extends TrabajadorFormEvent {

        DeleteEvent(CrearTrabajadorForm source, Trabajador trabajador) {
            super(source, trabajador);
        }

    }

    //Close Event
    public static class CloseEvent extends TrabajadorFormEvent {

        CloseEvent(CrearTrabajadorForm source) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
            ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}
