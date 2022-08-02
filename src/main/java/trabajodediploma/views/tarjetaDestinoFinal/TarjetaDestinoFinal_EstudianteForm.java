
package trabajodediploma.views.tarjetaDestinoFinal;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;
import trabajodediploma.data.entity.DestinoFinal;
import trabajodediploma.data.entity.Estudiante;
import trabajodediploma.data.entity.Modulo;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;


/**
 *
 * @author leinier
 */

public class TarjetaDestinoFinal_EstudianteForm  extends FormLayout{
   
    DestinoFinal tarjeta;
    ComboBox<Estudiante> estudiante = new ComboBox<>("Estudiante");
    ComboBox<Modulo> modulo = new ComboBox<>("Modulo");
    DatePicker fecha = new DatePicker("Fecha Entrega");
   
    Button save = new Button("Añadir", VaadinIcon.PLUS.create());
    Button close = new Button("Cancelar", VaadinIcon.ERASER.create());

    BeanValidationBinder<DestinoFinal> binder = new BeanValidationBinder<>(DestinoFinal.class);

    public TarjetaDestinoFinal_EstudianteForm(List<Estudiante> estudiantes, List<Modulo> modulos){
      
        addClassNames("tarjeta-estudiante-form");
        binder.bindInstanceFields(this);
        
        /*Config form*/
        
        /*Estudiante*/
        estudiante.setItems(estudiantes);
        estudiante.setItemLabelGenerator(est->est.getUser().getName());
        /*Libros*/
        modulo.setItems(modulos);
        modulo.setItemLabelGenerator(Modulo::getNombre);
        /*fecha de entrega*/
        fecha.setMin(LocalDate.now(ZoneId.systemDefault()));
        
        add(estudiante, modulo, fecha, createButtonsLayout());

    }

    private HorizontalLayout createButtonsLayout() {

        HorizontalLayout buttonlayout = new HorizontalLayout();
        buttonlayout.addClassName("button-layout");
        save.addClickListener(event -> validateAndSave());
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickShortcut(Key.ENTER);

        close.addClickListener(event -> fireEvent(new TarjetaDestinoFinal_EstudianteForm.CloseEvent(this)));
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        close.addClickShortcut(Key.ESCAPE);

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
        buttonlayout.add(save, close);

        return buttonlayout;
    }

    public void setDestinoFinal(DestinoFinal tarjeta) {
        this.tarjeta = tarjeta;
        binder.readBean(tarjeta);
    }

    private void validateAndSave() {
        try {
            binder.writeBean(tarjeta);
            fireEvent(new TarjetaDestinoFinal_EstudianteForm.SaveEvent(this, tarjeta));
        } catch (ValidationException e) {
            e.printStackTrace();
            Notification notification = Notification.show(
                    "Ocurrió un problema al intentar almacenar La tarjeta",
                    5000,
                    Notification.Position.MIDDLE
            );
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }


    // Events
    public static abstract class DestinoFinalEstudianteFormEvent extends ComponentEvent<TarjetaDestinoFinal_EstudianteForm> {

        private DestinoFinal destinoFinal;

        protected DestinoFinalEstudianteFormEvent(TarjetaDestinoFinal_EstudianteForm source, DestinoFinal DestinoFinal) {
            super(source, false);
            this.destinoFinal = DestinoFinal;
        }

        public DestinoFinal getDestinoFinal() {
            return destinoFinal;
        }
    }

    public static class SaveEvent extends DestinoFinalEstudianteFormEvent {

        SaveEvent(TarjetaDestinoFinal_EstudianteForm source, DestinoFinal DestinoFinal) {
            super(source, DestinoFinal);
        }
    }

    public static class DeleteEvent extends DestinoFinalEstudianteFormEvent {

        DeleteEvent(TarjetaDestinoFinal_EstudianteForm source, DestinoFinal DestinoFinal) {
            super(source, DestinoFinal);
        }

    }

    public static class CloseEvent extends DestinoFinalEstudianteFormEvent {

        CloseEvent(TarjetaDestinoFinal_EstudianteForm source) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
            ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }


}
