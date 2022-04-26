package trabajodediploma.data.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import trabajodediploma.data.entity.TarjetaPrestamo;
import trabajodediploma.data.repository.TarjetaPrestamoRepository;

@Service
public class TarjetaPrestamoService {

    private final TarjetaPrestamoRepository repository;

    public TarjetaPrestamoService(@Autowired TarjetaPrestamoRepository repository) {
        this.repository = repository;
    }
    
    public List<TarjetaPrestamo> findAll() {
        return repository.findAll();
    }
    
    public TarjetaPrestamo findById(UUID id){
     return repository.findById(id).get();
    }
    
    public TarjetaPrestamo save(TarjetaPrestamo libro) {
        return repository.save(libro);
    }

    
    public TarjetaPrestamo update(TarjetaPrestamo libro) {
        return repository.save(libro);
    }

    public void delete(TarjetaPrestamo libro) {
        repository.delete(libro);
    }

    public void deleteAll(Set<TarjetaPrestamo>libros){repository.deleteAll(libros);}
    
    public long count(){
      return repository.count();
    }
    

}
