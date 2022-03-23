package trabajodediploma.data.service;

import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import trabajodediploma.data.entity.Libro;

@Service
public class LibroService {

    private final LibroRepository repository;

    public LibroService(@Autowired LibroRepository repository) {
        this.repository = repository;
    }

    public Optional<Libro> get(UUID id) {
        return repository.findById(id);
    }

    public Libro update(Libro entity) {
        return repository.save(entity);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    public Page<Libro> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
