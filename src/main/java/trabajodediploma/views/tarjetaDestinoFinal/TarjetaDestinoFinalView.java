package trabajodediploma.views.tarjetaDestinoFinal;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Div;

import trabajodediploma.data.entity.DestinoFinal;
import trabajodediploma.views.footer.MyFooter;

public class TarjetaDestinoFinalView extends Div {
    
    Grid<DestinoFinal> grid = new Grid<>(DestinoFinal.class, false);
    GridListDataView<DestinoFinal> gridListDataView;
    Grid.Column<DestinoFinal> moduloColumn;
    Grid.Column<DestinoFinal> fechaColumn;
    Grid.Column<DestinoFinal> cantidadColumn;
    
    MyFooter myFooter;
    
    public TarjetaDestinoFinalView (){

    }


}
