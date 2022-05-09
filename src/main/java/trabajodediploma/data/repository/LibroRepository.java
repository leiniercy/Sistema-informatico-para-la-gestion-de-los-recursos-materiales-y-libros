package trabajodediploma.data.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import trabajodediploma.data.entity.Libro;

@Repository
public interface LibroRepository extends JpaRepository<Libro, UUID> {

     @Query("SELECT l FROM Libro l ORDER BY titulo")
     List<Libro> ordenarAlfabeticamente();
    
}