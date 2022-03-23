package trabajodediploma.data.service;

import trabajodediploma.data.repository.ModuloRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import trabajodediploma.data.entity.Modulo;

@Service
public class ModuloService {

    private final ModuloRepository repository;

    public ModuloService(@Autowired ModuloRepository repository) {
        this.repository = repository;
    }

    public Optional<Modulo> get(UUID id) {
        return repository.findById(id);
    }

    public Modulo update(Modulo entity) {
        return repository.save(entity);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    public Page<Modulo> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
