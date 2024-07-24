FROM amazoncorretto:17

WORKDIR /app

# Copier le fichier pom.xml et le wrapper Maven
COPY pom.xml /app/
COPY mvnw /app/
COPY .mvn /app/.mvn

# Installer les outils nécessaires
RUN yum update -y && yum install -y tar gzip

# Télécharger les dépendances Maven sans compiler
RUN ./mvnw dependency:go-offline

# Copier le code source de l'application
COPY src /app/src

# Construire l'application sans exécuter les tests
RUN ./mvnw package -DskipTests

# Copier le JAR généré dans le répertoire de travail
COPY target/Back-end-0.0.1-SNAPSHOT.jar /app/Back-end.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/app/Back-end.jar"]
