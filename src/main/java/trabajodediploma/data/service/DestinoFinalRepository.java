package trabajodediploma.data.service;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import trabajodediploma.data.entity.DestinoFinal;

public interface DestinoFinalRepository extends JpaRepository<DestinoFinal, UUID> {

}