package trabajodediploma.views.tarjetaDestinoFinal.Trabajador;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.shared.Registration;
import trabajodediploma.data.entity.DestinoFinal;
import trabajodediploma.data.entity.DestinoFinalTrabajador;
import trabajodediploma.data.entity.Trabajador;
import trabajodediploma.data.entity.Modulo;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author leinier
 */
public class TarjetaDestinoFinal_TrabajadorFrom extends FormLayout {

    DestinoFinalTrabajador tarjeta;
    ComboBox<Trabajador> trabajador = new ComboBox<>("Trabajador");
    ComboBox<Modulo> modulo = new ComboBox<>("Modulo");
    DatePicker fecha = new DatePicker("Fecha Entrega");

    Button save = new Button("Añadir", VaadinIcon.PLUS.create());
    Button close = new Button("Cancelar", VaadinIcon.ERASER.create());

    BeanValidationBinder<DestinoFinal> binder = new BeanValidationBinder<>(DestinoFinal.class);

    public TarjetaDestinoFinal_TrabajadorFrom(List<Trabajador> trabajadors, List<Modulo> modulos) {

        addClassNames("tarjeta-trabajador-form");
        binder.bindInstanceFields(this);

        /*Config form*/
 /*Trabajador*/
        trabajador.setItems(trabajadors);
        trabajador.setItemLabelGenerator(est -> est.getUser().getName());
        trabajador.setRenderer(new ComponentRenderer<>(event -> {
            HorizontalLayout hl = new HorizontalLayout();
            hl.setAlignItems(FlexComponent.Alignment.CENTER);
            Avatar avatar = new Avatar(event.getUser().getName(), event.getUser().getProfilePictureUrl());
            VerticalLayout vl = new VerticalLayout();
            vl.getStyle().set("line-height", "0");
            Span name = new Span();
            name.addClassNames("name");
            name.setText(event.getUser().getName());
            Span email = new Span();
            email.addClassNames("text-s", "text-secondary");
            email.setText(event.getEmail());
            vl.add(name, email);
            hl.add(avatar, vl);
            return hl;
        })
        );
        /*Libros*/
        modulo.setItems(modulos);
        modulo.setItemLabelGenerator(Modulo::getNombre);
        /*fecha de entrega*/
        fecha.setMin(LocalDate.now(ZoneId.systemDefault()));

        add(trabajador, modulo, fecha, createButtonsLayout());

    }

    private HorizontalLayout createButtonsLayout() {

        HorizontalLayout buttonlayout = new HorizontalLayout();
        buttonlayout.addClassName("button-layout");
        save.addClickListener(event -> validateAndSave());
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickShortcut(Key.ENTER);

        close.addClickListener(event -> fireEvent(new TarjetaDestinoFinal_TrabajadorFrom.CloseEvent(this)));
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        close.addClickShortcut(Key.ESCAPE);

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
        buttonlayout.add(save, close);

        return buttonlayout;
    }

    public void setDestinoFinal(DestinoFinalTrabajador tarjeta) {
        this.tarjeta = tarjeta;
        binder.readBean(tarjeta);
        if (tarjeta.getTrabajadores() != null) {
            List<Trabajador> list = new LinkedList<>(tarjeta.getTrabajadores());
            if (list.size() >= 1) {
                trabajador.setValue(list.get(0));
                modulo.setValue(tarjeta.getModulo());
                fecha.setValue(tarjeta.getFecha());
            }
        }
    }

    private void validateAndSave() {
        try {
            binder.writeBean(tarjeta);
            Set<Trabajador> est = new HashSet<Trabajador>();
            est.add(trabajador.getValue());
            this.tarjeta.setTrabajadores(est);
            this.tarjeta.setModulo(modulo.getValue());
            this.tarjeta.setFecha(fecha.getValue());
            fireEvent(new TarjetaDestinoFinal_TrabajadorFrom.SaveEvent(this, tarjeta));
        } catch (ValidationException e) {
            e.printStackTrace();
            Notification notification = Notification.show(
                    "Ocurrió un problema al intentar almacenar La tarjeta",
                    2000,
                    Notification.Position.MIDDLE
            );
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);

        }
    }

    // Events
    public static abstract class DestinoFinalTrabajadorFormEvent extends ComponentEvent<TarjetaDestinoFinal_TrabajadorFrom> {

        private DestinoFinalTrabajador destinoFinal;

        protected DestinoFinalTrabajadorFormEvent(TarjetaDestinoFinal_TrabajadorFrom source, DestinoFinalTrabajador destinoFinal) {
            super(source, false);
            this.destinoFinal = destinoFinal;
        }

        public DestinoFinalTrabajador getDestinoFinal() {
            return destinoFinal;
        }
    }

    public static class SaveEvent extends DestinoFinalTrabajadorFormEvent {

        SaveEvent(TarjetaDestinoFinal_TrabajadorFrom source, DestinoFinalTrabajador DestinoFinal) {
            super(source, DestinoFinal);
        }
    }

    public static class DeleteEvent extends DestinoFinalTrabajadorFormEvent {

        DeleteEvent(TarjetaDestinoFinal_TrabajadorFrom source, DestinoFinalTrabajador DestinoFinal) {
            super(source, DestinoFinal);
        }

    }

    public static class CloseEvent extends DestinoFinalTrabajadorFormEvent {

        CloseEvent(TarjetaDestinoFinal_TrabajadorFrom source) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
            ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

}
