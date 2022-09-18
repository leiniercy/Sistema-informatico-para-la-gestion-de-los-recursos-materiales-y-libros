package trabajodediploma.views;

import trabajodediploma.views.menu_personal.modificar_clave.ModificarClaveView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Nav;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import trabajodediploma.data.entity.Estudiante;
import trabajodediploma.data.entity.Trabajador;
import trabajodediploma.data.entity.User;
import trabajodediploma.data.service.AreaService;
import trabajodediploma.data.service.EstudianteService;
import trabajodediploma.data.service.GrupoService;
import trabajodediploma.data.service.TrabajadorService;
import trabajodediploma.data.service.UserService;
import trabajodediploma.data.tools.EmailSenderService;
import trabajodediploma.security.AuthenticatedUser;
import trabajodediploma.views.catalogo.CatalogoView;
import trabajodediploma.views.inicio.InicioView;
import trabajodediploma.views.libros.LibroView;
import trabajodediploma.views.libros.estadisticas.EstadisticasView;
import trabajodediploma.views.menu_personal.modificar_perfil.ModificarPerfilView;
import trabajodediploma.views.modeoPago.ModeloPagoView;
import trabajodediploma.views.modulo.ModuloView;
import trabajodediploma.views.usuarios.UsuarioView;
import trabajodediploma.views.recursosmateriales.RecursosMaterialesView;
import trabajodediploma.views.tarjetaDestinoFinal.TarjetaDestinoFinalView;
import trabajodediploma.views.tarjeta_personal_prestamo.TarjetaPersonalPrestamoView;
import trabajodediploma.views.tarjetaprestamo.TarjetaPrestamoView;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {

    private AuthenticatedUser authenticatedUser;
    private AccessAnnotationChecker accessChecker;
    private UserService userService;
    private EstudianteService estudianteService;
    private TrabajadorService trabajadorService;
    private GrupoService grupoService;
    private AreaService areaService;
    private EmailSenderService senderService;
    private PasswordEncoder passwordEncoder;
    private Div titleDiv;
    private Dialog modificarPerfil;
    private Dialog modificarClave;
    private User user;
    private Div header;

    /**
     * A simple navigation item component, based on ListItem element.
     */
    public static class MenuItemInfo extends ListItem {

        private final Class<? extends Component> view;

        public MenuItemInfo(String menuTitle, String iconClass, Class<? extends Component> view) {
            this.view = view;
            RouterLink link = new RouterLink();
            link.addClassNames("menu-item-link");
            link.setRoute(view);

            Span text = new Span(menuTitle);
            text.addClassNames("menu-item-text");

            link.add(new LineAwesomeIcon(iconClass), text);
            add(link);
        }

        public Class<?> getView() {
            return view;
        }

        /**
         * Simple wrapper to create icons using LineAwesome iconset. See
         * https://icons8.com/line-awesome
         */
        @NpmPackage(value = "line-awesome", version = "1.3.0")
        public static class LineAwesomeIcon extends Span {

            public LineAwesomeIcon(String lineawesomeClassnames) {
                addClassNames("menu-item-icon");
                if (!lineawesomeClassnames.isEmpty()) {
                    addClassNames(lineawesomeClassnames);
                }
            }
        }

    }

    public MainLayout(
            AuthenticatedUser authenticatedUser,
            AccessAnnotationChecker accessChecker,
            @Autowired UserService userService,
            @Autowired EstudianteService estudianteService,
            @Autowired TrabajadorService trabajadorService,
            @Autowired GrupoService grupoService,
            @Autowired AreaService areaService,
            @Autowired PasswordEncoder passwordEncoder,
            @Autowired EmailSenderService senderService) {
        this.authenticatedUser = authenticatedUser;
        this.accessChecker = accessChecker;
        this.userService = userService;
        this.estudianteService = estudianteService;
        this.trabajadorService = trabajadorService;
        this.grupoService = grupoService;
        this.areaService = areaService;
        this.senderService = senderService;
        this.passwordEncoder = passwordEncoder;
        setPrimarySection(Section.DRAWER);
        addToNavbar(true, createHeaderContent());
        addToDrawer(createDrawerContent());
    }

    // barra de menu
    private Component createHeaderContent() {

        DrawerToggle toggle = new DrawerToggle();
        toggle.addClassNames("toggle", "text-secondary");
        toggle.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        toggle.getElement().setAttribute("aria-label", "Menu toggle");
        Image iconPage = new Image("images/pageLogo.png", "Logo");
        titleDiv = new Div(toggle, iconPage);
        titleDiv.addClassName("div-toggle-title");

        Div layout = new Div();
        layout.addClassNames("flex", "items-center", "my-s", "px-m", "py-xs");
        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            user = maybeUser.get();
            
            Avatar avatar = new Avatar(user.getUsername(), user.getProfilePictureUrl());
            avatar.addClassNames("avatar", "me-xs");
                        
            ContextMenu userMenu = new ContextMenu(avatar);
            userMenu.setOpenOnClick(true);
            userMenu.addItem("Perfil", e -> {
                ModificarUsuario();
            });
            userMenu.addItem("Cambiar clave", e -> {
                ModificarClave();
            });
            userMenu.addItem("Cerrar sesión", e -> {
                authenticatedUser.logout();
            });

            Span name = new Span(user.getName());
            name.addClassNames("span-name", "font-medium", "text-s", "text-secondary");
            layout.add(avatar, name);
            header = new Div(titleDiv, layout);
        } else {

            Anchor loginLink = new Anchor(/* "login", "Sign in" */);
            loginLink.addClassName("login__link");
            loginLink.setHref("login");
            loginLink.add("Acceder");
            header = new Div(titleDiv, loginLink);
        }
        header.addClassNames("div-header");
        return header;
    }

    // barra de menu desplegable lateral izquierda
    private Component createDrawerContent() {
        H2 appName = new H2("Menú");
        appName.addClassNames("app-name");

        com.vaadin.flow.component.html.Section section = new com.vaadin.flow.component.html.Section(appName,
                createNavigation());
        section.addClassNames("drawer-section");
        return section;
    }

    //
    private Nav createNavigation() {
        Nav nav = new Nav();
        nav.addClassNames("menu-item-container");
        nav.getElement().setAttribute("aria-labelledby", "views");
        // Wrap the links in a list; improves accessibility
        UnorderedList list = new UnorderedList();
        list.addClassNames("navigation-list");
        nav.add(list);
        for (MenuItemInfo menuItem : createMenuItems()) {
            if (accessChecker.hasAccess(menuItem.getView())) {
                list.add(menuItem);
            }

        }
        return nav;
    }

    //
    private MenuItemInfo[] createMenuItems() {
        return new MenuItemInfo[] { //
                new MenuItemInfo("Inicio", "la la-home", InicioView.class), //

                new MenuItemInfo("Catálogo", "la la-th-list", CatalogoView.class), //

                new MenuItemInfo("Tarjeta Personal", "la la-columns", TarjetaPersonalPrestamoView.class), //

                new MenuItemInfo("Usuario", "la la-user", UsuarioView.class), //

                new MenuItemInfo("Estadística", "la la-chart-bar", EstadisticasView.class), //

                new MenuItemInfo("Libros", "la la-book", LibroView.class), //

                new MenuItemInfo("Recursos Materiales ", "la la-tools", RecursosMaterialesView.class), //

                new MenuItemInfo("Tarjeta Prestamo", "la la-columns", TarjetaPrestamoView.class), //

                new MenuItemInfo("Modelo de Pago", "la la-columns", ModeloPagoView.class), //
                
                new MenuItemInfo("Modulo", "la la-gift", ModuloView.class), //

                new MenuItemInfo("Destino Final", "la la-user-check", TarjetaDestinoFinalView.class), //
        };
    }

    //
    private void ModificarUsuario() {
        modificarPerfil = new Dialog();
        ModificarPerfilView modificarPerfilView = new ModificarPerfilView(user, userService, estudianteService,
                trabajadorService, grupoService, areaService, senderService, modificarPerfil);
        modificarPerfil.add(modificarPerfilView);

        List<Estudiante> estudiantes = estudianteService.findAll();
        estudiantes = estudiantes.stream().filter(est -> est.getUser().equals(user)).collect(Collectors.toList());
        List<Trabajador> trabajadores = trabajadorService.findAll();
        trabajadores = trabajadores.stream().filter(trab -> trab.getUser().equals(user)).collect(Collectors.toList());

        if (estudiantes.size() == 0 && trabajadores.size() == 0) {
            Notification notification = Notification.show(
                    "Información de perfil no disponible",
                    2000,
                    Notification.Position.MIDDLE);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        } else {
            modificarPerfil.open();
        }
    }

    //
    private void ModificarClave() {
        modificarClave = new Dialog();
        ModificarClaveView claveView = new ModificarClaveView(user, userService, passwordEncoder, modificarClave);
        modificarClave.add(claveView);
        modificarClave.open();
    }

}
