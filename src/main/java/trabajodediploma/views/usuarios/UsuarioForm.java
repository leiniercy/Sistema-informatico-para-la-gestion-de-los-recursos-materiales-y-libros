/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabajodediploma.views.usuarios;

import trabajodediploma.data.entity.User;
import trabajodediploma.data.tools.MyUploadI18n;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;
import elemental.json.Json;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import org.springframework.web.util.UriUtils;
import trabajodediploma.data.Rol;

/**
 *
 * @author leinier
 */
public class UsuarioForm extends FormLayout {

    private User usuario;
    private Image imagePreview;
    Upload profilePictureUrl = new Upload();
    TextField name = new TextField();
    TextField username = new TextField();
    ComboBox roles = new ComboBox<>();

    Button save = new Button("Añadir", VaadinIcon.PLUS.create());
    Button close = new Button("Cancelar", VaadinIcon.ERASER.create());

    //BeanValidationBinder<User> binder = new BeanValidationBinder<>(User.class);
    public UsuarioForm() {
        addClassName("user-form");

        //binder.bindInstanceFields(this);
        //Config form
        //foto de perfil
        //int maxFileSizeInBytes = 10 * 1024 * 1024; // 10MB
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

        //name
        name.setLabel("Nombre");
        name.setAutofocus(true);
        name.setRequired(true);
        name.setMinLength(2);
        name.setMaxLength(255);
        name.setErrorMessage("Solo letras, mínimo 2 caracteres y máximo 255");
        name.addValueChangeListener(event -> {
            event.getSource().setHelperText(event.getValue().length() + "/" + 255);
        });

        //username
        username.setLabel("Nombre");
        username.setAutofocus(true);
        username.setRequired(true);
        username.setMinLength(2);
        username.setMaxLength(255);
        username.setErrorMessage("Solo letras, mínimo 2 caracteres y máximo 255");
        username.addValueChangeListener(event -> {
            event.getSource().setHelperText(event.getValue().length() + "/" + 255);
        });

        //Rol
        roles.setItems(
//                Collections.singleton(Rol.VD_ADIMN_ECONOMIA),
//                Collections.singleton(Rol.ASISTENTE_CONTROL),
//                Collections.singleton(Rol.RESP_ALMACEN),
//                Collections.singleton(Rol.USER)
                Rol.VD_ADIMN_ECONOMIA,Rol.ASISTENTE_CONTROL,Rol.RESP_ALMACEN,Rol.USER
        //               "vicedecano","asistente","responsable_almacen","usuario"
        );

        attachImageUpload(profilePictureUrl, imagePreview);

        add(roles, createButtonsLayout());
    }

    private HorizontalLayout createButtonsLayout() {

        HorizontalLayout buttonlayout = new HorizontalLayout();
        buttonlayout.addClassName("button-layout");
        save.addClickListener(event -> validateAndSave());
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickShortcut(Key.ENTER);

        close.addClickListener(event -> fireEvent(new CloseEvent(this)));
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        close.addClickShortcut(Key.ESCAPE);

        // binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
        buttonlayout.add(save, close);

        return buttonlayout;
    }

    public void setUser(User usuario) {
        this.usuario = usuario;
        // binder.readBean(usuario);
        if (usuario == null) {
            this.imagePreview.setSrc("");
        } else {
            this.imagePreview.setSrc(usuario.getProfilePictureUrl());
        }
    }

    private void validateAndSave() {
        // try {
//            binder.writeBean(usuario);
        this.usuario.setProfilePictureUrl(imagePreview.getSrc());
        fireEvent(new SaveEvent(this, usuario));
        //    } catch (ValidationException e) {
        //  e.printStackTrace();
        Notification notification = Notification.show(
                "Ocurrió un problema al intentar actualizar el usuario",
                5000,
                Notification.Position.MIDDLE
        );
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        // }
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

    // Events
    public static abstract class UsuarioFormEvent extends ComponentEvent<UsuarioForm> {

        private User usuario;

        protected UsuarioFormEvent(UsuarioForm source, User usuario) {
            super(source, false);
            this.usuario = usuario;
        }

        public User getUsuario() {
            return usuario;
        }
    }

    public static class SaveEvent extends UsuarioFormEvent {

        SaveEvent(UsuarioForm source, User usuario) {
            super(source, usuario);
        }
    }

    public static class DeleteEvent extends UsuarioFormEvent {

        DeleteEvent(UsuarioForm source, User usuario) {
            super(source, usuario);
        }

    }

    public static class CloseEvent extends UsuarioFormEvent {

        CloseEvent(UsuarioForm source) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
            ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

}
