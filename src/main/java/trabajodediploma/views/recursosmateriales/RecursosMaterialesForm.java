package trabajodediploma.views.recursosmateriales;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;

import trabajodediploma.data.entity.RecursoMaterial;

public class RecursosMaterialesForm extends FormLayout {

    RecursoMaterial material;
    TextField codigo = new TextField();
    TextField descripcion = new TextField();
    TextField unidadMedida = new TextField();
    IntegerField cantidad = new IntegerField();
    Button save = new Button("Añadir", VaadinIcon.PLUS.create());
    Button close = new Button("Cancelar", VaadinIcon.ERASER.create());
    BeanValidationBinder<RecursoMaterial> binder = new BeanValidationBinder<>(RecursoMaterial.class);

    public RecursosMaterialesForm() {
        addClassName("recurso-material-form");
        // Config form
        // codigo
        codigo.setLabel("Código");
        codigo.setAutofocus(true);
        codigo.setRequired(true);
        codigo.setMaxLength(255);
        codigo.setErrorMessage("Solo letras, máximo 255 caracteres");
        codigo.addValueChangeListener(event -> {
            event.getSource().setHelperText(event.getValue().length() + "/" + 255);
        });

        descripcion.setLabel("Descripción");
        descripcion.setAutofocus(true);
        descripcion.setRequired(true);
        descripcion.setMaxLength(255);
        descripcion.setErrorMessage("Solo letras y numeros, máximo 255 caracteres");
        descripcion.addValueChangeListener(event -> {
            event.getSource().setHelperText(event.getValue().length() + "/" + 255);
        });

        unidadMedida.setLabel("Unidad de Medida");
        unidadMedida.setAutofocus(true);
        unidadMedida.setRequired(true);
        unidadMedida.setMaxLength(3);

        cantidad = new IntegerField("Cantidad");
        cantidad.setValue(1);
        cantidad.setRequiredIndicatorVisible(true);
        cantidad.setHasControls(true);
        cantidad.setMin(1);
        cantidad.setHelperText("Mínimo 1");

        add(codigo, descripcion, unidadMedida, cantidad, createButtonsLayout());

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

    public void setMaterial(RecursoMaterial material) {
        this.material = material;
        binder.readBean(material);
    }

    private void validateAndSave() {
        try {
            binder.writeBean(material);
            fireEvent(new SaveEvent(this, material));
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
    public static abstract class RecursoMaterialFormEvent extends ComponentEvent<RecursosMaterialesForm> {

        private RecursoMaterial material;

        protected RecursoMaterialFormEvent(RecursosMaterialesForm source, RecursoMaterial material) {
            super(source, false);
            this.material = material;
        }

        public RecursoMaterial getRecursoMaterial() {
            return material;
        }
    }

    public static class SaveEvent extends RecursoMaterialFormEvent {

        SaveEvent(RecursosMaterialesForm source, RecursoMaterial material) {
            super(source, material);
        }
    }

    public static class DeleteEvent extends RecursoMaterialFormEvent {

        DeleteEvent(RecursosMaterialesForm source, RecursoMaterial material) {
            super(source, material);
        }

    }

    public static class CloseEvent extends RecursoMaterialFormEvent {

        CloseEvent(RecursosMaterialesForm source) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
            ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

}
