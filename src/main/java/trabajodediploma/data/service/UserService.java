package trabajodediploma.data.service;

import java.util.List;
import trabajodediploma.data.repository.UserRepository;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import trabajodediploma.data.entity.User;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(@Autowired UserRepository repository) {
        this.repository = repository;
    }

     public List<User> findAll() {
        return repository.findAll();
    }
    
    public User findById(UUID id){
     return repository.findById(id).get();
    }
    
    public User save(User user) {
        return repository.saveAndFlush(user);
    }

    
    public User update(User user) {
        return repository.saveAndFlush(user);
    }

    public void delete(User  user) {
        repository.delete( user);
    }

    public void deleteAll(Set<User>libros){repository.deleteAll(libros);}
    
    public long count(){
      return repository.count();
    }
}
