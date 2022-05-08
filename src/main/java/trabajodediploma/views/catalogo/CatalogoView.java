package trabajodediploma.views.catalogo;

import com.vaadin.flow.component.button.Button;
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
import com.vaadin.flow.server.auth.AnonymousAllowed;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import trabajodediploma.data.entity.Libro;
import trabajodediploma.data.service.LibroService;
import trabajodediploma.views.MainLayout;

@PageTitle("Catalogo")
@Route(value = "catalogo-libros", layout = MainLayout.class)
@AnonymousAllowed
public class CatalogoView extends Div {

    List<Libro> libros;
    LibroService libroService;
    Select<String> sortBy;
    TextField filtrar;
    Div content;
    private Div header;

    public CatalogoView(@Autowired LibroService libroService) {
        addClassNames("catalogo-view");
        this.libroService = libroService;
        this.libros = libroService.findAll();
        Configuracion();
        barraDeNavegacion();
        post(libros);
        add(header, content);
    }

    private void Configuracion() {
        header = new Div();
        header.addClassName("header");
        content = new Div();
        content.addClassName("content");
        
        
    }
    
    private void OrdenamientoPorOrdenAlfabetico(){
        
    }
    private void OrdenamientoPorAsignatura(){
        
    }

    private void barraDeNavegacion() {
        sortBy = new Select<>();
        sortBy.addClassName("select");
        sortBy.setLabel("Ordenar por");
        sortBy.setItems("Orden Alfabético", "Asignatura");
        sortBy.setValue("Orden Alfabético");

        filtrar = new TextField();
        filtrar.addClassName("filtrar");
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
                        .filter(lib -> StringUtils.containsIgnoreCase(lib.getAutor(),filtrar.getValue())
                        || StringUtils.containsIgnoreCase(lib.getTitulo(),filtrar.getValue())
                        || StringUtils.containsIgnoreCase(Integer.toString(lib.getVolumen()), filtrar.getValue())
                        || StringUtils.containsIgnoreCase(Integer.toString(lib.getTomo()), filtrar.getValue())
                        || StringUtils.containsIgnoreCase(Integer.toString(lib.getParte()), filtrar.getValue())
                        )
                        .collect(Collectors.toList());
                content.removeAll();
                post(libros);
            }
        });
        header.add(filtrar, sortBy);
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

            H1 titulo = new H1("Título: " + libros.get(i).getTitulo());
            H2 autor = new H2("Autor: " + libros.get(i).getAutor());
            H3 volumen = new H3("Vólumen: " + libros.get(i).getVolumen().toString());
            H3 tomo = new H3("Tomo: " + libros.get(i).getTomo().toString());
            H3 parte = new H3("Parte: " + libros.get(i).getParte().toString());

            info.add(titulo, autor);
            if (libros.get(i).getVolumen() != null) {
                info.add(volumen);
            } else if (libros.get(i).getParte() != null) {
                info.add(parte);
            } else if (libros.get(i).getTomo() != null) {
                info.add(tomo);
            }

            Button leerButton = new Button("LEER");
            leerButton.addClassName("leer-button");
            Div leerDiv = new Div(leerButton);
            leerDiv.addClassName("leer-div");

            Div post = new Div(imagenContainerDiv, info, leerDiv);
            post.addClassName("post");

            content.add(post);
        }

    }

}
