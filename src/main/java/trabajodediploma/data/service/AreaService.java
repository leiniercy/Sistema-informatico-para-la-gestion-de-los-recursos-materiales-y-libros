package trabajodediploma.data.service;

import java.util.List;
import trabajodediploma.data.repository.AreaRepository;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import trabajodediploma.data.entity.Area;

@Service
public class AreaService {

    private final AreaRepository repository;

    public AreaService(@Autowired AreaRepository repository) {
        this.repository = repository;
    }

    public List<Area> findAll() {
        return repository.findAll();
    }
    
    public Area findById(Integer id){
     return repository.findById(id).get();
    }
    
    public Area findByNombre(String nombre){
     return repository.findByNombre(nombre);
    }
    
    public Area save(Area area) {
        return repository.save(area);
    }

    public Area update(Area area) {
        return repository.save(area);
    }

    public void delete(Area area) {
        repository.delete(area);
    }

    public void deleteAll(Set<Area>areas){repository.deleteAll(areas);}
    
    public long count(){
      return repository.count();
    }
}
