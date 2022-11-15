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
import trabajodediploma.data.entity.Area;
import trabajodediploma.data.entity.Estudiante;
import trabajodediploma.data.entity.Grupo;
import trabajodediploma.data.entity.Trabajador;
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

    private Binder<User> binderUser = new Binder<>(User.class);
    private Binder<Grupo> binderGrupo = new Binder<>(Grupo.class);
    private Binder<Estudiante> binderEstudiante = new Binder<>(Estudiante.class);
    private Binder<Area> binderArea = new Binder<>(Area.class);
    private Binder<Trabajador> binderTrabajador = new Binder<>(Trabajador.class);

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
//            comprobando si la persona existe en la BD UCI
            if (datos.getPersona().isAutenticado()) {
                //si existe
                Persona persona = helloWorldClient.sayHello(event.getUsername(), "");
                services.ObtenerPersonaDadoUsuarioResponse p = datosUCIWSDL.obtenerPersonaDadoUsuario(event.getUsername());
//            comprobando si el usuario existe en la BD local
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
//                    comprobando si es etudiante o trabajador
                    if (persona.getCargo().getNombreCargo().equals("Estudiante")) {
                        //actualizando los datos del estudiante
                        modificarEstudiante(persona, p);
                    } else {
                        //actualizando los datos del trabajador
                        modificarTrabajador(persona, p);
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
//                    comprobando si es etudiante o trabajador
                    if (persona.getCargo().getNombreCargo().equals("Estudiante")) {
                        //Añadiendo estudiante
                        crearEstudiante(newUser, persona, p);
                    } else {
                        //Añadiendo Trabajador
                        crearTrabajador(newUser, persona, p);
                    }
                    loginOverlay.setOpened(false);
                }
            }
        });

    }

    private void crearEstudiante(User newUser, Persona persona, services.ObtenerPersonaDadoUsuarioResponse p) {
        //Añadiendo estudiante
        Estudiante estudiante = new Estudiante();
        estudiante.setUser(newUser);
        estudiante.setSolapin(p.getReturn().getValue().getCredencial().getValue());
        estudiante.setCi(p.getReturn().getValue().getCI().getValue());
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
    }

    private void modificarEstudiante(Persona persona, services.ObtenerPersonaDadoUsuarioResponse p) {
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
    }

    private void crearTrabajador(User newUser, Persona persona, services.ObtenerPersonaDadoUsuarioResponse p) {
        //Añadiendo Trabajador
        Trabajador trabajador = new Trabajador();
        trabajador.setUser(newUser);
        trabajador.setSolapin(p.getReturn().getValue().getCredencial().getValue());
        trabajador.setEmail(persona.getCorreo());
        trabajador.setCategoria(p.getReturn().getValue().getCategoria().getValue());
        trabajador.setCargo(p.getReturn().getValue().getCargo().getValue().getNombreCargo().getValue());
        trabajador.setCi(p.getReturn().getValue().getCI().getValue());
        Area area = areaService.findByNombre(p.getReturn().getValue().getArea().getValue().getNombreArea().getValue());
        if (area == null) {
            Area newArea = new Area();
            newArea.setNombre(p.getReturn().getValue().getArea().getValue().getNombreArea().getValue());
            binderArea.writeBeanIfValid(newArea);
            areaService.save(newArea);
            trabajador.setArea(newArea);
            binderArea.readBean(new Area());
        } else {
            trabajador.setArea(area);
        }
        binderTrabajador.writeBeanIfValid(trabajador);
        trabajadorService.save(trabajador);
        binderTrabajador.readBean(new Trabajador());
    }

    private void modificarTrabajador(Persona persona, services.ObtenerPersonaDadoUsuarioResponse p) {
        //actualizando los datos del trabajador
        Trabajador trabajador = trabajadorService.findBySolapin(p.getReturn().getValue().getCredencial().getValue());
        trabajador.setCategoria(p.getReturn().getValue().getCategoria().getValue());
        trabajador.setCargo(p.getReturn().getValue().getCargo().getValue().getNombreCargo().getValue());
        Area area = areaService.findByNombre(p.getReturn().getValue().getArea().getValue().getNombreArea().getValue());
        if (area == null) {
            Area newArea = new Area();
            newArea.setNombre(p.getReturn().getValue().getArea().getValue().getNombreArea().getValue());
            binderArea.writeBeanIfValid(newArea);
            areaService.save(newArea);
            trabajador.setArea(newArea);
            binderArea.readBean(new Area());
        } else {
            trabajador.setArea(area);
        }
        binderTrabajador.writeBeanIfValid(trabajador);
        trabajadorService.save(trabajador);
        binderTrabajador.readBean(new Trabajador());
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        // inform the user about an authentication error
        if (beforeEnterEvent.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("error")) {
            loginOverlay.setError(true);
        } else if (!loginOverlay.isOpened()) {
            // Already logged in
            //loginOverlay.setOpened(false);
            beforeEnterEvent.forwardTo("");
        }
    }

}
