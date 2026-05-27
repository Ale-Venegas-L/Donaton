FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY backend/maven-settings.xml /usr/share/maven/conf/settings.xml
COPY backend/pom.xml backend/pom.xml
COPY backend/businessdomain/pom.xml backend/businessdomain/pom.xml
COPY backend/businessdomain/shared backend/businessdomain/shared
COPY backend/businessdomain/campaign/pom.xml backend/businessdomain/campaign/pom.xml
COPY backend/businessdomain/campaign/src backend/businessdomain/campaign/src
RUN mvn -B -q -f backend/pom.xml install -N -Dmaven.test.skip=true && \
    mvn -B -q -f backend/businessdomain/pom.xml install -N -Dmaven.test.skip=true && \
    mvn -B -q -f backend/businessdomain/shared/pom.xml install -Dmaven.test.skip=true && \
    mvn -B -q -f backend/businessdomain/campaign/pom.xml clean package -Dmaven.test.skip=true

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/backend/businessdomain/campaign/target/campaign-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/app/app.jar"]
