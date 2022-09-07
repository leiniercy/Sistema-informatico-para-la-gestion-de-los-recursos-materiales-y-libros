package trabajodediploma.views.error_pages;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.client.HttpServerErrorException.InternalServerError;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;

public class ServerError extends Composite<Div>
        implements HasErrorParameter<InternalServerError> {
    private Label label_error;
    private Label label_sms;
    private Image internal_error;
    private Anchor link_home;

    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<InternalServerError> parameter) {
        getContent().addClassName("internal-sever-error-view");
        getContent().add(Imagen(), Mensaje());
        return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
    }
    
    private Div Mensaje() {
        Div sms = new Div();
        sms.addClassName("div_sms");
        label_error = new Label("Error 500: Error interno del servidor ");
        label_sms = new Label("Algo anda mal, contacnte con el administrador.");
        link_home = new Anchor();
        link_home.addClassName("div_sms__link_home");
        link_home.setHref("inicio");
        link_home.add(VaadinIcon.HOME.create());
        link_home.add(new Label("regresar a inicio"));

        sms.add(label_error, label_sms, link_home);
        return sms;
}

private Div Imagen() {
        Div div_img = new Div();
        div_img.addClassName("div_img");
        internal_error = new Image("images/error-500-internal-server-error.png", "Internal Server Error");
        internal_error.addClassName("div_img__page_not_found");
        div_img.add(internal_error);
        return div_img;
}

}
