package oliveira.trackmylearn.studysession;


import oliveira.trackmylearn.subject.Subject;
import oliveira.trackmylearn.subject.SubjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import oliveira.trackmylearn.TestcontainersConfiguration;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class StudySessionControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    StudySessionRepository repository;

    @Autowired
    SubjectRepository subjectRepository;

    Subject subject;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        subjectRepository.deleteAll();
        subject = subjectRepository.save(new Subject("Matemática", 150));
    }

    @Test
    @DisplayName("POST /study-sessions retorna 201 com a sessão de estudo criada")
    void createsStudySession() throws Exception {
        mockMvc.perform(post("/study-sessions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                            {
                                "subjectId": %d,
                                "topic": "Funções",
                                "startedAt": "2025-05-14T11:15",
                                "durationMinutes": 60
                            }
                            """.formatted(subject.getId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.subjectId").value(subject.getId()))
                .andExpect(jsonPath("$.topic").value("Funções"))
                .andExpect(jsonPath("$.startedAt").value("2025-05-14T11:15:00"))
                .andExpect(jsonPath("$.durationMinutes").value(60));
    }

    @Test
    @DisplayName("GET /study-sessions retorna 200 com duas sessões de estudo")
    void returnLengthTwo() throws Exception {
        repository.save(new StudySession(subject, "Funções", LocalDateTime.of(2025, 5, 14, 11, 15), 60));
        repository.save(new StudySession(subject, "Logaritmo", LocalDateTime.of(2025, 5, 14, 11, 15), 60));
        mockMvc.perform(get("/study-sessions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("POST /study-sessions com Matéria inexistente retorna 404")
    void rejectUknownSubject() throws Exception {
        mockMvc.perform(post("/study-sessions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                            {
                                "subjectId": %d,
                                "topic": "Funções",
                                "startedAt": "2025-05-14T11:15",
                                "durationMinutes": 60
                            }
                            """.formatted(subject.getId()+1000)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Matéria não encontrada: " + (subject.getId()+1000)));
    }

    @Test
    @DisplayName("GET /study-sessions/{id} retorna 200 com a sessão de estudos")
    void findById() throws Exception {
        StudySession session = repository.save(new StudySession(subject, "Funções", LocalDateTime.of(2025, 5, 14, 11, 15), 60));

        mockMvc.perform(get("/study-sessions/{id}", session.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(session.getId()))
                .andExpect(jsonPath("$.subjectId").value(session.getSubject().getId()))
                .andExpect(jsonPath("$.topic").value(session.getTopic()))
                .andExpect(jsonPath("$.startedAt").value("2025-05-14T11:15:00"))
                .andExpect(jsonPath("$.durationMinutes").value(session.getDurationMinutes()));
    }

    @Test
    @DisplayName("DELETE /study-sessions/{id} retorna 204")
    void deleteStudySession() throws Exception {
        StudySession session = repository.save(new StudySession(subject, "Funções", LocalDateTime.of(2025, 5, 14, 11, 15), 60));

        mockMvc.perform(delete("/study-sessions/{id}", session.getId()))
                .andExpect(status().isNoContent());
    }

    //TODO: Tests with invalid date, invalid duration, and invalid topic
}
