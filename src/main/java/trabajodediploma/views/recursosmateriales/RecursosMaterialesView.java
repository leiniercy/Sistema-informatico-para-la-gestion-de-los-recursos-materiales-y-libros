package trabajodediploma.views.recursosmateriales;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.color.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;
import trabajodediploma.data.entity.RecursoMaterial;
import trabajodediploma.data.service.RecursoMaterialService;
import trabajodediploma.views.MainLayout;
import trabajodediploma.views.footer.MyFooter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.LinkedList;

@org.springframework.stereotype.Component
@Scope("prototype")
@PageTitle("Recursos Materiales ")
@Route(value = "recurso-material-view", layout = MainLayout.class)
@RolesAllowed("VD_ADIMN_ECONOMIA")
public class RecursosMaterialesView extends Div {

    private Grid<RecursoMaterial> grid = new Grid<>(RecursoMaterial.class, false);

    RecursoMaterialService materialService;
    GridListDataView<RecursoMaterial> gridListDataView;
    Grid.Column<RecursoMaterial> codigoColumn;
    Grid.Column<RecursoMaterial> descripcionColumn;
    Grid.Column<RecursoMaterial> tipoColumn;
    Grid.Column<RecursoMaterial> editColumn;

    MyFooter myFooter;
    RecursosMaterialesForm form;
    private TextField filterCodigo;
    private TextField filterDescripcion;
    private TextField filterTipo;

    private Dialog dialog;
    private Html total;
    private HorizontalLayout toolbar;
    private HorizontalLayout buttons;
    private Div header;

    //Campos del exportar modelo de recursos materiales
    private Dialog reportDialog;
    private TextField codigo;
    private TextField almacenSolicitud;
    private TextField proceso;
    private TextField ordenProd;
    private TextField lote;
    private TextField centroCosto;
    private TextField codigo2;
    private TextField ordenTrab;
    private TextField productCod;
    private TextField otros;
    private TextField nombre1;
    private TextField nombre2;
    private TextField nombre3;

    public RecursosMaterialesView(@Autowired RecursoMaterialService materialService) {
        addClassNames("recurso-material-view");
        this.materialService = materialService;
        setSizeFull();
        Filtros();
        configureGrid();
        configureForm();
        myFooter = new MyFooter();
        add(menuBar(), getContent(), myFooter);
        updateList();
        closeEditor();
    }

    /* Contenido de la vista */
    private Div getContent() {
        Div formContent = new Div(form);
        formContent.addClassName("form-content");
        Div gridContent = new Div(grid);
        gridContent.addClassName("grid-content");

        Div content = new Div(gridContent);
        content.addClassName("content");
        content.setSizeFull();
        /* Dialog Header */
        Button closeButton = new Button(new Icon("lumo", "cross"), (e) -> dialog.close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Span title = new Span("Recuso Material");
        Div titleDiv = new Div(title);
        titleDiv.addClassName("div-dialog-title");
        Div buttonDiv = new Div(closeButton);
        buttonDiv.addClassName("div-dialog-button");
        header = new Div(titleDiv, buttonDiv);
        header.addClassName("div-dialog-header");
        /* Dialog Header */

        dialog = new Dialog(header, formContent);

        return content;
    }

    /* Tabla */
 /* Configuracion de la tabla */
    private void configureGrid() {
        grid.setClassName("recursoMaterial-grid");

        codigoColumn = grid.addColumn(RecursoMaterial::getCodigo).setHeader("Código").setAutoWidth(true)
                .setSortable(true);

        descripcionColumn = grid.addColumn(RecursoMaterial::getDescripcion).setHeader("Nombre")
                .setAutoWidth(true)
                .setSortable(true);

        tipoColumn = grid.addColumn(RecursoMaterial::getTipo).setHeader("Tipo de material")
                .setAutoWidth(true)
                .setSortable(true);

        editColumn = grid.addComponentColumn(material -> {
            Button editButton = new Button(VaadinIcon.EDIT.create());
            editButton.addClickListener(e -> this.editMaterial(material));
            editButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            return editButton;
        }).setFlexGrow(0);

        HeaderRow headerRow = grid.appendHeaderRow();
        headerRow.getCell(codigoColumn).setComponent(filterCodigo);
        headerRow.getCell(descripcionColumn).setComponent(filterDescripcion);
        headerRow.getCell(tipoColumn).setComponent(filterTipo);

        gridListDataView = grid.setItems(materialService.findAll());
        grid.setAllRowsVisible(true);
        grid.setSizeFull();
        grid.setWidthFull();
        grid.setHeightFull();
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);

    }

