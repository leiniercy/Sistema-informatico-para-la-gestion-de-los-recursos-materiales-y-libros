package trabajodediploma.data.service;

import trabajodediploma.data.repository.AreaRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import trabajodediploma.data.entity.Area;

@Service
public class AreaService {

    private final AreaRepository repository;

    public AreaService(@Autowired AreaRepository repository) {
        this.repository = repository;
    }

    public Optional<Area> get(UUID id) {
        return repository.findById(id);
    }

    public Area update(Area entity) {
        return repository.save(entity);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    public Page<Area> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
