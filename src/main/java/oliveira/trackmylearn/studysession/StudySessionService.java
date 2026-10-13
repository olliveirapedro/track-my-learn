package oliveira.trackmylearn.studysession;

import oliveira.trackmylearn.subject.Subject;
import oliveira.trackmylearn.subject.SubjectNotFoundException;
import oliveira.trackmylearn.subject.SubjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import jakarta.persistence.criteria.Predicate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    public Page<StudySessionResponse> list(Long subjectId, LocalDate from, LocalDate to, int page, int size) {
        if (from != null && to != null && from.isAfter(to)) {
            throw new InvalidStudySessionFilterException("A data inicial não pode ser posterior à data final.");
        }
        if (page < 0) {
            throw new InvalidStudySessionFilterException("O parâmetro page deve ser maior ou igual a zero.");
        }
        if (size < 1 || size > 100) {
            throw new InvalidStudySessionFilterException("O parâmetro size deve estar entre 1 e 100.");
        }

        LocalDateTime start = from == null ? null : from.atStartOfDay();
        LocalDateTime end = to == null ? null : to.atStartOfDay();

        return repository.findAll((root, query, criteriaBuilder) -> {
                    List<Predicate> predicates = new ArrayList<>();
                    if (subjectId != null) {
                        predicates.add(criteriaBuilder.equal(root.get("subject").get("id"), subjectId));
                    }
                    if (start != null) {
                        predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("startedAt"), start));
                    }
                    if (end != null) {
                        predicates.add(criteriaBuilder.lessThan(root.get("startedAt"), end));
                    }
                    return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
                }, PageRequest.of(page, size, Sort.by(
                        Sort.Order.desc("startedAt"),
                        Sort.Order.desc("id")
                )))
                .map(StudySessionResponse::from);
    }

    @Transactional
    public StudySessionResponse findById(Long id) {
        return repository.findById(id)
                .map(StudySessionResponse::from)
                .orElseThrow(() -> new StudySessionNotFoundException(id));
    }

    @Transactional
    public StudySessionResponse update(Long id, StudySessionResponse response) {
        StudySession studySession = repository.findById(id)
                .orElseThrow(() -> new StudySessionNotFoundException(id));

        Subject subject = subjectRepository.findById(response.subjectId())
                .orElseThrow(() -> new SubjectNotFoundException(response.subjectId()));

        studySession.update(
                subject,
                response.topic(),
                response.startedAt(),
                response.durationMinutes(),
                response.notes()
        );

        return StudySessionResponse.from(studySession);
    }

    @Transactional
    public void delete(Long id) {
        StudySession studySession = repository.findById(id)
                .orElseThrow(() -> new StudySessionNotFoundException(id));

        repository.delete(studySession);
    }

}
