package trabajodediploma.data.service;

import trabajodediploma.data.repository.DestinoFinalRepository;
import java.util.Optional;
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

    public Optional<DestinoFinal> get(UUID id) {
        return repository.findById(id);
    }

    public DestinoFinal update(DestinoFinal entity) {
        return repository.save(entity);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    public Page<DestinoFinal> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
