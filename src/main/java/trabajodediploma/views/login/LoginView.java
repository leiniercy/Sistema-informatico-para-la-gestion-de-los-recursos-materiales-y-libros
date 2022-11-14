package trabajodediploma.views.login;

import com.vaadin.flow.component.UI;
import trabajodediploma.views.login.crear_usuario.CrearUsuarioView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import https.autenticacion2_uci_cu.v7.AutenticarUsuarioResponse;
import java.util.Collections;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import trabajodediploma.data.Rol;
import trabajodediploma.data.entity.User;
import trabajodediploma.data.service.UserService;
import trabajodediploma.data.tools.EmailSenderService;
import trabajodediploma.data.tools.serviciosUCI.ClienteAutenticacionUCIWSDL;
import trabajodediploma.security.AuthenticatedUser;

@PageTitle("Login")
@Route(value = "login")
public class LoginView extends Div implements BeforeEnterObserver {

    private UserService userService;
    private PasswordEncoder passwordEncoder;
    private LoginOverlay loginOverlay;
    private Dialog dialog;
    private CrearUsuarioView crearUsuario;
    private Div header;

    Binder<User> binder = new Binder<>(User.class);
    private AuthenticatedUser authenticatedUser;
    private ClienteAutenticacionUCIWSDL autenticacionUCIWSDL;
    private String usuario = new String();
    private String clave = new String();

    public LoginView(
            @Autowired UserService userService,
            @Autowired PasswordEncoder passwordEncoder,
            @Autowired AuthenticatedUser authenticatedUser,
            @Autowired ClienteAutenticacionUCIWSDL autenticacionUCIWSDL
    ) {
        addClassName("login-view");
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.authenticatedUser = authenticatedUser;
        this.autenticacionUCIWSDL = autenticacionUCIWSDL;
        Configuracion();

        LoginI18n i18n = LoginI18n.createDefault();

        i18n.setHeader(new LoginI18n.Header());
        i18n.getHeader().setTitle("GENIUS");
        i18n.getHeader().setDescription("Sistema Informático para la gestión de información de los recursos materiales y libros en la facultad 4.");

        i18n.setAdditionalInformation(null);

        i18n.getForm().setTitle("");
        i18n.getForm().setUsername("Usuario:");
        i18n.getForm().setPassword("Contraseña:");
        i18n.getForm().setForgotPassword("Registrarse");
        i18n.getForm().setSubmit("Iniciar sesión");

        i18n.getErrorMessage().setTitle("Error:");
        i18n.getErrorMessage().setMessage("Usuario o contraseña incorrectos");

        loginOverlay.setI18n(i18n);
        loginOverlay.addForgotPasswordListener(event -> {
            dialog.open();
        });
        loginOverlay.setForgotPasswordButtonVisible(true);
        loginOverlay.setOpened(true);
        add(loginOverlay);

        loginOverlay.addLoginListener(event -> {
            AutenticarUsuarioResponse datos = autenticacionUCIWSDL.autenticar(event.getUsername(), event.getPassword());
            if (datos.getPersona().isAutenticado()) {
                User user = userService.findByUsername(event.getUsername());
                if (user.getId() != null) {
                    if (!user.getConfirmPassword().equals(event.getPassword())) {
                        user.setHashedPassword(passwordEncoder.encode(event.getPassword()));
                        user.setConfirmPassword(event.getPassword());
                        binder.writeBeanIfValid(user);
                        userService.save(user);
                        binder.readBean(new User());
                        loginOverlay.setOpened(false);
                    }
                } else {
                    User newUser = new User();
                    newUser.setName(datos.getPersona().getNombres() + " " + datos.getPersona().getApellidos());
                    newUser.setUsername(event.getUsername());
                    newUser.setHashedPassword(passwordEncoder.encode(event.getPassword()));
                    newUser.setConfirmPassword(event.getPassword());
                    newUser.setRoles(Collections.singleton(Rol.USER));
                    binder.writeBeanIfValid(newUser);
                    userService.save(newUser);
                    binder.readBean(new User());
                    loginOverlay.setOpened(false);
                }
            }
        });

    }

    private void Configuracion() {

        dialog = new Dialog();
        crearUsuario = new CrearUsuarioView(userService, passwordEncoder, dialog);
        crearUsuario.addClassName("crear-usuario");
        loginOverlay = new LoginOverlay();
        loginOverlay.setAction("login");

        /*Header crear usuario*/
        Button closeButton = new Button(new Icon("lumo", "cross"), (e) -> dialog.close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        Span title = new Span("Crear usuario");
        Div titleDiv = new Div(title);
        titleDiv.addClassName("title-div");
        Div buttonDiv = new Div(closeButton);
        buttonDiv.addClassName("button-div");
        header = new Div(titleDiv, buttonDiv);
        header.addClassName("registrar-header");
        /*Fin -> Header crear usuario*/

        dialog.add(header, crearUsuario);

    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if (!loginOverlay.isOpened()) {
            // Already logged in
            //loginOverlay.setOpened(false);
            beforeEnterEvent.forwardTo("");
        }
        // inform the user about an authentication error
        else if (beforeEnterEvent.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("error")) {
            loginOverlay.setError(true);
        }
        
        
    }

}
