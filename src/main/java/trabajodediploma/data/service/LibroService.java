package trabajodediploma.data.service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import trabajodediploma.data.entity.Libro;
import trabajodediploma.data.repository.LibroRepository;

@Service
public class LibroService {

    private final LibroRepository repository;

    public LibroService(@Autowired LibroRepository repository) {
        this.repository = repository;
    }

    public List<Libro> findAll() {
        return repository.findAll();
    }
    
    public Libro findById(Integer id){
     return repository.findById(id).get();
    }
    
    public Libro save(Libro libro) {
        return repository.save(libro);
    }

    
    public Libro update(Libro libro) {
        return repository.save(libro);
    }

    public void delete(Libro libro) {
        repository.delete(libro);
    }

    public void deleteAll(Set<Libro>libros){repository.deleteAll(libros);}
    
    public long count(){
      return repository.count();
    }
}
