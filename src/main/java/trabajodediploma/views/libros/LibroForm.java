/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabajodediploma.views.libros;

import trabajodediploma.data.entity.Libro;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;
import elemental.json.Json;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.web.util.UriUtils;

import java.io.InputStream;

/**
 *
 * @author leinier
 */
public class LibroForm extends FormLayout {

    private Libro libro;

    Upload imagen = new Upload();
    TextField titulo = new TextField();
    TextField autor = new TextField();
    IntegerField volumen = new IntegerField();
    IntegerField tomo = new IntegerField();
    IntegerField parte = new IntegerField();
    IntegerField cantidad = new IntegerField();
    NumberField precio = new NumberField();

    Button save = new Button("Añadir", VaadinIcon.PLUS.create());
    Button close = new Button("Cancelar", VaadinIcon.ERASER.create());

    BeanValidationBinder<Libro> binder = new BeanValidationBinder<>(Libro.class);

    public LibroForm() {
        addClassName("libro-form");

        binder.bindInstanceFields(this);

        //Config form
        //imagen
        Button uploadButton = new Button("Imágen");
        uploadButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        imagen.setUploadButton(uploadButton);
        imagen.setDropLabel(new Span("Arrastra la imagen aquí"));

        MemoryBuffer buffer = new MemoryBuffer();
        imagen = new Upload(buffer);
        imagen.getStyle().set("box-sizing", "border-box");
        imagen.setAcceptedFileTypes("image/tiff", ".png", ".jpg");
        imagen.addFileRejectedListener(event -> {
            String errorMessage = "El archivo seleccionado no tiene el formato correcto";

            Notification notification = Notification.show(
                    errorMessage,
                    5000,
                    Notification.Position.MIDDLE
            );

            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        });

        //titulo
        titulo.setLabel("Título");
        titulo.setAutofocus(true);
        titulo.setRequired(true);
        titulo.setMinLength(2);
        titulo.setMaxLength(255);
        titulo.setErrorMessage("Solo letras, mínimo 2 caracteres y máximo 100");
        titulo.addValueChangeListener(event -> {
            event.getSource().setHelperText(event.getValue().length() + "/" + 255);
        });

        //autor
        autor.setLabel("Autor");
        autor.getElement().setAttribute("autor", "Ejemplo:Perez Diaz");
        autor.setAutofocus(true);
        autor.setRequired(true);
        autor.setMinLength(2);
        autor.setMaxLength(250);
        autor.setErrorMessage("Solo letras, mínimo 3 caracteres y máximo 250");
        autor.addValueChangeListener(event -> {
            event.getSource().setHelperText(event.getValue().length() + "/" + 250);
        });
        //volumen
        volumen.setLabel("Vólumen");
        volumen.setValue(0);
        volumen.setHasControls(true);
        volumen.setMin(0);
        volumen.setMax(10);
        volumen.setHelperText("Máximo 10");
        //tomo
        tomo.setLabel("Tomo");
        tomo.setValue(0);
        tomo.setHasControls(true);
        tomo.setMin(0);
        tomo.setMax(10);
        tomo.setHelperText("Máximo 10");
        //parte
        parte.setLabel("Parte");
        parte.setValue(0);
        parte.setHasControls(true);
        parte.setMin(0);
        parte.setMax(10);
        parte.setHelperText("Máximo 10");
        //cantidad
        cantidad = new IntegerField("Cantidad");
        cantidad.setValue(1);
        cantidad.setRequiredIndicatorVisible(true);
        cantidad.setHasControls(true);
        cantidad.setMin(1);
        cantidad.setHelperText("Míximo 1");
        //precio
        precio = new NumberField("Precio");
        Div dollarPrefix = new Div();
        dollarPrefix.setText("$");
        precio.setPrefixComponent(dollarPrefix);
        Div cupSuffix = new Div();
        cupSuffix.setText("cup");
        precio.setSuffixComponent(cupSuffix);
        precio.setRequiredIndicatorVisible(true);
        precio.setHasControls(true);
        precio.setStep(0.5);
        precio.setMin(0);

        add(
                imagen,
                titulo,
                autor,
                volumen,
                tomo,
                parte,
                cantidad,
                precio,
                createButtonsLayout());
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

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));

        buttonlayout.add(save, close);

        return buttonlayout;
    }

    public void setLibro(Libro libro) {
        this.libro = libro;
        binder.readBean(libro);
    }

    private void validateAndSave() {
        try {
            binder.writeBean(libro);
            fireEvent(new SaveEvent(this, libro));
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

    private void attachImageUpload(Upload upload, Image preview) {
        ByteArrayOutputStream uploadBuffer = new ByteArrayOutputStream();
        upload.setAcceptedFileTypes("image/tiff", ".png", ".jpg");
        upload.setReceiver((fileName, mimeType) -> {
            return uploadBuffer;
        });
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
    public static abstract class LibroFormEvent extends ComponentEvent<LibroForm> {

        private Libro estudiante;

        protected LibroFormEvent(LibroForm source, Libro libro) {
            super(source, false);
            this.estudiante = libro;
        }

        public Libro getLibro() {
            return estudiante;
        }
    }

    public static class SaveEvent extends LibroFormEvent {

        SaveEvent(LibroForm source, Libro estudiante) {
            super(source, estudiante);
        }
    }

    public static class DeleteEvent extends LibroFormEvent {

        DeleteEvent(LibroForm source, Libro libro) {
            super(source, libro);
        }

    }

    public static class CloseEvent extends LibroFormEvent {

        CloseEvent(LibroForm source) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
            ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}
