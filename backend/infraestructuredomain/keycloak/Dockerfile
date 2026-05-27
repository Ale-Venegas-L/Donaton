FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY backend/maven-settings.xml /usr/share/maven/conf/settings.xml
COPY backend/pom.xml backend/pom.xml
COPY backend/infraestructuredomain/pom.xml backend/infraestructuredomain/pom.xml
COPY backend/infraestructuredomain/keycloak/pom.xml backend/infraestructuredomain/keycloak/pom.xml
COPY backend/infraestructuredomain/keycloak/src backend/infraestructuredomain/keycloak/src
RUN mvn -B -q -f backend/pom.xml install -N -Dmaven.test.skip=true && \
    mvn -B -q -f backend/infraestructuredomain/pom.xml install -N -Dmaven.test.skip=true && \
    mvn -B -q -f backend/infraestructuredomain/keycloak/pom.xml clean package -DskipTests

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/backend/infraestructuredomain/keycloak/target/keycloak-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
