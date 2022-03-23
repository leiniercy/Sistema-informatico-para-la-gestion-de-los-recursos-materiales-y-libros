package trabajodediploma.data.service;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import trabajodediploma.data.entity.Libro;

public interface LibroRepository extends JpaRepository<Libro, UUID> {

}