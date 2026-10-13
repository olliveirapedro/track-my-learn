package oliveira.trackmylearn.studysession;

import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

import java.time.LocalDate;

@RestController
@RequestMapping("/study-sessions")
public class StudySessionController {

    private final StudySessionService service;

    public StudySessionController(StudySessionService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<StudySessionResponse> create(@Valid @RequestBody StudySessionRequest request) {
        StudySessionResponse created = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public Page<StudySessionResponse> list(
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return service.list(subjectId, from, to, page, size);
    }

    @GetMapping("/{id}")
    public StudySessionResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public StudySessionResponse update(@PathVariable Long id, @Valid @RequestBody StudySessionResponse response) {
        return service.update(id, response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
