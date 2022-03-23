package trabajodediploma.data.service;

import trabajodediploma.data.repository.TarjetaPrestamoEstudianteRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import trabajodediploma.data.entity.TarjetaPrestamoEstudiante;

@Service
public class TarjetaPrestamoEstudianteService {

    private final TarjetaPrestamoEstudianteRepository repository;

    public TarjetaPrestamoEstudianteService(@Autowired TarjetaPrestamoEstudianteRepository repository) {
        this.repository = repository;
    }

    public Optional<TarjetaPrestamoEstudiante> get(UUID id) {
        return repository.findById(id);
    }

    public TarjetaPrestamoEstudiante update(TarjetaPrestamoEstudiante entity) {
        return repository.save(entity);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    public Page<TarjetaPrestamoEstudiante> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
