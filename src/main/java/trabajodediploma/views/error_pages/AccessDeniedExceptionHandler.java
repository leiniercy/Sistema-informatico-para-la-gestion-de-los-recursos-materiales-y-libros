package trabajodediploma.views.error_pages;

import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.ParentLayout;

import trabajodediploma.views.MainLayout;

@Tag(Tag.DIV)
@ParentLayout(MainLayout.class)
public class AccessDeniedExceptionHandler extends Composite<Div>
                implements HasErrorParameter<AccessDeniedException> {

        private Label label_error;
        private Label label_sms;
        private Image page_not_found;
        private Anchor link_home;

        @Override
        public int setErrorParameter(BeforeEnterEvent event,
                        ErrorParameter<AccessDeniedException> parameter) {
                getContent().addClassName("forbidden-view");
                getContent().add(Imagen(), Mensaje());
                return HttpServletResponse.SC_FORBIDDEN;
        }

        private Div Mensaje() {
                Div sms = new Div();
                sms.addClassName("div_sms");
                label_error = new Label("Error 403: Acceso Prohibido");
                label_sms = new Label("Intenté navegar a una vista con los derechos de acceso correctos.");
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
                page_not_found = new Image("images/website-error-403-forbidden.png.PNG", "Forbidden");
                page_not_found.addClassName("div_img__page_not_found");
                div_img.add(page_not_found);
                return div_img;
        }

}