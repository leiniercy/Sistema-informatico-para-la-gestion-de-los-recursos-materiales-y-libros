package trabajodediploma.views.login;

import trabajodediploma.views.login.crear_usuario.CrearUsuarioView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import trabajodediploma.data.service.UserService;
import trabajodediploma.data.tools.EmailSenderService;

@PageTitle("Login")
@Route(value = "login")
public class LoginView extends Div implements BeforeEnterObserver {

    UserService userService;
    PasswordEncoder passwordEncoder;
    LoginOverlay loginOverlay;
    Dialog dialog;
    CrearUsuarioView crearUsuario;
    Div header;

    public LoginView(
            @Autowired UserService userService,
            @Autowired PasswordEncoder passwordEncoder
    ) {
        addClassName("login-view");
        this.userService = userService;
        this.passwordEncoder  = passwordEncoder;
        Configuracion();

        LoginI18n i18n = LoginI18n.createDefault();

        i18n.setHeader(new LoginI18n.Header());
        i18n.getHeader().setTitle("GENIUS");
        i18n.getHeader().setDescription("Sistema Informático para la gestión de la información de los recursos materiales y libros en la facultad 4.");

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
    }

    private void Configuracion() {

        dialog = new Dialog();
        crearUsuario = new CrearUsuarioView(userService, passwordEncoder,dialog);
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
        // inform the user about an authentication error
        if (beforeEnterEvent.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("error")) {
            loginOverlay.setError(true);
        }
    }

}
