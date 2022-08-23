/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabajodediploma.views.login.crear_informacion_perfil;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;
import java.util.List;
import java.util.Random;

import trabajodediploma.data.entity.Area;
import trabajodediploma.data.entity.Trabajador;
import trabajodediploma.data.entity.User;
import trabajodediploma.data.tools.EmailSenderService;

/**
 *
 * @author leinier
 */
public class CrearTrabajadorForm extends FormLayout {

    private Trabajador trabajador;
    private User user;
    private Div div_codigo;
    private EmailSenderService senderService;
    private StringBuffer codigo_buffer;
    private List<Area> listArea;
    EmailField email = new EmailField();
    TextField solapin = new TextField();
    ComboBox<String> categoria = new ComboBox<>();
    ComboBox<Area> area = new ComboBox<>();
    TextField codigo = new TextField();
    Button btn_codigo = new Button("Obtener");

    BeanValidationBinder<Trabajador> binder = new BeanValidationBinder<>(Trabajador.class);

    Button save = new Button("Añadir", VaadinIcon.PLUS.create());
    Button close = new Button("Cancelar", VaadinIcon.ERASER.create());

    public CrearTrabajadorForm(List<Area> listArea, User user, EmailSenderService senderService) {
        this.listArea = listArea;
        this.user = user;
        this.senderService = senderService;
        Configuration();
        add(solapin, email, categoria, area, div_codigo, createButtonsLayout());
    }

    // Configuration
    private void Configuration() {
        addClassName("crear-trabajdor-form");
        binder.bindInstanceFields(this);

        // email
        email.setLabel("Correo");
        email.setPlaceholder("usuario@uci.cu");
        email.setValue("usuario@uci.cu");
        email.setClearButtonVisible(true);
        email.setPattern("^[a-zA-Z][a-zA-Z0-9_\\.][a-zA-Z0-9]+(@uci\\.cu)$");
        email.setErrorMessage("Por favor escriba un correo válido");
        // solapin
        solapin.setLabel("Solapín");
        solapin.setPlaceholder("E1705587");
        solapin.getElement().setAttribute("solapin", "E1705587");
        solapin.setAutofocus(true);
        solapin.setMinLength(7);
        solapin.setMaxLength(7);
        solapin.setErrorMessage("Una letra , mínimo 7 caracteres y máximo 7");
        solapin.addValueChangeListener(event -> {
            event.getSource().setHelperText(event.getValue().length() + "/" + 7);
        });
        // categoria
        categoria.setPlaceholder("Categoría");
        categoria.setItems("Tabajador", "otras");
        // area
        area.setPlaceholder("Área");
        area.setItems(listArea);
        area.setItemLabelGenerator(Area::getNombre);

        /* Codigo */

        // Los caracteres de interés en un array de char.
        char[] chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        // Longitud del array de char.
        int charsLength = chars.length;
        // Instanciamos la clase Random
        Random random = new Random();
        // Un StringBuffer para componer la cadena aleatoria de forma eficiente
        codigo_buffer = new StringBuffer();
        // Bucle para elegir una cadena de 6 caracteres al azar
        for (int i = 0; i < 6; i++) {
            // Añadimos al buffer un caracter al azar del array
            codigo_buffer.append(chars[random.nextInt(charsLength)]);
        }

        div_codigo = new Div();
        div_codigo.addClassName("div_codigo");

        codigo.addClassName("div_codigo__input");
        codigo.setPlaceholder("Código");
        codigo.setMinLength(6);
        codigo.setMaxLength(6);
        codigo.setErrorMessage("mínimo 6 caracteres");
        codigo.setEnabled(false);

        btn_codigo.addClassName("div_codigo__btn");
        btn_codigo.addClickListener(click -> {
            codigo.setEnabled(true);
            senderService.sendSimpleEmail(
                    /* enviado a: */ email.getValue(),
                    /* asunto: */ "Código de identificación",
                    /* mensaje: */ "Bienvenido a Genius \n"
                            + "Su código de identificación es: "
                            + codigo_buffer.toString());
            Notification notification = Notification.show(
                    "El código de identificación ha enviado a su correo electrónico",
                    5000,
                    Notification.Position.BOTTOM_START);
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        });
        div_codigo.add(btn_codigo, codigo);
        /* Fin->Codigo */

    }

    // Buttons
    private HorizontalLayout createButtonsLayout() {
        HorizontalLayout buttonlayout = new HorizontalLayout();
        buttonlayout.addClassName("button-layout");
        save.addClickListener(event -> validateAndSave());
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickShortcut(Key.ENTER);
        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        close.addClickShortcut(Key.ESCAPE);
        buttonlayout.add(save, close);
        return buttonlayout;
    }

    // Set Trabajador
    public void setTrabajador(Trabajador trabajador) {
        this.trabajador = trabajador;
        binder.readBean(trabajador);
        if (user == null) {
            this.trabajador.setUser(null);
        } else {
            this.trabajador.setUser(user);
            ;
        }
    }

    // Validate and Save
    private void validateAndSave() {
        try {
            if (codigo.getValue() != codigo_buffer.toString()) {
            binder.writeBean(trabajador);
            this.trabajador.setUser(user);
            this.trabajador.setSolapin(solapin.getValue());
            this.trabajador.setEmail(email.getValue());
            this.trabajador.setCategoria(categoria.getValue());
            this.trabajador.setArea(area.getValue());
            fireEvent(new CrearTrabajadorForm.SaveEvent(this, trabajador));
        } else {
            Notification notification = Notification.show(
                    "Código de identificación incorrecto",
                    5000,
                    Notification.Position.MIDDLE);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
        } catch (ValidationException e) {
            e.printStackTrace();
            Notification notification = Notification.show(
                    "Ocurrió un problema al intentar guardar el trabajador",
                    5000,
                    Notification.Position.MIDDLE);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    // Events
    public static abstract class TrabajadorFormEvent extends ComponentEvent<CrearTrabajadorForm> {

        private Trabajador trabajador;

        protected TrabajadorFormEvent(CrearTrabajadorForm source, Trabajador trabajador) {
            super(source, false);
            this.trabajador = trabajador;
        }

        public Trabajador getTrabajador() {
            return trabajador;
        }
    }

    // Save Event
    public static class SaveEvent extends TrabajadorFormEvent {

        SaveEvent(CrearTrabajadorForm source, Trabajador trabajador) {
            super(source, trabajador);
        }
    }

    // Delete Event
    public static class DeleteEvent extends TrabajadorFormEvent {

        DeleteEvent(CrearTrabajadorForm source, Trabajador trabajador) {
            super(source, trabajador);
        }

    }

    // Close Event
    public static class CloseEvent extends TrabajadorFormEvent {

        CloseEvent(CrearTrabajadorForm source) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
            ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}
