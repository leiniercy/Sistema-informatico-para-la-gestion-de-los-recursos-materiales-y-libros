package trabajodediploma.data.service;

import trabajodediploma.data.repository.RecursoMaterialRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import trabajodediploma.data.entity.RecursoMaterial;

@Service
public class RecursoMaterialService {

    private final RecursoMaterialRepository repository;

    public RecursoMaterialService(@Autowired RecursoMaterialRepository repository) {
        this.repository = repository;
    }

    public Optional<RecursoMaterial> get(UUID id) {
        return repository.findById(id);
    }

    public RecursoMaterial update(RecursoMaterial entity) {
        return repository.save(entity);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    public Page<RecursoMaterial> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
