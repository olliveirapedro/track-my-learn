package oliveira.trackmylearn.studysession;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StudySessionRepository extends JpaRepository<StudySession, Long>, JpaSpecificationExecutor<StudySession> {
    boolean existsBySubjectId(Long subjectId);
}
