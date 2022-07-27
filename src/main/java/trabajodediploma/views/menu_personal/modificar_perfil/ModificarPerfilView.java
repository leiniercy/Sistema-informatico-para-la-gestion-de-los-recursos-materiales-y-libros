/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabajodediploma.views.menu_personal.modificar_perfil;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import java.util.List;
import java.util.stream.Collectors;
import trabajodediploma.data.entity.Estudiante;
import trabajodediploma.data.entity.Trabajador;
import trabajodediploma.data.entity.User;
import trabajodediploma.data.service.AreaService;
import trabajodediploma.data.service.EstudianteService;
import trabajodediploma.data.service.GrupoService;
import trabajodediploma.data.service.TrabajadorService;
import trabajodediploma.data.service.UserService;

/**
 *
 * @author leinier
 */
public class ModificarPerfilView extends Div {

    private User user;
    private UserService userService;
    private EstudianteService estudianteService;
    private TrabajadorService trabajadorService;
    private GrupoService grupoService;
    private AreaService areaService;
    private Dialog dialog;
    private List<Estudiante> estudiantes;
    private List<Trabajador> trabajadores;
    private Div container;

    ModificarPerfilEstudianteForm estudianteForm;
    ModificarPerfilTrabajadorForm trabajadorForm;

    public ModificarPerfilView(
            User user,
            UserService userService,
            EstudianteService estudianteService,
            TrabajadorService trabajadorService,
            GrupoService grupoService,
            AreaService areaService,
            Dialog dialog
    ) {
        this.user = user;
        this.userService = userService;
        this.estudianteService = estudianteService;
        this.trabajadorService = trabajadorService;
        this.grupoService = grupoService;
        this.areaService = areaService;
        this.dialog = dialog;
        Configuration();
        add(container);
    }

    private void Configuration() {

        container = new Div();
        container.addClassName("container-form");

        estudiantes = estudianteService.findAll();
        estudiantes = estudiantes.stream().filter(est -> est.getUser().equals(user)).collect(Collectors.toList());
        trabajadores = trabajadorService.findAll();
        trabajadores = trabajadores.stream().filter(trab -> trab.getUser().equals(user)).collect(Collectors.toList());

        if (estudiantes.size() != 0) {
            container.removeAll();
            Estudiante est = estudiantes.get(0);
            estudianteForm = new ModificarPerfilEstudianteForm(grupoService.findAll(), user, est);
            estudianteForm.addListener(ModificarPerfilEstudianteForm.SaveEvent.class, this::saveEstudiante);
            estudianteForm.addListener(ModificarPerfilEstudianteForm.CloseEvent.class, e -> closeEstudianteEditor());
            editEstudiante(est, user);
            container.add(estudianteForm);
        } else if (trabajadores.size() != 0) {
            container.removeAll();
            Trabajador trab = trabajadores.get(0);
            trabajadorForm = new ModificarPerfilTrabajadorForm(areaService.findAll(), user, trab);
            trabajadorForm.addListener(ModificarPerfilTrabajadorForm.SaveEvent.class, this::saveTrabajador);
            trabajadorForm.addListener(ModificarPerfilTrabajadorForm.CloseEvent.class, e -> closeTrabajadorEditor());
            editTrabajador(trab,user);
            container.add(trabajadorForm);
        }
    }

    //Estudiante
    //save
    private void saveEstudiante(ModificarPerfilEstudianteForm.SaveEvent event) {

        List<Estudiante> listEstudiante = estudianteService.findAll();

        listEstudiante = listEstudiante.parallelStream()
                .filter(est -> event.getEstudiante().getEmail().equals(est.getEmail())
                && event.getEstudiante().getSolapin().equals(est.getSolapin())
                && event.getEstudiante().getAnno_academico().equals(est.getAnno_academico())
                && event.getEstudiante().getGrupo().equals(est.getGrupo())
                && event.getEstudiante().getFacultad().equals(est.getFacultad())
                && event.getUser().equals(est.getUser())
                )
                .collect(Collectors.toList());

        List<User> listUsuario = userService.findAll();

        listUsuario = listUsuario.parallelStream()
                .filter(user -> event.getUser().getUsername().equals(user.getUsername())
                && event.getUser().getName().equals(user.getName())
                )
                .collect(Collectors.toList());

        if (listEstudiante.size() != 0 || listUsuario.size() != 0) {
            Notification notification = Notification.show(
                    "Debe modificar al menos un campo",
                    5000,
                    Notification.Position.MIDDLE
            );
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        } else {
            estudianteService.update(event.getEstudiante());
            userService.update(event.getUser());
            Notification notification = Notification.show(
                    "Su información de perfil ha sido modificada",
                    5000,
                    Notification.Position.BOTTOM_START
            );
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            closeEstudianteEditor();
        }
    }

    //edit
    public void editEstudiante(Estudiante estudiante, User user) {
        if (estudiante == null) {
            closeEstudianteEditor();
        } else {
            estudianteForm.setEstudiante(estudiante, user);
            estudianteForm.setVisible(true);
            addClassName("editing");
        }
    }

    //close
    private void closeEstudianteEditor() {
        estudianteForm.setEstudiante(null, null);
        estudianteForm.setVisible(false);
        removeClassName("editing");
        dialog.close();
    }

    //Trabajador
    //save
    private void saveTrabajador(ModificarPerfilTrabajadorForm.SaveEvent event) {

        List<Trabajador> listTrabajadores = trabajadorService.findAll();

        listTrabajadores = listTrabajadores.parallelStream()
                .filter(trab -> event.getTrabajador().getEmail().equals(trab.getEmail())
                && event.getTrabajador().getSolapin().equals(trab.getSolapin())
                && event.getTrabajador().getCategoria().equals(trab.getCategoria())
                && event.getTrabajador().getArea().equals(trab.getArea())
                && event.getTrabajador().getUser().equals(trab.getUser())
                )
                .collect(Collectors.toList());

        List<User> listUsuario = userService.findAll();

        listUsuario = listUsuario.parallelStream()
                .filter(user -> event.getUser().getUsername().equals(user.getUsername())
                && event.getUser().getName().equals(user.getName())
                )
                .collect(Collectors.toList());

        if (listTrabajadores.size() != 0 || listUsuario.size() != 0) {
            Notification notification = Notification.show(
                    "Debe modificar al menos un campo",
                    5000,
                    Notification.Position.MIDDLE
            );
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        } else {
            userService.update(event.getUser());
            trabajadorService.update(event.getTrabajador());
            Notification notification = Notification.show(
                    "Su información de perfil ha sido modificada",
                    5000,
                    Notification.Position.BOTTOM_START
            );
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            closeTrabajadorEditor();
        }
    }

    //edit
    public void editTrabajador(Trabajador trabajador, User user) {
        if (trabajador == null) {
            closeTrabajadorEditor();
        } else {
            trabajadorForm.setTrabajador(trabajador,user);
            trabajadorForm.setVisible(true);
            addClassName("editing");
        }
    }

    //close
    private void closeTrabajadorEditor() {
        trabajadorForm.setTrabajador(null,null);
        trabajadorForm.setVisible(false);
        removeClassName("editing");
        dialog.close();
    }

}
