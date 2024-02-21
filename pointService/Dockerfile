FROM openjdk:12

WORKDIR /app

COPY target/*.jar /app/app.jar
COPY target/*.conf /app/app.conf

CMD ["java", "-jar", "app.jar"]

# rodar mvn: mvn compile dependency:copy-dependencies -DincludeScope=runtime