    // Filtros
    private void Filtros() {

        filterCodigo = new TextField();
        filterCodigo.setPlaceholder("Filtrar");
        filterCodigo.setPrefixComponent(VaadinIcon.SEARCH.create());
        filterCodigo.setClearButtonVisible(true);
        filterCodigo.setWidth("100%");
        filterCodigo.setValueChangeMode(ValueChangeMode.EAGER);
        filterCodigo.addValueChangeListener(
                event -> gridListDataView
                        .addFilter(material -> StringUtils.containsIgnoreCase(
                        material.getCodigo(),
                        filterCodigo.getValue())));

        filterDescripcion = new TextField();
        filterDescripcion.setPlaceholder("Filtrar");
        filterDescripcion.setPrefixComponent(VaadinIcon.SEARCH.create());
        filterDescripcion.setClearButtonVisible(true);
        filterDescripcion.setWidth("100%");
        filterDescripcion.setValueChangeMode(ValueChangeMode.EAGER);
        filterDescripcion.addValueChangeListener( event -> gridListDataView.addFilter(material -> StringUtils.containsIgnoreCase(material.getDescripcion(),filterDescripcion.getValue())));

        filterTipo = new TextField();
        filterTipo.setPlaceholder("Filtrar");
        filterTipo.setPrefixComponent(VaadinIcon.SEARCH.create());
        filterTipo.setClearButtonVisible(true);
        filterTipo.setWidth("100%");
        filterTipo.setValueChangeMode(ValueChangeMode.EAGER);
        filterTipo.addValueChangeListener(event -> gridListDataView.addFilter(material -> StringUtils.containsIgnoreCase(material.getTipo(),filterTipo.getValue())));

    }

    private HorizontalLayout menuBar() {
        buttons = new HorizontalLayout();
        Button refreshButton = new Button(VaadinIcon.REFRESH.create());
        refreshButton.addClickListener(click -> updateList());
        refreshButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button deleteButton = new Button(VaadinIcon.TRASH.create());
        deleteButton.addClickListener(click -> deleteMaterial());
        deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button addButton = new Button(VaadinIcon.PLUS.create());
        addButton.addClickListener(click -> addMaterial());
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button modelButton = new Button(VaadinIcon.FILE.create());
        modelButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        modelButton.addClickListener(click -> formReporte());
        buttons.add(refreshButton, watchColumns(), deleteButton, addButton/*, modelButton*/);
        if (materialService.count() == 1) {
            total = new Html("<span>Total: <b>" + materialService.count() + "</b> material</span>");

        } else if (materialService.count() == 0 || materialService.count() > 1) {
            total = new Html("<span>Total: <b>" + materialService.count() + "</b> materiales</span>");
        }
        toolbar = new HorizontalLayout(buttons, total);
        toolbar.addClassName("toolbar");
        toolbar.setAlignItems(FlexComponent.Alignment.CENTER);
        toolbar.setWidth("100%");
        toolbar.setFlexGrow(1, buttons);
        toolbar.getStyle()
                .set("padding", "var(--lumo-space-wide-m)");

        return toolbar;
    }

