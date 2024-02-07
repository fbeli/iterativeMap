FROM openjdk:12

WORKDIR /app

COPY target/*.jar /app/app.jar

COPY wait-for-it.sh /wait-for-it.sh
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /wait-for-it.sh

# rodar mvn: mvn compile package dependency:copy-dependencies -DincludeScope=runtime