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
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;
import java.util.List;
import java.util.Random;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.web.util.UriUtils;
import elemental.json.Json;
import trabajodediploma.data.entity.Estudiante;
import trabajodediploma.data.entity.Grupo;
import trabajodediploma.data.entity.User;
import trabajodediploma.data.tools.EmailSenderService;
import trabajodediploma.data.tools.MyUploadI18n;


/**
 *
 * @author leinier
 */
public class ModificarPerfilEstudianteForm extends FormLayout {

    private User user;
    private Estudiante estudiante;
    private List<Grupo> listGrupos;
    private EmailSenderService senderService;
    private StringBuffer codigo_buffer;
    private Div div_codigo;
    private  Label imageSize;
    private Image imagePreview;
    Upload profilePictureUrl = new Upload();
    TextField name = new TextField();
    EmailField email = new EmailField();
    TextField solapin = new TextField();
    IntegerField anno_academico = new IntegerField();
    ComboBox<String> facultad = new ComboBox<>();
    ComboBox<Grupo> grupo = new ComboBox<>();
    TextField codigo = new TextField();
    Button btn_codigo = new Button("Obtener");

    Button save = new Button("Añadir", VaadinIcon.PLUS.create());
    Button close = new Button("Cancelar", VaadinIcon.ERASER.create());

    BeanValidationBinder<Estudiante> binderEstudiante = new BeanValidationBinder<>(Estudiante.class);
    BeanValidationBinder<User> binderUser = new BeanValidationBinder<>(User.class);

    public ModificarPerfilEstudianteForm(List<Grupo> listGrupos, User user,Estudiante estudiante,EmailSenderService senderService) {
        this.user = user;
        this.estudiante = estudiante;
        this.listGrupos = listGrupos;
        this.senderService = senderService;
        Configuration();
        add( /*imageSize, profilePictureUrl,*/ name,email,solapin,anno_academico, facultad, grupo, div_codigo,createButtonsLayout());
    }

    //Configuration
    private void Configuration() {
        binderEstudiante.bindInstanceFields(this);
        binderUser.bindInstanceFields(this);
        // foto de perfil
        // int maxFileSizeInBytes = 10 * 1024 * 1024; // 10MB
        imageSize = new Label("Tamaño maximo: 1mb");
        imageSize.getStyle().set("color", "var(--lumo-secondary-text-color)");
        imagePreview = new Image();
        imagePreview.setWidth("100%");
        profilePictureUrl.getStyle().set("box-sizing", "border-box");
        profilePictureUrl.getElement().appendChild(imagePreview.getElement());
        Button uploadButton = new Button("Seleccionar imagen...");
        uploadButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        profilePictureUrl.setUploadButton(uploadButton);
        attachImageUpload(profilePictureUrl, imagePreview);
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
            try {
                senderService.sendSimpleEmail(
                        /* enviado a: */ email.getValue(),
                        /* asunto: */ "Código de identificación",
                        /* mensaje: */ "Bienvenido a Genius \n"
                                + "Su código de identificación es: "
                                + codigo_buffer.toString());
                Notification notification = Notification.show(
                        "El código de identificación ha enviado a su correo electrónico",
                        2000,
                        Notification.Position.BOTTOM_START);
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                codigo.setEnabled(true);
            } catch (Exception e) {
                // TODO: handle exception
                Notification notification = Notification.show(
                        "Error al enviar correo electrónico a la dirección de correo seleccionada",
                        2000,
                        Notification.Position.MIDDLE);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        div_codigo.add(btn_codigo, codigo);
        /* Fin->Codigo */
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
        this.imagePreview.setVisible(user != null);
        binderEstudiante.readBean(estudiante);
        binderUser.readBean(user);
    }

    //Validate and Save
    private void validateAndSave() {
        try {
            if (codigo.isEnabled() && codigo.getValue() == codigo_buffer.toString()) {
            binderEstudiante.writeBean(estudiante);
            binderUser.writeBean(user);
            //user
            this.user.setName(name.getValue());
            // this.user.setProfilePictureUrl(imagePreview.getSrc());
            //estudiante
            this.estudiante.setAnno_academico(anno_academico.getValue());
            this.estudiante.setEmail(email.getValue());
            this.estudiante.setFacultad(facultad.getValue());
            this.estudiante.setGrupo(grupo.getValue());
            this.estudiante.setSolapin(solapin.getValue());
            this.estudiante.setUser(user);
            fireEvent(new SaveEvent(this, estudiante,user));
        }  else if( !codigo.isEnabled() || codigo.getValue() == codigo_buffer.toString() ){
            Notification notification = Notification.show(
                    "Código de identificación incorrecto",
                    2000,
                    Notification.Position.MIDDLE);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
        } catch (ValidationException e) {
            e.printStackTrace();
            Notification notification = Notification.show(
                    "Ocurrió un problema al intentar guardar el estudiante",
                    2000,
                    Notification.Position.MIDDLE
            );
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void configuracionErroresImagen() {

        MyUploadI18n i18n = new MyUploadI18n();
        i18n.getAddFiles().setOne("Cargar Imágen...");
        i18n.getDropFiles().setOne("Arrastra la imágen aquí");

        i18n.getError()
                .setFileIsTooBig("El archivo excede el tamaño máximo permitido de 1 MB.")
                .setIncorrectFileType("El archivo seleccionado no es una imágen.");
        ;
        profilePictureUrl.setI18n(i18n);

    }

    private void attachImageUpload(Upload upload, Image preview) {
        ByteArrayOutputStream uploadBuffer = new ByteArrayOutputStream();
        upload.setAcceptedFileTypes("image/tiff", ".png", ".jpg");
        upload.setReceiver((fileName, mimeType) -> {
            return uploadBuffer;
        });
        upload.setMaxFileSize(1 * 1024 * 1024);
        upload.addFileRejectedListener(event -> {
            String errorMessage = event.getErrorMessage();

            Notification notification = Notification.show(
                    errorMessage,
                    2000,
                    Notification.Position.MIDDLE);

            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        });
        configuracionErroresImagen();
        upload.addSucceededListener(e -> {
            String mimeType = e.getMIMEType();
            String base64ImageData = Base64.getEncoder().encodeToString(uploadBuffer.toByteArray());
            String dataUrl = "data:" + mimeType + ";base64,"
                    + UriUtils.encodeQuery(base64ImageData, StandardCharsets.UTF_8);
            upload.getElement().setPropertyJson("files", Json.createArray());
            preview.setSrc(dataUrl);
            uploadBuffer.reset();
        });
        preview.setVisible(false);
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
