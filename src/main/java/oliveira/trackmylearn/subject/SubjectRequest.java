package oliveira.trackmylearn.subject;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record SubjectRequest(
        @NotBlank @Size(max = 100) String name,
        @Positive Integer weeklyGoalMinutes
) { }
