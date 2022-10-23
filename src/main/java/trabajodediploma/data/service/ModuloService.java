package trabajodediploma.data.service;

import trabajodediploma.data.repository.ModuloRepository;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import trabajodediploma.data.entity.Modulo;

@Service
public class ModuloService {

    private final ModuloRepository repository;

    public ModuloService(@Autowired ModuloRepository repository) {
        this.repository = repository;
    }

    public List<Modulo> findAll() {
        return repository.findAll();
    }

    public Modulo findById(Integer id) {
        return repository.findById(id).get();
    }

    public Modulo save(Modulo modulo) {
        return repository.save(modulo);
    }

    public Modulo update(Modulo modulo) {
        return repository.save(modulo);
    }

    public void delete(Modulo modulo) {
        repository.delete(modulo);
    }

    public void deleteAll(Set<Modulo>moduloes){repository.deleteAll(moduloes);}

    public long count() {
        return repository.count();
    }

}
