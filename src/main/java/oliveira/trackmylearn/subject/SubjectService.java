package oliveira.trackmylearn.subject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SubjectService {

    private final SubjectRepository repository;

    public SubjectService(SubjectRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public SubjectResponse create(SubjectRequest request) {
        String newName = request.name().strip();
        if (repository.existsByNameAndIdNot(newName, null)) {
            throw new SubjectAlreadyExistException(newName);
        }

        Subject saved = repository.save(new Subject(newName, request.weeklyGoalMinutes()));
        return SubjectResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<SubjectResponse> list() {
        return repository.findAll().stream()
                .map(SubjectResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public SubjectResponse findById(Long id) {
        return repository.findById(id)
                .map(SubjectResponse::from)
                .orElseThrow(() -> new SubjectNotFoundException(id));
    }

    @Transactional
    public SubjectResponse update(Long id, SubjectRequest request) {
        String newName = request.name().strip();

        Subject subject = repository.findById(id)
                .orElseThrow(() -> new SubjectNotFoundException(id));

        if (repository.existsByNameAndIdNot(newName, id)) {
            throw new SubjectAlreadyExistException(newName);
        }

        subject.update(newName, request.weeklyGoalMinutes());

        return SubjectResponse.from(subject);
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
