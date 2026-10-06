package oliveira.trackmylearn.subject;

public class SubjectAlreadyExistException extends RuntimeException {
    public SubjectAlreadyExistException(String name) {
        super("Matéria já existe: " + name);
    }
}
