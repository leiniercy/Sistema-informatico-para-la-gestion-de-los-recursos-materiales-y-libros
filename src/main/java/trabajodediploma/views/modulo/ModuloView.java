package trabajodediploma.views.modulo;

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
import trabajodediploma.data.entity.Modulo;
import trabajodediploma.data.service.ModuloService;
import trabajodediploma.views.MainLayout;

@PageTitle("Modulo")
@Route(value = "modulo-view/:moduloID?/:action?(edit)", layout = MainLayout.class)
@RolesAllowed("ASISTENTE_CONTROL")
public class ModuloView extends Div implements BeforeEnterObserver {

    private final String MODULO_ID = "moduloID";
    private final String MODULO_EDIT_ROUTE_TEMPLATE = "modulo-view/%s/edit";

    private Grid<Modulo> grid = new Grid<>(Modulo.class, false);

    private TextField anno_academico;

    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");

    private BeanValidationBinder<Modulo> binder;

    private Modulo modulo;

    private ModuloService moduloService;

    public ModuloView(@Autowired ModuloService moduloService) {
        this.moduloService = moduloService;
        addClassNames("modulo-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("anno_academico").setAutoWidth(true);
        grid.setItems(query -> moduloService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(MODULO_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(ModuloView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Modulo.class);

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
                if (this.modulo == null) {
                    this.modulo = new Modulo();
                }
                binder.writeBean(this.modulo);

                moduloService.update(this.modulo);
                clearForm();
                refreshGrid();
                Notification.show("Modulo details stored.");
                UI.getCurrent().navigate(ModuloView.class);
            } catch (ValidationException validationException) {
                Notification.show("An exception happened while trying to store the modulo details.");
            }
        });

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<UUID> moduloId = event.getRouteParameters().get(MODULO_ID).map(UUID::fromString);
        if (moduloId.isPresent()) {
            Optional<Modulo> moduloFromBackend = moduloService.get(moduloId.get());
            if (moduloFromBackend.isPresent()) {
                populateForm(moduloFromBackend.get());
            } else {
                Notification.show(String.format("The requested modulo was not found, ID = %s", moduloId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(ModuloView.class);
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
        anno_academico = new TextField("Anno_academico");
        Component[] fields = new Component[]{anno_academico};

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

    private void populateForm(Modulo value) {
        this.modulo = value;
        binder.readBean(this.modulo);

    }
}
