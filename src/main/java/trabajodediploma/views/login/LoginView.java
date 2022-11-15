package trabajodediploma.views.login;

import com.vaadin.flow.component.UI;
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
import https.autenticacion2_uci_cu.v7.Persona;
import java.util.Collections;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import trabajodediploma.data.Rol;
import trabajodediploma.data.entity.Estudiante;
import trabajodediploma.data.entity.Grupo;
import trabajodediploma.data.entity.User;
import trabajodediploma.data.service.AreaService;
import trabajodediploma.data.service.EstudianteService;
import trabajodediploma.data.service.GrupoService;
import trabajodediploma.data.service.TrabajadorService;
import trabajodediploma.data.service.UserService;
import trabajodediploma.data.tools.EmailSenderService;
import trabajodediploma.data.tools.serviciosUCI.ClienteAutenticacionUCIWSDL;
import trabajodediploma.data.tools.serviciosUCI.ClienteDatosUCIWSDL;
import trabajodediploma.data.tools.serviciosUCI.HelloWorldClient;
import trabajodediploma.security.AuthenticatedUser;

@PageTitle("Login")
@Route(value = "login")
public class LoginView extends Div implements BeforeEnterObserver {

    private UserService userService;
    private PasswordEncoder passwordEncoder;
    private LoginOverlay loginOverlay;
    private Dialog dialog;
    private Div header;

    Binder<User> binderUser = new Binder<>(User.class);
    Binder<Grupo> binderGrupo = new Binder<>(Grupo.class);
    Binder<Estudiante> binderEstudiante = new Binder<>(Estudiante.class);
    private AuthenticatedUser authenticatedUser;
    private ClienteAutenticacionUCIWSDL autenticacionUCIWSDL;
    private ClienteDatosUCIWSDL datosUCIWSDL;
    private HelloWorldClient helloWorldClient;
    private EstudianteService estudianteService;
    private GrupoService grupoService;
    private TrabajadorService trabajadorService;
    private AreaService areaService;

    private String usuario = new String();
    private String clave = new String();

