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
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;
import elemental.json.Json;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.web.util.UriUtils;
import trabajodediploma.data.entity.User;
import trabajodediploma.data.tools.MyUploadI18n;
import trabajodediploma.views.libros.LibroForm;

/**
 *
 * @author leinier
 */
public class CrearUsuarioForm extends FormLayout {

    private User user;
    private Image imagePreview;
    Upload profilePictureUrl;
    TextField name;
    TextField username;
    PasswordField hashedPassword;
    PasswordField confirmPassword;

    Button save = new Button("Añadir", VaadinIcon.PLUS.create());
    Button close = new Button("Cancelar", VaadinIcon.ERASER.create());

    BeanValidationBinder<User> binder = new BeanValidationBinder<>(User.class);

    public CrearUsuarioForm() {
        Configuracion();

        add(profilePictureUrl, name, username, hashedPassword, confirmPassword, createButtonsLayout());
    }

    private void Configuracion() {
        addClassName("crear-usuario-form");
        binder.bindInstanceFields(this);

        /*imagen*/
        Label imageSize = new Label("Tamaño maximo: 400kb");
        imageSize.getStyle().set("color", "var(--lumo-secondary-text-color)");
        imagePreview = new Image();
        imagePreview.setWidth("100%");
        profilePictureUrl = new Upload();
        profilePictureUrl.getStyle().set("box-sizing", "border-box");
        profilePictureUrl.getElement().appendChild(imagePreview.getElement());
        Button uploadButton = new Button("Seleccionar imagen...");
        uploadButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        profilePictureUrl.setUploadButton(uploadButton);
        /*Fin->imagen*/

 /*nombre*/
        name = new TextField();
        name.setPlaceholder("Nombre y apellidos ...");
        name.setRequired(true);
        name.setMinLength(2);
        name.setMaxLength(255);
        name.setErrorMessage("Solo letras, mínimo 2 caracteres y máximo 255");
        name.addValueChangeListener(event -> {
            event.getSource().setHelperText(event.getValue().length() + "/" + 255);
        });
        /*Fin->nombre*/

 /*usuario*/
        username = new TextField();
        username.setPlaceholder("Usuario...");
        username.setRequired(true);
        username.setMinLength(2);
        username.setMaxLength(255);
        username.setErrorMessage("Solo letras, mínimo 2 caracteres y máximo 255");
        username.addValueChangeListener(event -> {
            event.getSource().setHelperText(event.getValue().length() + "/" + 255);
        });
        /*Fin->usuario*/

 /*contrasena*/
        hashedPassword = new PasswordField();
        hashedPassword.setPlaceholder("Contraseña...");
        hashedPassword.setMinLength(8);
        hashedPassword.setMaxLength(255);
        hashedPassword.setErrorMessage("mínimo 8 caracteres y máximo 255");
        hashedPassword.addValueChangeListener(event -> {
            event.getSource().setHelperText(event.getValue().length() + "/" + 255);
        });
        /*Fin->Contrasena*/

 /*confirmacion*/
        confirmPassword = new PasswordField();
        confirmPassword.setPlaceholder("Confirmar contraseña...");
        confirmPassword.setMinLength(8);
        confirmPassword.setMaxLength(255);
        confirmPassword.setErrorMessage("mínimo 8 caracteres y máximo 255");
        confirmPassword.addValueChangeListener(event -> {
            event.getSource().setHelperText(event.getValue().length() + "/" + 255);
        });
        /*Fin->confirmacion*/

        attachImageUpload(profilePictureUrl, imagePreview);

    }

    private HorizontalLayout createButtonsLayout() {

        HorizontalLayout buttonlayout = new HorizontalLayout();
        buttonlayout.addClassName("button-layout");

        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickShortcut(Key.ENTER);

        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        close.addClickShortcut(Key.ESCAPE);

        buttonlayout.add(save, close);

        return buttonlayout;
    }

    public void setUser(User user) {
        this.user = user;
        binder.readBean(user);
        this.imagePreview.setVisible(user != null);
        if (user == null) {
            this.imagePreview.setSrc("");
        } else {
            this.imagePreview.setSrc(user.getProfilePictureUrl());
        }
    }

    private void validateAndSave() {
        try {
            if (hashedPassword.getValue().equals(confirmPassword.getValue())) {
                binder.writeBean(user);
                this.user.setProfilePictureUrl(imagePreview.getSrc());
                fireEvent(new CrearUsuarioForm.SaveEvent(this, user));
            } else {
                Notification notification = Notification.show(
                        "Usuario o contraseña incorrectos",
                        5000,
                        Notification.Position.MIDDLE
                );
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        } catch (ValidationException e) {
            e.printStackTrace();
            Notification notification = Notification.show(
                    "Ocurrió un problema al intentar almacenar el libro",
                    5000,
                    Notification.Position.MIDDLE
            );
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void attachImageUpload(Upload upload, Image preview) {
        ByteArrayOutputStream uploadBuffer = new ByteArrayOutputStream();
        upload.setAcceptedFileTypes("image/tiff", ".png", ".jpg");
        upload.setReceiver((fileName, mimeType) -> {
            return uploadBuffer;
        });
        upload.setMaxFileSize(400 * 1024);
        upload.addFileRejectedListener(event -> {
            String errorMessage = event.getErrorMessage();

            Notification notification = Notification.show(
                    errorMessage,
                    5000,
                    Notification.Position.MIDDLE
            );

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

    private void configuracionErroresImagen() {
        MyUploadI18n i18n = new MyUploadI18n();
        i18n.getAddFiles().setOne("Cargar Imágen...");
        i18n.getDropFiles().setOne("Arrastra la imágen aquí");

        i18n.getError()
                .setFileIsTooBig("El archivo excede el tamaño máximo permitido de 400 Kb.")
                .setIncorrectFileType("El archivo seleccionado no es una imágen.");;
        profilePictureUrl.setI18n(i18n);

    }

    /* Events*/
    public static abstract class UserFormEvent extends ComponentEvent<CrearUsuarioForm> {

        private User user;

        protected UserFormEvent(CrearUsuarioForm source, User user) {
            super(source, false);
            this.user = user;
        }

        public User getUser() {
            return user;
        }
    }

    public static class SaveEvent extends UserFormEvent {

        SaveEvent(CrearUsuarioForm source, User user) {
            super(source, user);
        }
    }

    public static class DeleteEvent extends UserFormEvent {

        DeleteEvent(CrearUsuarioForm source, User user) {
            super(source, user);
        }

    }

    public static class CloseEvent extends UserFormEvent {

        CloseEvent(CrearUsuarioForm source) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
            ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

}
