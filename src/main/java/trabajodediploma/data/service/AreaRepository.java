package trabajodediploma.data.service;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import trabajodediploma.data.entity.Area;

public interface AreaRepository extends JpaRepository<Area, UUID> {

}