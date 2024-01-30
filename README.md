# iterativeMap
Interesting point on map.

Compile with maven command for each project before run docker-compose
```mvn compile package dependency:copy-dependencies -DincludeScope=runtime
docker-compose build
docker-compose run --service-ports point_service   

Startar o html executar os comandos python dentro da pasta mapbox 
python -m SimpleHTTPServer 8000 ou python3 -m http.server 8000
