/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabajodediploma.views.libros;

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
import trabajodediploma.data.entity.Libro;

/**
 *
 * @author leinier
 */
public class LibroForm extends FormLayout {

    private Libro libro;

//    Upload imagen;
//    Image imagenPreview;
    TextField titulo;
    TextField autor;
    IntegerField volumen;
    IntegerField tomo;
    IntegerField parte;
    IntegerField cantidad;
    NumberField precio;

    Button save = new Button("Añadir", VaadinIcon.PLUS.create());
    Button close = new Button("Cancelar", VaadinIcon.REFRESH.create());

    BeanValidationBinder<Libro> binder;

    public LibroForm() {

        addClassName("libro-form");

        // Configure Form
        binder = new BeanValidationBinder<>(Libro.class);

        binder.bindInstanceFields(this);

        Label imagenLabel = new Label("Imagen");
//        imagenPreview = new Image();
//        imagenPreview.setWidth("100%");
//        imagen = new Upload();
//        imagen.getStyle().set("box-sizing", "border-box");
//        imagen.getElement().appendChild(imagenPreview.getElement());

        titulo = new TextField();
        titulo.setLabel("Título");
        titulo.setRequired(true);
        titulo.setMinLength(2);
        titulo.setMaxLength(255);
        titulo.addValueChangeListener(event -> {
            event.getSource().setHelperText(event.getValue().length() + "/" + 255);
        });

        autor = new TextField();
        autor.setLabel("Autor");
        autor.setRequired(true);
        autor.setMinLength(2);
        autor.setMaxLength(100);
        autor.addValueChangeListener(event -> {
            event.getSource().setHelperText(event.getValue().length() + "/" + 100);
        });

        volumen = new IntegerField("Vólumen");
        volumen.setValue(0);
        volumen.setHasControls(true);
        volumen.setMin(0);
        volumen.setMax(10);
        volumen.setHelperText("Máximo 10");

        tomo = new IntegerField("Tomo");
        tomo.setValue(0);
        tomo.setHasControls(true);
        tomo.setMin(0);
        tomo.setMax(10);
        tomo.setHelperText("Máximo 10");

        parte = new IntegerField("Parte");
        parte.setValue(0);
        parte.setHasControls(true);
        parte.setMin(0);
        parte.setMax(10);
        parte.setHelperText("Máximo 10");

        cantidad = new IntegerField("Cantidad");
        cantidad.setValue(1);
        cantidad.setRequiredIndicatorVisible(true);
        cantidad.setHasControls(true);
        cantidad.setMin(1);
        cantidad.setHelperText("Míximo 1");

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
                imagenLabel, /*imagen,*/ titulo, autor,
                volumen, tomo, parte, cantidad, precio,
                createButtonsLayout()
        );

    }

    private HorizontalLayout createButtonsLayout() {

        save.addClickListener(event -> validateAndSave());
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickShortcut(Key.ENTER);

        close.addClickListener(event -> fireEvent(new CloseEvent(this)));
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        close.addClickShortcut(Key.ESCAPE);

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));

        return new HorizontalLayout(save, close);
    }

    public void setBook(Libro libro) {
        this.libro = libro;
        binder.readBean(libro);
    }

    private void validateAndSave() {
        try {
            binder.writeBean(libro);
            fireEvent(new SaveEvent(this, libro));
        } catch (ValidationException e) {
            Notification.show("Ocurrio un problema al intentar guardad el libro.");
        }
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
