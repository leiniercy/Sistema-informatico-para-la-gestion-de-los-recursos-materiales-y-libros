package trabajodediploma;

import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
@PWA(   name = "GENIUS", 
        shortName = "GENIUS",
        offlinePath="offline.html", 
        offlineResources = { "images/logo.png", "images/offline.png"  })
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
        logger.info("... generando 1 Usuario");

        // Vicedecano
        createUser1("User", "user", "user");
        createUser2("Asistente", "asistente", "asistente");
        createUser3("Almacenero", "almacen", "almacen");
        createUser4("Admin", "admin", "admin");
        createUser5("Leinier", "leiniercy", "1234");
        createUser6("Vicedecano", "vicedecano", "vicedecano");
    }

    private User createUser1(String name, String username, String password) {
        User user = new User();
        user.setName(name);
        user.setUsername(username);
        user.setHashedPassword(passwordEncoder.encode(password));
        user.setRoles(Collections.singleton(Rol.USER));
        userRepository.saveAndFlush(user);
        return user;
    }

    private User createUser2(String name, String username, String password) {
        User user = new User();
        user.setName(name);
        user.setUsername(username);
        user.setHashedPassword(passwordEncoder.encode(password));
        user.setRoles(Collections.singleton(Rol.ASISTENTE_CONTROL));
        userRepository.saveAndFlush(user);
        return user;
    }

    private User createUser3(String name, String username, String password) {
        User user = new User();
        user.setName(name);
        user.setUsername(username);
        user.setHashedPassword(passwordEncoder.encode(password));
        user.setRoles(Collections.singleton(Rol.RESP_ALMACEN));
        userRepository.saveAndFlush(user);
        return user;
    }

    private User createUser4(String name, String username, String password) {
        User user = new User();
        user.setName(name);
        user.setUsername(username);
        user.setHashedPassword(passwordEncoder.encode(password));
        user.setRoles(Collections.singleton(Rol.ADMIN));
        userRepository.saveAndFlush(user);
        return user;
    }

    private User createUser5(String name, String username, String password) {
        User user = new User();
        user.setName(name);
        user.setUsername(username);
        user.setHashedPassword(passwordEncoder.encode(password));
        user.setRoles(Stream.of(Rol.ADMIN, Rol.VD_ADIMN_ECONOMIA, Rol.RESP_ALMACEN, Rol.ASISTENTE_CONTROL, Rol.USER)
                .collect(Collectors.toSet()));
        userRepository.saveAndFlush(user);
        return user;
    }

    private User createUser6(String name, String username, String password) {
        User user = new User();
        user.setName(name);
        user.setUsername(username);
        user.setHashedPassword(passwordEncoder.encode(password));
        user.setRoles(Collections.singleton(Rol.VD_ADIMN_ECONOMIA));
        userRepository.saveAndFlush(user);
        return user;
    }

    @Autowired
    private EmailSenderService senderService;

    @EventListener(ApplicationReadyEvent.class)
    public void triggerMail() {
    senderService.sendSimpleEmail("leiniercaraballo08@gmail.com",
    "This is email body",
    "This is email subject");
    }
}
