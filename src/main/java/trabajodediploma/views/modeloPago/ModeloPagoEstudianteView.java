package trabajodediploma.views.modeloPago;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LitRenderer;
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

import trabajodediploma.data.entity.Estudiante;
import trabajodediploma.data.entity.Libro;
import trabajodediploma.data.entity.ModeloPago;
import trabajodediploma.data.entity.ModeloPagoEstudiante;
import trabajodediploma.data.entity.RecursoMaterial;
import trabajodediploma.data.service.EstudianteService;
import trabajodediploma.data.service.LibroService;
import trabajodediploma.data.service.ModeloPagoService;
import trabajodediploma.data.service.RecursoMaterialService;
import trabajodediploma.views.MainLayout;
import trabajodediploma.views.footer.MyFooter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.vaadin.gatanaso.MultiselectComboBox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

public class ModeloPagoEstudianteView extends Div {

    private Grid<ModeloPago> grid = new Grid<>(ModeloPago.class, false);
    private GridListDataView<ModeloPago> gridListDataView;
    private Grid.Column<ModeloPago> imagenColumn;
    private Grid.Column<ModeloPago> estudianteColumn;
    private Grid.Column<ModeloPago> librosColumn;
    private Grid.Column<ModeloPago> editColumn;
    private EstudianteService estudianteService;
    private LibroService libroService;
    private Dialog dialog;
    private Html total;
    private HorizontalLayout toolbar;
    private HorizontalLayout buttons;
    private Div header;
    private Dialog reportDialog;
    private ModeloPagoService modeloPagoService;
    private List<ModeloPago> listModelosPago;
    private ModeloPagoEstudiante modeloEstudiante;
    private ComboBox<Estudiante> estudianteFilter;

    ModeloPagoEstudianteForm form;

    ComboBox<Estudiante> estudiante = new ComboBox<>();
    MultiselectComboBox<Libro> libros = new MultiselectComboBox<>();

    public ModeloPagoEstudianteView(
            @Autowired ModeloPagoService modeloPagoService,
            @Autowired EstudianteService estudianteService,
            @Autowired LibroService libroService) {
        addClassName("container__modelo");
        this.modeloPagoService = modeloPagoService;
        this.estudianteService = estudianteService;
        this.libroService = libroService;
        listModelosPago = new LinkedList<>();;
        updateList();
        configureForm();
        configureGrid();
        add(menuBar(), getContent());
    }

