package trabajodediploma.data.service;

import trabajodediploma.data.repository.DestinoFinalRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import trabajodediploma.data.entity.DestinoFinal;

@Service
public class DestinoFinalService {

    private final DestinoFinalRepository repository;

    public DestinoFinalService(@Autowired DestinoFinalRepository repository) {
        this.repository = repository;
    }

    public List<DestinoFinal> findAll() {
        return repository.findAll();
    }
    
    public DestinoFinal findById(UUID id){
     return repository.findById(id).get();
    }
    
    public DestinoFinal save(DestinoFinal DestinoFinal) {
        return repository.save(DestinoFinal);
    }

    
    public DestinoFinal update(DestinoFinal DestinoFinal) {
        return repository.save(DestinoFinal);
    }

    public void delete(DestinoFinal DestinoFinal) {
        repository.delete(DestinoFinal);
    }

    public void deleteAll(Set<DestinoFinal>DestinoFinals){repository.deleteAll(DestinoFinals);}
    
    public long count(){
      return repository.count();
    }
    
}
