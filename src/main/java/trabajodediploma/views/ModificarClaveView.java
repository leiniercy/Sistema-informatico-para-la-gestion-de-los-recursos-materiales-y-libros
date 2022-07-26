/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabajodediploma.views;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import java.util.List;
import java.util.stream.Collectors;
import trabajodediploma.data.entity.User;
import trabajodediploma.data.service.UserService;

/**
 *
 * @author leinier
 */
public class ModificarClaveView extends Div {

    User user;
    ModificarClaveForm form;
    UserService userService;

    public ModificarClaveView(User user,UserService userService) {
        addClassName("modificar-clave-view");
        this.user = user;
        this.userService = userService;
        Configuracion();
        editUser();
        add(form);
    }

    private void Configuracion() {
        form = new ModificarClaveForm(user);
        form.addListener(ModificarClaveForm.SaveEvent.class, this::saveUser);
        form.addListener(ModificarClaveForm.CloseEvent.class, e -> closeEditor());        
    }
    private void saveUser(ModificarClaveForm.SaveEvent event) {
            userService.save(event.getUser());
            Notification notification = Notification.show(
                    "clave modificada",
                    5000,
                    Notification.Position.BOTTOM_START
            );
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            closeEditor();
    }

    public void editUser() {
        if (user == null) {
            closeEditor();
        } else {
            form.setUser(user);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void closeEditor() {
        form.setUser(null);
        form.setVisible(false);
        removeClassName("editing");
       // dialog.close();
    }


}
