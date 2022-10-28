/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabajodediploma.views.login.crear_informacion_perfil;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import trabajodediploma.data.entity.Estudiante;
import trabajodediploma.data.entity.Trabajador;
import trabajodediploma.data.entity.User;
import trabajodediploma.data.service.AreaService;
import trabajodediploma.data.service.EstudianteService;
import trabajodediploma.data.service.GrupoService;
import trabajodediploma.data.service.TrabajadorService;
import trabajodediploma.data.tools.EmailSenderService;

/**
 *
 * @author leinier
 */
public class CrearInformacionPerfilView extends VerticalLayout {

    User user;
    AreaService areaService;
    GrupoService grupoService;
    EstudianteService estudianteService;
    TrabajadorService trabajadorService;
    EmailSenderService senderService;
    Select<String> selection;
    CrearEstudianteForm form_estudiante;
    CrearTrabajadorForm form_trabajador;
    Div container;
    Dialog dialog;

    public CrearInformacionPerfilView(
            User user,
            EstudianteService estudianteService,
            TrabajadorService trabajadorService,
            AreaService areaService,
            GrupoService grupoService,
            EmailSenderService senderService,
            Dialog dialog
    ) {
        this.user = user;
        this.estudianteService = estudianteService;
        this.trabajadorService = trabajadorService;
        this.areaService = areaService;
        this.grupoService = grupoService;
        this.senderService = senderService;
        this.dialog = dialog;

        Configuracion();
        selection.addValueChangeListener(event -> {
            if (event.getValue() == "Estudiante") {
                container.removeAll();
                addEstudiante();
                container.add(form_estudiante);
                add(container);
            } else if (event.getValue() == "Trabajador") {
                container.removeAll();
                addTrabajador();
                container.add(form_trabajador);
                add(container);
            }
        });
        add(selection);
    }

    private void Configuracion() {

        form_estudiante = new CrearEstudianteForm(grupoService.findAll(), user, senderService);
        form_estudiante.addListener(CrearEstudianteForm.SaveEvent.class, this::saveEstudiante);
        form_estudiante.addListener(CrearEstudianteForm.CloseEvent.class, e -> closeEstudianteEditor());
        form_trabajador = new CrearTrabajadorForm(areaService.findAll(), user, senderService);
        form_trabajador.addListener(CrearTrabajadorForm.SaveEvent.class, this::saveTrabajador);
        form_trabajador.addListener(CrearTrabajadorForm.CloseEvent.class, e -> closeTrabajadorEditor());
        container = new Div();
        container.addClassName("container-form");

        selection = new Select<>();
        selection.setLabel("Categoría");
        selection.setPlaceholder("Seleccione una categoría ");
        selection.setItems("Estudiante", "Trabajador");
    }

    //Estudiante
    //save
    private void saveEstudiante(CrearEstudianteForm.SaveEvent event) {

        List<Estudiante> listEstudiante = new LinkedList<>();
        boolean band = false;
        for (int i = 0; i < estudianteService.findAll().size() && band == false; i++) {
            Estudiante est = estudianteService.findAll().get(i);
            if (event.getEstudiante().getEmail().equals(est.getEmail())
                    && event.getEstudiante().getSolapin().equals(est.getSolapin())
                    && event.getEstudiante().getAnno_academico().equals(est.getAnno_academico())
                    && event.getEstudiante().getGrupo().equals(est.getGrupo())
                    && event.getEstudiante().getFacultad().equals(est.getFacultad())
                    && event.getEstudiante().getUser().equals(est.getUser())) {
                listEstudiante.add(est);
                band = true;
            }
        }

        if (listEstudiante.size() > 0) {
            Notification notification = Notification.show(
                    "El usuario ya existe",
                    2000,
                    Notification.Position.MIDDLE
            );
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        } else {
            estudianteService.save(event.getEstudiante());
            Notification notification = Notification.show(
                    "registro exitoso",
                    2000,
                    Notification.Position.BOTTOM_START
            );
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            closeEstudianteEditor();
        }
    }

    //edit
    public void editEstudiante(Estudiante estudiante) {
        if (estudiante == null) {
            closeEstudianteEditor();
        } else {
            form_estudiante.setEstudiante(estudiante);
            form_estudiante.setVisible(true);
            addClassName("editing");
        }
    }

    //add
    void addEstudiante() {
        editEstudiante(new Estudiante());
    }

    //close
    private void closeEstudianteEditor() {
        dialog.close();
        form_estudiante.setEstudiante(new Estudiante());
        form_estudiante.setVisible(false);
        removeClassName("editing");
    }

    //Trabajador
    //save
    private void saveTrabajador(CrearTrabajadorForm.SaveEvent event) {

        List<Trabajador> listTrabajadores = new LinkedList<>();
        boolean band= false;
        for (int i = 0; i < trabajadorService.findAll().size() && band == false; i++) {
            Trabajador trab = trabajadorService.findAll().get(i);
            if (event.getTrabajador().getEmail().equals(trab.getEmail())
                    && event.getTrabajador().getSolapin().equals(trab.getSolapin())
                    && event.getTrabajador().getCategoria().equals(trab.getCategoria())
                    && event.getTrabajador().getArea().equals(trab.getArea())
                    && event.getTrabajador().getUser().equals(trab.getUser())) {
                    listTrabajadores.add(trab);
                    band = true;
            }
        }

        if (listTrabajadores.size() > 0) {
            Notification notification = Notification.show(
                    "El usuario ya existe",
                    2000,
                    Notification.Position.MIDDLE
            );
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        } else {
            trabajadorService.save(event.getTrabajador());
            Notification notification = Notification.show(
                    "registro exitoso",
                    2000,
                    Notification.Position.BOTTOM_START
            );
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            closeTrabajadorEditor();
        }
    }

    //edit
    public void editTrabajador(Trabajador trabajador) {
        if (trabajador == null) {
            closeTrabajadorEditor();
        } else {
            form_trabajador.setTrabajador(trabajador);
            form_trabajador.setVisible(true);
            addClassName("editing");
        }
    }

    //add
    void addTrabajador() {
        editTrabajador(new Trabajador());
    }

    //close
    private void closeTrabajadorEditor() {
        dialog.close();
        form_trabajador.setTrabajador(new Trabajador());
        form_trabajador.setVisible(false);
        removeClassName("editing");
    }

}
