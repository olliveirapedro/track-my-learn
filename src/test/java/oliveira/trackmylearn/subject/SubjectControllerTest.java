package oliveira.trackmylearn.subject;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    @BeforeEach
    void cleanDatabase() {
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
    @DisplayName("GET /subjects retorna 200 com length igual a 2")
    void returnLengthTwo() throws Exception {
        repository.save(new Subject("Matemática", 150));
        repository.save(new Subject("Português", 150));

        mockMvc.perform(get("/subjects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
}
