package oliveira.trackmylearn.studysession;

import oliveira.trackmylearn.subject.Subject;
import oliveira.trackmylearn.subject.SubjectNotFoundException;
import oliveira.trackmylearn.subject.SubjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StudySessionService {

    private final SubjectRepository subjectRepository;
    private final StudySessionRepository repository;

    public StudySessionService(StudySessionRepository repository, SubjectRepository subjectRepository) {
        this.repository = repository;
        this.subjectRepository = subjectRepository;
    }

    @Transactional
    public StudySessionResponse create(StudySessionRequest request) {
        Subject subject = subjectRepository.findById(request.subjectId())
                .orElseThrow(() -> new SubjectNotFoundException(request.subjectId()));

        StudySession saved = repository.save(new StudySession(subject, request.topic(), request.startedAt(), request.durationMinutes(), request.notes()));
        return StudySessionResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<StudySessionResponse> list() {
        return repository.findAll().stream()
                .map(StudySessionResponse::from)
                .toList();
    }

}
