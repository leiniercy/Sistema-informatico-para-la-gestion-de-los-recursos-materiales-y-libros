package trabajodediploma.data.tools;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;

public class EmptyList extends Div {
    
    public EmptyList(){
        H1 errorSMS = new H1("No existe ningun elemento");
        add(errorSMS);
    }

}
