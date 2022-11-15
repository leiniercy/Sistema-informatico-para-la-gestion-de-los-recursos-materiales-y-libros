package trabajodediploma.data.service;

import java.util.List;
import trabajodediploma.data.repository.TrabajadorRepository;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import trabajodediploma.data.entity.Trabajador;

@Service
public class TrabajadorService {

    private final TrabajadorRepository repository;

    public TrabajadorService(@Autowired TrabajadorRepository repository) {
        this.repository = repository;
    }

    public List<Trabajador> findAll() {
        return repository.findAll();
    }

    public Trabajador findById(Integer id) {
        return repository.findById(id).get();
    }

    public Trabajador findBySolapin(String solapin) {
        return repository.findBySolapin(solapin);
    }

    public Trabajador save(Trabajador trabajador) {
        return repository.save(trabajador);
    }

    public Trabajador update(Trabajador trabajador) {
        return repository.save(trabajador);
    }

    public void delete(Trabajador trabajador) {
        repository.delete(trabajador);
    }

    public void deleteAll(Set<Trabajador> trabajadors) {
        repository.deleteAll(trabajadors);
    }

    public long count() {
        return repository.count();
    }

}
