package trabajodediploma.views.inicio;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import trabajodediploma.views.MainLayout;
import trabajodediploma.views.footer.MyFooter;

@PageTitle("Inicio")
@Route(value = "inicio", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@AnonymousAllowed
public class InicioView extends Div {

    MyFooter footer;
    Div content;
    private Div imageDiv;
    
    public InicioView() {
        addClassName("inicio-view");
        Configuracion();
        add(content);
    }
    
    private void Configuracion(){
        content = new Div();
        content.addClassName("div-content");
        footer = new MyFooter();
        footer.addClassName("footer");
        
        Image img = new Image("images/empty-plant.png", "placeholder plant");
        img.setWidth("200px");
        
        imageDiv =  new Div();
        imageDiv.addClassName("div-image");
      
        content.add(imageDiv,footer);
        
    } 
    

}
