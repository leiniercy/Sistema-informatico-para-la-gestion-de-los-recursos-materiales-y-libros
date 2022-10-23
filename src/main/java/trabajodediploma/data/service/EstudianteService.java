package trabajodediploma.data.service;

import java.util.List;
import trabajodediploma.data.repository.EstudianteRepository;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import trabajodediploma.data.entity.Estudiante;

@Service
public class EstudianteService {

    private final EstudianteRepository repository;

    public EstudianteService(@Autowired EstudianteRepository repository) {
        this.repository = repository;
    }

    public List<Estudiante> findAll() {
        return repository.findAll();
    }
    
    public Estudiante findById(Integer id){
     return repository.findById(id).get();
    }
    
    public Estudiante save(Estudiante estudiante) {
        return repository.save(estudiante);
    }

    
    public Estudiante update(Estudiante estudiante) {
        return repository.save(estudiante);
    }

    public void delete(Estudiante estudiante) {
        repository.delete(estudiante);
    }

    public void deleteAll(Set<Estudiante>estudiantes){repository.deleteAll(estudiantes);}
    
    public long count(){
      return repository.count();
    }
    
}