    public LoginView(
            @Autowired UserService userService,
            @Autowired PasswordEncoder passwordEncoder,
            @Autowired AuthenticatedUser authenticatedUser,
            @Autowired ClienteAutenticacionUCIWSDL autenticacionUCIWSDL,
            @Autowired ClienteDatosUCIWSDL datosUCIWSDL,
            @Autowired HelloWorldClient helloWorldClient,
            @Autowired EstudianteService estudianteService,
            @Autowired GrupoService grupoService,
            @Autowired TrabajadorService trabajadorService,
            @Autowired AreaService areaService
    ) {
        addClassName("login-view");
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.authenticatedUser = authenticatedUser;
        this.autenticacionUCIWSDL = autenticacionUCIWSDL;
        this.datosUCIWSDL = datosUCIWSDL;
        this.helloWorldClient = helloWorldClient;
        this.estudianteService = estudianteService;
        this.grupoService = grupoService;
        this.trabajadorService = trabajadorService;
        this.areaService = areaService;
        
        loginOverlay = new LoginOverlay();
        loginOverlay.setAction("login");
//        Configuracion();

        LoginI18n i18n = LoginI18n.createDefault();

        i18n.setHeader(new LoginI18n.Header());
        i18n.getHeader().setTitle("GENIUS");
        i18n.getHeader().setDescription("Sistema Informático para la gestión de información de los recursos materiales y libros en la facultad 4.");

        i18n.setAdditionalInformation(null);

        i18n.getForm().setTitle("");
        i18n.getForm().setUsername("Usuario:");
        i18n.getForm().setPassword("Contraseña:");
        i18n.getForm().setForgotPassword("");
        i18n.getForm().setSubmit("Iniciar sesión");

        i18n.getErrorMessage().setTitle("Error:");
        i18n.getErrorMessage().setMessage("Usuario o contraseña incorrectos");

        loginOverlay.setI18n(i18n);
        loginOverlay.setForgotPasswordButtonVisible(true);
        loginOverlay.setOpened(true);
        add(loginOverlay);

        loginOverlay.addLoginListener(event -> {
            AutenticarUsuarioResponse datos = autenticacionUCIWSDL.autenticar(event.getUsername(), event.getPassword());
            if (datos.getPersona().isAutenticado()) {
                Persona persona = helloWorldClient.sayHello(event.getUsername(), "");
                services.ObtenerPersonaDadoUsuarioResponse p = datosUCIWSDL.obtenerPersonaDadoUsuario(event.getUsername());
                User user = userService.findByUsername(event.getUsername());
                if (user != null) {
                    //si existe
                    //actualizando los datos del usuario
                    user.setHashedPassword(passwordEncoder.encode(event.getPassword()));
                    user.setConfirmPassword(event.getPassword());
//                    user.setProfilePictureUrl(persona.getFoto().getUrlFoto());
                    binderUser.writeBeanIfValid(user);
                    userService.save(user);
                    binderUser.readBean(new User());
                    if (persona.getCargo().getNombreCargo().equals("Estudiante")) {
                        //actualizando los datos del estudiante
                        Estudiante estudiante = estudianteService.findBySolapin(p.getReturn().getValue().getCredencial().getValue());
                        Grupo grupo = grupoService.findByNumero(p.getReturn().getValue().getArea().getValue().getNombreArea().getValue());
                        if (grupo == null) {
                            Grupo newGrupo = new Grupo();
                            newGrupo.setNumero(p.getReturn().getValue().getArea().getValue().getNombreArea().getValue());
                            binderGrupo.writeBeanIfValid(newGrupo);
                            grupoService.save(newGrupo);
                            estudiante.setGrupo(newGrupo);
                            binderGrupo.readBean(new Grupo());
                        } else {
                            estudiante.setGrupo(grupo);
                        }
                        String anno_academico = p.getReturn().getValue().getArea().getValue().getNombreArea().getValue().charAt(4) + "";
                        try {
                            int number = Integer.parseInt(anno_academico);
                            estudiante.setAnno_academico(number);
                        } catch (NumberFormatException ex) {
                            ex.printStackTrace();
                        }
                        estudiante.setFacultad(persona.getArea().getNombreArea());
                        binderEstudiante.writeBeanIfValid(estudiante);
                        estudianteService.save(estudiante);
                        binderEstudiante.readBean(new Estudiante());
                    } else {
                        //actualizando los datos del trabajador
                    }
                    loginOverlay.setOpened(false);
                } else {
//                    si no existe
//                    creando usuario
                    User newUser = new User();
                    newUser.setName(datos.getPersona().getNombres() + " " + datos.getPersona().getApellidos());
                    newUser.setUsername(event.getUsername());
//                    newUser.setProfilePictureUrl(persona.getFoto().getUrlFoto());
                    newUser.setHashedPassword(passwordEncoder.encode(event.getPassword()));
                    newUser.setConfirmPassword(event.getPassword());
                    newUser.setRoles(Collections.singleton(Rol.USER));
                    binderUser.writeBeanIfValid(newUser);
                    userService.save(newUser);
                    binderUser.readBean(new User());

                    if (persona.getCargo().getNombreCargo().equals("Estudiante")) {
                        //Añadiendo estudiante
                        Estudiante estudiante = new Estudiante();
                        estudiante.setUser(newUser);
                        estudiante.setSolapin(p.getReturn().getValue().getCredencial().getValue());
                        estudiante.setEmail(persona.getCorreo());
                        String anno_academico = p.getReturn().getValue().getArea().getValue().getNombreArea().getValue().charAt(4) + "";
                        try {
                            int number = Integer.parseInt(anno_academico);
                            estudiante.setAnno_academico(number);
                        } catch (NumberFormatException ex) {
                            ex.printStackTrace();
                        }
                        Grupo grupo = grupoService.findByNumero(p.getReturn().getValue().getArea().getValue().getNombreArea().getValue());
                        if (grupo == null) {
                            Grupo newGrupo = new Grupo();
                            newGrupo.setNumero(p.getReturn().getValue().getArea().getValue().getNombreArea().getValue());
                            binderGrupo.writeBeanIfValid(newGrupo);
                            grupoService.save(newGrupo);
                            estudiante.setGrupo(newGrupo);
                            binderGrupo.readBean(new Grupo());
                        } else {
                            estudiante.setGrupo(grupo);
                        }
                        estudiante.setFacultad(persona.getArea().getNombreArea());
                        binderEstudiante.writeBeanIfValid(estudiante);
                        estudianteService.save(estudiante);
                        binderEstudiante.readBean(new Estudiante());
                    } else {
                        //Añadiendo Trabajador
                    }
                    loginOverlay.setOpened(false);
                }
            }
        });

    }
    
    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if (!loginOverlay.isOpened()) {
            // Already logged in
            //loginOverlay.setOpened(false);
            beforeEnterEvent.forwardTo("");
        } // inform the user about an authentication error
        else if (beforeEnterEvent.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("error")) {
            loginOverlay.setError(true);
        }

    }

}
