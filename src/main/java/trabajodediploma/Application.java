package trabajodediploma;

import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import trabajodediploma.data.Rol;
import trabajodediploma.data.entity.Estudiante;
import trabajodediploma.data.entity.Grupo;
import trabajodediploma.data.entity.User;
import trabajodediploma.data.repository.UserRepository;
import trabajodediploma.data.tools.EmailSenderService;

/**
 * The entry point of the Spring Boot application.
 *
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 *
 */
@SpringBootApplication
@Theme(value = "scdrm")
@PWA(name = "GENIUS",
        shortName = "GENIUS",
        offlinePath = "offline.html",
        offlineResources = {"images/logo.png", "images/offline.png"})
@NpmPackage(value = "line-awesome", version = "1.3.0")
public class Application extends SpringBootServletInitializer implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    public void fillDB() {
        Logger logger = LoggerFactory.getLogger(getClass());
        if (userRepository.count() != 0L) {
            logger.info("Using existing database");
            return;
        }

        logger.info("Generating demo data");
        logger.info("... generando Usuarios");

        // Estudiantes
        createUser1("Leinier Caraballo Yanes", "leiniercy", "1234");
        createUser("Frank A Valero López", "frankavl", "1234");
        createUser("Daryan Gustavo Góngora", "daryanggg", "1234");
        createUser("Christian Sosa Jímenez", "christiansj", "1234");
        createUser("Catherín Mya Zamora Hernández", "catherinmzh", "1234");
        createUser("Leanet Alfonso Tirse", "leanetat", "1234");
        createUser("Sabrina Izquierdo Borrero", "sabrinadlcib", "1234");
        createUser("Sulennis Saray Jiménez Viltres", "sulennissjv", "1234");

        //Trabajadores
        createUser("José Emilio Badia Valdés", "jebadia", "1234");
        createUser("Aranelys Lazo Campo", "amlazo", "1234");
        createUser("Marcos Henrique Pelegrino Infante", "mhpelegrino", "1234");
        createUser("Maydalis Hernández Pérez", "mhernandezp", "1234");
        createUser("Reiman Alfonso Azcuy", "razcuy", "1234");
        createUser("Angel Alberto Vazquez Sánchez", "aavazquez", "1234");
        createUser("Yadira Ramírez Rodríguez", "yramirezr", "1234");
        createUser("Yordankis Matos López", "yluguen", "1234");
        createUser("Yasirys Terry González", "yterry", "1234");
    }

    private User createUser(String name, String username, String password) {
        User user = new User();
        user.setName(name);
        user.setUsername(username);
        user.setHashedPassword(passwordEncoder.encode(password));
        user.setRoles(Collections.singleton(Rol.USER));
        userRepository.saveAndFlush(user);
        return user;
    }

    private User createUser1(String name, String username, String password) {
        User user = new User();
        user.setName(name);
        user.setUsername(username);
        user.setHashedPassword(passwordEncoder.encode(password));
        user.setRoles(Stream.of(Rol.ADMIN, Rol.VD_ADIMN_ECONOMIA, Rol.RESP_ALMACEN, Rol.ASISTENTE_CONTROL, Rol.USER)
                .collect(Collectors.toSet()));
        userRepository.saveAndFlush(user);
        return user;
    }

//    @Autowired
//    private EmailSenderService senderService;

//    @EventListener(ApplicationReadyEvent.class)
//    public void triggerMail() {
//       
//        senderService.sendSimpleEmail("leiniercy@estudiantes.uci.cu",
//                "This is email body",
//                "This is email subject");
//        
//    }

}
