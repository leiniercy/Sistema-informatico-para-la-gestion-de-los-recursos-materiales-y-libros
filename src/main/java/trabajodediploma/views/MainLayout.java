package trabajodediploma.views;

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
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import java.util.Optional;
import trabajodediploma.data.entity.Estudiante;
import trabajodediploma.data.entity.Trabajador;
import trabajodediploma.data.entity.User;
import trabajodediploma.security.AuthenticatedUser;
import trabajodediploma.views.catalogo.CatalogoView;
import trabajodediploma.views.destinofinal.DestinoFinalView;
import trabajodediploma.views.inicio.InicioView;
import trabajodediploma.views.libros.LibroView;
import trabajodediploma.views.login.CrearEstudianteForm;
import trabajodediploma.views.login.CrearTrabajadorForm;
import trabajodediploma.views.modulo.ModuloView;
import trabajodediploma.views.recursosmateriales.RecursosMaterialesView;
import trabajodediploma.views.tarjetaprestamo.TarjetaPrestamoView;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {

    private Div titleDiv;
    private Dialog modificarPerfil;
    private Dialog modificarClave;
    private CrearEstudianteForm crearEstudianteForm;
    private CrearTrabajadorForm craCrearTrabajadorForm;
    private User user;

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

    private H1 viewTitle;

    private AuthenticatedUser authenticatedUser;
    private AccessAnnotationChecker accessChecker;

    public MainLayout(AuthenticatedUser authenticatedUser, AccessAnnotationChecker accessChecker) {
        this.authenticatedUser = authenticatedUser;
        this.accessChecker = accessChecker;

        setPrimarySection(Section.DRAWER);
        addToNavbar(true, createHeaderContent());
        addToDrawer(createDrawerContent());
    }

    private Component createHeaderContent() {
        
        DrawerToggle toggle = new DrawerToggle();
        toggle.addClassNames("toggle","text-secondary");
        toggle.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        toggle.getElement().setAttribute("aria-label", "Menu toggle");
        
        viewTitle = new H1();
        viewTitle.addClassNames("h1-title","m-0", "text-l");
        viewTitle.getStyle()
                .set("font-size", "var(--lumo-font-size-l)")
                .set("margin", "0");
        
        titleDiv = new Div(toggle,viewTitle);
        titleDiv.addClassName("div-toggle-title");
        
        Div layout = new Div();
        layout.addClassNames("flex", "items-center", "my-s", "px-m", "py-xs");
        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            user = maybeUser.get();

            Avatar avatar = new Avatar(user.getUsername(), user.getProfilePictureUrl());
            avatar.addClassNames("avatar","me-xs");

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
            name.addClassNames("span-name","font-medium", "text-s", "text-secondary");
            layout.add(avatar, name);

        } else {

            Anchor loginLink = new Anchor(/*"login", "Sign in"*/);
            loginLink.setHref("login");
            loginLink.add(new Span("Acceder"));
            layout.add(loginLink);
            layout.addClassNames("bg-base", "border-b", "border-contrast-10", "box-border","rounded-l");
        }

        Div header = new Div(titleDiv, layout);
        header.addClassNames("div-header","bg-primary");
        return header;
    }

    private Component createDrawerContent() {
        H2 appName = new H2("Menú");
        appName.addClassNames("app-name");

        com.vaadin.flow.component.html.Section section = new com.vaadin.flow.component.html.Section(appName,
                createNavigation());
        section.addClassNames("drawer-section");
        return section;
    }

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

    private MenuItemInfo[] createMenuItems() {
        return new MenuItemInfo[]{ //
            new MenuItemInfo("Inicio", "la la-home", InicioView.class), //

            new MenuItemInfo("Catálogo", "la la-th-list", CatalogoView.class), //

            new MenuItemInfo("Libros", "la la-book", LibroView.class), //

            new MenuItemInfo("Recursos Materiales ", "la la-columns", RecursosMaterialesView.class), //

            new MenuItemInfo("Tarjeta Prestamo", "la la-columns", TarjetaPrestamoView.class), //

            new MenuItemInfo("Modulo", "la la-columns", ModuloView.class), //

            new MenuItemInfo("Destino Final", "la la-columns", DestinoFinalView.class), //
        };
    }
    
   
    private void ModificarUsuario(){
        modificarPerfil = new Dialog();
        //crearEstudianteForm = new CrearEstudianteForm();
        //craCrearTrabajadorForm = new CrearTrabajadorForm();
        
        Optional<Estudiante> estudiante;
        Optional<Trabajador> trabajador;
        
    }
    
    private void ModificarClave(){
        ModificarClaveView claveView = new ModificarClaveView();
        modificarClave = new Dialog(claveView);
        modificarClave.open();
    }
    

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}