    /* Contenido de la vista */
    private Div getContent() {

        Div formContent = new Div(form);
        formContent.addClassName("form_content");
        Div gridContent = new Div(grid);
        gridContent.addClassName("grid_content");

        Div container = new Div(gridContent);
        container.addClassName("container");
        container.setSizeFull();

        /* Dialog Header */
        Button closeButton = new Button(new Icon("lumo", "cross"), (e) -> dialog.close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Span title = new Span("Libro");
        Div titleDiv = new Div(title);
        titleDiv.addClassName("div-dialog-title");
        Div buttonDiv = new Div(closeButton);
        buttonDiv.addClassName("div-dialog-button");
        header = new Div(titleDiv, buttonDiv);
        header.addClassName("div-dialog-header");
        /* Dialog Header */

        dialog = new Dialog(header, formContent);

        return container;
    }

    /* Tabla */
 /* Configuracion de la tabla */
    private void configureGrid() {
        grid.setClassName("container__modelo__grid");
        grid.getStyle().set("max-height", "550px");

        LitRenderer<ModeloPago> imagenRenderer = LitRenderer
                .<ModeloPago>of("<img style='height: 64px' src=${item.imagen} />")
                .withProperty("imagen", ModeloPago::getImagen);

        imagenColumn = grid.addColumn(imagenRenderer).setHeader("Imagen").setAutoWidth(true);

        estudianteColumn = grid.addColumn(new ComponentRenderer<>(modelo -> {
            modeloEstudiante = (ModeloPagoEstudiante) modelo;
            HorizontalLayout hl = new HorizontalLayout();
            hl.setAlignItems(Alignment.CENTER);
            Avatar avatar = new Avatar(modeloEstudiante.getEstudiante().getUser().getName(),
                    modeloEstudiante.getEstudiante().getUser().getProfilePictureUrl());
            VerticalLayout vl = new VerticalLayout();
            vl.getStyle().set("line-height", "0");
            Span name = new Span();
            name.addClassNames("name");
            name.setText(modeloEstudiante.getEstudiante().getUser().getName());
            Span email = new Span();
            email.addClassNames("text-s", "text-secondary");
            email.setText(modeloEstudiante.getEstudiante().getEmail());
            vl.add(name, email);
            hl.add(avatar, vl);
            return hl;
        })).setHeader("Estudiante").setFrozen(true).setAutoWidth(true).setSortable(true);

        librosColumn = grid.addColumn(new ComponentRenderer<>(modelo -> {
            VerticalLayout layout = new VerticalLayout();
            layout.getStyle().set("line-height", "0.5");
            modeloEstudiante = (ModeloPagoEstudiante) modelo;
            Span span_libros = new Span();
            span_libros.setWidth("100%");
            List<Libro> libros = new LinkedList<>(modeloEstudiante.getLibros());
            String listLibros = new String();
            if (libros.size() != 0) {
                listLibros += "" + libros.get(0).getTitulo();
                for (int i = 1; i < libros.size(); i++) {
                    listLibros += ", " + libros.get(i).getTitulo();
                }
            }
            span_libros.setText(listLibros);
            layout.add(span_libros);
            layout.setWidth("100%");
            return layout;
        })).setHeader("Libros").setAutoWidth(true);

        editColumn = grid.addComponentColumn(modelo -> {
            modeloEstudiante = (ModeloPagoEstudiante) modelo;
            Button editButton = new Button(VaadinIcon.EDIT.create());
            editButton.addClickListener(e -> this.editModelo(modeloEstudiante));
            editButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            return editButton;
        }).setTextAlign(ColumnTextAlign.CENTER).setFrozen(true).setFlexGrow(0);

        Filtros();

        HeaderRow headerRow = grid.appendHeaderRow();
        headerRow.getCell(estudianteColumn).setComponent(estudianteFilter);

        gridListDataView = grid.setItems(listModelosPago);
        grid.setPageSize(listModelosPago.size());
        grid.setAllRowsVisible(true);
        grid.setSizeFull();
        grid.setWidthFull();
        grid.setHeightFull();
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);

    }

    /* Filtros */
    private void Filtros() {

        estudianteFilter = new ComboBox<>();
        estudianteFilter.setItems(estudianteService.findAll());
        estudianteFilter.setItemLabelGenerator(estudiante -> estudiante.getUser().getName());
        estudianteFilter.setPlaceholder("Filtrar");
        estudianteFilter.setClearButtonVisible(true);
        estudianteFilter.setWidth("100%");
        estudianteFilter.addValueChangeListener(event -> {
            if (estudianteFilter.getValue() == null) {
                gridListDataView = grid.setItems(listModelosPago);
            } else {
                gridListDataView.addFilter(modelo -> areEstudianteEqual(modelo, estudianteFilter));
            }
        });

    }

    private boolean areEstudianteEqual(ModeloPago modelo, ComboBox<Estudiante> estudianteFilter) {
        String estudianteFilterValue = estudianteFilter.getValue().getUser().getName();
        modeloEstudiante = (ModeloPagoEstudiante) modelo;
        if (estudianteFilterValue != null) {
            return StringUtils.equals(modeloEstudiante.getEstudiante().getUser().getName(),
                    estudianteFilterValue);
        }
        return true;
    }

