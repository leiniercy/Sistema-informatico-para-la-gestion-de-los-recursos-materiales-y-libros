package trabajodediploma.data.service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import trabajodediploma.data.entity.ModeloPago;
import trabajodediploma.data.repository.ModeloPagoRepository;

@Service
public class ModeloPagoService {
    
    private final ModeloPagoRepository repository;

    public ModeloPagoService(@Autowired ModeloPagoRepository repository) {
        this.repository = repository;
    }

    public List<ModeloPago> findAll() {
        return repository.findAll();
    }
    
    public ModeloPago findById(Integer id){
     return repository.findById(id).get();
    }
    
    public ModeloPago save(ModeloPago ModeloPago) {
        return repository.save(ModeloPago);
    }

    
    public ModeloPago update(ModeloPago ModeloPago) {
        return repository.save(ModeloPago);
    }

    public void delete(ModeloPago ModeloPago) {
        repository.delete(ModeloPago);
    }

    public void deleteAll(Set<ModeloPago>ModeloPagos){repository.deleteAll(ModeloPagos);}
    
    public long count(){
      return repository.count();
    }
}
