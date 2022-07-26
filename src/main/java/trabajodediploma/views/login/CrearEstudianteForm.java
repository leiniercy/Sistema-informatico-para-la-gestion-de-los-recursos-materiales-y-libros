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
import com.vaadin.flow.component.html.Label;
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
import trabajodediploma.data.entity.Estudiante;
import trabajodediploma.data.entity.Grupo;
import trabajodediploma.data.entity.User;

/**
 *
 * @author leinier
 */
public class CrearEstudianteForm extends FormLayout {

    User user;
    Estudiante estudiante;
    EmailField email = new EmailField();
    TextField solapin = new TextField();
    IntegerField anno_academico = new IntegerField();
    ComboBox<String> facultad = new ComboBox<>();
    ComboBox<Grupo> grupo = new ComboBox<>();
    List<Grupo> listGrupos;
    
    Button save = new Button("Añadir", VaadinIcon.PLUS.create());

    BeanValidationBinder<Estudiante> binder = new BeanValidationBinder<>(Estudiante.class);

    public CrearEstudianteForm(List<Grupo> listGrupos, User usuario) {
        this.user = usuario;
        this.listGrupos = listGrupos;
        binder.bindInstanceFields(this);
        Configuration();
        add(solapin, email, anno_academico, facultad, grupo,createButtonsLayout());
    }

    //Configuration
    private void Configuration() {

        //email
        email.setLabel("Correo");
        email.setPlaceholder("usuario@estudiantes.uci.cu");
        email.setValue("usuario@estudiantes.uci.cu");
        email.setClearButtonVisible(true);
        email.setPattern("^[a-zA-Z][a-zA-Z0-9_\\.][a-zA-Z0-9]+(@estudiantes\\.uci\\.cu)$");
        email.setErrorMessage("Por favor escriba un correo válido");
        
        //solapin
        solapin.setLabel("Solapín");
        solapin.setPlaceholder("E1705587");
        solapin.getElement().setAttribute("solapin", "E1705587");
        solapin.setAutofocus(true);
        solapin.setRequired(true);
        solapin.setMinLength(7);
        solapin.setMaxLength(7);
        solapin.setPattern("^[A-Z][0-9]+$");
        solapin.setErrorMessage("Una letra , mínimo 7 caracteres y máximo 7");
        solapin.addValueChangeListener(event -> {
            event.getSource().setHelperText(event.getValue().length() + "/" + 7);
        });
        //anno academico
        anno_academico.setLabel("Año académico");
        anno_academico.setHelperText("Máximo 5");
        anno_academico.setValue(1);
        anno_academico.setHasControls(true);
        anno_academico.setMin(1);
        anno_academico.setMax(5);
        //facultad
        facultad.setPlaceholder("Facultad");
        facultad.setItems("Facultad 1", "Facultad 2", "Facultad 3", "Facultad 4", "CITEC", "FTE");
        //Grupo 
        grupo.setPlaceholder("Grupo");
        grupo.setItems(listGrupos);
        grupo.setItemLabelGenerator(Grupo::getNumero);
    }

    //Buttons 
    private HorizontalLayout createButtonsLayout() {
        HorizontalLayout buttonlayout = new HorizontalLayout();
        buttonlayout.addClassName("button-layout");
        save.addClickListener(event -> validateAndSave());
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickShortcut(Key.ENTER);
        // binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
        buttonlayout.add(save);

        return buttonlayout;
    }

    //Set Estudiante
    public void setEstudiante(Estudiante estudiante) {
        this.estudiante = estudiante;
        binder.readBean(estudiante);
         if (user == null) {
            this.estudiante.setUser(null);
        } else {
            this.estudiante.setUser(user);;
        }
    }

    //Validate and Save
    private void validateAndSave() {
        try {
            binder.writeBean(estudiante);
            this.estudiante.setAnno_academico(anno_academico.getValue());
            this.estudiante.setEmail(email.getValue());
            this.estudiante.setFacultad(facultad.getValue());
            this.estudiante.setGrupo(grupo.getValue());
            this.estudiante.setSolapin(solapin.getValue());
            this.estudiante.setUser(user);

            fireEvent(new SaveEvent(this, estudiante));
        } catch (ValidationException e) {
            e.printStackTrace();
            Notification notification = Notification.show(
                    "Ocurrió un problema al intentar guardar el estudiante",
                    5000,
                    Notification.Position.MIDDLE
            );
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    // Events
    public static abstract class EstudianteFormEvent extends ComponentEvent<CrearEstudianteForm> {

        private Estudiante estudiante;

        protected EstudianteFormEvent(CrearEstudianteForm source, Estudiante estudiante) {
            super(source, false);
            this.estudiante = estudiante;
        }

        public Estudiante getEstudiante() {
            return estudiante;
        }
    }

    //Save Event
    public static class SaveEvent extends EstudianteFormEvent {

        SaveEvent(CrearEstudianteForm source, Estudiante estudiante) {
            super(source, estudiante);
        }
    }

    //Delete Event
    public static class DeleteEvent extends EstudianteFormEvent {

        DeleteEvent(CrearEstudianteForm source, Estudiante estudiante) {
            super(source, estudiante);
        }

    }

    //Close Event
    public static class CloseEvent extends EstudianteFormEvent {

        CloseEvent(CrearEstudianteForm source) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
            ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

}
