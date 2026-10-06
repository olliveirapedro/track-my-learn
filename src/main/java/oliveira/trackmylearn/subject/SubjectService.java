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
        if (repository.existsByName(newName)) {
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
}
