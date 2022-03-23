package trabajodediploma.views.trabajador;

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
import trabajodediploma.data.entity.Trabajador;
import trabajodediploma.data.service.TrabajadorService;
import trabajodediploma.views.MainLayout;

@PageTitle("Trabajador")
@Route(value = "trabajador/:trabajadorID?/:action?(edit)", layout = MainLayout.class)
@AnonymousAllowed
public class TrabajadorView extends Div implements BeforeEnterObserver {

    private final String TRABAJADOR_ID = "trabajadorID";
    private final String TRABAJADOR_EDIT_ROUTE_TEMPLATE = "trabajador/%s/edit";

    private Grid<Trabajador> grid = new Grid<>(Trabajador.class, false);

    private TextField nombre;
    private TextField apellidos;
    private TextField ci;
    private TextField solapin;
    private TextField categoria;

    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");

    private BeanValidationBinder<Trabajador> binder;

    private Trabajador trabajador;

    private TrabajadorService trabajadorService;

    public TrabajadorView(@Autowired TrabajadorService trabajadorService) {
        this.trabajadorService = trabajadorService;
        addClassNames("trabajador-view");

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
        grid.addColumn("categoria").setAutoWidth(true);
        grid.setItems(query -> trabajadorService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(TRABAJADOR_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(TrabajadorView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Trabajador.class);

        // Bind fields. This is where you'd define e.g. validation rules

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.trabajador == null) {
                    this.trabajador = new Trabajador();
                }
                binder.writeBean(this.trabajador);

                trabajadorService.update(this.trabajador);
                clearForm();
                refreshGrid();
                Notification.show("Trabajador details stored.");
                UI.getCurrent().navigate(TrabajadorView.class);
            } catch (ValidationException validationException) {
                Notification.show("An exception happened while trying to store the trabajador details.");
            }
        });

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<UUID> trabajadorId = event.getRouteParameters().get(TRABAJADOR_ID).map(UUID::fromString);
        if (trabajadorId.isPresent()) {
            Optional<Trabajador> trabajadorFromBackend = trabajadorService.get(trabajadorId.get());
            if (trabajadorFromBackend.isPresent()) {
                populateForm(trabajadorFromBackend.get());
            } else {
                Notification.show(String.format("The requested trabajador was not found, ID = %s", trabajadorId.get()),
                        3000, Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(TrabajadorView.class);
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
        categoria = new TextField("Categoria");
        Component[] fields = new Component[]{nombre, apellidos, ci, solapin, categoria};

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

    private void populateForm(Trabajador value) {
        this.trabajador = value;
        binder.readBean(this.trabajador);

    }
}
