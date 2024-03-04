package com.becb.processnewpoint.service.dynamodb;


import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;


import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.becb.processnewpoint.domain.Point;
import com.becb.processnewpoint.service.AprovedEnum;
import lombok.Setter;

import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class DynamoDbClient {

    @Setter
    private String pointTable;

    private DynamoDB dynamoDB;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public void setDynamoDB(DynamoDB dynamoDB) {
        this.dynamoDB = dynamoDB;
    }

    public Item savePoint(Point point) {
        logger.info("Saving Point: {} ", point);

        Item item = new Item()
                .withString("pointId", point.getPointId())
                .withString("point_title", point.getTitle())
                .withString("point_description", point.getDescription())
                .withString("point_short_description", point.getShortDescription())
                .withString("point_latitude", point.getLatitude())
                .withString("point_longitude", point.getLongitude())
                .withString("user_id", point.getUser().getUserId())
                .withString("user_name", point.getUser().getUserName())
                .withString("user_email", point.getUser().getUserEmail())
                .withString("aprovado", AprovedEnum.asFalse.getValue())
                .withString("language", point.getLanguage().getValue());

        if(point.getAudio() != null && !point.getAudio().isBlank()) {
                item.withString("audio", point.getAudio());
        }


        PutItemSpec itemSpec = new PutItemSpec();
        itemSpec.withItem(item);

        PutItemOutcome putItem = dynamoDB.getTable(pointTable).putItem(itemSpec);
        return putItem.getItem();
    }

    public Item getPoint(String pointId) {

        Table table = dynamoDB.getTable(pointTable);

        return table.getItem(new PrimaryKey("pointId", pointId));

    }
    public long getTotalPoints() {


        Table table = dynamoDB.getTable("points");

        return table.describe().getItemCount();

    }

    public void listAllValues() {

        dynamoDB.getTable(pointTable).scan().iterator().forEachRemaining(System.out::println);
    }

    public boolean updatePointToAproved(Point point, String aprovedValue) {
        logger.info("Updating aproved as {} point {}",aprovedValue, point.getPointId());

        UpdateItemSpec updateItemSpec =  new UpdateItemSpec().withPrimaryKey("pointId", point.getPointId())
                .withUpdateExpression("SET aprovado = :val0, usuario_aprovador = :val1 " )
                .withValueMap(new ValueMap()
                        .with(":val1", point.getUser().getUserEmail())
                        .with(":val0", aprovedValue))
                .withReturnValues(ReturnValue.ALL_NEW);
        Table table = dynamoDB.getTable(pointTable);
        UpdateItemOutcome updateItemOutcome = table.updateItem(updateItemSpec);

        if(!updateItemOutcome.getItem().get("aprovado").equals(aprovedValue)){
            logger.error("Error updating point {} to aproved", point.getPointId());
            throw new RuntimeException("Error updating point to aproved");
        }
        return true;
    }

    private DynamoDBQueryExpression<Point> createQueryByAtivo(String ativoOpt) {
        String ativoValue = "ativo = :ativoOpt";
        AttributeValue atributeValue = new AttributeValue();
        atributeValue.setS("ativoOpt");
        Map<String, AttributeValue> ativoMap = new HashMap<>();
        ativoMap.put(":ativo", atributeValue);

        return new DynamoDBQueryExpression<Point>()
                .withIndexName("ativo-index")
                .withKeyConditionExpression(ativoValue)
                .withExpressionAttributeValues(ativoMap)
                .withConsistentRead(false);
    }


    public ItemCollection<ScanOutcome>  getPointsByAproved(String aprovado) {
        Table table = dynamoDB.getTable(pointTable);
        Map<String, Object> expressionMap = new HashMap<>();
        expressionMap.put(":ativo", aprovado);
        ItemCollection<ScanOutcome> outcome =  table.scan("aprovado = :ativo",null,null,  expressionMap);
        return outcome;
    }

}