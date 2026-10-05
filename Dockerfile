# ---------- Estágio 1: build ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copia só o pom primeiro para aproveitar o cache das dependências
COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B package -DskipTests

# ---------- Estágio 2: runtime ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

# Usuário não-root
RUN useradd -r -u 1001 app
COPY --from=build /app/target/*.jar app.jar
USER app

EXPOSE 8080

# Limita o heap a uma fração da memória do container (bom para VM pequena)
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=60", "-jar", "app.jar"]