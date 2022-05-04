package trabajodediploma.views.login;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Login")
@Route(value = "login")
public class LoginView extends Div implements BeforeEnterObserver {

    LoginOverlay loginOverlay;

    public LoginView() {
        addClassName("login-view");
        loginOverlay = new LoginOverlay();
        loginOverlay.setAction("login");

        LoginI18n i18n = LoginI18n.createDefault();
        
        i18n.setHeader(new LoginI18n.Header());
        i18n.getHeader().setTitle("SCDRM");
        i18n.getHeader().setDescription("Sistema de Control y Distribución de los Recursos Materiales");

        i18n.setAdditionalInformation(null);

        i18n.getForm().setTitle("");
        i18n.getForm().setUsername("Usuario:");
        i18n.getForm().setPassword("Contraseña:");
        // i18n.getForm().setForgotPassword("Registrarse");
        i18n.getForm().setSubmit("Iniciar sesión");

        i18n.getErrorMessage().setTitle("Error:");
        i18n.getErrorMessage().setMessage("Usuario o contraseña incorrectos");

        loginOverlay.setI18n(i18n);
        loginOverlay.setForgotPasswordButtonVisible(false);
        loginOverlay.setOpened(true);
        add(loginOverlay);
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
