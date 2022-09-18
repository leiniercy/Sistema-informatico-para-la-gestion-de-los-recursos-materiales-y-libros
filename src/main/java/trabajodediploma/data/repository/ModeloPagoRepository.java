package trabajodediploma.data.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import trabajodediploma.data.entity.ModeloPago;

public interface ModeloPagoRepository extends JpaRepository<ModeloPago, UUID> {
    
}
