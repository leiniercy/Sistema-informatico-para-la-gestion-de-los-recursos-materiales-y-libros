package trabajodediploma.data.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import trabajodediploma.data.entity.RecursoMaterial;

@Repository
public interface RecursoMaterialRepository extends JpaRepository<RecursoMaterial, Integer> {

}