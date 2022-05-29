package trabajodediploma.views.destinofinal;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
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
import trabajodediploma.data.entity.DestinoFinal;
import trabajodediploma.data.service.DestinoFinalService;
import trabajodediploma.views.MainLayout;

@PageTitle("Destino Final")
@Route(value = "destino-final/:destinoFinalID?/:action?(edit)", layout = MainLayout.class)
@RolesAllowed("ASISTENTE_CONTROL")
public class DestinoFinalView extends Div implements BeforeEnterObserver {

    private final String DESTINOFINAL_ID = "destinoFinalID";
    private final String DESTINOFINAL_EDIT_ROUTE_TEMPLATE = "destino-final/%s/edit";

    private Grid<DestinoFinal> grid = new Grid<>(DestinoFinal.class, false);

    private DatePicker fecha;
    private TextField cantidad;

    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");

    private BeanValidationBinder<DestinoFinal> binder;

    private DestinoFinal destinoFinal;

    private DestinoFinalService destinoFinalService;

    public DestinoFinalView(@Autowired DestinoFinalService destinoFinalService) {
        this.destinoFinalService = destinoFinalService;
        addClassNames("destino-final-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("fecha").setAutoWidth(true);
        grid.addColumn("cantidad").setAutoWidth(true);
        grid.setItems(query -> destinoFinalService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(DESTINOFINAL_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(DestinoFinalView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(DestinoFinal.class);

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
                if (this.destinoFinal == null) {
                    this.destinoFinal = new DestinoFinal();
                }
                binder.writeBean(this.destinoFinal);

                destinoFinalService.update(this.destinoFinal);
                clearForm();
                refreshGrid();
                Notification.show("DestinoFinal details stored.");
                UI.getCurrent().navigate(DestinoFinalView.class);
            } catch (ValidationException validationException) {
                Notification.show("An exception happened while trying to store the destinoFinal details.");
            }
        });

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<UUID> destinoFinalId = event.getRouteParameters().get(DESTINOFINAL_ID).map(UUID::fromString);
        if (destinoFinalId.isPresent()) {
            Optional<DestinoFinal> destinoFinalFromBackend = destinoFinalService.get(destinoFinalId.get());
            if (destinoFinalFromBackend.isPresent()) {
                populateForm(destinoFinalFromBackend.get());
            } else {
                Notification.show(
                        String.format("The requested destinoFinal was not found, ID = %s", destinoFinalId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(DestinoFinalView.class);
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
        fecha = new DatePicker("Fecha");
        cantidad = new TextField("Cantidad");
        Component[] fields = new Component[]{fecha, cantidad};

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

    private void populateForm(DestinoFinal value) {
        this.destinoFinal = value;
        binder.readBean(this.destinoFinal);

    }
}
