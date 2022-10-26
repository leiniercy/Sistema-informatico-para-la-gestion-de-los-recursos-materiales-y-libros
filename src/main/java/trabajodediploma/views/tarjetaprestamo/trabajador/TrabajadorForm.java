/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabajodediploma.views.tarjetaprestamo.trabajador;

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
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import trabajodediploma.data.entity.Trabajador;
import trabajodediploma.data.entity.Libro;
import trabajodediploma.data.entity.TarjetaPrestamo;
import trabajodediploma.data.entity.TarjetaPrestamoTrabajador;

/**
 *
 * @author leinier
 */
public class TrabajadorForm extends FormLayout {

    private List<TarjetaPrestamoTrabajador> tarjetasPrestamo;
    private List<Trabajador> trabajadores;
    private VerticalLayout content = new VerticalLayout();
    ComboBox<Libro> libro = new ComboBox<>("Libro");
    DatePicker fechaPrestamo = new DatePicker("Fecha Prestamo");
    DatePicker fechaDevolucion = new DatePicker("Fecha Devolucion");

    Button save = new Button("Añadir", VaadinIcon.PLUS.create());
    Button close = new Button("Cancelar", VaadinIcon.ERASER.create());

    BeanValidationBinder<TarjetaPrestamo> binder = new BeanValidationBinder<>(TarjetaPrestamo.class);

    public TrabajadorForm(List<Trabajador> trabajadores, List<Libro> listLibros) {
        addClassNames("tarjeta-trabajador-form");
        this.trabajadores = trabajadores;
        binder.bindInstanceFields(this);
        /* Config form */
        /* Libros */
        libro.setItems(listLibros);
        libro.setItemLabelGenerator(Libro::getTitulo);
        /* fecha de prestamo */
        fechaPrestamo.setMin(LocalDate.now(ZoneId.systemDefault()));
        /* fecha de devolucion */
        fechaDevolucion.setMin(LocalDate.now(ZoneId.systemDefault()));

        add(libro, fechaPrestamo, createButtonsLayout());
    }

    private HorizontalLayout createButtonsLayout() {

        HorizontalLayout buttonlayout = new HorizontalLayout();
        buttonlayout.addClassName("button-layout");
        save.addClickListener(event -> validateAndSave());
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickShortcut(Key.ENTER);

        close.addClickListener(event -> fireEvent(new TrabajadorForm.CloseEvent(this)));
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        close.addClickShortcut(Key.ESCAPE);

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
        buttonlayout.add(save, close);

        return buttonlayout;
    }

    public void setTarjetaPrestamo(List<TarjetaPrestamoTrabajador> tarjetasPrestamo) {
        this.tarjetasPrestamo = tarjetasPrestamo;
        for (int i = 0; i < tarjetasPrestamo.size(); i++) {
            binder.readBean(tarjetasPrestamo.get(i));
        }
    }

    private void validateAndSave() {
        try {
            for (int i = 0; i < trabajadores.size(); i++) {
                binder.writeBean(tarjetasPrestamo.get(i));
                this.tarjetasPrestamo.get(i).setTrabajador(trabajadores.get(i));
                this.tarjetasPrestamo.get(i).setLibro(libro.getValue());
                this.tarjetasPrestamo.get(i).setFechaPrestamo(fechaPrestamo.getValue());
                this.tarjetasPrestamo.get(i).setFechaDevolucion(fechaDevolucion.getValue());
            }
            fireEvent(new TrabajadorForm.SaveEvent(this, tarjetasPrestamo));
        } catch (ValidationException e) {
            e.printStackTrace();
            Notification notification = Notification.show(
                    "Ocurrió un problema al intentar almacenar La tarjeta",
                    2000,
                    Notification.Position.MIDDLE);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    // Events
    public static abstract class TarjetaPrestamoTrabajadorFormEvent
            extends ComponentEvent<TrabajadorForm> {

        private List<TarjetaPrestamoTrabajador> tarjetaPrestamo;

        protected TarjetaPrestamoTrabajadorFormEvent(TrabajadorForm source,
                List<TarjetaPrestamoTrabajador> tarjetaPrestamo) {
            super(source, false);
            this.tarjetaPrestamo = tarjetaPrestamo;
        }

        public List<TarjetaPrestamoTrabajador> getTarjetaPrestamo() {
            return tarjetaPrestamo;
        }
    }

    public static class SaveEvent extends TarjetaPrestamoTrabajadorFormEvent {

        SaveEvent(TrabajadorForm source, List<TarjetaPrestamoTrabajador> tarjetaPrestamo) {
            super(source, tarjetaPrestamo);
        }
    }

    public static class DeleteEvent extends TarjetaPrestamoTrabajadorFormEvent {

        DeleteEvent(TrabajadorForm source, List<TarjetaPrestamoTrabajador> tarjetaPrestamo) {
            super(source, tarjetaPrestamo);
        }

    }

    public static class CloseEvent extends TarjetaPrestamoTrabajadorFormEvent {

        CloseEvent(TrabajadorForm source) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
            ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

}
