package trabajodediploma.views.modulo;

import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;

import trabajodediploma.data.entity.Modulo;
import trabajodediploma.data.entity.RecursoMaterial;

public class ModuloForm extends FormLayout {

    private Modulo modulo;
    TextField nombre = new TextField();
    //MultiSelectListBox<RecursoMaterial> recursosMateriales = new MultiSelectListBox<>();
    Button save = new Button("Añadir", VaadinIcon.PLUS.create());
    Button close = new Button("Cancelar", VaadinIcon.ERASER.create());
    BeanValidationBinder<Modulo> binder = new BeanValidationBinder<>(Modulo.class);

    public ModuloForm(List<RecursoMaterial> materiales) {
        addClassName("modulo-form");
        binder.bindInstanceFields(this);
        // Config form
        // nombre
        nombre.setLabel("Nombre");
        nombre.setAutofocus(true);
        nombre.setRequired(true);
        nombre.setMinLength(1);
        nombre.setMaxLength(100);
        nombre.setErrorMessage("Solo letras y número, mínimo 1 caracteres y máximo 100");
        nombre.addValueChangeListener(event -> {
            event.getSource().setHelperText(event.getValue().length() + "/" + 100);
        });
        // Recursos Materiales
        //recursosMateriales.setItems(new HashSet<>(materiales));
        //recursosMateriales.setRenderer(new ComponentRenderer<>(material -> new Text(material.getDescripcion())));

        add(nombre, /*recursosMateriales,*/createButtonsLayout());
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

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));

        buttonlayout.add(save, close);

        return buttonlayout;
    }

    public void setModulo(Modulo modulo) {
        this.modulo = modulo;
        binder.readBean(modulo);
    }

    private void validateAndSave() {
        try {
            binder.writeBean(modulo);
            this.modulo.setNombre(nombre.getValue());
           
            fireEvent(new SaveEvent(this, modulo));
        } catch (ValidationException e) {
            e.printStackTrace();
            Notification notification = Notification.show(
                    "Ocurrió un problema al intentar almacenar el recurso",
                    5000,
                    Notification.Position.MIDDLE);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    // Events
    public static abstract class ModuloFormEvent extends ComponentEvent<ModuloForm> {

        private Modulo modulo;

        protected ModuloFormEvent(ModuloForm source, Modulo modulo) {
            super(source, false);
            this.modulo = modulo;
        }

        public Modulo getModulo() {
            return modulo;
        }
    }

    // save-event
    public static class SaveEvent extends ModuloFormEvent {
        SaveEvent(ModuloForm source, Modulo modulo) {
            super(source, modulo);
        }
    }

    // delete-event
    public static class DeleteEvent extends ModuloFormEvent {

        DeleteEvent(ModuloForm source, Modulo modulo) {
            super(source, modulo);
        }

    }

    // close-event
    public static class CloseEvent extends ModuloFormEvent {

        CloseEvent(ModuloForm source) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
            ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

}
