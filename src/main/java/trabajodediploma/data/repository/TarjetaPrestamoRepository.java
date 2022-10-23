package trabajodediploma.data.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import trabajodediploma.data.entity.TarjetaPrestamo;

@Repository
public interface TarjetaPrestamoRepository extends JpaRepository<TarjetaPrestamo, Integer> {

}