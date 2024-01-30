# iterativeMap
Interesting point on map.

Compile with maven command for each project before run docker-compose
```mvn compile package dependency:copy-dependencies -DincludeScope=runtime
docker-compose build
docker-compose run --service-ports auth   

