package trabajodediploma.views.libros;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import elemental.json.Json;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.util.UriUtils;
import trabajodediploma.data.entity.Libro;
import trabajodediploma.data.service.LibroService;
import trabajodediploma.views.MainLayout;

@PageTitle("Libros")
@Route(value = "libro-view/:libroID?/:action?(edit)", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class LibrosView extends Div implements BeforeEnterObserver {

    private final String LIBRO_ID = "libroID";
    private final String LIBRO_EDIT_ROUTE_TEMPLATE = "libro-view/%s/edit";

    private Grid<Libro> grid = new Grid<>(Libro.class, false);

    private Upload image;
    private Image imagePreview;
    private TextField titulo;
    private TextField autor;
    private TextField volumen;
    private TextField tomo;
    private TextField parte;
    private TextField cantidad;
    private TextField precio;

    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");

    private BeanValidationBinder<Libro> binder;

    private Libro libro;

    private LibroService libroService;

    public LibrosView(@Autowired LibroService libroService) {
        this.libroService = libroService;
        addClassNames("libros-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        LitRenderer<Libro> imageRenderer = LitRenderer.<Libro>of("<img style='height: 64px' src=${item.image} />")
                .withProperty("image", Libro::getImage);
        grid.addColumn(imageRenderer).setHeader("Image").setWidth("68px").setFlexGrow(0);

        grid.addColumn("titulo").setAutoWidth(true);
        grid.addColumn("autor").setAutoWidth(true);
        grid.addColumn("volumen").setAutoWidth(true);
        grid.addColumn("tomo").setAutoWidth(true);
        grid.addColumn("parte").setAutoWidth(true);
        grid.addColumn("cantidad").setAutoWidth(true);
        grid.addColumn("precio").setAutoWidth(true);
        grid.setItems(query -> libroService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(LIBRO_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(LibrosView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Libro.class);

        // Bind fields. This is where you'd define e.g. validation rules
        binder.forField(volumen).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("volumen");
        binder.forField(tomo).withConverter(new StringToIntegerConverter("Only numbers are allowed")).bind("tomo");
        binder.forField(parte).withConverter(new StringToIntegerConverter("Only numbers are allowed")).bind("parte");
        binder.forField(cantidad).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("cantidad");
        binder.forField(precio).withConverter(new StringToIntegerConverter("Only numbers are allowed")).bind("precio");

        binder.bindInstanceFields(this);

        attachImageUpload(image, imagePreview);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.libro == null) {
                    this.libro = new Libro();
                }
                binder.writeBean(this.libro);
                this.libro.setImage(imagePreview.getSrc());

                libroService.update(this.libro);
                clearForm();
                refreshGrid();
                Notification.show("Libro details stored.");
                UI.getCurrent().navigate(LibrosView.class);
            } catch (ValidationException validationException) {
                Notification.show("An exception happened while trying to store the libro details.");
            }
        });

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<UUID> libroId = event.getRouteParameters().get(LIBRO_ID).map(UUID::fromString);
        if (libroId.isPresent()) {
            Optional<Libro> libroFromBackend = libroService.get(libroId.get());
            if (libroFromBackend.isPresent()) {
                populateForm(libroFromBackend.get());
            } else {
                Notification.show(String.format("The requested libro was not found, ID = %s", libroId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(LibrosView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        Label imageLabel = new Label("Image");
        imagePreview = new Image();
        imagePreview.setWidth("100%");
        image = new Upload();
        image.getStyle().set("box-sizing", "border-box");
        image.getElement().appendChild(imagePreview.getElement());
        titulo = new TextField("Titulo");
        autor = new TextField("Autor");
        volumen = new TextField("Volumen");
        tomo = new TextField("Tomo");
        parte = new TextField("Parte");
        cantidad = new TextField("Cantidad");
        precio = new TextField("Precio");
        Component[] fields = new Component[]{imageLabel, image, titulo, autor, volumen, tomo, parte, cantidad, precio};

        formLayout.add(fields);
        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void attachImageUpload(Upload upload, Image preview) {
        ByteArrayOutputStream uploadBuffer = new ByteArrayOutputStream();
        upload.setAcceptedFileTypes("image/*");
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

    private void refreshGrid() {
        grid.select(null);
        grid.getLazyDataView().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Libro value) {
        this.libro = value;
        binder.readBean(this.libro);
        this.imagePreview.setVisible(value != null);
        if (value == null) {
            this.imagePreview.setSrc("");
        } else {
            this.imagePreview.setSrc(value.getImage());
        }

    }
}
