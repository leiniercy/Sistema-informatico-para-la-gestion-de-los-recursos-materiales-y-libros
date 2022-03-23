package trabajodediploma.data.service;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import trabajodediploma.data.entity.Trabajador;

public interface TrabajadorRepository extends JpaRepository<Trabajador, UUID> {

}