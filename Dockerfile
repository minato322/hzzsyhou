FROM eclipse-temurin:11-jdk AS builder

WORKDIR /app

COPY pom.xml .
COPY src/ src/

RUN mvn clean package -DskipTests

FROM eclipse-temurin:11-jre

WORKDIR /app

COPY --from=builder /app/target/zhishui-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]