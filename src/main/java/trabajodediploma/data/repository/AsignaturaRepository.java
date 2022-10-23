/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabajodediploma.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import trabajodediploma.data.entity.Asignatura;

/**
 *
 * @author leinier
 */
@Repository
public interface AsignaturaRepository extends JpaRepository<Asignatura, Integer>  {
    
}
