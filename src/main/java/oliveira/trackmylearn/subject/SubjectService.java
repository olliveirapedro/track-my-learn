package oliveira.trackmylearn.subject;

import oliveira.trackmylearn.studysession.StudySessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SubjectService {

    private final SubjectRepository repository;

    private final StudySessionRepository studySessionRepository;

    public SubjectService(SubjectRepository repository, StudySessionRepository studySessionRepository) {
        this.repository = repository;
        this.studySessionRepository = studySessionRepository;
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
        Subject subject = repository.findById(id)
                .orElseThrow(() -> new SubjectNotFoundException(id));

        if (studySessionRepository.existsBySubjectId(id)) {
            throw new SubjectHasStudySessionsException(id);
        }

        repository.delete(subject);
    }
}
