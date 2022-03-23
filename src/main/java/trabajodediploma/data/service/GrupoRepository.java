package trabajodediploma.data.service;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import trabajodediploma.data.entity.Grupo;

public interface GrupoRepository extends JpaRepository<Grupo, UUID> {

}