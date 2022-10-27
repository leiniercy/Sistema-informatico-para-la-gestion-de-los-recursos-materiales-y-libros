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
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.vaadin.gatanaso.MultiselectComboBox;
import trabajodediploma.data.entity.Grupo;

/**
 *
 * @author leinier
 */
public class TarjetaDestinoFinal_TrabajadorForm_Area extends FormLayout {

    private List<DestinoFinalTrabajador> tarjetas;
    private List<Trabajador> trabajadores;
    ComboBox<Modulo> modulo = new ComboBox<>("Módulo");
    DatePicker fecha = new DatePicker("Fecha de Entrega");

    Button save = new Button("Añadir", VaadinIcon.PLUS.create());
    Button close = new Button("Cancelar", VaadinIcon.ERASER.create());

    BeanValidationBinder<DestinoFinal> binder = new BeanValidationBinder<>(DestinoFinal.class);

    public TarjetaDestinoFinal_TrabajadorForm_Area(List<Trabajador> trabajadores, List<Modulo> modulos) {
        addClassNames("tarjeta-trabajador-form");
        this.trabajadores = trabajadores;
        binder.bindInstanceFields(this);

        modulo.setItems(modulos);
        modulo.setItemLabelGenerator(Modulo::getNombre);

        fecha.setMin(LocalDate.now(ZoneId.systemDefault()));

        add(modulo, fecha, createButtonsLayout());

    }

    private HorizontalLayout createButtonsLayout() {

        HorizontalLayout buttonlayout = new HorizontalLayout();
        buttonlayout.addClassName("button-layout");
        save.addClickListener(event -> validateAndSave());
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickShortcut(Key.ENTER);

        close.addClickListener(event -> fireEvent(new TarjetaDestinoFinal_TrabajadorForm_Area.CloseEvent(this)));
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        close.addClickShortcut(Key.ESCAPE);

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
        buttonlayout.add(save, close);

        return buttonlayout;
    }

    public void setDestinoFinal(List<DestinoFinalTrabajador> tarjetas) {
        this.tarjetas = tarjetas;
        for (int i = 0; i < this.tarjetas.size(); i++) {
            binder.readBean(tarjetas.get(i));
        }
    }

    private void validateAndSave() {
        try {
            for (int i = 0; i < trabajadores.size(); i++) {
                binder.writeBean(tarjetas.get(i));
                this.tarjetas.get(i).setTrabajador(trabajadores.get(i));
                this.tarjetas.get(i).setModulo(modulo.getValue());
                this.tarjetas.get(i).setFecha(fecha.getValue());
            }
            fireEvent(new TarjetaDestinoFinal_TrabajadorForm_Area.SaveEvent(this, tarjetas));

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
    public static abstract class DestinoFinalTrabajadorFormEvent extends ComponentEvent<TarjetaDestinoFinal_TrabajadorForm_Area> {

        private List<DestinoFinalTrabajador> tarjetas;

        protected DestinoFinalTrabajadorFormEvent(TarjetaDestinoFinal_TrabajadorForm_Area source, List<DestinoFinalTrabajador> tarjetas) {
            super(source, false);
            this.tarjetas = tarjetas;
        }

        public List<DestinoFinalTrabajador> getDestinoFinal() {
            return tarjetas;
        }
    }

    public static class SaveEvent extends DestinoFinalTrabajadorFormEvent {

        SaveEvent(TarjetaDestinoFinal_TrabajadorForm_Area source, List<DestinoFinalTrabajador> tarjetas) {
            super(source, tarjetas);
        }
    }

    public static class DeleteEvent extends DestinoFinalTrabajadorFormEvent {

        DeleteEvent(TarjetaDestinoFinal_TrabajadorForm_Area source, List<DestinoFinalTrabajador> tarjetas) {
            super(source, tarjetas);
        }

    }

    public static class CloseEvent extends DestinoFinalTrabajadorFormEvent {

        CloseEvent(TarjetaDestinoFinal_TrabajadorForm_Area source) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
            ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

}