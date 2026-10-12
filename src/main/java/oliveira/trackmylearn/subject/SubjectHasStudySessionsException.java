package oliveira.trackmylearn.subject;

public class SubjectHasStudySessionsException extends RuntimeException {
    public SubjectHasStudySessionsException(Long id) {
        super("A matéria tem sessã(ões) de estudos vinculada" + id);
    }
}
