package trabajodediploma.data.service;

import trabajodediploma.data.repository.EstudianteRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import trabajodediploma.data.entity.Estudiante;

@Service
public class EstudianteService {

    private final EstudianteRepository repository;

    public EstudianteService(@Autowired EstudianteRepository repository) {
        this.repository = repository;
    }

    public Optional<Estudiante> get(UUID id) {
        return repository.findById(id);
    }

    public Estudiante update(Estudiante entity) {
        return repository.save(entity);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    public Page<Estudiante> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
