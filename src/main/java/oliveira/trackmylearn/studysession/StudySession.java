package oliveira.trackmylearn.studysession;

import jakarta.persistence.*;
import oliveira.trackmylearn.subject.Subject;

import java.time.LocalDateTime;

@Entity
@Table(name = "study_session")
public class StudySession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(nullable = false, length = 150)
    private String topic;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    @Column(nullable = false)
    private Integer durationMinutes;

    @Column
    private String notes;

    protected StudySession() { }

    public StudySession(Subject subject, String topic, LocalDateTime startedAt, Integer durationMinutes) {
        this(subject, topic, startedAt, durationMinutes, null);
    }

    public StudySession(Subject subject, String topic, LocalDateTime startedAt, Integer durationMinutes, String notes) {
        this.subject = subject;
        this.topic = topic;
        this.startedAt = startedAt;
        this.durationMinutes = durationMinutes;
        this.notes = notes;
    }

    public Long getId() {
        return id;
    }

    public Subject getSubject() {
        return subject;
    }

    public String getTopic() {
        return topic;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public String getNotes() {
        return notes;
    }

    public void update(Subject subject, String topic, LocalDateTime startedAt, Integer durationMinutes, String notes) {
        this.subject = subject;
        this.topic = topic;
        this.startedAt = startedAt;
        this.durationMinutes = durationMinutes;
        this.notes = notes;
    }
}
