package trabajodediploma.views.grupo;

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
import trabajodediploma.data.entity.Grupo;
import trabajodediploma.data.service.GrupoService;
import trabajodediploma.views.MainLayout;

@PageTitle("Grupo")
@Route(value = "Grupo/:grupoID?/:action?(edit)", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class GrupoView extends Div implements BeforeEnterObserver {

    private final String GRUPO_ID = "grupoID";
    private final String GRUPO_EDIT_ROUTE_TEMPLATE = "Grupo/%s/edit";

    private Grid<Grupo> grid = new Grid<>(Grupo.class, false);

    private TextField numero;

    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");

    private BeanValidationBinder<Grupo> binder;

    private Grupo grupo;

    private GrupoService grupoService;

    public GrupoView(@Autowired GrupoService grupoService) {
        this.grupoService = grupoService;
        addClassNames("grupo-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("numero").setAutoWidth(true);
        grid.setItems(query -> grupoService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(GRUPO_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(GrupoView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Grupo.class);

        // Bind fields. This is where you'd define e.g. validation rules
        binder.forField(numero).withConverter(new StringToIntegerConverter("Only numbers are allowed")).bind("numero");

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.grupo == null) {
                    this.grupo = new Grupo();
                }
                binder.writeBean(this.grupo);

                grupoService.update(this.grupo);
                clearForm();
                refreshGrid();
                Notification.show("Grupo details stored.");
                UI.getCurrent().navigate(GrupoView.class);
            } catch (ValidationException validationException) {
                Notification.show("An exception happened while trying to store the grupo details.");
            }
        });

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<UUID> grupoId = event.getRouteParameters().get(GRUPO_ID).map(UUID::fromString);
        if (grupoId.isPresent()) {
            Optional<Grupo> grupoFromBackend = grupoService.get(grupoId.get());
            if (grupoFromBackend.isPresent()) {
                populateForm(grupoFromBackend.get());
            } else {
                Notification.show(String.format("The requested grupo was not found, ID = %s", grupoId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(GrupoView.class);
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
        numero = new TextField("Numero");
        Component[] fields = new Component[]{numero};

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

    private void populateForm(Grupo value) {
        this.grupo = value;
        binder.readBean(this.grupo);

    }
}
