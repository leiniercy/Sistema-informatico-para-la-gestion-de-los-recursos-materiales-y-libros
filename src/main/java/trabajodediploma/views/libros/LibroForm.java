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
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
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
import trabajodediploma.data.tools.MyUploadI18n;

/**
 *
 * @author leinier
 */
public class LibroForm extends FormLayout {

    private Libro libro;

    private Image imagePreview;
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
        //int maxFileSizeInBytes = 10 * 1024 * 1024; // 10MB
        Label imageSize = new Label("Tamaño maximo: 400kb");
        imageSize.getStyle().set("color", "var(--lumo-secondary-text-color)");
        imagePreview = new Image();
        imagePreview.setWidth("100%");
        imagen = new Upload();
        imagen.getStyle().set("box-sizing", "border-box");
        imagen.getElement().appendChild(imagePreview.getElement());
        Button uploadButton = new Button("Seleccionar imagen...");
        uploadButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        imagen.setUploadButton(uploadButton);

        //titulo
        titulo.setLabel("Título");
        titulo.setAutofocus(true);
        titulo.setRequired(true);
        titulo.setMinLength(2);
        titulo.setMaxLength(255);
        titulo.setErrorMessage("Solo letras, mínimo 2 caracteres y máximo 255");
        titulo.addValueChangeListener(event -> {
            event.getSource().setHelperText(event.getValue().length() + "/" + 255);
        });

        //autor
        autor.setLabel("Autor");
        autor.getElement().setAttribute("autor", "Ejemplo: Jose Diaz Perez");
        autor.setAutofocus(true);
        autor.setRequired(true);
        autor.setMinLength(2);
        autor.setMaxLength(255);
        autor.setErrorMessage("Solo letras, mínimo 2 caracteres y máximo 255");
        autor.addValueChangeListener(event -> {
            event.getSource().setHelperText(event.getValue().length() + "/" + 255);
        });
        //volumen
        volumen.setLabel("Vólumen");
        volumen.setValue(1);
        volumen.setHasControls(true);
        volumen.setMin(1);
        volumen.setMax(10);
        volumen.setHelperText("Máximo 10");
        //tomo
        tomo.setLabel("Tomo");
        tomo.setValue(1);
        tomo.setHasControls(true);
        tomo.setMin(1);
        tomo.setMax(10);
        tomo.setHelperText("Máximo 10");
        //parte
        parte.setLabel("Parte");
        parte.setValue(1);
        parte.setHasControls(true);
        parte.setMin(1);
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
        precio.setValue(0.0);
        precio.setMin(0);
        precio.setStep(0.5);


        attachImageUpload(imagen, imagePreview);

        add(
                imageSize,
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
        this.imagePreview.setVisible(libro != null);
        if (libro == null) {
            this.imagePreview.setSrc("");
        } else {
            this.imagePreview.setSrc(libro.getImagen());
        }
    }

    private void validateAndSave() {
        try {
            binder.writeBean(libro);
            this.libro.setImagen(imagePreview.getSrc());
            fireEvent(new SaveEvent(this, libro));
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

    private void configuracionErroresImagen() {

        MyUploadI18n i18n = new MyUploadI18n();
        i18n.getAddFiles().setOne("Cargar Imágen...");
        i18n.getDropFiles().setOne("Arrastra la imágen aquí");

        i18n.getError()
                .setFileIsTooBig("El archivo excede el tamaño máximo permitido de 400 Kb.")
                .setIncorrectFileType("El archivo seleccionado no es una imágen.");;
        imagen.setI18n(i18n);

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
    public static abstract class LibroFormEvent extends ComponentEvent<LibroForm> {

        private Libro libro1;

        protected LibroFormEvent(LibroForm source, Libro libro) {
            super(source, false);
            this.libro1 = libro;
        }

        public Libro getLibro() {
            return libro1;
        }
    }

    public static class SaveEvent extends LibroFormEvent {

        SaveEvent(LibroForm source, Libro libro) {
            super(source, libro);
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
