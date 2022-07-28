package trabajodediploma.views.libros;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import trabajodediploma.data.entity.Libro;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class LibroFormTest {

//    List<Libro> libros;
//    private Libro libro1;
//    private Libro libro2;

    /*
     * @Before
     * agrega datos ficticios que se utilizan para las pruebas
     * este metodo se ejecuta antes de cada metodo @Test
     */
    @Before
    public void setupData() {
//
//        libros = new ArrayList<>();
//
//        libro1 = new Libro();
//
//        libro2 = new Libro();
//
//        libros.add(libro1);
//        libros.add(libro2);

    }

    /*
     * JUnit:
     * -> Valida que los campos se completen correctamente,
     * primero inicializando el formulario de libro
     * con algunos libros y luego configurando un bean de libro para el formulario
     * -> assertEquals() para comparar los valores de los campos disponibles
     */

    @Test
    public void formFieldsPopulated() {

    }

    /*
     * Funcionalidad guardar:
     * 1: Inicialice el formulario con un libro vacío .
     * 2: Rellene los valores en el formulario.
     * 3: Capture el libro guardada en una AtomicReference .
     * 4: Haga clic en el botón Guardar y lea el contacto guardado.
     * 5: Una vez que los datos del evento estén disponibles, verifique que el bean
     * contenga los valores esperados.
     */
    @Test
    public void saveEventHasCorrectValues() {
//        LibroForm form = new LibroForm();
//        Libro l = new Libro();
//        form.titulo.setValue("Progamación Orientada a Objetos");
//        form.autor.setValue("Leinier Caraballo");
//        form.volumen.setValue(1);
//        form.cantidad.setValue(5);
//        form.precio.setValue(10.0);
//
//        AtomicReference<Libro> saveLibroRef = new AtomicReference<>(null);  
//        form.addListener(LibroForm.SaveEvent.class, e -> {
//            saveLibroRef.set(e.getLibro());
//        });
//        form.save.click();
//
//        Libro saveLibro = saveLibroRef.get();
//
//        Assert.assertEquals("Progamación Orientada a Objetos", saveLibro.getTitulo());
//        Assert.assertEquals("Leinier Caraballo", saveLibro.getAutor());
//        Assert.assertEquals("1", saveLibro.getVolumen().toString());
//        Assert.assertEquals("5", saveLibro.getCantidad().toString());
//        Assert.assertEquals("10.0", saveLibro.getPrecio().toString());
    }
}