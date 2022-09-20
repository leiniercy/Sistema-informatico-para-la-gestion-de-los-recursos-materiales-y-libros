package trabajodediploma.views.modeloPago;

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

import trabajodediploma.data.entity.Trabajador;
import trabajodediploma.data.entity.Libro;
import trabajodediploma.data.entity.ModeloPago;
import trabajodediploma.data.entity.ModeloPagoTrabajador;
import elemental.json.Json;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import org.springframework.web.util.UriUtils;
import org.vaadin.gatanaso.MultiselectComboBox;

import trabajodediploma.data.tools.MyUploadI18n;

public class ModeloPagoTrabajadorForm  extends FormLayout {
    
    ModeloPagoTrabajador modelo;
    
    private Dialog reportDialog;
    private Image imagePreview;
    private Label imageSize;
    Upload imagen = new Upload();
    ComboBox<Trabajador> trabajador = new ComboBox<>();
    MultiselectComboBox<Libro> libros = new MultiselectComboBox<>();

    Button save = new Button("Añadir", VaadinIcon.PLUS.create());
    Button close = new Button("Cancelar", VaadinIcon.ERASER.create());

    BeanValidationBinder<ModeloPagoTrabajador> binder = new BeanValidationBinder<>(ModeloPagoTrabajador.class);

    public ModeloPagoTrabajadorForm(List<Trabajador> listTrabajadors, List<Libro> listLibros){
        addClassName("modelo-pago-form");
        this.reportDialog = reportDialog;

        binder.bindInstanceFields(this);
        // imagen
        // int maxFileSizeInBytes = 10 * 1024 * 1024; // 10MB
        imageSize = new Label("Tamaño máximo: 1MB");
        imageSize.getStyle().set("color", "var(--lumo-secondary-text-color)");
        imagePreview = new Image();
        imagePreview.setWidth("100%");
        imagen.getStyle().set("box-sizing", "border-box");
        imagen.getElement().appendChild(imagePreview.getElement());
        Button uploadButton = new Button("Seleccionar imagen...");
        uploadButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        imagen.setUploadButton(uploadButton);

        trabajador.setPlaceholder("Trabajador");
        trabajador.setRequired(true);
        trabajador.setErrorMessage("Campo obligatorio");
        trabajador.setItems(listTrabajadors);
        trabajador.setItemLabelGenerator(e -> e.getUser().getName());

        libros.setPlaceholder("Libros");
        libros.setRequired(true);
        libros.setErrorMessage("Campo obligatorio");
        libros.setItems(listLibros);
        libros.setItemLabelGenerator(libro -> libro.getTitulo());
        
        attachImageUpload(imagen, imagePreview);

        add(imageSize, imagen, trabajador, libros, createButtonsLayout());

    }

    // Buttons
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

    // Set Book
    public void setModeloPago(ModeloPagoTrabajador modelo) {
        this.modelo = modelo;
        binder.readBean(modelo);
        this.imagePreview.setVisible(modelo != null);

        this.imagePreview.setSrc(modelo.getImagen());
    }

    // Validate and Save
    private void validateAndSave() {
        try {
            if (imagePreview.getSrc() == "" || imagePreview.getSrc() == null || libros.isEmpty()) {
                Notification notification = Notification.show(
                        "Campos obligatorios vacios",
                        1000,
                        Notification.Position.MIDDLE);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }else {
                binder.writeBean(modelo);
                this.modelo.setImagen(imagePreview.getSrc());
                fireEvent(new SaveEvent(this, modelo));
            }
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

    public static abstract class ModeloPagoTrabajadorFormEvent extends ComponentEvent<ModeloPagoTrabajadorForm> {

        private ModeloPagoTrabajador modeloPago;

        protected ModeloPagoTrabajadorFormEvent(ModeloPagoTrabajadorForm source, ModeloPagoTrabajador modeloPago) {
            super(source, false);
            this.modeloPago = modeloPago;
        }

        public ModeloPagoTrabajador getModeloPago() {
            return modeloPago;
        }
    }

    // Save Event
    public static class SaveEvent extends ModeloPagoTrabajadorFormEvent {

        SaveEvent(ModeloPagoTrabajadorForm source, ModeloPagoTrabajador modeloPago) {
            super(source, modeloPago);
        }
    }

    // Delete Event
    public static class DeleteEvent extends ModeloPagoTrabajadorFormEvent {

        DeleteEvent(ModeloPagoTrabajadorForm source, ModeloPagoTrabajador modeloPago) {
            super(source, modeloPago);
        }

    }

    // Close Event
    public static class CloseEvent extends ModeloPagoTrabajadorFormEvent {

        CloseEvent(ModeloPagoTrabajadorForm source) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
            ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

}
