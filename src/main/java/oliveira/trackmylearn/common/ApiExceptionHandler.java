package oliveira.trackmylearn.common;

import oliveira.trackmylearn.studysession.StudySessionNotFoundException;
import oliveira.trackmylearn.subject.SubjectAlreadyExistException;
import oliveira.trackmylearn.subject.SubjectHasStudySessionsException;
import oliveira.trackmylearn.subject.SubjectNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(SubjectAlreadyExistException.class)
    public ProblemDetail handleConflict(SubjectAlreadyExistException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(SubjectNotFoundException.class)
    public ProblemDetail handleNotFound(SubjectNotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(SubjectHasStudySessionsException.class)
    public ProblemDetail handleSubjectHasStudySessions(SubjectHasStudySessionsException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(StudySessionNotFoundException.class)
    public ProblemDetail handleStudySessionNotFound(StudySessionNotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }
}
