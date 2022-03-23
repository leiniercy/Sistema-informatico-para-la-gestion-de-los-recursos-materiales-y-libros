package trabajodediploma.views.estudiante;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import trabajodediploma.data.entity.Estudiante;
import trabajodediploma.data.service.EstudianteService;
import trabajodediploma.views.MainLayout;

@PageTitle("Estudiante")
@Route(value = "estudiante-view/:estudianteID?/:action?(edit)", layout = MainLayout.class)
@AnonymousAllowed
public class EstudianteView extends Div implements BeforeEnterObserver {

    private final String ESTUDIANTE_ID = "estudianteID";
    private final String ESTUDIANTE_EDIT_ROUTE_TEMPLATE = "estudiante-view/%s/edit";

    private Grid<Estudiante> grid = new Grid<>(Estudiante.class, false);

    private TextField nombre;
    private TextField apellidos;
    private TextField ci;
    private TextField solapin;
    private TextField anno_academico;
    private TextField facultad;

    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");

    private BeanValidationBinder<Estudiante> binder;

    private Estudiante estudiante;

    private EstudianteService estudianteService;

    public EstudianteView(@Autowired EstudianteService estudianteService) {
        this.estudianteService = estudianteService;
        addClassNames("estudiante-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("nombre").setAutoWidth(true);
        grid.addColumn("apellidos").setAutoWidth(true);
        grid.addColumn("ci").setAutoWidth(true);
        grid.addColumn("solapin").setAutoWidth(true);
        grid.addColumn("anno_academico").setAutoWidth(true);
        grid.addColumn("facultad").setAutoWidth(true);
        grid.setItems(query -> estudianteService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(ESTUDIANTE_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(EstudianteView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Estudiante.class);

        // Bind fields. This is where you'd define e.g. validation rules
        binder.forField(anno_academico).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("anno_academico");

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.estudiante == null) {
                    this.estudiante = new Estudiante();
                }
                binder.writeBean(this.estudiante);

                estudianteService.update(this.estudiante);
                clearForm();
                refreshGrid();
                Notification.show("Estudiante details stored.");
                UI.getCurrent().navigate(EstudianteView.class);
            } catch (ValidationException validationException) {
                Notification.show("An exception happened while trying to store the estudiante details.");
            }
        });

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<UUID> estudianteId = event.getRouteParameters().get(ESTUDIANTE_ID).map(UUID::fromString);
        if (estudianteId.isPresent()) {
            Optional<Estudiante> estudianteFromBackend = estudianteService.get(estudianteId.get());
            if (estudianteFromBackend.isPresent()) {
                populateForm(estudianteFromBackend.get());
            } else {
                Notification.show(String.format("The requested estudiante was not found, ID = %s", estudianteId.get()),
                        3000, Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(EstudianteView.class);
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
        nombre = new TextField("Nombre");
        apellidos = new TextField("Apellidos");
        ci = new TextField("Ci");
        solapin = new TextField("Solapin");
        anno_academico = new TextField("Anno_academico");
        facultad = new TextField("Facultad");
        Component[] fields = new Component[]{nombre, apellidos, ci, solapin, anno_academico, facultad};

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

    private void refreshGrid() {
        grid.select(null);
        grid.getLazyDataView().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Estudiante value) {
        this.estudiante = value;
        binder.readBean(this.estudiante);

    }
}
