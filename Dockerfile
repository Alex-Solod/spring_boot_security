# Builder stage
FROM eclipse-temurin:23-jre AS builder
WORKDIR /app
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=tools -jar application.jar extract --layers --launcher --destination application

#Final stage
FROM eclipse-temurin:23-jre
WORKDIR /application
COPY --from=builder /app/application/dependencies/ ./
COPY --from=builder /app/application/spring-boot-loader/ ./
COPY --from=builder /app/application/snapshot-dependencies/ ./
COPY --from=builder /app/application/application/ ./
EXPOSE 8080
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
