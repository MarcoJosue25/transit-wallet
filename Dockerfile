# Etapa 1: compilar el proyecto
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Primero solo lo necesario para bajar dependencias (así Docker reutiliza esta capa si no cambian)
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw -q dependency:go-offline

# Después el código y la compilación (sin tests: el de contexto necesita base de datos)
COPY src ./src
RUN ./mvnw -q package -DskipTests

# Etapa 2: imagen final, solo con el JRE y el .jar
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]