    // Barra de menu
    private HorizontalLayout menuBar() {
        buttons = new HorizontalLayout();
        Button refreshButton = new Button(VaadinIcon.REFRESH.create());
        refreshButton.addClickListener(click -> updateList());
        refreshButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button deleteButton = new Button(VaadinIcon.TRASH.create());
        deleteButton.addClickListener(click -> deleteModelo());
        deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button addButton = new Button(VaadinIcon.PLUS.create());
        addButton.addClickListener(click -> addModelo());
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button modelButton = new Button(VaadinIcon.FILE.create());
        modelButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        modelButton.addClickListener(click -> formModeloPago());

        buttons.add(refreshButton, deleteButton, addButton, modelButton);

        if (listModelosPago.size() == 1) {
            total = new Html("<span>Total: <b>" + listModelosPago.size() + "</b> modelo</span>");
        } else if (listModelosPago.size() > 1 || listModelosPago.size() == 0) {
            total = new Html("<span>Total: <b>" + listModelosPago.size() + "</b> modelos</span>");
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

    private void deleteModelo() {
        try {

            if (grid.asMultiSelect().isEmpty()) {
                Notification notification = Notification.show("Debe elegir al menos un campo", 5000,
                        Notification.Position.MIDDLE);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } else {
                deleteItems(grid.getSelectedItems().size(), grid.getSelectedItems());
                updateList();
                toolbar.remove(total);
                if (listModelosPago.size() == 1) {
                    total = new Html("<span>Total: <b>" + listModelosPago.size()
                            + "</b> modelo</span>");
                } else if (listModelosPago.size() > 1 || listModelosPago.size() == 0) {
                    total = new Html("<span>Total: <b>" + listModelosPago.size()
                            + "</b> modelos</span>");
                }
                toolbar.addComponentAtIndex(1, total);
                toolbar.setFlexGrow(1, buttons);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Notification notification = Notification.show(
                    "Ocurrió un problema al intentar eliminar el estudiante",
                    2000,
                    Notification.Position.MIDDLE);
            ;
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void deleteItems(int cantidad, Set<ModeloPago> modelo) {
        Notification notification;
        modeloPagoService.deleteAll(modelo);
        if (cantidad == 1) {
            notification = Notification.show("Modelo de pago eliminado", 2000,
                    Notification.Position.BOTTOM_START);
        } else {
            notification = Notification.show("Han sido eliminados" + cantidad + " modelos de pago", 5000,
                    Notification.Position.BOTTOM_START);
        }
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void configureForm() {
        form = new ModeloPagoEstudianteForm(estudianteService.findAll(), libroService.findAll());
        form.setWidth("25em");
        form.addListener(ModeloPagoEstudianteForm.SaveEvent.class, this::saveModelo);
        form.addListener(ModeloPagoEstudianteForm.CloseEvent.class, e -> closeEditor());
    }

    private void saveModelo(ModeloPagoEstudianteForm.SaveEvent event) {

        listModelosPago.clear();
        modeloPagoService.findAll().stream().forEach(modelo -> {
            if (modelo instanceof ModeloPagoEstudiante) {
                modeloEstudiante = (ModeloPagoEstudiante) modelo;
                if (modeloEstudiante.getEstudiante().getId() == event.getModeloPago().getEstudiante().getId()) {
                    listModelosPago.add(modeloEstudiante);
                }
            }
        });

        if (listModelosPago.size() != 0) {
            Notification notification = Notification.show(
                    "Este estudiante ya tiene un modelo de pago",
                    5000,
                    Notification.Position.MIDDLE);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        } else {
            if (event.getModeloPago().getId() == null) {
                modeloPagoService.save(event.getModeloPago());
                Notification notification = Notification.show(
                        "Modelo de pago añadido",
                        2000,
                        Notification.Position.BOTTOM_START);
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } else {
                modeloPagoService.update(event.getModeloPago());
                Notification notification = Notification.show(
                        "Modelo modificado",
                        5000,
                        Notification.Position.BOTTOM_START);
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            }
        }
        toolbar.remove(total);
        if (listModelosPago.size() == 1) {
            total = new Html("<span>Total: <b>" + listModelosPago.size() + "</b> modelo</span>");
        } else if (listModelosPago.size() > 1 || listModelosPago.size() == 0) {
            total = new Html("<span>Total: <b>" + listModelosPago.size() + "</b> modelos</span>");
        }
        toolbar.addComponentAtIndex(1, total);
        toolbar.setFlexGrow(1, buttons);
        updateList();
        closeEditor();
    }

    private void editModelo(ModeloPagoEstudiante modeloPago) {
        if (modeloPago == null) {
            closeEditor();
        } else {
            form.setModeloPago(modeloPago);
            form.setVisible(true);
            addClassName("editing");
            dialog.open();
        }
    }

    private void addModelo() {
        grid.asMultiSelect().clear();
        ModeloPagoEstudiante modelo = new ModeloPagoEstudiante();
        modelo.setImagen("");
        editModelo(modelo);
    }

    private void closeEditor() {
        ModeloPagoEstudiante modelo = new ModeloPagoEstudiante();
        modelo.setImagen("");
        form.setModeloPago(modelo);
        form.setVisible(false);
        removeClassName("editing");
        dialog.close();
    }

    private void updateList() {
        listModelosPago.clear();
        List<ModeloPago> aux = modeloPagoService.findAll();
        for (int i = 0; i < aux.size(); i++) {
            if (aux.get(i) instanceof ModeloPagoEstudiante) {
                modeloEstudiante = (ModeloPagoEstudiante) aux.get(i);
                listModelosPago.add(modeloEstudiante);
            }
        }
        grid.setItems(listModelosPago);
        grid.setPageSize(listModelosPago.size());
    }

    /* Form crear modelo de pago */
    private void formModeloPago() {
        reportDialog = new Dialog();
        Div reportContainer = new Div();
        reportContainer.addClassNames("report-form-container");

        /* Dialog Header */
        Button closeButton = new Button(new Icon("lumo", "cross"), (e) -> reportDialog.close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Span title = new Span("Modelo de Pago");
        Div titleDiv = new Div(title);
        titleDiv.addClassName("div-dialog-title");
        Div buttonDiv = new Div(closeButton);
        buttonDiv.addClassName("div-dialog-button");
        header = new Div(titleDiv, buttonDiv);
        header.addClassName("div-dialog-header");
        /* Dialog Header */

        estudiante.setPlaceholder("Estudiante");
        estudiante.setRequired(true);
        estudiante.setItems(estudianteService.findAll());
        estudiante.setItemLabelGenerator(estudiante -> estudiante.getUser().getName());

        libros.setPlaceholder("Libros");
        libros.setRequired(true);
        libros.setItems(libroService.findAll());
        libros.setItemLabelGenerator(libro -> libro.getTitulo());

        Anchor reporteLink = new Anchor(ModeloPagoPDF(), "Crear Modelo");
        reporteLink.addClassNames("link-modelo");
        reporteLink.setEnabled(false);
        reporteLink.setTarget("_BLANK");

        reportContainer.add(header, estudiante, libros, reporteLink);
        reportDialog.add(reportContainer);
        reportDialog.open();

        estudiante.addValueChangeListener(e -> {
            libros.addValueChangeListener(event -> {
                if (estudiante.getValue() != null && libros.getValue() != null) {
                    reporteLink.setEnabled(true);
                }
            });
        });

        libros.addValueChangeListener(e -> {
            estudiante.addValueChangeListener(event -> {
                if (estudiante.getValue() != null && libros.getValue() != null) {
                    reporteLink.setEnabled(true);
                }
            });
        });

        reporteLink.addBlurListener(e -> {
            if (!estudiante.isEmpty() && !libros.isEmpty()) {
                reportDialog.close();
            } else {
                Notification notification = Notification.show(
                        "Debe seleccionar todos los campos del formulario",
                        5000,
                        Notification.Position.MIDDLE);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

    }

    /* cear pdf modelo de pago */
    private StreamResource ModeloPagoPDF() {
        StreamResource source = new StreamResource("ModeloPago.pdf", () -> {

            String path = "src/main/resources/META-INF/resources/archivos/ModeloPago.pdf";

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

                document.close();

                File initialFile = new File(path);
                InputStream targetStream = new FileInputStream(initialFile);
                return targetStream;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                System.out.println("Error al crear el modelo");
                return null;
            }

        });
        return source;
    }

    private Table primeraFila() {
        float firstGrow_columnWidth[] = {300, 125, 175};
        Table firstGrow = new Table(firstGrow_columnWidth);
        /* Primer Fila */
        firstGrow.addCell(new Cell().add("Organismo: Ministerio Educación Superior")
                .setTextAlignment(TextAlignment.LEFT)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setMarginTop(1f)
                .setMarginBottom(1f)
                .setFontSize(10f));

        firstGrow.addCell(new Cell().add("()Factura")
                .setTextAlignment(TextAlignment.LEFT)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setMarginTop(1f)
                .setMarginBottom(1f)
                .setBorderRight(Border.NO_BORDER)
                .setBorderBottom(Border.NO_BORDER)
                .setFontSize(10f));

        firstGrow.addCell(new Cell().add("()Vale de entrega o Devolución")
                .setTextAlignment(TextAlignment.LEFT)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setMarginTop(1f)
                .setMarginBottom(1f)
                .setBorderLeft(Border.NO_BORDER)
                .setBorderBottom(Border.NO_BORDER)
                .setFontSize(10f));
        /* Primer Fila */
 /* Segunda Fila */
        firstGrow.addCell(new Cell().add("Empresa: Universidad de las Ciencias Informáticas")
                .setTextAlignment(TextAlignment.LEFT)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setMarginTop(1f)
                .setMarginBottom(1f)
                .setFontSize(10f));

        firstGrow.addCell(new Cell().add("()Transferencia/Almacenes")
                .setTextAlignment(TextAlignment.LEFT)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setMarginTop(1f)
                .setMarginBottom(1f)
                .setBorderRight(Border.NO_BORDER)
                .setBorderBottom(Border.NO_BORDER)
                .setBorderTop(Border.NO_BORDER)
                .setFontSize(10f));

        firstGrow.addCell(new Cell().add("()Solicitud de Entreg. Mat.")
                .setTextAlignment(TextAlignment.LEFT)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setMarginTop(1f)
                .setMarginBottom(1f)
                .setBorderLeft(Border.NO_BORDER)
                .setBorderTop(Border.NO_BORDER)
                .setBorderBottom(Border.NO_BORDER)
                .setFontSize(10f));
        /* Segunda Fila */
 /* Tercera Fila */
        firstGrow.addCell(new Cell().add("Unidad/Almacén: " + estudiante.getValue().getFacultad())
                .setTextAlignment(TextAlignment.LEFT)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setMarginTop(1f)
                .setMarginBottom(1f)
                .setFontSize(10f));

        firstGrow.addCell(new Cell().add("()Ajuste de inventario")
                .setTextAlignment(TextAlignment.LEFT)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setMarginTop(1f)
                .setMarginBottom(1f)
                .setBorderRight(Border.NO_BORDER)
                .setBorderTop(Border.NO_BORDER)
                .setBorderBottom(Border.NO_BORDER)
                .setFontSize(10f));

        firstGrow.addCell(new Cell().add("()Produción Terminada")
                .setTextAlignment(TextAlignment.LEFT)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setMarginTop(1f)
                .setMarginBottom(1f)
                .setBorderLeft(Border.NO_BORDER)
                .setBorderTop(Border.NO_BORDER)
                .setBorderBottom(Border.NO_BORDER)
                .setFontSize(10f));
        /* Tercera Fila */
 /* Cuarta Fila */
        firstGrow.addCell(new Cell().add("Suministrador: " + estudiante.getValue().getUser().getName())
                .setTextAlignment(TextAlignment.LEFT)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setMarginTop(1f)
                .setMarginBottom(1f)
                .setFontSize(10f));

        firstGrow.addCell(new Cell().add("()Informe de Recepción")
                .setTextAlignment(TextAlignment.LEFT)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setMarginTop(1f)
                .setMarginBottom(1f)
                .setBorderRight(Border.NO_BORDER)
                .setBorderTop(Border.NO_BORDER)
                .setFontSize(10f));

        firstGrow.addCell(new Cell().add("()Conduce")
                .setTextAlignment(TextAlignment.LEFT)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setMarginTop(1f)
                .setMarginBottom(1f)
                .setBorderLeft(Border.NO_BORDER)
                .setBorderTop(Border.NO_BORDER)
                .setFontSize(10f));
        /* Cuarta Fila */

        return firstGrow;
    }

    private Table segundaFila() {
        float secondGrow_columnWidth[] = {2, 2, 200, 140, 140};
        Table secondGrow = new Table(secondGrow_columnWidth);
        /* Primer Fila */
        secondGrow.addCell(new Cell().add("END")
                .setTextAlignment(TextAlignment.LEFT)
                .setVerticalAlignment(VerticalAlignment.BOTTOM)
                .setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setRotationAngle(Math.toRadians(90))
                .setBorderRight(Border.NO_BORDER)
                .setBorderTop(Border.NO_BORDER)
                .setBorderBottom(Border.NO_BORDER)
                .setFontSize(10f));

        secondGrow.addCell(new Cell().add("CED")
                .setTextAlignment(TextAlignment.LEFT)
                .setVerticalAlignment(VerticalAlignment.BOTTOM)
                .setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setRotationAngle(Math.toRadians(90))
                .setBorderLeft(Border.NO_BORDER)
                .setBorderTop(Border.NO_BORDER)
                .setBorderBottom(Border.NO_BORDER)
                .setFontSize(10f));

        secondGrow.addCell(new Cell().add("Orden No. ")
                .setTextAlignment(TextAlignment.LEFT)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBorderTop(Border.NO_BORDER)
                .setFontSize(10f));

        secondGrow.addCell(new Cell().add("Centro de Costo. ")
                .setTextAlignment(TextAlignment.LEFT)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBorderTop(Border.NO_BORDER)
                .setFontSize(10f));

        secondGrow.addCell(new Cell().add("Código. ")
                .setTextAlignment(TextAlignment.LEFT)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBorderTop(Border.NO_BORDER)
                .setFontSize(10f));
        /* Primer Fila */
 /* Segunda Fila */
        secondGrow.addCell(new Cell().add("DEP")
                .setTextAlignment(TextAlignment.RIGHT)
                .setVerticalAlignment(VerticalAlignment.BOTTOM)
                .setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setRotationAngle(Math.toRadians(90))
                .setBorderRight(Border.NO_BORDER)
                .setBorderTop(Border.NO_BORDER)
                .setFontSize(10f));

        secondGrow.addCell(new Cell().add("PRO")
                .setTextAlignment(TextAlignment.RIGHT)
                .setVerticalAlignment(VerticalAlignment.BOTTOM)
                .setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setRotationAngle(Math.toRadians(90))
                .setBorderLeft(Border.NO_BORDER)
                .setBorderTop(Border.NO_BORDER)
                .setFontSize(10f));

        secondGrow.addCell(new Cell().add("Lote No. ")
                .setTextAlignment(TextAlignment.LEFT)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBorderTop(Border.NO_BORDER)
                .setFontSize(10f));

        secondGrow.addCell(new Cell().add("Producto. ")
                .setTextAlignment(TextAlignment.LEFT)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBorderTop(Border.NO_BORDER)
                .setFontSize(10f));

        secondGrow.addCell(new Cell().add("Otros. ")
                .setTextAlignment(TextAlignment.LEFT)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBorderTop(Border.NO_BORDER)
                .setFontSize(10f));
        /* Segunda Fila */

        return secondGrow;
    }

    private Table terceraFila() {
        float terceraFila_columnWidth[] = {80, 200, 30, 30, 80, 80, 80};
        Table terceraFila = new Table(terceraFila_columnWidth);

        terceraFila.addCell(new Cell().add("Código")
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBorderTop(Border.NO_BORDER)
                .setFontSize(10f));

        terceraFila.addCell(new Cell().add("Descripción")
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBorderTop(Border.NO_BORDER)
                .setFontSize(10f));

        terceraFila.addCell(new Cell().add("U.M.")
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBorderTop(Border.NO_BORDER)
                .setFontSize(10f));

        terceraFila.addCell(new Cell().add("Cantidad")
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBorderTop(Border.NO_BORDER)
                .setFontSize(10f));

        terceraFila.addCell(new Cell().add("Precio")
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBorderTop(Border.NO_BORDER)
                .setFontSize(10f));

        terceraFila.addCell(new Cell().add("Importe")
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBorderTop(Border.NO_BORDER)
                .setFontSize(10f));

        terceraFila.addCell(new Cell().add("Saldo Existencia")
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBorderTop(Border.NO_BORDER)
                .setFontSize(10f));

        return terceraFila;
    }

    private Table cuartaFila() {
        float cuartaFila_columnWidth[] = {78, 177, 32, 48, 78, 78, 80};
        Table cuartaFila = new Table(cuartaFila_columnWidth);

        List<Libro> listLibros = new LinkedList<>(libros.getValue());

        for (int i = 0; i < listLibros.size(); i++) {

            cuartaFila.addCell(new Cell().add("  ")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setBorderTop(Border.NO_BORDER)
                    .setFontSize(10f));

            cuartaFila.addCell(new Cell().add(listLibros.get(i).getTitulo())
                    .setTextAlignment(TextAlignment.LEFT)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setBorderTop(Border.NO_BORDER)
                    .setFontSize(10f));

            cuartaFila.addCell(new Cell().add("U")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setBorderTop(Border.NO_BORDER)
                    .setFontSize(10f));

            cuartaFila.addCell(new Cell().add("1")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setBorderTop(Border.NO_BORDER)
                    .setFontSize(10f));

            cuartaFila.addCell(new Cell().add("  ")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setBorderTop(Border.NO_BORDER)
                    .setFontSize(10f));

            cuartaFila.addCell(new Cell().add("  ")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setBorderTop(Border.NO_BORDER)
                    .setFontSize(10f));

            cuartaFila.addCell(new Cell().add("  ")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setBorderTop(Border.NO_BORDER)
                    .setFontSize(10f));

        }

        return cuartaFila;
    }

    private Table quintaFila() {
        float quintaFila_columnWidth[] = {400, 400, 400};
        Table quintaFila = new Table(quintaFila_columnWidth);

        quintaFila.addCell(new Cell().add("Despachado por:")
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBorderTop(Border.NO_BORDER)
                .setFontSize(10f));

        quintaFila.addCell(new Cell().add("Recibido por:")
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBorderTop(Border.NO_BORDER)
                .setFontSize(10f));

        quintaFila.addCell(new Cell().add("Autorizado por:")
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBorderTop(Border.NO_BORDER)
                .setFontSize(10f));

        return quintaFila;
    }

    private Table sextaFila() {
        float sexta_columnWidth[] = {320, 100, 320, 65, 320, 80};
        Table sextaFila = new Table(sexta_columnWidth);

        sextaFila.addCell(new Cell().add("Nombre y apellidos:")
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBorderTop(Border.NO_BORDER)
                .setFontSize(10f));

        sextaFila.addCell(new Cell().add("Firma:")
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBorderTop(Border.NO_BORDER)
                .setFontSize(10f));

        sextaFila.addCell(new Cell().add("Nombre y apellidos:")
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBorderTop(Border.NO_BORDER)
                .setFontSize(10f));

        sextaFila.addCell(new Cell().add("Firma:")
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBorderTop(Border.NO_BORDER)
                .setFontSize(10f));

        sextaFila.addCell(new Cell().add("Nombre y apellidos:")
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBorderTop(Border.NO_BORDER)
                .setFontSize(10f));

        sextaFila.addCell(new Cell().add("Firma:")
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBorderTop(Border.NO_BORDER)
                .setFontSize(10f));

        return sextaFila;
    }

    private Table septimaFila() {
        float septimaFila_columnWidth[] = {305, 120, 305, 100, 305, 105};
        Table septimaFila = new Table(septimaFila_columnWidth);

        septimaFila.addCell(new Cell().add(" ")
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBorderTop(Border.NO_BORDER)
                .setMarginTop(10f)
                .setMarginBottom(10f)
                .setFontSize(10f));

        septimaFila.addCell(new Cell().add(" ")
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBorderTop(Border.NO_BORDER)
                .setMarginTop(10f)
                .setMarginBottom(10f)
                .setFontSize(10f));

        septimaFila.addCell(new Cell().add(" ")
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBorderTop(Border.NO_BORDER)
                .setMarginTop(10f)
                .setMarginBottom(10f)
                .setFontSize(10f));

        septimaFila.addCell(new Cell().add(" ")
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBorderTop(Border.NO_BORDER)
                .setMarginTop(10f)
                .setMarginBottom(10f)
                .setFontSize(10f));

        septimaFila.addCell(new Cell().add(" ")
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBorderTop(Border.NO_BORDER)
                .setMarginTop(10f)
                .setMarginBottom(10f)
                .setFontSize(10f));

        septimaFila.addCell(new Cell().add(" ")
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBorderTop(Border.NO_BORDER)
                .setMarginTop(10f)
                .setMarginBottom(10f)
                .setFontSize(10f));

        return septimaFila;
    }

    private Table octavaFila() {
        float octavaFila_columnWidth[] = {300, 300, 300, 40, 40, 40, 40};
        Table octavaFila = new Table(octavaFila_columnWidth);

        octavaFila.addCell(new Cell().add("Anotado Sub-Mayor Inventario:")
                .setTextAlignment(TextAlignment.LEFT)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBorderTop(Border.NO_BORDER)
                .setMarginBottom(10f)
                .setFontSize(10f));

        octavaFila.addCell(new Cell().add("Contabilizado por:")
                .setTextAlignment(TextAlignment.LEFT)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBorderTop(Border.NO_BORDER)
                .setMarginBottom(10f)
                .setFontSize(10f));

        octavaFila.addCell(new Cell().add("Solicitud de materiales No.")
                .setTextAlignment(TextAlignment.LEFT)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBorderTop(Border.NO_BORDER)
                .setMarginBottom(10f)
                .setFontSize(10f));

        octavaFila.addCell(new Cell().add("D")
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBorderTop(Border.NO_BORDER)
                .setMarginBottom(10f)
                .setFontSize(10f));

        octavaFila.addCell(new Cell().add("M")
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBorderTop(Border.NO_BORDER)
                .setMarginBottom(10f)
                .setFontSize(10f));

        octavaFila.addCell(new Cell().add("A")
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBorderTop(Border.NO_BORDER)
                .setMarginBottom(10f)
                .setFontSize(10f));

        octavaFila.addCell(new Cell().add("No.")
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBorderTop(Border.NO_BORDER)
                .setMarginBottom(10f)
                .setFontSize(10f));

        return octavaFila;
    }
    /* Fin-> cear pdf modelo de pago */
}
