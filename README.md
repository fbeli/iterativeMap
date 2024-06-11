# iterativeMap
Interesting point on map.


## How to run ##



First of all, you need to have docker and docker-compose installed in your machine.
## Update jar para voice
In directory resources

``
mvn install:install-file -Dfile=voicerss_tts.jar -DgroupId=com.voicerss.tts -DartifactId=voicerss -Dversion=0.0.1-SNAPSHOT -Dpackaging=jar
``

## you need docker
You can download docker from [here](https://www.docker.com/products/docker-desktop) and docker-compose from [here](https://docs.docker.com/compose/install/).

You have 2 docker-compose.yml, firs to run with your code, you need build it and create jar files using maven:
``` 
into folders pointService and processNewPoint 
run: mvn compile package dependency:copy-dependencies -DincludeScope=runtime

go to dir /Docker and run: docker-compose build and docker-compose up
```

If you want run stable version:

```
you can use the docker-compose.yml in /Docker/stable/docker-compose.yml
run: docker-compose up
```



## Extra information

Compile with maven command for each project before run docker-compose
```
mvn compile package dependency:copy-dependencies -DincludeScope=runtime
docker-compose build
docker-compose run --service-ports point_service   
```
Startar o html executar os comandos python dentro da pasta mapbox 
python -m SimpleHTTPServer 8000 ou python3 -m http.server 8000


## DynamoDb


Criar tabela no dynamo db:
aws dynamodb create-table --table-name points --endpoint-url http://dynamodb:8000  --region ue-central-1 
--attribute-definitions AttributeName=pointId,AttributeType=S AttributeName=aprovado,AttributeType=S 
--key-schema AttributeName=pointId,KeyType=HASH --provisioned-throughput ReadCapacityUnits=2,WriteCapacityUnits=2 
--global-secondary-indexes "{\"IndexName\":\"aprovado-index\",\"KeySchema\":[{\"AttributeName\":\"aprovado\",\"KeyType\":\"HASH\"}],
\"Projection\":{\"ProjectionType\":\"ALL\"},\"ProvisionedThroughput\":{\"ReadCapacityUnits\":5,\"WriteCapacityUnits\":5}}"


aws dynamodb update index in dynamo db:
 aws dynamodb update-table --table-name points --attribute-definitions AttributeName=\"aprovado\",AttributeType=S --global-secondary-index-updates "[ { \"Create\": { \"IndexName\": \"aprovado-index\", \"KeySchema\": [{\"AttributeName\":\"aprovado\",\"KeyType\":\"HASH\"}], \"Projection\": { \"ProjectionType\":\"ALL\" }, \"ProvisionedThroughput\":{\"ReadCapacityUnits\":5,\"WriteCapacityUnits\":5}}}]" --endpoint-url http://dynamodb:8000 --region us-west-2

aws dynamodb list-tables --endpoint-url http://dynamodb:8000  --region us-west-2

aws dynamodb query \
 --table-name points \
 --index-name aprovado-index \
 --key-condition-expression "aprovado=:aprovado" \
 --expression-attribute-values "{\":aprovado\":{\"S\":\"false\"}}" \
 --endpoint-url http://dynamodb:8000  --region us-west-2

aws dynamodb query --table-name points --key-condition-expression "aprovado = :name" --expression-attribute-values  '{":name":{"S":"false"}}' \
 --endpoint-url http://dynamodb:8000  --region us-west-2

aws dynamodb scan    --table-name points --endpoint-url http://dynamodb:8000  --region us-west-2
     --filter-expression "aprovado = :name" \
     --expression-attribute-values '{":name":{"S":"false"}}' \
 --endpoint-url http://dynamodb:8000  --region us-west-2