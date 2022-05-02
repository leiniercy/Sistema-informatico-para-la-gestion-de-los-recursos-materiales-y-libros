/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabajodediploma.views.tarjetaprestamo;

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
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import trabajodediploma.data.entity.Libro;
import trabajodediploma.data.entity.TarjetaPrestamo;
import trabajodediploma.data.entity.Trabajador;


/**
 *
 * @author leinier
 */
public class TarjetaPrestamoTrabajadorForm extends FormLayout {

    private TarjetaPrestamo tarjetaPrestamo;

    ComboBox<Trabajador> trabajador = new ComboBox<>("Trabajador");
    ComboBox<Libro> libro = new ComboBox<>("Libro");
    DatePicker fechaPrestamo = new DatePicker("Fecha Prestamo");
    DatePicker fechaDevolucion = new DatePicker("Fecha Devolucion");

    Button save = new Button("Añadir", VaadinIcon.PLUS.create());
    Button close = new Button("Cancelar", VaadinIcon.ERASER.create());

    BeanValidationBinder<TarjetaPrestamo> binder = new BeanValidationBinder<>(TarjetaPrestamo.class);

    public TarjetaPrestamoTrabajadorForm(Trabajador t, List<Libro> listLibros) {

        addClassNames("tarjeta-trabajador-form");
        binder.bindInstanceFields(this);
        
        /*Config form*/
        
        /*Trabajador*/
        trabajador.setItems(t);
        trabajador.setItemLabelGenerator(Trabajador::getNombreApellidos);
        trabajador.setValue(t);
        /*Libros*/
        libro.setItems(listLibros);
        libro.setItemLabelGenerator(Libro::getTitulo);
        /*fecha de prestamo*/
        fechaPrestamo.setMin(LocalDate.now(ZoneId.systemDefault()));
        /*fecha de devolucion*/
        fechaDevolucion.setMin(LocalDate.now(ZoneId.systemDefault()));

        add(trabajador, libro, fechaPrestamo, fechaDevolucion, createButtonsLayout());

    }

    private HorizontalLayout createButtonsLayout() {

        HorizontalLayout buttonlayout = new HorizontalLayout();
        buttonlayout.addClassName("button-layout");
        save.addClickListener(event -> validateAndSave());
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickShortcut(Key.ENTER);

        close.addClickListener(event -> fireEvent(new TarjetaPrestamoTrabajadorForm.CloseEvent(this)));
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        close.addClickShortcut(Key.ESCAPE);

        //binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
        buttonlayout.add(save, close);

        return buttonlayout;
    }

    public void setTarjetaPrestamo(TarjetaPrestamo tarjetaPrestamo) {
        this.tarjetaPrestamo = tarjetaPrestamo;
        binder.readBean(tarjetaPrestamo);
    }

    private void validateAndSave() {
        try {
            binder.writeBean(tarjetaPrestamo);
            fireEvent(new TarjetaPrestamoTrabajadorForm.SaveEvent(this, tarjetaPrestamo));
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
    public static abstract class TarjetaPrestamoTrabajadorFormEvent extends ComponentEvent<TarjetaPrestamoTrabajadorForm> {

        private TarjetaPrestamo tarjetaPrestamo;

        protected TarjetaPrestamoTrabajadorFormEvent(TarjetaPrestamoTrabajadorForm source, TarjetaPrestamo tarjetaPrestamo) {
            super(source, false);
            this.tarjetaPrestamo = tarjetaPrestamo;
        }

        public TarjetaPrestamo getTarjetaPrestamo() {
            return tarjetaPrestamo;
        }
    }

    public static class SaveEvent extends TarjetaPrestamoTrabajadorFormEvent {

        SaveEvent(TarjetaPrestamoTrabajadorForm source, TarjetaPrestamo tarjetaPrestamo) {
            super(source, tarjetaPrestamo);
        }
    }

    public static class DeleteEvent extends TarjetaPrestamoTrabajadorFormEvent {

        DeleteEvent(TarjetaPrestamoTrabajadorForm source, TarjetaPrestamo tarjetaPrestamo) {
            super(source, tarjetaPrestamo);
        }

    }

    public static class CloseEvent extends TarjetaPrestamoTrabajadorFormEvent {

        CloseEvent(TarjetaPrestamoTrabajadorForm source) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
            ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

}
