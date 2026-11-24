# Track My Learn

API REST para registrar e acompanhar sessões de estudo por matéria, com meta semanal de horas e filtros por período.

Construída com **Java 21** e **Spring Boot 4**, persistência em **PostgreSQL** com migrações versionadas via **Flyway**, documentação automática com **OpenAPI/Swagger** e testes de integração com **Testcontainers**.

---

## Sumário

- [Funcionalidades](#funcionalidades)
- [Stack](#stack)
- [Modelo de domínio](#modelo-de-domínio)
- [Endpoints](#endpoints)
- [Tratamento de erros](#tratamento-de-erros)
- [Como rodar](#como-rodar)
- [Testes](#testes)
- [Documentação da API](#documentação-da-api)
- [CI/CD](#cicd)
- [Estrutura do projeto](#estrutura-do-projeto)
- [Licença](#licença)

---

## Funcionalidades

- CRUD completo de **matérias** (`Subject`), com meta semanal em minutos.
- CRUD completo de **sessões de estudo** (`StudySession`), sempre vinculadas a uma matéria.
- Listagem paginada de sessões com filtros por **matéria** e **intervalo de datas**.
- Validação de entrada (Bean Validation) e respostas de erro padronizadas com `ProblemDetail` (RFC 9457).
- Bloqueio de exclusão de matérias que ainda possuem sessões de estudo.
- Migrações de banco versionadas (Flyway) — o schema nunca é criado pelo Hibernate (`ddl-auto=validate`).

## Stack

| Camada         | Tecnologia                                                  |
| -------------- | ----------------------------------------------------------- |
| Linguagem      | Java 21                                                     |
| Framework      | Spring Boot 4.1 (Web MVC, Data JPA, Validation)              |
| Banco          | PostgreSQL 16                                               |
| Migrações      | Flyway                                                      |
| Documentação   | springdoc-openapi (Swagger UI)                              |
| Testes         | JUnit 5, Spring Boot Test, Testcontainers (PostgreSQL)       |
| Build          | Maven (wrapper incluído)                                    |
| Infra          | Docker / Docker Compose, GitHub Actions, GHCR               |

## Modelo de domínio

```
Subject (subject)                     StudySession (study_session)
├── id                  BIGSERIAL     ├── id                 BIGSERIAL
├── name        UNIQUE  VARCHAR(100)  ├── subject_id         BIGINT → subject(id)
└── weekly_goal_minutes INT           ├── topic              VARCHAR(150)
                                      ├── started_at         TIMESTAMP
                                      ├── duration_minutes   INT (> 0)
                                      └── notes              TEXT
```

Uma matéria pode ter várias sessões de estudo; uma sessão pertence a exatamente uma matéria.

## Endpoints

Base URL local: `http://localhost:8080`

### Matérias — `/subjects`

| Método   | Rota             | Descrição                          | Sucesso |
| -------- | ---------------- | ---------------------------------- | ------- |
| `POST`   | `/subjects`      | Cria uma matéria                   | `201`   |
| `GET`    | `/subjects`      | Lista todas as matérias            | `200`   |
| `GET`    | `/subjects/{id}` | Busca matéria por ID               | `200`   |
| `PUT`    | `/subjects/{id}` | Atualiza uma matéria               | `200`   |
| `DELETE` | `/subjects/{id}` | Remove uma matéria (sem sessões)   | `204`   |

**Corpo (POST/PUT):**

```json
{
  "name": "Estruturas de Dados",
  "weeklyGoalMinutes": 300
}
```

**Resposta:**

```json
{
  "id": 1,
  "name": "Estruturas de Dados",
  "weeklyGoalMinutes": 300
}
```

### Sessões de estudo — `/study-sessions`

| Método   | Rota                  | Descrição                                | Sucesso |
| -------- | --------------------- | ---------------------------------------- | ------- |
| `POST`   | `/study-sessions`     | Registra uma sessão de estudo            | `201`   |
| `GET`    | `/study-sessions`     | Lista sessões (filtros + paginação)      | `200`   |
| `GET`    | `/study-sessions/{id}` | Busca sessão por ID                     | `200`   |
| `PUT`    | `/study-sessions/{id}`| Atualiza uma sessão                      | `200`   |
| `DELETE` | `/study-sessions/{id}`| Remove uma sessão                        | `204`   |

**Corpo (POST/PUT):**

```json
{
  "subjectId": 1,
  "topic": "Árvores binárias de busca",
  "startedAt": "2026-11-23T19:30:00",
  "durationMinutes": 90,
  "notes": "Revisão de inserção e remoção"
}
```

**Resposta:**

```json
{
  "id": 10,
  "subjectId": 1,
  "topic": "Árvores binárias de busca",
  "startedAt": "2026-11-23T19:30:00",
  "durationMinutes": 90,
  "notes": "Revisão de inserção e remoção"
}
```

**Parâmetros de consulta do `GET /study-sessions`:**

| Parâmetro   | Tipo       | Padrão | Descrição                                             |
| ----------- | ---------- | ------ | ----------------------------------------------------- |
| `subjectId` | `Long`     | —      | Filtra por matéria                                    |
| `from`      | `date`     | —      | Data inicial (inclusiva), formato `YYYY-MM-DD`        |
| `to`        | `date`     | —      | Data final (exclusiva), formato `YYYY-MM-DD`          |
| `page`      | `int`      | `0`    | Página (≥ 0)                                          |
| `size`      | `int`      | `20`   | Tamanho da página (1–100)                             |

As sessões são ordenadas por `startedAt` e `id`, ambas em ordem decrescente. A resposta é um `Page` do Spring Data (`content`, `totalElements`, `totalPages`, etc.).

Exemplo:

```
GET /study-sessions?subjectId=1&from=2026-11-01&to=2026-12-01&page=0&size=10
```

## Tratamento de erros

Os erros seguem o formato `ProblemDetail`:

```json
{
  "type": "about:blank",
  "title": "Conflict",
  "status": 409,
  "detail": "Já existe uma matéria com o nome: Estruturas de Dados"
}
```

| Situação                                        | Status |
| ----------------------------------------------- | ------ |
| Matéria inexistente / sessão inexistente         | `404`  |
| Nome de matéria duplicado                        | `409`  |
| Exclusão de matéria com sessões vinculadas       | `409`  |
| Filtros inválidos (`from > to`, `page`, `size`)  | `400`  |
| Corpo inválido (validação de campos)             | `400`  |

## Como rodar

### Pré-requisitos

- Docker e Docker Compose (para o modo containerizado)
- JDK 21 (para rodar localmente fora do Docker)

### 1. Variáveis de ambiente

Copie o arquivo de exemplo e defina a senha do banco:

```bash
cp .env.example .env
```

```dotenv
POSTGRES_DB=track_my_learn
POSTGRES_USER=track_my_learn
POSTGRES_PASSWORD=uma-senha-segura
```

### 2. Com Docker Compose (recomendado)

Sobe banco + aplicação:

```bash
docker compose up --build
```

A API ficará disponível em `http://localhost:8080`. O serviço `app` aguarda o banco ficar saudável antes de iniciar, e as migrações do Flyway rodam automaticamente no boot.

Para usar a imagem publicada no GHCR em vez de buildar localmente:

```bash
docker compose pull app
docker compose up -d
```

### 3. Localmente (IDE ou Maven)

Subir apenas o banco, descomentando a seção `ports` do serviço `db` no `docker-compose.yaml`:

```bash
docker compose up -d db
```

E rodar a aplicação apontando para o banco local:

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/track_my_learn
export SPRING_DATASOURCE_USERNAME=track_my_learn
export SPRING_DATASOURCE_PASSWORD=uma-senha-segura

./mvnw spring-boot:run
```

## Testes

Os testes de integração usam **Testcontainers** para subir um PostgreSQL real, então é necessário ter o Docker em execução.

```bash
./mvnw test
```

Ou o build completo com verificação:

```bash
./mvnw verify
```

## Documentação da API

Com a aplicação rodando:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## CI/CD

O workflow `.github/workflows/ci.yml` roda `./mvnw -B verify` em todo `push` e `pull request` para a branch `main`, com JDK 21 (Temurin) e cache do Maven.

O `Dockerfile` usa build multi-stage (Maven para compilar, JRE 21 para executar), roda como usuário não-root e limita o heap a 60% da memória do container — pensado para VMs pequenas.

## Estrutura do projeto

```
src/main/java/oliveira/trackmylearn/
├── TrackMyLearnApplication.java
├── common/
│   └── ApiExceptionHandler.java        # Tratamento global de erros (ProblemDetail)
├── subject/                            # CRUD de matérias
│   ├── Subject.java
│   ├── SubjectController.java
│   ├── SubjectRepository.java
│   ├── SubjectRequest.java
│   ├── SubjectResponse.java
│   ├── SubjectService.java
│   └── *Exception.java
└── studysession/                       # CRUD + filtros de sessões de estudo
    ├── StudySession.java
    ├── StudySessionController.java
    ├── StudySessionRepository.java
    ├── StudySessionRequest.java
    ├── StudySessionResponse.java
    ├── StudySessionService.java
    └── *Exception.java

src/main/resources/
├── application.properties
└── db/migration/V1__init.sql           # Schema inicial (Flyway)

src/test/java/oliveira/trackmylearn/
├── TestcontainersConfiguration.java
├── subject/SubjectControllerTest.java
└── studysession/StudySessionControllerTest.java
```

## Licença

Distribuído sob a licença MIT. Veja [LICENSE](LICENSE) para mais detalhes.
