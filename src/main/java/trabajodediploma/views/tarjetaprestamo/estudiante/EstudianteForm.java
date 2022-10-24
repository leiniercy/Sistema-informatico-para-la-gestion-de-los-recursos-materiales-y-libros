/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabajodediploma.views.tarjetaprestamo.estudiante;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import java.util.List;
import org.vaadin.gatanaso.MultiselectComboBox;
import trabajodediploma.data.entity.Estudiante;
import trabajodediploma.data.entity.Libro;
import trabajodediploma.views.tarjetaprestamo.trabajadorPrestamo.TarjetaPrestamoTrabajadorForm;

/**
 *
 * @author leinier
 */
public class EstudianteForm extends FormLayout {

    MultiselectComboBox<Libro> libros = new MultiselectComboBox();
    DatePicker fechaPrestamo = new DatePicker("Fecha Prestamo");
    DatePicker fechaDevolucion = new DatePicker("Fecha Devolucion");

    Button save = new Button("Añadir", VaadinIcon.PLUS.create());
    Button close = new Button("Cancelar", VaadinIcon.ERASER.create());

    public EstudianteForm(List<Estudiante> estudiantes, List<Libro> listlibros ) {
        addClassName("estudiante-prestamo-form");
        Configuracion();
        add(libros, fechaPrestamo, fechaDevolucion, createButtonsLayout());
    }

    private void Configuracion() {
        
        libros.setLabel("Libros");
        libros.setPlaceholder("Libros");
        libros.setItemLabelGenerator(Libro::getTitulo);
        libros.setItems();

        fechaPrestamo.addValueChangeListener(e -> fechaDevolucion.setMin(e.getValue()));
        fechaDevolucion.addValueChangeListener(e -> fechaPrestamo.setMax(e.getValue()));

    }
    
    private HorizontalLayout createButtonsLayout() {

        HorizontalLayout buttonlayout = new HorizontalLayout();
        buttonlayout.addClassName("button-layout");
//        save.addClickListener(event -> validateAndSave());
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickShortcut(Key.ENTER);

//        close.addClickListener(event -> fireEvent(new TarjetaPrestamoTrabajadorForm.CloseEvent(this)));
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        close.addClickShortcut(Key.ESCAPE);

//        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
        buttonlayout.add(save, close);

        return buttonlayout;
    }

}
