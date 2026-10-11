package oliveira.trackmylearn.studysession;

import java.time.LocalDateTime;

public record StudySessionResponse(
        Long id,
        Long subjectId,
        String topic,
        LocalDateTime startedAt,
        Integer durationMinutes,
        String notes
) {
    public static StudySessionResponse from(StudySession studySession) {
        return new StudySessionResponse(
                studySession.getId(),
                studySession.getSubject().getId(),
                studySession.getTopic(),
                studySession.getStartedAt(),
                studySession.getDurationMinutes(),
                studySession.getNotes()
        );
    }
}
