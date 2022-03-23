package trabajodediploma.views.recursosmateriales;

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
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import trabajodediploma.data.entity.RecursoMaterial;
import trabajodediploma.data.service.RecursoMaterialService;
import trabajodediploma.views.MainLayout;

@PageTitle("Recursos Materiales ")
@Route(value = "recurso-material-view/:recursoMaterialID?/:action?(edit)", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class RecursosMaterialesView extends Div implements BeforeEnterObserver {

    private final String RECURSOMATERIAL_ID = "recursoMaterialID";
    private final String RECURSOMATERIAL_EDIT_ROUTE_TEMPLATE = "recurso-material-view/%s/edit";

    private Grid<RecursoMaterial> grid = new Grid<>(RecursoMaterial.class, false);

    private TextField codigo;
    private TextField descripcion;
    private TextField unidadMedida;
    private TextField cantidad;

    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");

    private BeanValidationBinder<RecursoMaterial> binder;

    private RecursoMaterial recursoMaterial;

    private RecursoMaterialService recursoMaterialService;

    public RecursosMaterialesView(@Autowired RecursoMaterialService recursoMaterialService) {
        this.recursoMaterialService = recursoMaterialService;
        addClassNames("recursos-materiales-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("codigo").setAutoWidth(true);
        grid.addColumn("descripcion").setAutoWidth(true);
        grid.addColumn("unidadMedida").setAutoWidth(true);
        grid.addColumn("cantidad").setAutoWidth(true);
        grid.setItems(query -> recursoMaterialService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(RECURSOMATERIAL_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(RecursosMaterialesView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(RecursoMaterial.class);

        // Bind fields. This is where you'd define e.g. validation rules
        binder.forField(cantidad).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("cantidad");

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.recursoMaterial == null) {
                    this.recursoMaterial = new RecursoMaterial();
                }
                binder.writeBean(this.recursoMaterial);

                recursoMaterialService.update(this.recursoMaterial);
                clearForm();
                refreshGrid();
                Notification.show("RecursoMaterial details stored.");
                UI.getCurrent().navigate(RecursosMaterialesView.class);
            } catch (ValidationException validationException) {
                Notification.show("An exception happened while trying to store the recursoMaterial details.");
            }
        });

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<UUID> recursoMaterialId = event.getRouteParameters().get(RECURSOMATERIAL_ID).map(UUID::fromString);
        if (recursoMaterialId.isPresent()) {
            Optional<RecursoMaterial> recursoMaterialFromBackend = recursoMaterialService.get(recursoMaterialId.get());
            if (recursoMaterialFromBackend.isPresent()) {
                populateForm(recursoMaterialFromBackend.get());
            } else {
                Notification.show(
                        String.format("The requested recursoMaterial was not found, ID = %s", recursoMaterialId.get()),
                        3000, Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(RecursosMaterialesView.class);
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
        codigo = new TextField("Codigo");
        descripcion = new TextField("Descripcion");
        unidadMedida = new TextField("Unidad Medida");
        cantidad = new TextField("Cantidad");
        Component[] fields = new Component[]{codigo, descripcion, unidadMedida, cantidad};

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

    private void populateForm(RecursoMaterial value) {
        this.recursoMaterial = value;
        binder.readBean(this.recursoMaterial);

    }
}
