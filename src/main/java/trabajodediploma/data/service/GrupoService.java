package trabajodediploma.data.service;

import java.util.List;
import trabajodediploma.data.repository.GrupoRepository;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import trabajodediploma.data.entity.Grupo;

@Service
public class GrupoService {

    private final GrupoRepository repository;

    public GrupoService(@Autowired GrupoRepository repository) {
        this.repository = repository;
    }

    public List<Grupo> findAll() {
        return repository.findAll();
    }

    public Grupo findById(Integer id) {
        return repository.findById(id).get();
    }

    public  Grupo findByNumero(String numero){
        return repository.findByNumero(numero);
    }
    
    public Grupo save(Grupo grupo) {
        return repository.save(grupo);
    }

    public Grupo update(Grupo grupo) {
        return repository.save(grupo);
    }

    public void delete(Grupo grupo) {
        repository.delete(grupo);
    }

    public void deleteAll(Set<Grupo> grupos) {
        repository.deleteAll(grupos);
    }

    public long count() {
        return repository.count();
    }

}
