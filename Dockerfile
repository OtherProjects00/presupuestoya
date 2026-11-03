# syntax=docker/dockerfile:1

# ---------- Etapa 1: compilar con Maven ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

# Copiamos primero el pom para aprovechar la caché de dependencias.
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

# Ahora el código y el empaquetado.
COPY src ./src
RUN mvn -B -q clean package -DskipTests

# ---------- Etapa 2: imagen final mínima ----------
FROM eclipse-temurin:21-jre-alpine

# Usuario sin privilegios: si alguien compromete la app, no es root.
RUN addgroup -S app && adduser -S -G app -h /app app

WORKDIR /app
COPY --from=build /workspace/target/*.jar /app/app.jar
RUN mkdir -p /app/data && chown -R app:app /app

USER app
EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=prod \
    DB_URL="jdbc:h2:file:/app/data/presupuestoya;DB_CLOSE_ON_EXIT=FALSE" \
    SESSION_COOKIE_SECURE=true

# El healthcheck usa el único endpoint de Actuator expuesto.
HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
    CMD wget -q -O - http://127.0.0.1:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/app.jar"]
