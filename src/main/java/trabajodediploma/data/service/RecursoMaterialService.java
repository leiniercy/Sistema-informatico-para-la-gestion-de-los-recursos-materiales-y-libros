package trabajodediploma.data.service;

import trabajodediploma.data.repository.RecursoMaterialRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
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

    public List<RecursoMaterial> findAll() {
        return repository.findAll();
    }

    public RecursoMaterial findById(UUID id) {
        return repository.findById(id).get();
    }

    public RecursoMaterial save(RecursoMaterial material) {
        return repository.save(material);
    }

    public RecursoMaterial update(RecursoMaterial material) {
        return repository.save(material);
    }

    public void delete(RecursoMaterial material) {
        repository.delete(material);
    }

    public void deleteAll(Set<RecursoMaterial>materiales){repository.deleteAll(materiales);}

    public long count() {
        return repository.count();
    }

}
