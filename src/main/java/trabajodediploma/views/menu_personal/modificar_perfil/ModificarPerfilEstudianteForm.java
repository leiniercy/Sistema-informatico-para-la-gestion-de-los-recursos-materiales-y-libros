/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabajodediploma.views.menu_personal.modificar_perfil;

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
public class ModificarPerfilEstudianteForm extends FormLayout {

    private User user;
    private Estudiante estudiante;
    private List<Grupo> listGrupos;
    TextField name = new TextField();
    EmailField email = new EmailField();
    TextField solapin = new TextField();
    IntegerField anno_academico = new IntegerField();
    ComboBox<String> facultad = new ComboBox<>();
    ComboBox<Grupo> grupo = new ComboBox<>();

    Button save = new Button("Añadir", VaadinIcon.PLUS.create());
    Button close = new Button("Cancelar", VaadinIcon.ERASER.create());

    BeanValidationBinder<Estudiante> binderEstudiante = new BeanValidationBinder<>(Estudiante.class);
    BeanValidationBinder<User> binderUser = new BeanValidationBinder<>(User.class);

    public ModificarPerfilEstudianteForm(List<Grupo> listGrupos, User user,Estudiante estudiante) {
        this.user = user;
        this.estudiante = estudiante;
        this.listGrupos = listGrupos;
        Configuration();
        add(name,email,solapin,anno_academico, facultad, grupo,createButtonsLayout());
    }

    //Configuration
    private void Configuration() {
        binderEstudiante.bindInstanceFields(this);
        binderUser.bindInstanceFields(this);
        //nombre
        name.setLabel("Nombre");
        name.setPlaceholder("Nombre y  apellidos...");
        name.setClearButtonVisible(true);
        name.setValue(estudiante.getUser().getName());
        //email
        email.setLabel("Correo");
        email.setPlaceholder("usuario@estudiantes.uci.cu");
        email.setValue(estudiante.getEmail());
        email.setClearButtonVisible(true);
        email.setPattern("^[a-zA-Z][a-zA-Z0-9_\\.][a-zA-Z0-9]+(@estudiantes\\.uci\\.cu)$");
        email.setErrorMessage("Por favor escriba un correo válido");

        //solapin
        solapin.setLabel("Solapín");
        solapin.setPlaceholder("E1705587");
        solapin.setValue(estudiante.getSolapin());
        solapin.setClearButtonVisible(true);
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
        anno_academico.setValue(estudiante.getAnno_academico());
        anno_academico.setHasControls(true);
        anno_academico.setMin(1);
        anno_academico.setMax(5);
        //facultad
        facultad.setPlaceholder("Facultad");
        facultad.setItems("Facultad 1", "Facultad 2", "Facultad 3", "Facultad 4", "CITEC", "FTE");
        facultad.setValue(estudiante.getFacultad());
        //Grupo 
        grupo.setPlaceholder("Grupo");
        grupo.setItems(listGrupos);
        grupo.setItemLabelGenerator(Grupo::getNumero);
        grupo.setValue(estudiante.getGrupo());
    }

    //Buttons 
    private HorizontalLayout createButtonsLayout() {
        HorizontalLayout buttonlayout = new HorizontalLayout();
        buttonlayout.addClassName("button-layout");
        save.addClickListener(event -> validateAndSave());
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickShortcut(Key.ENTER);
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        close.addClickShortcut(Key.ESCAPE);
        binderEstudiante.addStatusChangeListener(e -> save.setEnabled(binderEstudiante.isValid()));
        buttonlayout.add(save, close);

        return buttonlayout;
    }

    //Set Estudiante
    public void setEstudiante(Estudiante estudiante,User user) {
        this.estudiante = estudiante;
        this.user = user;
        binderEstudiante.readBean(estudiante);
        binderUser.readBean(user);
        if (user == null) {
            this.estudiante.setUser(null);
        } else {
            this.estudiante.setUser(user);;
        }
    }

    //Validate and Save
    private void validateAndSave() {
        try {
            binderEstudiante.writeBean(estudiante);
            binderUser.writeBean(user);
            //user
            this.user.setName(name.getValue());
            //estudiante
            this.estudiante.setAnno_academico(anno_academico.getValue());
            this.estudiante.setEmail(email.getValue());
            this.estudiante.setFacultad(facultad.getValue());
            this.estudiante.setGrupo(grupo.getValue());
            this.estudiante.setSolapin(solapin.getValue());
            this.estudiante.setUser(user);
            fireEvent(new SaveEvent(this, estudiante,user));
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
    public static abstract class EstudianteFormEvent extends ComponentEvent<ModificarPerfilEstudianteForm> {

        private Estudiante estudiante;
        private User user;

        protected EstudianteFormEvent(ModificarPerfilEstudianteForm source, Estudiante estudiante,User user) {
            super(source, false);
            this.estudiante = estudiante;
            this.user = user;
        }

        public Estudiante getEstudiante() {
            return estudiante;
        }

        public User getUser() {
            return user;
        }
        
    }

    //Save Event
    public static class SaveEvent extends EstudianteFormEvent {

        SaveEvent(ModificarPerfilEstudianteForm source, Estudiante estudiante,User user) {
            super(source, estudiante,user);
        }
    }

    //Delete Event
    public static class DeleteEvent extends EstudianteFormEvent {

        DeleteEvent(ModificarPerfilEstudianteForm source, Estudiante estudiante,User user) {
            super(source, estudiante,user);
        }

    }

    //Close Event
    public static class CloseEvent extends EstudianteFormEvent {

        CloseEvent(ModificarPerfilEstudianteForm source) {
            super(source, null,null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
            ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}
