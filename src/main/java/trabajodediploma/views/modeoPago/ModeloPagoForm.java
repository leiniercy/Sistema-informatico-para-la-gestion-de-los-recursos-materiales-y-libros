package trabajodediploma.views.modeoPago;

import java.util.List;

import org.vaadin.gatanaso.MultiselectComboBox;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.shared.Registration;

import trabajodediploma.data.entity.Estudiante;
import trabajodediploma.data.entity.Libro;
import trabajodediploma.data.entity.ModeloPago;

import elemental.json.Json;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.web.util.UriUtils;
import trabajodediploma.data.tools.MyUploadI18n;

public class ModeloPagoForm extends FormLayout {

    ModeloPago modelo;
    private Dialog reportDialog;
    private Image imagePreview;
    private Label imageSize;
    Upload imagen = new Upload();
    ComboBox<Estudiante> estudiante /* = new ComboBox<>() */;
    MultiselectComboBox<Libro> libros /* = new MultiselectComboBox<>() */;

    Anchor save;

    Button close = new Button("Cancelar", VaadinIcon.ERASER.create());

    BeanValidationBinder<ModeloPago> binder = new BeanValidationBinder<>(ModeloPago.class);

    public ModeloPagoForm(ComboBox<Estudiante> estudiante, MultiselectComboBox<Libro> libros, StreamResource source,
            Dialog reportDialog) {
        addClassName("modelo-pago-form");
        this.reportDialog = reportDialog;
        save = new Anchor(source, "Crear Modelo");

        binder.bindInstanceFields(this);
        // imagen
        // int maxFileSizeInBytes = 10 * 1024 * 1024; // 10MB
        imageSize = new Label("Tamaño maximo: 400kb");
        imageSize.getStyle().set("color", "var(--lumo-secondary-text-color)");
        imagePreview = new Image();
        imagePreview.setWidth("100%");
        imagen.getStyle().set("box-sizing", "border-box");
        imagen.getElement().appendChild(imagePreview.getElement());
        Button uploadButton = new Button("Seleccionar imagen...");
        uploadButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        imagen.setUploadButton(uploadButton);

        this.estudiante = estudiante;
        // estudiante.setPlaceholder("Estudiante");
        // estudiante.setRequired(true);
        // estudiante.setItems(listEstudiantes);
        // estudiante.setItemLabelGenerator(estudiante ->
        // estudiante.getUser().getName());

        this.libros = libros;
        // libros.setPlaceholder("Libros");
        // libros.setRequired(true);
        // libros.setItems(listLibros);
        // libros.setItemLabelGenerator(libro -> libro.getTitulo());

        attachImageUpload(imagen, imagePreview);

        add(imageSize, imagen, estudiante, libros, createButtonsLayout());

    }

    // Buttons
    private HorizontalLayout createButtonsLayout() {

        HorizontalLayout buttonlayout = new HorizontalLayout();
        buttonlayout.addClassName("button-layout");
        // save.addClickListener(event -> validateAndSave());
        save.addClassNames("link-modelo");
        save.setTarget("_BLANK");
        save.addBlurListener(e -> validateAndSave());
        save.addBlurListener(e -> reportDialog.close());

        close.addClickListener(event -> fireEvent(new CloseEvent(this)));
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        close.addClickShortcut(Key.ESCAPE);

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));

        buttonlayout.add(save, close);

        return buttonlayout;
    }

    // Set Book
    public void setModeloPago(ModeloPago modelo) {
        this.modelo = modelo;
        binder.readBean(modelo);
        this.imagePreview.setVisible(modelo != null);
        if (modelo == null) {
            this.imagePreview.setSrc("");
        } else {
            this.imagePreview.setSrc(modelo.getImagen());
        }
    }

    // Validate and Save
    private void validateAndSave() {
        try {
            binder.writeBean(modelo);
            this.modelo.setImagen(imagePreview.getSrc());
            fireEvent(new SaveEvent(this, modelo));
        } catch (ValidationException e) {
            e.printStackTrace();
            Notification notification = Notification.show(
                    "Ocurrió un problema con el modelo",
                    2000,
                    Notification.Position.MIDDLE);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    // Response to images errors
    private void configuracionErroresImagen() {
        MyUploadI18n i18n = new MyUploadI18n();
        i18n.getAddFiles().setOne("Cargar Imágen...");
        i18n.getDropFiles().setOne("Arrastra la imágen aquí");

        i18n.getError()
                .setFileIsTooBig("El archivo excede el tamaño máximo permitido de 1MB.")
                .setIncorrectFileType("El archivo seleccionado no es una imágen png o jpg.");
        ;
        imagen.setI18n(i18n);

    }

    // Convert images to bytes
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

    public static abstract class ModeloPagoFormEvent extends ComponentEvent<ModeloPagoForm> {

        private ModeloPago modeloPago;

        protected ModeloPagoFormEvent(ModeloPagoForm source, ModeloPago modeloPago) {
            super(source, false);
            this.modeloPago = modeloPago;
        }

        public ModeloPago getModeloPago() {
            return modeloPago;
        }
    }

    // Save Event
    public static class SaveEvent extends ModeloPagoFormEvent {

        SaveEvent(ModeloPagoForm source, ModeloPago modeloPago) {
            super(source, modeloPago);
        }
    }

    // Delete Event
    public static class DeleteEvent extends ModeloPagoFormEvent {

        DeleteEvent(ModeloPagoForm source, ModeloPago modeloPago) {
            super(source, modeloPago);
        }

    }

    // Close Event
    public static class CloseEvent extends ModeloPagoFormEvent {

        CloseEvent(ModeloPagoForm source) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
            ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

}