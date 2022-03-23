package trabajodediploma.views.tarjetaprestamo;

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
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
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
import trabajodediploma.data.entity.TarjetaPrestamoEstudiante;
import trabajodediploma.data.service.TarjetaPrestamoEstudianteService;
import trabajodediploma.views.MainLayout;

@PageTitle("Tarjeta Prestamo")
@Route(value = "tarjeta-prestamo/:tarjetaPrestamoEstudianteID?/:action?(edit)", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class TarjetaPrestamoView extends Div implements BeforeEnterObserver {

    private final String TARJETAPRESTAMOESTUDIANTE_ID = "tarjetaPrestamoEstudianteID";
    private final String TARJETAPRESTAMOESTUDIANTE_EDIT_ROUTE_TEMPLATE = "tarjeta-prestamo/%s/edit";

    private Grid<TarjetaPrestamoEstudiante> grid = new Grid<>(TarjetaPrestamoEstudiante.class, false);

    private DatePicker fechaPrestamo;
    private DatePicker fechaDevolucion;

    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");

    private BeanValidationBinder<TarjetaPrestamoEstudiante> binder;

    private TarjetaPrestamoEstudiante tarjetaPrestamoEstudiante;

    private TarjetaPrestamoEstudianteService tarjetaPrestamoEstudianteService;

    public TarjetaPrestamoView(@Autowired TarjetaPrestamoEstudianteService tarjetaPrestamoEstudianteService) {
        this.tarjetaPrestamoEstudianteService = tarjetaPrestamoEstudianteService;
        addClassNames("tarjeta-prestamo-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("fechaPrestamo").setAutoWidth(true);
        grid.addColumn("fechaDevolucion").setAutoWidth(true);
        grid.setItems(query -> tarjetaPrestamoEstudianteService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(
                        String.format(TARJETAPRESTAMOESTUDIANTE_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(TarjetaPrestamoView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(TarjetaPrestamoEstudiante.class);

        // Bind fields. This is where you'd define e.g. validation rules

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.tarjetaPrestamoEstudiante == null) {
                    this.tarjetaPrestamoEstudiante = new TarjetaPrestamoEstudiante();
                }
                binder.writeBean(this.tarjetaPrestamoEstudiante);

                tarjetaPrestamoEstudianteService.update(this.tarjetaPrestamoEstudiante);
                clearForm();
                refreshGrid();
                Notification.show("TarjetaPrestamoEstudiante details stored.");
                UI.getCurrent().navigate(TarjetaPrestamoView.class);
            } catch (ValidationException validationException) {
                Notification.show("An exception happened while trying to store the tarjetaPrestamoEstudiante details.");
            }
        });

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<UUID> tarjetaPrestamoEstudianteId = event.getRouteParameters().get(TARJETAPRESTAMOESTUDIANTE_ID)
                .map(UUID::fromString);
        if (tarjetaPrestamoEstudianteId.isPresent()) {
            Optional<TarjetaPrestamoEstudiante> tarjetaPrestamoEstudianteFromBackend = tarjetaPrestamoEstudianteService
                    .get(tarjetaPrestamoEstudianteId.get());
            if (tarjetaPrestamoEstudianteFromBackend.isPresent()) {
                populateForm(tarjetaPrestamoEstudianteFromBackend.get());
            } else {
                Notification.show(String.format("The requested tarjetaPrestamoEstudiante was not found, ID = %s",
                        tarjetaPrestamoEstudianteId.get()), 3000, Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(TarjetaPrestamoView.class);
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
        fechaPrestamo = new DatePicker("Fecha Prestamo");
        fechaDevolucion = new DatePicker("Fecha Devolucion");
        Component[] fields = new Component[]{fechaPrestamo, fechaDevolucion};

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

    private void populateForm(TarjetaPrestamoEstudiante value) {
        this.tarjetaPrestamoEstudiante = value;
        binder.readBean(this.tarjetaPrestamoEstudiante);

    }
}
