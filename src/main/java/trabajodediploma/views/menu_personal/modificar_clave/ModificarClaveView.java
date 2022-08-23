/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabajodediploma.views.menu_personal.modificar_clave;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import org.springframework.security.crypto.password.PasswordEncoder;
import trabajodediploma.data.entity.User;
import trabajodediploma.data.service.UserService;

/**
 *
 * @author leinier
 */
public class ModificarClaveView extends Div {

    User user;
    private PasswordEncoder passwordEncoder;
    ModificarClaveForm form;
    UserService userService;
    Dialog dialog;

    public ModificarClaveView(User user,UserService userService, PasswordEncoder passwordEncoder, Dialog dialog) {
        addClassName("modificar-clave-view");
        this.user = user;
        this.userService = userService;
        this.dialog = dialog;
        this.passwordEncoder = passwordEncoder;
        Configuracion();
        editUser();
        add(form);
    }

    private void Configuracion() {
        form = new ModificarClaveForm(user,passwordEncoder);
        form.addListener(ModificarClaveForm.SaveEvent.class, this::saveUser);
        form.addListener(ModificarClaveForm.CloseEvent.class, e -> closeEditor());        
    }
    private void saveUser(ModificarClaveForm.SaveEvent event) {
            userService.save(event.getUser());
            Notification notification = Notification.show(
                    "clave modificada",
                    2000,
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
        dialog.close();
    }


}
