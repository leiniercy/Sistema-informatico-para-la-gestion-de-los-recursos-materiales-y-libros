package trabajodediploma.data.service;

import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import trabajodediploma.data.entity.Trabajador;

@Service
public class TrabajadorService {

    private final TrabajadorRepository repository;

    public TrabajadorService(@Autowired TrabajadorRepository repository) {
        this.repository = repository;
    }

    public Optional<Trabajador> get(UUID id) {
        return repository.findById(id);
    }

    public Trabajador update(Trabajador entity) {
        return repository.save(entity);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    public Page<Trabajador> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
