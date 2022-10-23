/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabajodediploma.data.service;

import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import trabajodediploma.data.entity.Asignatura;
import trabajodediploma.data.repository.AsignaturaRepository;

/**
 *
 * @author leinier
 */
@Service
public class AsignaturaService {

    private final AsignaturaRepository repository;

    public AsignaturaService(@Autowired AsignaturaRepository repository) {
        this.repository = repository;
    }

    public List<Asignatura> findAll() {
        return repository.findAll();
    }
    
    public Asignatura findById(Integer id){
     return repository.findById(id).get();
    }
    
    public Asignatura save(Asignatura asignatura) {
        return repository.save(asignatura);
    }

    
    public Asignatura update(Asignatura asignatura) {
        return repository.save(asignatura);
    }

    public void delete(Asignatura asignatura) {
        repository.delete(asignatura);
    }

    public void deleteAll(Set<Asignatura>asignaturas){repository.deleteAll(asignaturas);}
    
    public long count(){
      return repository.count();
    }
}