    private void deleteMaterial() {
        try {

            if (grid.asMultiSelect().isEmpty()) {
                Notification notification = Notification.show("Debe elegir al menos un campo", 5000,
                        Notification.Position.MIDDLE);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } else {
                deleteItems(grid.getSelectedItems().size(), grid.getSelectedItems());
                updateList();
                toolbar.remove(total);
                if (materialService.count() == 1) {
                    total = new Html("<span>Total: <b>" + materialService.count()
                            + "</b> material</span>");

                } else if (materialService.count() == 0 || materialService.count() > 1) {
                    total = new Html("<span>Total: <b>" + materialService.count()
                            + "</b> materiales</span>");
                }
                toolbar.addComponentAtIndex(1, total);
                toolbar.setFlexGrow(1, buttons);

            }

        } catch (Exception e) {
            e.printStackTrace();
            Notification notification = Notification.show(
                    "Ocurrió un problema al intentar eliminar el material", 5000,
                    Notification.Position.MIDDLE);
            ;
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void deleteItems(int cantidad, Set<RecursoMaterial> materiales) {
        Notification notification;
        materialService.deleteAll(materiales);
        if (cantidad == 1) {
            notification = Notification.show("El recuso material ha sido eliminado", 5000,
                    Notification.Position.BOTTOM_START);
        } else {
            notification = Notification.show("Han sido eliminados" + cantidad + " recursos materiales",
                    5000,
                    Notification.Position.BOTTOM_START);
        }
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    /* Menu de Columnas */
    private Button watchColumns() {
        Button menuButton = new Button(/* "Mostar/Ocultar Columnas" */VaadinIcon.EYE.create());
        menuButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        ColumnToggleContextMenu columnToggleContextMenu = new ColumnToggleContextMenu(
                menuButton);
        columnToggleContextMenu.addColumnToggleItem("Código", codigoColumn);
        columnToggleContextMenu.addColumnToggleItem("Nombre", descripcionColumn);
        columnToggleContextMenu.addColumnToggleItem("Tipo", tipoColumn);
        return menuButton;
    }

    private static class ColumnToggleContextMenu extends ContextMenu {

        public ColumnToggleContextMenu(Component target) {
            super(target);
            setOpenOnClick(true);
        }

        void addColumnToggleItem(String label, Grid.Column<RecursoMaterial> column) {
            MenuItem menuItem = this.addItem(label, e -> {
                column.setAutoWidth(true).setSortable(true).setVisible(e.getSource().isChecked());
            });
            menuItem.setCheckable(true);
            menuItem.setChecked(column.isVisible());

        }
    }

    /* Fin-Menu de Columnas */
 /* Fin-Tabla */

 /* Formulario */
    private void configureForm() {
        form = new RecursosMaterialesForm();
        form.setWidth("25em");
        form.addListener(RecursosMaterialesForm.SaveEvent.class, this::saveMaterial);
        form.addListener(RecursosMaterialesForm.CloseEvent.class, e -> closeEditor());
    }

    private void saveMaterial(RecursosMaterialesForm.SaveEvent event) {

        List<RecursoMaterial> materiales = new LinkedList<>();
        boolean band = false;
        for (int i = 0; i < materialService.findAll().size() && band == false; i++) {
            RecursoMaterial mat = materialService.findAll().get(i);
            if (event.getRecursoMaterial().getCodigo().equals(mat.getCodigo())
                    && event.getRecursoMaterial().getDescripcion().equals(mat.getDescripcion())
                    && event.getRecursoMaterial().getTipo().equals(mat.getTipo())
                    ) {
                materiales.add(mat);
                band = true;
            }
        }

        if (materiales.size() > 0) {
            Notification notification = Notification.show(
                    "El recurso material ya existe",
                    2000,
                    Notification.Position.MIDDLE);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        } else {
            if (event.getRecursoMaterial().getId() == null) {
                materialService.save(event.getRecursoMaterial());
                Notification notification = Notification.show(
                        "Recurso material añadido",
                        2000,
                        Notification.Position.BOTTOM_START);
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } else {
                materialService.update(event.getRecursoMaterial());
                Notification notification = Notification.show(
                        "Recurso material modificado",
                        2000,
                        Notification.Position.BOTTOM_START);
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            }
            toolbar.remove(total);
            if (materialService.count() == 1) {
                total = new Html("<span>Total: <b>" + materialService.count() + "</b> material</span>");

            } else if (materialService.count() == 0 || materialService.count() > 1) {
                total = new Html("<span>Total: <b>" + materialService.count()
                        + "</b> materiales</span>");
            }
            toolbar.addComponentAtIndex(1, total);
            toolbar.setFlexGrow(1, buttons);
            updateList();
            closeEditor();
        }

    }

    private void editMaterial(RecursoMaterial material) {
        if (material == null) {
            closeEditor();
        } else {
            form.setMaterial(material);
            form.setVisible(true);
            addClassName("editing");
            dialog.open();
        }
    }

    private void addMaterial() {
        grid.asMultiSelect().clear();
        RecursoMaterial r = new RecursoMaterial();
        editMaterial(r);
    }

    private void closeEditor() {
        form.setMaterial(null);
        form.setVisible(false);
        removeClassName("editing");
        dialog.close();
    }

    private void updateList() {
        grid.setItems(materialService.findAll());
    }

    /* Fin-Formulario */
 /* Reporte */
    private void formReporte() {
        reportDialog = new Dialog();
        Div reportContainer = new Div();
        reportContainer.addClassNames("report-form-container");
        Anchor reporteLink = new Anchor(ReportePDF(), "Crear Modelo");
        reporteLink.addClassNames("link-modelo");
        reporteLink.setTarget("_BLANK");
        reporteLink.addBlurListener(e -> reportDialog.close());

        /* Dialog Header */
        Button closeButton = new Button(new Icon("lumo", "cross"), (e) -> reportDialog.close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Span title = new Span("Modelo de Recursos Materiales");
        Div titleDiv = new Div(title);
        titleDiv.addClassName("div-dialog-title");
        Div buttonDiv = new Div(closeButton);
        buttonDiv.addClassName("div-dialog-button");
        header = new Div(titleDiv, buttonDiv);
        header.addClassName("div-dialog-header");
        /* Dialog Header */

        codigo = new TextField();
        codigo.setPlaceholder("CÓDIGO");

        almacenSolicitud = new TextField();
        almacenSolicitud.setPlaceholder("ALMACÉN AL QUE SE SOLICITAN");
        proceso = new TextField();
        proceso.setPlaceholder("PROCESO");

        ordenProd = new TextField();
        ordenProd.setPlaceholder("ORDEN DE PRODUCCIÓN No.");
        lote = new TextField();
        lote.setPlaceholder("LOTE No");
        centroCosto = new TextField();
        centroCosto.setPlaceholder("CENTRO DE COSTO");
        codigo2 = new TextField();
        codigo2.setPlaceholder("CÓDIGO");
        ordenTrab = new TextField();
        ordenTrab.setPlaceholder("ORDEN DE TRABAJO No");
        productCod = new TextField();
        productCod.setPlaceholder("PRODUCTO CÓDIGO");
        otros = new TextField();
        otros.setPlaceholder("OTROS");

        nombre1 = new TextField();
        nombre1.setPlaceholder("SOLICITADO POR ...");
        nombre2 = new TextField();
        nombre2.setPlaceholder("AUTORIZADO POR...");
        nombre3 = new TextField();
        nombre3.setPlaceholder("RECIBIDO POR ...");

        reportContainer.add(header, codigo, almacenSolicitud, proceso, ordenProd, lote, centroCosto, codigo2, ordenTrab, productCod, otros, nombre1, nombre2, nombre3, reporteLink);
        reportDialog.add(reportContainer);
        reportDialog.open();
    }

    /*Link del crear modulo*/
    private StreamResource ReportePDF() {
        StreamResource source = new StreamResource("ReporteEvaluaciones.pdf", () -> {
            String path = "src/main/resources/META-INF/resources/archivos/RecurosMateriales.pdf";

            try {
                PdfWriter pdfWriter = new PdfWriter(path);
                PdfDocument pdfDocument = new PdfDocument(pdfWriter);
                Document document = new Document(pdfDocument);

                document.add(primeraFila());
                document.add(segundaFila());
                document.add(terceraFila());
                document.add(cuartaFila());
                document.add(quintaFila());
                document.add(sextaFila());
                document.add(septimaFila());
                document.add(octavaFila());
                document.add(novenaFila());
                document.add(decimaFila());
                document.add(ultimaFila());
                document.close();

                File initialFile = new File(path);
                InputStream targetStream = new FileInputStream(initialFile);
                return targetStream;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            }

        });
        return source;
    }

    /* Primera Fila -> UCI, Solicitud, D,M,A */
    private Table primeraFila() {

        float firstGrow_columnWidth[] = {320, 200, 50, 50, 50};

        Table firstGrow = new Table(firstGrow_columnWidth);

        firstGrow.addCell(new Cell().add("UNIVERSIDAD DE LAS CIENCIAS INFORMÁTICAS")
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setMarginTop(30f)
                .setMarginBottom(30f)
                .setFontSize(10f));
        firstGrow.addCell(new Cell().add("SOLICITUD DE MATERIALES")
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setMarginTop(30f)
                .setMarginBottom(30f)
                .setFontSize(14f)
                .setBorderBottom(Border.NO_BORDER)
                .setBold());

        firstGrow.addCell(new Cell().add("D")
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setMarginTop(30f)
                .setMarginBottom(30f)
                .setFontSize(10f));
        firstGrow.addCell(new Cell().add("M")
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setMarginTop(30f)
                .setMarginBottom(30f)
                .setFontSize(10f));
        firstGrow.addCell(new Cell().add("A")
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setMarginTop(30f)
                .setMarginBottom(30f)
                .setFontSize(10f));
        return firstGrow;
    }

    /* Segunda Fila -> direccion, codigo */
    private Table segundaFila() {

        float secondGrow_columnWidth[] = {148, 148, 231, 49, 50, 42};
        Table secondGrow = new Table(secondGrow_columnWidth);

        secondGrow.addCell(new Cell().add("DIRECCIÓN:")
                .setTextAlignment(TextAlignment.LEFT)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setFontSize(10f));

        secondGrow.addCell(new Cell().add("CÓDIGO:")
                .setTextAlignment(TextAlignment.LEFT)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBorderTop(Border.NO_BORDER)
                .setFontSize(10f));

        secondGrow.addCell(new Cell().add("")
                .setTextAlignment(TextAlignment.LEFT)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setFontSize(10f)
                .setBorderTop(Border.NO_BORDER));

        LocalDate date = LocalDate.now();
        // Día
        secondGrow.addCell(new Cell().add(Integer.toString(date.getDayOfMonth()))
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setFontSize(10f));
        // Mes
        secondGrow.addCell(new Cell().add(Integer.toString(date.getMonthValue()))
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setFontSize(10f));
        // Año
        secondGrow.addCell(new Cell().add(Integer.toString(date.getYear()))
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setFontSize(10f));

        return secondGrow;
    }

    /* Tercera Fila-> almacen, proceso */
    private Table terceraFila() {
        float terceraFila_columnWidth[] = {475, 125};
        Table terceraFila = new Table(terceraFila_columnWidth);

        terceraFila.addCell(new Cell().add("ALMACÉN AL QUE SE SOLICITAN:")
                .setTextAlignment(TextAlignment.LEFT)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setFontSize(10f));

        terceraFila.addCell(new Cell().add("PROCESO:")
                .setTextAlignment(TextAlignment.LEFT)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setFontSize(10f));

        return terceraFila;
    }

    /* Cuarta Fila-> destino, orden, lote, centro, codigo */
    private Table cuartaFila() {
        float cuartaFila_columnWidth[] = {5, 100, 100, 180, 123};
        Table cuartaFila = new Table(cuartaFila_columnWidth);

        cuartaFila.addCell(new Cell().add("DESTINO")
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.BOTTOM)
                .setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setRotationAngle(Math.toRadians(-90))
                .setBorderBottom(Border.NO_BORDER)
                .setFontSize(10f));

        cuartaFila.addCell(new Cell().add("ORDEN DE PRODUCCIÓN No.")
                .setTextAlignment(TextAlignment.LEFT)
                .setVerticalAlignment(VerticalAlignment.TOP)
                .setFontSize(10f));

        cuartaFila.addCell(new Cell().add("LOTE No.")
                .setTextAlignment(TextAlignment.LEFT)
                .setVerticalAlignment(VerticalAlignment.TOP)
                .setFontSize(10f));

        cuartaFila.addCell(new Cell().add("CENTRO DE COSTO:\n ÁREA:")
                .setTextAlignment(TextAlignment.LEFT)
                .setVerticalAlignment(VerticalAlignment.TOP)
                .setFontSize(10f));

        cuartaFila.addCell(new Cell().add("CÓDIGO:")
                .setTextAlignment(TextAlignment.LEFT)
                .setVerticalAlignment(VerticalAlignment.TOP)
                .setFontSize(10f));

        return cuartaFila;
    }

    /* Quinta Fila-> orden, producto, otros */
    private Table quintaFila() {
        float quintaFila_columnWidth[] = {66, 116, 240, 200};
        Table quintaFila = new Table(quintaFila_columnWidth);

        quintaFila.addCell(new Cell().add("       ")
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.TOP)
                .setBorderTop(Border.NO_BORDER)
                .setRotationAngle(Math.toRadians(-90))
                .setFontSize(10f));

        quintaFila.addCell(new Cell().add("ORDEN DE TRABAJO No")
                .setTextAlignment(TextAlignment.LEFT)
                .setVerticalAlignment(VerticalAlignment.TOP)
                .setFontSize(10f));

        quintaFila.addCell(new Cell().add("PRODUCTO CÓDIGO")
                .setTextAlignment(TextAlignment.LEFT)
                .setVerticalAlignment(VerticalAlignment.TOP)
                .setFontSize(10f));

        quintaFila.addCell(new Cell().add("OTROS")
                .setTextAlignment(TextAlignment.LEFT)
                .setVerticalAlignment(VerticalAlignment.TOP)
                .setFontSize(10f));

        return quintaFila;
    }

    /* Sexta Fila-> codigo, descripcion, u/m , cant */
    private Table sextaFila() {
        float sexta_columnWidth[] = {120, 250, 80, 100};
        Table sextaFila = new Table(sexta_columnWidth);

        sextaFila.addCell(new Cell().add("CÓDIGO")
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setFontSize(10f));

        sextaFila.addCell(new Cell().add("DESCRIPCIÓN:")
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setFontSize(10f));

        sextaFila.addCell(new Cell().add("U/M")
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setFontSize(10f));

        sextaFila.addCell(new Cell().add("Cant.")
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setFontSize(10f));

        return sextaFila;
    }

    /* Septima Fila-> En esta fila van los recursos Materiales */
    private Table septimaFila() {
        List<RecursoMaterial> materiales = getMateriales();
        float septimaFila_columnWidth[] = {120, 250, 80, 100};
        Table septimaFila = new Table(septimaFila_columnWidth);

        for (int i = 0; i < materiales.size(); i++) {

            septimaFila.addCell(new Cell().add(materiales.get(i).getCodigo())
                    .setTextAlignment(TextAlignment.LEFT)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setFontSize(10f));

            septimaFila.addCell(new Cell().add(materiales.get(i).getDescripcion())
                    .setTextAlignment(TextAlignment.LEFT)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setFontSize(10f));

            septimaFila.addCell(new Cell().add("")
                    .setTextAlignment(TextAlignment.LEFT)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setFontSize(10f));

            septimaFila.addCell(new Cell().add("")
                    .setTextAlignment(TextAlignment.LEFT)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setFontSize(10f));

        }

        return septimaFila;
    }

    /* Lista de materiales */
    private List<RecursoMaterial> getMateriales() {
        List<RecursoMaterial> list = materialService.findAll();
        if (filterCodigo.getValue() != null) {
            list = list.stream()
                    .filter(material -> StringUtils.containsIgnoreCase(material.getCodigo(),
                    filterCodigo.getValue()))
                    .collect(Collectors.toList());
        }
        if (filterDescripcion.getValue() != null) {
            list = list.stream()
                    .filter(material -> StringUtils.containsIgnoreCase(material.getDescripcion(),
                    filterDescripcion.getValue()))
                    .collect(Collectors.toList());
        }

        return list;
    }

    /* Octava Fila->solicitado,autorizado,recibido, No */
    private Table octavaFila() {
        float octavaFila_columnWidth[] = {170, 170, 170, 100};
        Table octavaFila = new Table(octavaFila_columnWidth);

        octavaFila.addCell(new Cell().add("SOLICITADO")
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setFontSize(10f));

        octavaFila.addCell(new Cell().add("AUTORIZADO")
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setFontSize(10f));

        octavaFila.addCell(new Cell().add("RECIBIDO")
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setFontSize(10f));

        octavaFila.addCell(new Cell().add("No")
                .setTextAlignment(TextAlignment.LEFT)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBorderBottom(Border.NO_BORDER)
                .setFontSize(10f));

        return octavaFila;
    }

    /* Novena Fila-> Nombre */
    private Table novenaFila() {

        float novenaFila_columnWidth[] = {170, 170, 170, 100};
        Table novenaFila = new Table(novenaFila_columnWidth);

        novenaFila.addCell(new Cell().add("NOMBRE")
                .setTextAlignment(TextAlignment.JUSTIFIED)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setFontSize(10f));

        novenaFila.addCell(new Cell().add("NOMBRE")
                .setTextAlignment(TextAlignment.JUSTIFIED)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setFontSize(10f));

        novenaFila.addCell(new Cell().add("NOMBRE")
                .setTextAlignment(TextAlignment.JUSTIFIED)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setFontSize(10f));

        novenaFila.addCell(new Cell().add("")
                .setTextAlignment(TextAlignment.JUSTIFIED)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBorderTop(Border.NO_BORDER)
                .setBorderBottom(Border.NO_BORDER)
                .setFontSize(10f));

        return novenaFila;
    }

    /* Decima Fila-> Firma, D, M, A */
    private Table decimaFila() {
        float terceraFila_columnWidth[] = {110, 20, 20, 20, 110, 20, 20, 20, 110, 20, 20, 20, 106};
        Table decimaFila = new Table(terceraFila_columnWidth);

        decimaFila.addCell(new Cell().add("FIRMA")
                .setTextAlignment(TextAlignment.JUSTIFIED)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setFontSize(10f));

        decimaFila.addCell(new Cell().add("D")
                .setTextAlignment(TextAlignment.JUSTIFIED)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setFontSize(10f));

        decimaFila.addCell(new Cell().add("M")
                .setTextAlignment(TextAlignment.JUSTIFIED)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setFontSize(10f));

        decimaFila.addCell(new Cell().add("A")
                .setTextAlignment(TextAlignment.JUSTIFIED)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setFontSize(10f));

        decimaFila.addCell(new Cell().add("FIRMA")
                .setTextAlignment(TextAlignment.JUSTIFIED)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setFontSize(10f));

        decimaFila.addCell(new Cell().add("D")
                .setTextAlignment(TextAlignment.JUSTIFIED)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setFontSize(10f));

        decimaFila.addCell(new Cell().add("M")
                .setTextAlignment(TextAlignment.JUSTIFIED)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setFontSize(10f));

        decimaFila.addCell(new Cell().add("A")
                .setTextAlignment(TextAlignment.JUSTIFIED)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setFontSize(10f));

        decimaFila.addCell(new Cell().add("FIRMA")
                .setTextAlignment(TextAlignment.JUSTIFIED)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setFontSize(10f));

        decimaFila.addCell(new Cell().add("D")
                .setTextAlignment(TextAlignment.JUSTIFIED)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setFontSize(10f));

        decimaFila.addCell(new Cell().add("M")
                .setTextAlignment(TextAlignment.JUSTIFIED)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setFontSize(10f));

        decimaFila.addCell(new Cell().add("A")
                .setTextAlignment(TextAlignment.JUSTIFIED)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setFontSize(10f));

        decimaFila.addCell(new Cell().add("")
                .setTextAlignment(TextAlignment.JUSTIFIED)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBorderTop(Border.NO_BORDER)
                .setBorderBottom(Border.NO_BORDER)
                .setFontSize(10f));

        return decimaFila;
    }

    /* Ultima Fila */
    private Table ultimaFila() {
        float ultimaFila_columnWidth[] = {112, 21, 21, 22, 112, 21, 21, 22, 112, 21, 21, 22, 100};
        Table ultimaFila = new Table(ultimaFila_columnWidth);

        ultimaFila.addCell(new Cell().add("")
                .setTextAlignment(TextAlignment.JUSTIFIED)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setMarginTop(10f)
                .setMarginBottom(10f)
                .setFontSize(10f));

        ultimaFila.addCell(new Cell().add("")
                .setTextAlignment(TextAlignment.JUSTIFIED)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setMarginTop(10f)
                .setMarginBottom(10f)
                .setFontSize(10f));

        ultimaFila.addCell(new Cell().add("")
                .setTextAlignment(TextAlignment.JUSTIFIED)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setMarginTop(10f)
                .setMarginBottom(10f)
                .setFontSize(10f));

        ultimaFila.addCell(new Cell().add("")
                .setTextAlignment(TextAlignment.JUSTIFIED)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setMarginTop(10f)
                .setMarginBottom(10f)
                .setFontSize(10f));

        ultimaFila.addCell(new Cell().add("")
                .setTextAlignment(TextAlignment.JUSTIFIED)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setMarginTop(10f)
                .setMarginBottom(10f)
                .setFontSize(10f));

        ultimaFila.addCell(new Cell().add("")
                .setTextAlignment(TextAlignment.JUSTIFIED)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setMarginTop(10f)
                .setMarginBottom(10f)
                .setFontSize(10f));

        ultimaFila.addCell(new Cell().add("")
                .setTextAlignment(TextAlignment.JUSTIFIED)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setMarginTop(10f)
                .setMarginBottom(10f)
                .setFontSize(10f));

        ultimaFila.addCell(new Cell().add("")
                .setTextAlignment(TextAlignment.JUSTIFIED)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setMarginTop(10f)
                .setMarginBottom(10f)
                .setFontSize(10f));

        ultimaFila.addCell(new Cell().add("")
                .setTextAlignment(TextAlignment.JUSTIFIED)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setFontSize(10f));

        ultimaFila.addCell(new Cell().add("")
                .setTextAlignment(TextAlignment.JUSTIFIED)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setMarginTop(10f)
                .setMarginBottom(10f)
                .setFontSize(10f));

        ultimaFila.addCell(new Cell().add("")
                .setTextAlignment(TextAlignment.JUSTIFIED)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setMarginTop(10f)
                .setMarginBottom(10f)
                .setFontSize(10f));

        ultimaFila.addCell(new Cell().add("")
                .setTextAlignment(TextAlignment.JUSTIFIED)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setMarginTop(10f)
                .setMarginBottom(10f)
                .setFontSize(10f));

        ultimaFila.addCell(new Cell().add("")
                .setTextAlignment(TextAlignment.JUSTIFIED)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBorderTop(Border.NO_BORDER)
                .setMarginTop(10f)
                .setMarginBottom(10f)
                .setFontSize(10f));

        return ultimaFila;
    }
    /* Reporte */
}
