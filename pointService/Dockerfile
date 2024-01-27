FROM openjdk:12

WORKDIR /app

COPY target/*.jar /app/app.jar

CMD ["java", "-jar", "app.jar"]

# rodar mvn: mvn compile dependency:copy-dependencies -DincludeScope=runtime