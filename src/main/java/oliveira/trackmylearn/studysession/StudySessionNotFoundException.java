package oliveira.trackmylearn.studysession;

public class StudySessionNotFoundException extends RuntimeException {
    public StudySessionNotFoundException(Long id) {
        super("Sessão de estudos não foi encontrada " + id);
    }
}
