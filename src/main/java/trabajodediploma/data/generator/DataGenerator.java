package trabajodediploma.data.generator;

import com.vaadin.exampledata.DataType;
import com.vaadin.exampledata.ExampleDataGenerator;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import domain.xsd.PersonaUCI;
import https.autenticacion2_uci_cu.v7.Persona;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import trabajodediploma.data.Rol;
import trabajodediploma.data.entity.Area;
import trabajodediploma.data.entity.DestinoFinal;
import trabajodediploma.data.entity.Estudiante;
import trabajodediploma.data.entity.Grupo;
import trabajodediploma.data.entity.Libro;
import trabajodediploma.data.entity.Modulo;
import trabajodediploma.data.entity.RecursoMaterial;
import trabajodediploma.data.entity.TarjetaPrestamo;
import trabajodediploma.data.entity.Trabajador;
import trabajodediploma.data.entity.User;
import trabajodediploma.data.repository.AreaRepository;
import trabajodediploma.data.repository.DestinoFinalRepository;
import trabajodediploma.data.repository.EstudianteRepository;
import trabajodediploma.data.repository.GrupoRepository;
import trabajodediploma.data.repository.LibroRepository;
import trabajodediploma.data.repository.ModuloRepository;
import trabajodediploma.data.repository.RecursoMaterialRepository;
import trabajodediploma.data.repository.TrabajadorRepository;
import trabajodediploma.data.repository.UserRepository;
import trabajodediploma.data.repository.TarjetaPrestamoRepository;
import trabajodediploma.data.service.AreaService;
import trabajodediploma.data.service.EstudianteService;
import trabajodediploma.data.service.GrupoService;
import trabajodediploma.data.service.TrabajadorService;
import trabajodediploma.data.service.UserService;
import trabajodediploma.data.tools.serviciosUCI.ClienteDatosUCIWSDL;
import trabajodediploma.data.tools.serviciosUCI.HelloWorldClient;

@SpringComponent
public class DataGenerator {

