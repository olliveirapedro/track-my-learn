package oliveira.trackmylearn.subject;

public class SubjectNotFoundException extends RuntimeException {
    public SubjectNotFoundException(Long id) {
        super("Matéria não encontrada: " + id);
    }
}
