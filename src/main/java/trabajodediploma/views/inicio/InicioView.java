package trabajodediploma.views.inicio;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.AnchorTarget;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import trabajodediploma.data.entity.Estudiante;
import trabajodediploma.data.entity.Trabajador;
import trabajodediploma.data.entity.User;
import trabajodediploma.data.service.AreaService;
import trabajodediploma.data.service.EstudianteService;
import trabajodediploma.data.service.GrupoService;
import trabajodediploma.data.service.TrabajadorService;
import trabajodediploma.data.tools.EmailSenderService;
import trabajodediploma.security.AuthenticatedUser;
import trabajodediploma.views.MainLayout;
import trabajodediploma.views.footer.MyFooter;
import trabajodediploma.views.login.crear_informacion_perfil.CrearInformacionPerfilView;

@Tag("div")
// @CssImport("./styles/css/bootstrap.css")
// @JavaScript("flow-frontend://src/js/bootstrap")

@PageTitle("Genius")
@Route(value = "inicio", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@AnonymousAllowed
public class InicioView extends Div {

    private CrearInformacionPerfilView crearPerfil;
    private MyFooter footer;
    private Div container;
    private List<Estudiante> estudiantes;
    private List<Trabajador> trabajadores;
    private Dialog dialog;
    private AuthenticatedUser authenticatedUser;
    private EstudianteService estudianteService;
    private TrabajadorService trabajadorService;
    private AreaService areaService;
    private GrupoService grupoService;
    private EmailSenderService senderService;
    private Div header;
    private User user;
    private Div container_seccion1;
    private Div container_seccion2;
    private Div container_seccion3;
    private Div container_seccion4;

    public InicioView(
            @Autowired AuthenticatedUser authenticatedUser,
            @Autowired EstudianteService estudianteService,
            @Autowired TrabajadorService trabajadorService,
            @Autowired AreaService areaService,
            @Autowired GrupoService grupoService,
            @Autowired EmailSenderService senderService) {
        addClassName("inicio-view");
        this.authenticatedUser = authenticatedUser;
        this.estudianteService = estudianteService;
        this.trabajadorService = trabajadorService;
        this.areaService = areaService;
        this.grupoService = grupoService;
        this.senderService = senderService;
        estudiantes = estudianteService.findAll();
        trabajadores = trabajadorService.findAll();
        Configuracion();
        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            user = maybeUser.get();
            estudiantes = estudiantes.parallelStream()
                    .filter(event -> event.getUser().getUsername().equals(user.getUsername()))
                    .collect(Collectors.toList());
            trabajadores = trabajadores.parallelStream()
                    .filter(event -> event.getUser().getUsername().equals(user.getUsername()))
                    .collect(Collectors.toList());

            if (estudiantes.size() == 0 && trabajadores.size() == 0) {
                /* crear perfil */
                dialog = new Dialog();
                crearPerfil = new CrearInformacionPerfilView(user, estudianteService, trabajadorService, areaService,
                        grupoService, senderService, dialog);
                dialog.open();
                /*
                 * Fin -> crear perfil
                 * Header crear perfil usuario
                 */
                Button closeButton = new Button(new Icon("lumo", "cross"), (e) -> dialog.close());
                closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
                Span title = new Span("Perfil");
                Div titleDiv = new Div(title);
                titleDiv.addClassName("div-perfil-title");
                Div buttonDiv = new Div(closeButton);
                buttonDiv.addClassName("div-perfil-button");
                header = new Div(titleDiv, buttonDiv);
                header.addClassName("div-perfil-header");
                dialog.add(header, crearPerfil);
                /* Fin -> Header crear perfil usuario */
                add(container/* ,footer */);
            } else {
                add(container/* ,footer */);
            }

        } else {
            add(container/* ,footer */);
        }

        // add(container/*,footer*/);
    }

    private void Configuracion() {
        container = new Div();
        container.addClassName("div-container");
        container_seccion1 = new Div(Seccion1());
        container_seccion1.addClassName("div-container-seccion1");
        container_seccion2 = new Div(Seccion2());
        container_seccion2.addClassName("div-container-seccion2");
        container_seccion3 = new Div(Seccion3());
        container_seccion3.addClassName("div-container-seccion3");
        container_seccion4 = new Div(Seccion4());
        container_seccion4.addClassName("div-container-seccion4");
        footer = new MyFooter();
        container.add(container_seccion1, container_seccion2, container_seccion3, container_seccion4, footer);
    }

    private Component Seccion1() {

        Div seccion1 = new Div();
        seccion1.addClassName("seccion1");

        /* Logo libro */
        Div seccion1_image = new Div();
        seccion1_image.addClassNames("seccion1-image");
        Div img_container = new Div();
        img_container.addClassNames("seccion1-image-container");
        Image image1 = new Image("images/logo_pag_2.png", "Logo");
        image1.addClassNames("seccion1-image-container-img");
        img_container.add(image1);
        seccion1_image.add(img_container);
        /* Logo libro */
 /* Enlace */
        Div seccion1_info = new Div();
        seccion1_info.addClassName("seccion1-info");
        Anchor enlace = new Anchor("catalogo-libros", "Ver Cátalogo");
        enlace.addClassName("seccion1-info-enlace");
        Paragraph parrafoInfo = new Paragraph(
                "Sistema Informático para la gestión de la información de los recursos materiales y libros en la facultad 4.");
        parrafoInfo.addClassName("seccion1-info-parrafo");
        seccion1_info.add(enlace, parrafoInfo);
        /* Enlace */
        seccion1.add(seccion1_image, seccion1_info);
        return seccion1;
    }

    private Component Seccion2() {
        Div seccion2 = new Div();
        seccion2.addClassNames("seccion2");
        return seccion2;
    }

    private Component Seccion3() {
        Div seccion3 = new Div();
        seccion3.addClassName("seccion3");

        H2 seccion3_Title = new H2("Acerca de nosotros");

        Div div__card_university = new Div();
        div__card_university.addClassName("seccion3__div");

        Div div__card_contact = new Div();
        div__card_contact.addClassName("seccion3__div");

        Div card_university = crearCard("images/uci.png",
                "Universidad de las Ciencias Informáticas",
                "UCI",
                "Universidad de las Ciencias Informáticas",
                "Centro de estudios universitarios, Cuba.",
                "https://www.uci.cu/");
        card_university.addClassName("seccion3__card");
        div__card_university.add(card_university);

        Div card_contact = crearCard("images/contacto.png",
                "Desarrollador",
                "Desarrollador",
                "Leinier Caraballo Yanes",
                "Email: leiniercy@estudiantes.uci.cu",
                "https://correo.uci.cu/");
        card_contact.addClassName("seccion3__div__card");
        div__card_contact.add(card_contact);

        seccion3.add(seccion3_Title, div__card_university, div__card_contact);
        return seccion3;
    }

    private Div crearCard(String img, String img_alt, String card_title, String card_description1,
            String card_description2, String card_enlace) {

        Div card = new Div();
        card.addClassName("seccion3__div__card");

        Image imagen = new Image(img, img_alt);
        card.add(imagen);

        Div card__content = new Div();
        card__content.addClassName("seccion3__div__card__content");
        card.add(card__content);

        H2 card__title = new H2(card_title);
        Paragraph card__description1 = new Paragraph(card_description1);
        Paragraph card__description2 = new Paragraph(card_description2);
        Anchor card__enlace = new Anchor(card_enlace, "Más información...");
        card__content.add(card__title, card__description1, card__description2, card__enlace);

        return card;
    }

    private Component Seccion4() {

        Div seccion4 = new Div();
        seccion4.addClassName("seccion4");

        /* SITIOS DE INTERÉS */
        H2 sitiosInteres = new H2("SITIOS DE INTERÉS");

        Anchor repositorioInstitucional = new Anchor("https://repositorio.uci.cu/jspui/");
        repositorioInstitucional.addClassName("seccion4-link");
        repositorioInstitucional.add(VaadinIcon.LINK.create());
        repositorioInstitucional.add("Repositorio Institucional");
        repositorioInstitucional.setTarget("_BLANK");

        Anchor biblioteca = new Anchor("https://biblioteca.uci.cu/");
        biblioteca.addClassName("seccion4-link");
        biblioteca.add(VaadinIcon.LINK.create());
        biblioteca.add("Biblioteca");
        biblioteca.setTarget("_BLANK");

        Anchor internos = new Anchor("https://internos.uci.cu/");
        internos.addClassName("seccion4-link");
        internos.add(VaadinIcon.LINK.create());
        internos.add("Internos");
        internos.setTarget("_BLANK");

        VerticalLayout sitiosDeInteres = new VerticalLayout(sitiosInteres, repositorioInstitucional, biblioteca,
                internos);
        /* SITIOS DE INTERÉS */

 /* OTROS SITIOS */
        H2 otroSitios = new H2("OTROS SITIOS");

        Anchor portalUCI = new Anchor("https://www.uci.cu/");
        portalUCI.addClassName("seccion4-link");
        portalUCI.add(VaadinIcon.LINK.create());
        portalUCI.add("Portal UCI");
        portalUCI.setTarget("_BLANK");

        Anchor intranet = new Anchor("https://intranet.uci.cu/");
        intranet.addClassName("seccion4-link");
        intranet.add(VaadinIcon.LINK.create());
        intranet.add("Intranet");
        intranet.setTarget("_BLANK");

        Anchor periodicoMella = new Anchor("https://periodico.uci.cu/");
        periodicoMella.addClassName("seccion4-link");
        periodicoMella.add(VaadinIcon.LINK.create());
        periodicoMella.add("Periódico Mella");
        periodicoMella.setTarget("_BLANK");

        VerticalLayout otrosSitios = new VerticalLayout(otroSitios, portalUCI, intranet, periodicoMella);
        /* OTROS SITIOS */

 /* COMUNIDADES UCI */
        H2 comunidadessUCI = new H2("COMUNIDADES UCI");

        Anchor humanos = new Anchor("https://humanos.uci.cu/");
        humanos.addClassName("seccion4-link");
        humanos.add(VaadinIcon.LINK.create());
        humanos.add("HumanOS");
        humanos.setTarget("_BLANK");

        Anchor fireFoxMania = new Anchor("https://firefoxmania.uci.cu/");
        fireFoxMania.addClassName("seccion4-link");
        fireFoxMania.add(VaadinIcon.LINK.create());
        fireFoxMania.add("FirefoxManía");
        fireFoxMania.setTarget("_BLANK");

        Anchor blog = new Anchor("https://iblog.uci.cu/");
        blog.addClassName("seccion4-link");
        blog.add(VaadinIcon.LINK.create());
        blog.add("iBlog");
        blog.setTarget("_BLANK");

        VerticalLayout comunidadesUCI = new VerticalLayout(comunidadessUCI, humanos, fireFoxMania, blog);
        /* COMUNIDADES UCI */

        seccion4.add(sitiosDeInteres, otrosSitios, comunidadesUCI);
        return seccion4;
    }

}
