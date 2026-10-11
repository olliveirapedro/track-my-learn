package oliveira.trackmylearn.studysession;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public record StudySessionRequest(
        @NotNull Long subjectId,
        @NotBlank @Size(max = 150) String topic,
        @NotNull @PastOrPresent LocalDateTime startedAt,
        @NotNull @Positive Integer durationMinutes,
        String notes
) { }
