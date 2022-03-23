package trabajodediploma.data.generator;

import com.vaadin.exampledata.DataType;
import com.vaadin.exampledata.ExampleDataGenerator;
import com.vaadin.flow.spring.annotation.SpringComponent;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import trabajodediploma.data.Role;
import trabajodediploma.data.entity.Area;
import trabajodediploma.data.entity.DestinoFinal;
import trabajodediploma.data.entity.Estudiante;
import trabajodediploma.data.entity.Grupo;
import trabajodediploma.data.entity.Libro;
import trabajodediploma.data.entity.Trabajador;
import trabajodediploma.data.entity.User;
import trabajodediploma.data.service.AreaRepository;
import trabajodediploma.data.service.DestinoFinalRepository;
import trabajodediploma.data.service.EstudianteRepository;
import trabajodediploma.data.service.GrupoRepository;
import trabajodediploma.data.service.LibroRepository;
import trabajodediploma.data.service.TrabajadorRepository;
import trabajodediploma.data.service.UserRepository;

@SpringComponent
public class DataGenerator {

    @Bean
    public CommandLineRunner loadData(PasswordEncoder passwordEncoder, UserRepository userRepository,
            AreaRepository areaRepository, GrupoRepository grupoRepository, LibroRepository libroRepository,
            DestinoFinalRepository destinoFinalRepository, EstudianteRepository estudianteRepository,
            TrabajadorRepository trabajadorRepository) {
        return args -> {
            Logger logger = LoggerFactory.getLogger(getClass());
            if (userRepository.count() != 0L) {
                logger.info("Using existing database");
                return;
            }
            int seed = 123;

            logger.info("Generating demo data");

            logger.info("... generating 2 User entities...");
            User user = new User();
            user.setName("John Normal");
            user.setUsername("user");
            user.setHashedPassword(passwordEncoder.encode("user"));
            user.setProfilePictureUrl(
                    "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=128&h=128&q=80");
            user.setRoles(Collections.singleton(Role.USER));
            userRepository.save(user);
            User admin = new User();
            admin.setName("Emma Powerful");
            admin.setUsername("admin");
            admin.setHashedPassword(passwordEncoder.encode("admin"));
            admin.setProfilePictureUrl(
                    "https://images.unsplash.com/photo-1607746882042-944635dfe10e?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=128&h=128&q=80");
            admin.setRoles(Set.of(Role.USER, Role.ADMIN));
            userRepository.save(admin);
            logger.info("... generating 100 Area entities...");
            ExampleDataGenerator<Area> areaRepositoryGenerator = new ExampleDataGenerator<>(Area.class,
                    LocalDateTime.of(2022, 3, 23, 0, 0, 0));
            areaRepositoryGenerator.setData(Area::setNombre, DataType.WORD);
            areaRepository.saveAll(areaRepositoryGenerator.create(100, seed));

            logger.info("... generating 100 Grupo entities...");
            ExampleDataGenerator<Grupo> grupoRepositoryGenerator = new ExampleDataGenerator<>(Grupo.class,
                    LocalDateTime.of(2022, 3, 23, 0, 0, 0));
            grupoRepositoryGenerator.setData(Grupo::setNumero, DataType.NUMBER_UP_TO_100);
            grupoRepository.saveAll(grupoRepositoryGenerator.create(100, seed));

            logger.info("... generating 100 Libro entities...");
            ExampleDataGenerator<Libro> libroRepositoryGenerator = new ExampleDataGenerator<>(Libro.class,
                    LocalDateTime.of(2022, 3, 23, 0, 0, 0));
            libroRepositoryGenerator.setData(Libro::setImage, DataType.BOOK_IMAGE_URL);
            libroRepositoryGenerator.setData(Libro::setTitulo, DataType.BOOK_TITLE);
            libroRepositoryGenerator.setData(Libro::setAutor, DataType.FULL_NAME);
            libroRepositoryGenerator.setData(Libro::setVolumen, DataType.NUMBER_UP_TO_10);
            libroRepositoryGenerator.setData(Libro::setTomo, DataType.NUMBER_UP_TO_10);
            libroRepositoryGenerator.setData(Libro::setParte, DataType.NUMBER_UP_TO_10);
            libroRepositoryGenerator.setData(Libro::setCantidad, DataType.NUMBER_UP_TO_1000);
            libroRepositoryGenerator.setData(Libro::setPrecio, DataType.NUMBER_UP_TO_100);
            libroRepository.saveAll(libroRepositoryGenerator.create(100, seed));

            logger.info("... generating 100 Destino Final entities...");
            ExampleDataGenerator<DestinoFinal> destinoFinalRepositoryGenerator = new ExampleDataGenerator<>(
                    DestinoFinal.class, LocalDateTime.of(2022, 3, 23, 0, 0, 0));
            destinoFinalRepositoryGenerator.setData(DestinoFinal::setFecha, DataType.DATE_OF_BIRTH);
            destinoFinalRepositoryGenerator.setData(DestinoFinal::setCantidad, DataType.NUMBER_UP_TO_100);
            destinoFinalRepository.saveAll(destinoFinalRepositoryGenerator.create(100, seed));

            logger.info("... generating 100 Estudiante entities...");
            ExampleDataGenerator<Estudiante> estudianteRepositoryGenerator = new ExampleDataGenerator<>(
                    Estudiante.class, LocalDateTime.of(2022, 3, 23, 0, 0, 0));
            estudianteRepositoryGenerator.setData(Estudiante::setNombre, DataType.FIRST_NAME);
            estudianteRepositoryGenerator.setData(Estudiante::setApellidos, DataType.LAST_NAME);
            estudianteRepositoryGenerator.setData(Estudiante::setCi, DataType.WORD);
            estudianteRepositoryGenerator.setData(Estudiante::setAnno_academico, DataType.NUMBER_UP_TO_100);
            estudianteRepositoryGenerator.setData(Estudiante::setFacultad, DataType.WORD);
            estudianteRepository.saveAll(estudianteRepositoryGenerator.create(100, seed));

            logger.info("... generating 100 Trabajador entities...");
            ExampleDataGenerator<Trabajador> trabajadorRepositoryGenerator = new ExampleDataGenerator<>(
                    Trabajador.class, LocalDateTime.of(2022, 3, 23, 0, 0, 0));
            trabajadorRepositoryGenerator.setData(Trabajador::setNombre, DataType.FIRST_NAME);
            trabajadorRepositoryGenerator.setData(Trabajador::setApellidos, DataType.LAST_NAME);
            trabajadorRepositoryGenerator.setData(Trabajador::setCi, DataType.WORD);
            trabajadorRepositoryGenerator.setData(Trabajador::setSolapin, DataType.WORD);
            trabajadorRepositoryGenerator.setData(Trabajador::setCategoria, DataType.WORD);
            trabajadorRepository.saveAll(trabajadorRepositoryGenerator.create(100, seed));

            logger.info("Generated demo data");
        };
    }

}