package oliveira.trackmylearn.subject;

import oliveira.trackmylearn.studysession.StudySession;
import oliveira.trackmylearn.studysession.StudySessionRepository;
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
class SubjectControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    SubjectRepository repository;

    @Autowired
    StudySessionRepository studySessionRepository;

    @BeforeEach
    void cleanDatabase() {
        studySessionRepository.deleteAll();
        repository.deleteAll();
    }

    @Test
    @DisplayName("POST /subjects retorna 201 com a matéria criada")
    void createsSubject() throws Exception {
        mockMvc.perform(post("/subjects")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"name\": \"Matemática\", \"weeklyGoalMinutes\": 464}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Matemática"));
    }

    @Test
    @DisplayName("POST /subjects retorna 409 com o nome repetido")
    void rejectDuplicateName() throws Exception {
        repository.save(new Subject("Matemática", 200));
        mockMvc.perform(post("/subjects")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"name\": \"Matemática\", \"weeklyGoalMinutes\": 150}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("Matéria já existe: Matemática"));
    }

    @Test
    @DisplayName("POST /subjects retorna 400 com o nome vazio")
    void rejectEmptyName() throws Exception {
        mockMvc.perform(post("/subjects")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"name\": \"\", \"weeklyGoalMinutes\": 150}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /subjects retorna 400 com o objetivo semanal negativo")
    void rejectNegativeWeeklyGoalMinutes() throws Exception {
        mockMvc.perform(post("/subjects")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"name\": \"Matemática\", \"weeklyGoalMinutes\": -150}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /subjects retorna 201 com a matéria sem espaços")
    void trimsNameBeforeSaving() throws Exception {
        mockMvc.perform(post("/subjects")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"name\": \" Matemática \", \"weeklyGoalMinutes\": 150}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Matemática"));
    }

    @Test
    @DisplayName("GET /subjects retorna 200 com duas matérias")
    void returnLengthTwo() throws Exception {
        repository.save(new Subject("Matemática", 150));
        repository.save(new Subject("Português", 150));

        mockMvc.perform(get("/subjects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("GET /subjects/{id} retorna 200 com a matéria")
    void returnSubjectById() throws Exception {
        Subject subject = repository.save(new Subject("Matemática", 150));

        mockMvc.perform(get("/subjects/{id}", subject.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Matemática"));
    }

    @Test
    @DisplayName("PUT /subjects/{id} retorna 200 com a matéria atualizada")
    void updateSubjectById() throws Exception {
        Subject subject = repository.save(new Subject("Matemática", 150));

        mockMvc.perform(put("/subjects/{id}", subject.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"name\": \"Física\", \"weeklyGoalMinutes\": 100}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Física"))
                .andExpect(jsonPath("$.weeklyGoalMinutes").value(100));
    }

    @Test
    @DisplayName("DELETE /subjects/{id} retorna 409 se a matéria tiver sessã(ões) de estudos vinculada")
    void deleteSubjectByIdConflict() throws Exception {
        Subject subject = repository.save(new Subject("Matemática", 150));
        StudySession studySession = studySessionRepository.save(new StudySession(subject, "Funções", LocalDateTime.now(), 30));

        mockMvc.perform(delete("/subjects/{id}", subject.getId()))
                .andExpect(status().isConflict());
    }
}