    @Bean
    public CommandLineRunner loadData(
            @Autowired ClienteDatosUCIWSDL datosUCIWSDL,
            @Autowired HelloWorldClient helloWorldClient,
            @Autowired PasswordEncoder passwordEncoder,
            @Autowired UserService userService,
            @Autowired EstudianteService estudianteService,
            @Autowired GrupoService grupoService,
            @Autowired TrabajadorService trabajadorService,
            @Autowired AreaService areaService
    ) {
        return args -> {
            Logger logger = LoggerFactory.getLogger(getClass());

            /**
             * ID del Grupo - Estudiantes - Facultad 4 Grupo 4101 -> Grupo 4102
             * -> 352 Grupo 4103 -> 385 Grupo 4104 -> 350 Grupo 4201 -> 392
             * Grupo 4202 -> 387 Grupo 4203 -> 397 Grupo 4204 -> 382 Grupo 4205
             * -> Grupo 4206 -> Grupo 4301 -> 421 Grupo 4302 -> 437 Grupo 4303
             * -> 460 Grupo 4304 -> 407 Grupo 4305 -> 373 ---> preguntar por
             * este Grupo 4401 -> 450 Grupo 4402 -> 359 Grupo 4403 -> 346 Grupo
             * 4404 -> Grupo 4501 -> 377 Grupo 4502 -> 430 Grupo 4503 -> 440
             *
             */
            List<PersonaUCI> lista = new LinkedList<>();
            List<String> listaUsuariosEstudiantes = new LinkedList<>();
            //1er año
//        services.ObtenerPersonasDadoIdEstructuraResponse grupo4101 = datosUCIWSDL.personasDadoArea();
            services.ObtenerPersonasDadoIdEstructuraResponse grupo4102 = datosUCIWSDL.personasDadoArea(352);
            services.ObtenerPersonasDadoIdEstructuraResponse grupo4103 = datosUCIWSDL.personasDadoArea(385);
            services.ObtenerPersonasDadoIdEstructuraResponse grupo4104 = datosUCIWSDL.personasDadoArea(350);
            //2do año
            services.ObtenerPersonasDadoIdEstructuraResponse grupo4201 = datosUCIWSDL.personasDadoArea(392);
            services.ObtenerPersonasDadoIdEstructuraResponse grupo4202 = datosUCIWSDL.personasDadoArea(387);
            services.ObtenerPersonasDadoIdEstructuraResponse grupo4203 = datosUCIWSDL.personasDadoArea(397);
            services.ObtenerPersonasDadoIdEstructuraResponse grupo4204 = datosUCIWSDL.personasDadoArea(382);
//        services.ObtenerPersonasDadoIdEstructuraResponse grupo4205 = datosUCIWSDL.personasDadoArea(350);
            //3er año
            services.ObtenerPersonasDadoIdEstructuraResponse grupo4301 = datosUCIWSDL.personasDadoArea(421);
            services.ObtenerPersonasDadoIdEstructuraResponse grupo4302 = datosUCIWSDL.personasDadoArea(437);
            services.ObtenerPersonasDadoIdEstructuraResponse grupo4303 = datosUCIWSDL.personasDadoArea(460);
            services.ObtenerPersonasDadoIdEstructuraResponse grupo4304 = datosUCIWSDL.personasDadoArea(407);
            services.ObtenerPersonasDadoIdEstructuraResponse grupo4305 = datosUCIWSDL.personasDadoArea(373);
            //4to año
            services.ObtenerPersonasDadoIdEstructuraResponse grupo4401 = datosUCIWSDL.personasDadoArea(450);
            services.ObtenerPersonasDadoIdEstructuraResponse grupo4402 = datosUCIWSDL.personasDadoArea(359);
            services.ObtenerPersonasDadoIdEstructuraResponse grupo4403 = datosUCIWSDL.personasDadoArea(346);
//        services.ObtenerPersonasDadoIdEstructuraResponse grupo4404 = datosUCIWSDL.personasDadoArea(350);
            //5to año
            services.ObtenerPersonasDadoIdEstructuraResponse grupo4501 = datosUCIWSDL.personasDadoArea(377);
            services.ObtenerPersonasDadoIdEstructuraResponse grupo4502 = datosUCIWSDL.personasDadoArea(430);
            services.ObtenerPersonasDadoIdEstructuraResponse grupo4503 = datosUCIWSDL.personasDadoArea(440);

            //1er año
            for (int i = 0; i < grupo4102.getReturn().size(); i++) {
                lista.add(grupo4102.getReturn().get(i));
            }
            for (int i = 0; i < grupo4103.getReturn().size(); i++) {
                lista.add(grupo4103.getReturn().get(i));
            }
            for (int i = 0; i < grupo4104.getReturn().size(); i++) {
                lista.add(grupo4104.getReturn().get(i));
            }
            //2do año
            for (int i = 0; i < grupo4201.getReturn().size(); i++) {
                lista.add(grupo4201.getReturn().get(i));
            }
            for (int i = 0; i < grupo4202.getReturn().size(); i++) {
                lista.add(grupo4202.getReturn().get(i));
            }
            for (int i = 0; i < grupo4203.getReturn().size(); i++) {
                lista.add(grupo4203.getReturn().get(i));
            }
            for (int i = 0; i < grupo4204.getReturn().size(); i++) {
                lista.add(grupo4204.getReturn().get(i));
            }
            //3er año
            for (int i = 0; i < grupo4301.getReturn().size(); i++) {
                lista.add(grupo4301.getReturn().get(i));
            }
            for (int i = 0; i < grupo4302.getReturn().size(); i++) {
                lista.add(grupo4302.getReturn().get(i));
            }
            for (int i = 0; i < grupo4303.getReturn().size(); i++) {
                lista.add(grupo4303.getReturn().get(i));
            }
            for (int i = 0; i < grupo4304.getReturn().size(); i++) {
                lista.add(grupo4304.getReturn().get(i));
            }
            for (int i = 0; i < grupo4305.getReturn().size(); i++) {
                lista.add(grupo4305.getReturn().get(i));
            }
            //4to año
            for (int i = 0; i < grupo4401.getReturn().size(); i++) {
                lista.add(grupo4401.getReturn().get(i));
            }
            for (int i = 0; i < grupo4402.getReturn().size(); i++) {
                lista.add(grupo4402.getReturn().get(i));
            }
            for (int i = 0; i < grupo4403.getReturn().size(); i++) {
                lista.add(grupo4403.getReturn().get(i));
            }
            //5to ano
            for (int i = 0; i < grupo4501.getReturn().size(); i++) {
                lista.add(grupo4501.getReturn().get(i));
            }
            for (int i = 0; i < grupo4502.getReturn().size(); i++) {
                lista.add(grupo4502.getReturn().get(i));
            }
            for (int i = 0; i < grupo4503.getReturn().size(); i++) {
                lista.add(grupo4503.getReturn().get(i));
            }
            for (int i = 0; i < lista.size(); i++) {
                listaUsuariosEstudiantes.add(lista.get(i).getUsuario().getValue());
            }

            Binder<User> binderUser = new Binder<>(User.class);
            Binder<Grupo> binderGrupo = new Binder<>(Grupo.class);
            Binder<Estudiante> binderEstudiante = new Binder<>(Estudiante.class);

            //Añadiendo usuarios a la BD
            if (estudianteService.findAll().size() == listaUsuariosEstudiantes.size()) {
                logger.info("Base de datos actualizada");
                return;
            }
            if (estudianteService.findAll().size() < listaUsuariosEstudiantes.size()) {
                logger.info("... LLenando BD con usuarios UCI...");
                // llenando la BD
                for (int i = 0; i < listaUsuariosEstudiantes.size(); i++) {
                    //obteniendo informacion del estudiante dado el usuario
                    Persona persona = helloWorldClient.sayHello(listaUsuariosEstudiantes.get(i), "");
                    services.ObtenerPersonaDadoUsuarioResponse p = datosUCIWSDL.obtenerPersonaDadoUsuario(listaUsuariosEstudiantes.get(i));
                    //obteniendo informacion del estudiante dado el usuario
                    User user = userService.findByUsername(listaUsuariosEstudiantes.get(i));
                    if (user == null) {
                        //creando Estudiante
                        Estudiante estudiante = new Estudiante();
                        //creando usuario
                        User newUser = new User();
                        newUser.setName(p.getReturn().getValue().getNombreCompleto().getValue());
                        newUser.setUsername(p.getReturn().getValue().getUsuario().getValue());
                        newUser.setHashedPassword(passwordEncoder.encode("1234"));
                        newUser.setConfirmPassword("1234");
                        newUser.setRoles(Collections.singleton(Rol.USER));
                        binderUser.writeBeanIfValid(newUser);
                        userService.save(newUser);
                        //modificando usuario
                        estudiante.setUser(newUser);
                        binderUser.readBean(new User());
                        //modificando  los  restantes datos de estudiante Estudiante
                        estudiante.setSolapin(p.getReturn().getValue().getCredencial().getValue());
                        estudiante.setEmail(persona.getCorreo());
                        String anno_academico = p.getReturn().getValue().getArea().getValue().getNombreArea().getValue().charAt(4) + "";
                        try {
                            int number = Integer.parseInt(anno_academico);
                            estudiante.setAnno_academico(number);
                        } catch (NumberFormatException ex) {
                            ex.printStackTrace();
                        }
                        //verificando si el grupo existe
                        Grupo grupo = grupoService.findByNumero(p.getReturn().getValue().getArea().getValue().getNombreArea().getValue());
                        if (grupo == null) {
                            //creando grupo
                            Grupo newGrupo = new Grupo();
                            newGrupo.setNumero(p.getReturn().getValue().getArea().getValue().getNombreArea().getValue());
                            binderGrupo.writeBeanIfValid(newGrupo);
                            grupoService.save(newGrupo);
                            estudiante.setGrupo(newGrupo);
                            binderGrupo.readBean(new Grupo());
                        } else {
                            estudiante.setGrupo(grupo);
                        }
                        estudiante.setFacultad(persona.getArea().getNombreArea());
                        binderEstudiante.writeBeanIfValid(estudiante);
                        estudianteService.save(estudiante);
                        binderEstudiante.readBean(new Estudiante());
                    }
                }
                logger.info("...La base de datos fue llenada satisfactoriamente ...");
            }

        };
    }

}
