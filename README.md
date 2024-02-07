# iterativeMap
Interesting point on map.

Compile with maven command for each project before run docker-compose
```mvn compile package dependency:copy-dependencies -DincludeScope=runtime
docker-compose build
docker-compose run --service-ports point_service   

Startar o html executar os comandos python dentro da pasta mapbox 
python -m SimpleHTTPServer 8000 ou python3 -m http.server 8000

Criar tabela no dynamo db:
aws dynamodb create-table --table-name points --endpoint-url http://dynamodb:8000  --region us-west-2 --attribute-definitions AttributeName=pointId,AttributeType=S AttributeName=aprovado,AttributeType=S --key-schema AttributeName=pointId,KeyType=HASH --provisioned-throughput ReadCapacityUnits=2,WriteCapacityUnits=2 --global-secondary-indexes "{\"IndexName\":\"aprovado-index\",\"KeySchema\":[{\"AttributeName\":\"aprovado\",\"KeyType\":\"HASH\"}],\"Projection\":{\"ProjectionType\":\"ALL\"},\"ProvisionedThroughput\":{\"ReadCapacityUnits\":5,\"WriteCapacityUnits\":5}}"


aws dynamodb update index in dynamo db:
 aws dynamodb update-table --table-name points --attribute-definitions AttributeName=\"aprovado\",AttributeType=S --global-secondary-index-updates "[ { \"Create\": { \"IndexName\": \"aprovado-index\", \"KeySchema\": [{\"AttributeName\":\"aprovado\",\"KeyType\":\"HASH\"}], \"Projection\": { \"ProjectionType\":\"ALL\" }, \"ProvisionedThroughput\":{\"ReadCapacityUnits\":5,\"WriteCapacityUnits\":5}}}]" --endpoint-url http://dynamodb:8000 --region us-west-2

aws dynamodb list-tables --endpoint-url http://dynamodb:8000  --region us-west-2

aws dynamodb query \
 --table-name points \
 --index-name aprovado-index \
 --key-condition-expression "aprovado=:aprovado" \
 --expression-attribute-values "{\":aprovado\":{\"S\":\"false\"}}" \
 --endpoint-url http://dynamodb:8000  --region us-west-2

aws dynamodb query \
    --table-name points \
    --key-condition-expression "aprovado = :name" \
    --expression-attribute-values  '{":name":{"S":"false"}}' \
 --endpoint-url http://dynamodb:8000  --region us-west-2

aws dynamodb scan    --table-name points --endpoint-url http://dynamodb:8000  --region us-west-2
     --filter-expression "aprovado = :name" \
     --expression-attribute-values '{":name":{"S":"false"}}' \
 --endpoint-url http://dynamodb:8000  --region us-west-2