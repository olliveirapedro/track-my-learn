package oliveira.trackmylearn.subject;

public record SubjectResponse(Long id, String name, Integer weeklyGoalMinutes) {

    public static SubjectResponse from(Subject subject) {
        return new SubjectResponse(subject.getId(), subject.getName(), subject.getWeeklyGoalMinutes());
    }
}
