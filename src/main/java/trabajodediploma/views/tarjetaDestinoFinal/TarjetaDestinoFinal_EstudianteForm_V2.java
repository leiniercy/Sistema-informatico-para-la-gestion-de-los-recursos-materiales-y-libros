package trabajodediploma.views.tarjetaDestinoFinal;

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
import trabajodediploma.data.entity.DestinoFinalEstudiante;
import trabajodediploma.data.entity.Estudiante;
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
public class TarjetaDestinoFinal_EstudianteForm_V2 extends FormLayout {

    DestinoFinalEstudiante tarjeta;
    ComboBox<Grupo> grupo = new ComboBox<>("Grupo");
    MultiselectComboBox<Estudiante> multiSelectEstudiante = new MultiselectComboBox<>("Estudiante");
    ComboBox<Modulo> modulo = new ComboBox<>("Modulo");
    DatePicker fecha = new DatePicker("Fecha Entrega");

    Button save = new Button("Añadir", VaadinIcon.PLUS.create());
    Button close = new Button("Cancelar", VaadinIcon.ERASER.create());

    BeanValidationBinder<DestinoFinal> binder = new BeanValidationBinder<>(DestinoFinal.class);

    public TarjetaDestinoFinal_EstudianteForm_V2(List<Grupo> grupos, List<Estudiante> estudiantes, List<Modulo> modulos) {

        addClassNames("tarjeta-estudiante-form");
        binder.bindInstanceFields(this);

        /*Config form*/
        grupo.setItems(grupos);
        grupo.setItemLabelGenerator(Grupo::getNumero);

        /*Estudiante*/
        multiSelectEstudiante.setItems(estudiantes);
        multiSelectEstudiante.setItemLabelGenerator(est -> est.getUser().getName());
        multiSelectEstudiante.setRenderer(new ComponentRenderer<>(event -> {
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
        multiSelectEstudiante.setReadOnly(true);

        /*Libros*/
        modulo.setItems(modulos);
        modulo.setItemLabelGenerator(Modulo::getNombre);
        modulo.setReadOnly(true);

        /*fecha de entrega*/
        fecha.setMin(LocalDate.now(ZoneId.systemDefault()));
        fecha.setReadOnly(true);

        grupo.addValueChangeListener(event -> {
            if (grupo.isEmpty()) {
                multiSelectEstudiante.setReadOnly(true);
                modulo.setReadOnly(true);
                fecha.setReadOnly(true);
            } else {
                List<Estudiante> estudiantesPorGrupo = estudiantes.stream().filter(e -> e.getGrupo().getId() == event.getValue().getId()).collect(Collectors.toList());
                multiSelectEstudiante.setReadOnly(false);
                multiSelectEstudiante.setValue(null);
                multiSelectEstudiante.setItems(estudiantesPorGrupo);
                modulo.setReadOnly(false);
                fecha.setReadOnly(false);
            }

        });

        add(grupo, multiSelectEstudiante, modulo, fecha, createButtonsLayout());

    }

    private HorizontalLayout createButtonsLayout() {

        HorizontalLayout buttonlayout = new HorizontalLayout();
        buttonlayout.addClassName("button-layout");
        save.addClickListener(event -> validateAndSave());
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickShortcut(Key.ENTER);

        close.addClickListener(event -> fireEvent(new TarjetaDestinoFinal_EstudianteForm_V2.CloseEvent(this)));
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        close.addClickShortcut(Key.ESCAPE);

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
        buttonlayout.add(save, close);

        return buttonlayout;
    }

    public void setDestinoFinal(DestinoFinalEstudiante tarjeta) {
        this.tarjeta = tarjeta;
        binder.readBean(tarjeta);
        if (tarjeta.getEstudiantes() != null) {
            List<Estudiante> list = new LinkedList<>(tarjeta.getEstudiantes());
            if (tarjeta.getEstudiantes().size() >= 1) {
                grupo.setValue(list.get(0).getGrupo());
                multiSelectEstudiante.setValue(tarjeta.getEstudiantes());
                modulo.setValue(tarjeta.getModulo());
                fecha.setValue(tarjeta.getFecha());
            }
        }
    }

    private void validateAndSave() {
        try {
            binder.writeBean(tarjeta);
            this.tarjeta.setEstudiantes(multiSelectEstudiante.getSelectedItems());
            this.tarjeta.setModulo(modulo.getValue());
            this.tarjeta.setFecha(fecha.getValue());
            fireEvent(new TarjetaDestinoFinal_EstudianteForm_V2.SaveEvent(this, tarjeta));

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
    public static abstract class DestinoFinalEstudianteFormEvent extends ComponentEvent<TarjetaDestinoFinal_EstudianteForm_V2> {

        private DestinoFinalEstudiante destinoFinal;

        protected DestinoFinalEstudianteFormEvent(TarjetaDestinoFinal_EstudianteForm_V2 source, DestinoFinalEstudiante DestinoFinal) {
            super(source, false);
            this.destinoFinal = DestinoFinal;
        }

        public DestinoFinalEstudiante getDestinoFinal() {
            return destinoFinal;
        }
    }

    public static class SaveEvent extends DestinoFinalEstudianteFormEvent {

        SaveEvent(TarjetaDestinoFinal_EstudianteForm_V2 source, DestinoFinalEstudiante DestinoFinal) {
            super(source, DestinoFinal);
        }
    }

    public static class DeleteEvent extends DestinoFinalEstudianteFormEvent {

        DeleteEvent(TarjetaDestinoFinal_EstudianteForm_V2 source, DestinoFinalEstudiante DestinoFinal) {
            super(source, DestinoFinal);
        }

    }

    public static class CloseEvent extends DestinoFinalEstudianteFormEvent {

        CloseEvent(TarjetaDestinoFinal_EstudianteForm_V2 source) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
            ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

}
