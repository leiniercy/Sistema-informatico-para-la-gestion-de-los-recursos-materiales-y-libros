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
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.select.Select;
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
    Select<String> sortBy;
    TextField filtrar;
    Div content;
    private Div navBar;
    private H1 titulo;
    private H2 autor;
    private H3 volumen;
    private H3 tomo;
    private H3 parte;
    private StreamResource source;
    private MyFooter footer;
    private Div tabContainer;

    public CatalogoView(@Autowired LibroService libroService) {
        addClassNames("catalogo-view");
        footer = new MyFooter();
        this.libroService = libroService;
        this.libros = libroService.findAll();
        Configuracion();
        barraDeNavegacion();
        post(libros);
        add(navBar, content, tabContainer, footer);
    }

    private void Configuracion() {
        navBar = new Div();
        navBar.addClassName("div-nav-bar");
        content = new Div();
        content.addClassName("div-content");
        tabContainer = new Div();
        tabContainer.addClassName("div-tab-cotainer");
    }

    private void barraDeNavegacion() {
        sortBy = new Select<>();
        sortBy.addClassName("div-nav-bar-select");
        sortBy.setLabel("Ordenar por");
        sortBy.setItems("Orden Alfabético", "Asignatura");
        sortBy.addValueChangeListener(event -> {
            if (sortBy.getValue().isEmpty()) {
                libros = libroService.findAll();
                content.removeAll();
                post(libros);
            } else if (sortBy.getValue().equals("Orden Alfabético")) {
                /* Ordenar por Titulo */
                content.removeAll();
                post(libros);
                libros = libroService.ordenarAlfabeticamente();
            } else if (sortBy.getValue().equals("Asignatura")) {

            }
        });

        filtrar = new TextField();
        filtrar.addClassName("div-nav-bar-filtrar");
        filtrar.setLabel("Filtrar por");
        filtrar.setPlaceholder("");
        filtrar.setPrefixComponent(VaadinIcon.SEARCH.create());
        filtrar.setClearButtonVisible(true);
        filtrar.setValueChangeMode(ValueChangeMode.EAGER);
        filtrar.addValueChangeListener(event -> {
            if (filtrar.getValue().isEmpty()) {
                libros = libroService.findAll();
                content.removeAll();
                post(libros);
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
                post(libros);
            }
        });
        navBar.add(filtrar, sortBy);
    }

    private void post(List<Libro> libros) {

        for (int i = 0; i < libros.size(); i++) {

            Image imagen = new Image(libros.get(i).getImagen(), libros.get(i).getTitulo());
            imagen.addClassName("imagen");
            Div imagenDiv = new Div(imagen);
            imagenDiv.addClassName("imagen-div");
            Div imagenContainerDiv = new Div(imagenDiv);
            imagenContainerDiv.addClassName("imagen-div-div");

            Div info = new Div();
            info.addClassName("info");

            titulo = new H1("Título: " + libros.get(i).getTitulo());
            autor = new H2("Autor: " + libros.get(i).getAutor());
            volumen = new H3("Vólumen: " + libros.get(i).getVolumen().toString());
            tomo = new H3("Tomo: " + libros.get(i).getTomo().toString());
            parte = new H3("Parte: " + libros.get(i).getParte().toString());

            info.add(titulo, autor);
            if (libros.get(i).getVolumen() != null) {
                info.add(volumen);
            } else if (libros.get(i).getParte() != null) {
                info.add(parte);
            } else if (libros.get(i).getTomo() != null) {
                info.add(tomo);
            }
            // int pos = i;
            // Button leerButton = new Button("LEER");
            // leerButton.addClassName("leer-button");
            // leerButton.addClickListener(event -> {
            // leerDocumento(pos);
            // });

            leerDocumento(i);
            Anchor leerButton = new Anchor(source, "LEER");
            leerButton.addClassName("leer-button");
            leerButton.setTarget("_BLANK");

            Div leerDiv = new Div(leerButton);
            leerDiv.addClassName("leer-div");

            Div post = new Div(imagenContainerDiv, info, leerDiv);
            post.addClassName("post");

            // content.add(post);
        }

    }

    private void crearpost(String titulo, String autor, String volumen, String parte, String tomo) {

    }

    private void leerDocumento(int pos) {
        source = new StreamResource(libros.get(pos).getTitulo() + ".pdf", () -> {
            String path = "libros/" + libros.get(pos).getTitulo() + ".pdf";
            try {

                File initialFile = new File(path);
                InputStream targetStream = new FileInputStream(initialFile);
                return targetStream;
            } catch (FileNotFoundException ex) {
                Logger.getLogger(CatalogoView.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        });
    }

}
