/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabajodediploma.data.tools;


import com.vaadin.flow.component.upload.UploadI18N;
import java.util.Arrays;

/**
 *
 * @author leinier
 */


public class MyUploadI18n extends UploadI18N{

    public MyUploadI18n() {setDropFiles(new DropFiles()
                .setOne("Arrastrar archivo aquí")
                .setMany("Arrastrar archivos aquí"));
        setAddFiles(new AddFiles()
                .setOne("Cargar archivo...")
                .setMany("Cargar archivos..."));
        setCancel("Cancelar");
        setError(new Error()
                .setTooManyFiles("Demasiados archivos.")
                .setFileIsTooBig("El archivo es muy grande.")
                .setIncorrectFileType("Tipo de archivo incorrecto."));
        setUploading(new Uploading()
                .setStatus(new Uploading.Status()
                        .setConnecting("Conectando...")
                        .setStalled("Deteriorado")
                        .setProcessing("Procesando archivo...")
                        .setHeld("Colocado en la cola"))
                .setRemainingTime(new Uploading.RemainingTime()
                        .setPrefix("tiempo restante: ")
                        .setUnknown("tiempo restante desconocido"))
                .setError(new Uploading.Error()
                        .setServerUnavailable("La carga falló, inténtalo de nuevo más tarde")
                        .setUnexpectedServerError("La carga falló debido a un error del servidor")
                        .setForbidden("Subir prohibido")));
        setUnits(new Units()
                .setSize(Arrays.asList("B", "kB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB")));
    }

}
