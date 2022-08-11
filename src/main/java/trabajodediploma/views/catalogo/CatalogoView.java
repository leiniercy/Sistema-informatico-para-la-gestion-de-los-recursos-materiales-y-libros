package trabajodediploma.views.catalogo;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import trabajodediploma.data.entity.Libro;
import trabajodediploma.data.service.LibroService;
import trabajodediploma.views.MainLayout;
import trabajodediploma.views.footer.MyFooter;

@PageTitle("Catalogo")
@Route(value = "catalogo-libros", layout = MainLayout.class)
@AnonymousAllowed
public class CatalogoView extends Div {

    List<Libro> libros;
    LibroService libroService;
    TextField filtrar;
    Div content;
    private Div navBar;
    private String imagen;
    private String titulo;
    private String autor;
    private String volumen;
    private String tomo;
    private String parte;
    private StreamResource source;
    private MyFooter footer;
    private Div div_frase;

    public CatalogoView(@Autowired LibroService libroService) {
        addClassNames("catalogo-view");
        footer = new MyFooter();
        this.libroService = libroService;
        this.libros = libroService.findAll();
        Configuracion();
        barraDeNavegacion();
        crearTarjetas();
        frase();
        add(navBar, content, div_frase, footer);
    }

    private void Configuracion() {
        navBar = new Div();
        navBar.addClassName("div_nav_bar");
        content = new Div();
        content.addClassName("div_container");
        div_frase = new Div();
        div_frase.addClassName("div_frase");
    }

    private void barraDeNavegacion() {
        filtrar = new TextField();
        filtrar.addClassName("div_nav_bar__filtrar");
        filtrar.setPlaceholder("Filtrar...");
        filtrar.setPrefixComponent(VaadinIcon.SEARCH.create());
        filtrar.setClearButtonVisible(true);
        filtrar.setValueChangeMode(ValueChangeMode.EAGER);
        filtrar.addValueChangeListener(event -> {
            if (filtrar.getValue().isEmpty()) {
                libros = libroService.findAll();
                content.removeAll();
                crearTarjetas();
            } else {
                libros = libros.parallelStream()
                        .filter(lib -> StringUtils.containsIgnoreCase(lib.getAutor(), filtrar.getValue())
                                || StringUtils.containsIgnoreCase(lib.getTitulo(), filtrar.getValue())
                                || StringUtils.containsIgnoreCase(Integer.toString(lib.getVolumen()),
                                        filtrar.getValue())
                                || StringUtils.containsIgnoreCase(Integer.toString(lib.getTomo()), filtrar.getValue())
                                || StringUtils.containsIgnoreCase(Integer.toString(lib.getParte()), filtrar.getValue()))
                        .collect(Collectors.toList());
                content.removeAll();
                crearTarjetas();
            }
        });
        navBar.add(filtrar);
    }

    private void crearTarjetas() {

        for (int i = 0; i < libros.size(); i++) {
            leerDocumento(i);
            Div target = new Div();
            target.addClassName("target");
            Div card = new Div();
            card.addClassName("target__div__card");
            Anchor link = new Anchor(source, "LEER");
            link.addClassName("target__link");
            link.setTarget("_BLANK");
            Boolean band = false;

            imagen = libros.get(i).getImagen();
            titulo = new String("Título: " + libros.get(i).getTitulo());
            autor = new String("Autor: " + libros.get(i).getAutor());
           
            if (libros.get(i).getVolumen() != null && band == false) {
                volumen = new String("Vólumen: " + libros.get(i).getVolumen().toString());
                card = crearCard(imagen, titulo, titulo, autor, volumen);
                band = true;
            } else if (libros.get(i).getParte() != null && band == false) {
                parte = new String("Parte: " + libros.get(i).getParte().toString());
                card = crearCard(imagen, titulo, titulo, autor, parte);
                band = true;
            } else if (libros.get(i).getTomo() != null && band == false) {
                tomo = new String("Tomo: " + libros.get(i).getTomo().toString());
                card = crearCard(imagen, titulo, titulo, autor, tomo);
                band = true;
            }

            target.add(card, link);
            content.add(target);
        }
    }

    private void frase() {
        H1 frase = new H1("Haz de los obstáculos escalones para aquello que quieres alcanzar.");
        div_frase.add(frase);
    }

    private Div crearCard(String img, String img_alt, String titulo, String autor, String volumen) {
        Div card = new Div();
        card.addClassName("target__div__card");

        Image imagen = new Image(img, img_alt);
        card.add(imagen);

        Div card__content = new Div();
        card__content.addClassName("target__div__card__content");
        card.add(card__content);

        H2 card__title = new H2(titulo);
        Paragraph card__description1 = new Paragraph(autor);
        Paragraph card__description2 = new Paragraph(volumen);
        card__content.add(card__title, card__description1, card__description2);
        return card;
    }

    private void leerDocumento(int pos) {
        source = new StreamResource(libros.get(pos).getTitulo() + ".pdf", () -> {
            String path = "libros/" + libros.get(pos).getTitulo() + ".pdf";
            try {

                File initialFile = new File(path);
                InputStream targetStream = new FileInputStream(initialFile);
                return targetStream;
            } catch (FileNotFoundException ex) {
                //Logger.getLogger(CatalogoView.class.getName()).log(Level.SEVERE, null, ex);
                Notification notification = Notification.show("Libro no disponible", 5000,
                Notification.Position.MIDDLE);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                return null;
            }
        });
    }
}
