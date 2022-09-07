package trabajodediploma.views.error_pages;

import javax.servlet.http.HttpServletResponse;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.NotFoundException;

// @Tag(Tag.DIV)
public class RouteNotFoundError extends Composite<Div>
    implements HasErrorParameter<NotFoundException> {

  private Label label_error;
  private Label label_sms;
  private Image page_not_found;
  private Anchor link_home;

  @Override
  public int setErrorParameter(BeforeEnterEvent event,
      ErrorParameter<NotFoundException> parameter) {

    getContent().addClassName("page-not-found-view");
    label_sms = new Label("No se pudo navegar a '" + event.getLocation().getPath() + "'.");

    getContent().add(Imagen(), Mensaje());

    return HttpServletResponse.SC_NOT_FOUND;
  }

  private Div Mensaje() {
    Div sms = new Div();
    sms.addClassName("div_sms");
    
    label_error = new Label("Error 404: Página no encontrada");
    
    link_home = new Anchor();
    link_home.addClassName("div_sms__link_home");
    link_home.setHref("inicio");
    link_home.add(VaadinIcon.HOME.create());
    link_home.add(new Label("regresar a inicio"));

    sms.add(label_error, label_sms,link_home);
    return sms;
  }

  private Div Imagen(){
    Div div_img = new Div();
    div_img.addClassName("div_img");
    page_not_found = new Image("images/page-not-found-error404.PNG", "Page not found");
    page_not_found.addClassName("div_img__page_not_found");
    div_img.add(page_not_found);
    return div_img;
  }

}
