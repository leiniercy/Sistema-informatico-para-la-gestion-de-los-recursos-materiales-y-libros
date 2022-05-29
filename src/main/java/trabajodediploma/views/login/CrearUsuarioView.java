/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabajodediploma.views.login;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import trabajodediploma.data.entity.User;
import trabajodediploma.data.service.UserService;

/**
 *
 * @author leinier
 */
public class CrearUsuarioView extends VerticalLayout {

    Div container;
    CrearUsuarioForm form;
    UserService userService;

    public CrearUsuarioView(@Autowired UserService userService) {
        addClassName("crear-usuario-view");
        this.userService = userService;
        Configuracion();
        add(container);

    }

    private void Configuracion() {
        form = new CrearUsuarioForm();
        form.addListener(CrearUsuarioForm.SaveEvent.class, this::saveUser);
        container = new Div();
        container.addClassName("container-form");
        container.add(form);
    }
    
    private void saveUser(CrearUsuarioForm.SaveEvent event) {

        List<User> listUsuario = userService.findAll();

        listUsuario = listUsuario.parallelStream()
                .filter(user -> event.getUser().getUsername().equals(user.getUsername())
               )
                .collect(Collectors.toList());

        if (listUsuario.size() != 0) {
            Notification notification = Notification.show(
                    "El usuario ya existe",
                    5000,
                    Notification.Position.MIDDLE
            );
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        } else {
            
                userService.save(event.getUser());
                Notification notification = Notification.show(
                        "registro exitoso",
                        5000,
                        Notification.Position.BOTTOM_START
                );
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);            
    }
    
    }
    
//    public void editUser(User user) {
//        if (user == null) {
////            closeEditor();
//        } else {
//            form.setUser(user);
//            form.setVisible(true);
//            addClassName("editing");
//        }
//    }
//
//    void addUser() {
//        User u = new User();
//        u.setProfilePictureUrl("");
//        editUser(u);
//    }
//    
    
}
