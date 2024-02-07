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

    public void savePoint(Point point) {
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
                .withString("language", "Portuguese");

        PutItemSpec itemSpec = new PutItemSpec();
        itemSpec.withItem(item);

        dynamoDB.getTable(pointTable).putItem(itemSpec);

        //dynamoDB.getTable(pointTable).scan().iterator().forEachRemaining(System.out::println);
       // getPointsNotAproved().iterator();
       //  getAuditData(point.getLatitude());



    }

    public Item getPoint(String pointId) {
        logger.info("Getting Value from DynamoDb ");

        Table table = dynamoDB.getTable(pointTable);

        return table.getItem(new PrimaryKey("pointId", pointId));

    }
    public long getTotalPoints() {
        logger.info("Getting Value from DynamoDb ");

        Table table = dynamoDB.getTable("points");

        return table.describe().getItemCount();

    }

    public void listAllValues() {

        dynamoDB.getTable(pointTable).scan().iterator().forEachRemaining(System.out::println);
    }
    public boolean updateNotApprovedPoint(String pointId, String userEmail){

        logger.info("Updating aproved point {}",pointId);

        UpdateItemOutcome updateItemOutcome = dynamoDB.getTable(pointTable)
                .updateItem(
                        new UpdateItemSpec().withPrimaryKey("pointId", pointId)
                                .withUpdateExpression("SET aprovado = :val0, usuario_aprovador = :val1")
                                .withValueMap(new ValueMap().with(":val1", userEmail)
                                        .with(":val0", AprovedEnum.asForget.getValue()))
                                .withReturnValues(ReturnValue.ALL_NEW)
                );
        if(!updateItemOutcome.getItem().get("aprovado").equals("forget")){
            logger.error("Error updating point {} to aproved", pointId);
            throw new RuntimeException("Error updating point to aproved");
        }
        return true;
    }

    public boolean updatePointToAproved(Point point, String aprovedValue) {
        logger.info("Updating aproved point {}",point.getPointId());

        UpdateItemOutcome updateItemOutcome = dynamoDB.getTable(pointTable)
                .updateItem(
                        new UpdateItemSpec().withPrimaryKey("pointId", point.getPointId())
                                .withUpdateExpression("SET aprovado = :val0, usuario_aprovador = :val1 " )
                                //+ "usuario_aprovador_id = :val2")
                                .withValueMap(new ValueMap().with(":val1", point.getUser().getUserEmail())
                                    .with(":val0", aprovedValue))
                                    //.with("val2", point.getUser().getUserId()))
                                .withReturnValues(ReturnValue.ALL_NEW)
                );
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
                //.withHashKeyValues(ativoOpt)
                .withIndexName("ativo-index")
                .withKeyConditionExpression(ativoValue)
                .withExpressionAttributeValues(ativoMap)
                .withConsistentRead(false);
                //.withLimit(pPageSize);
    }


    public ItemCollection<ScanOutcome>  getPointsByAproved(String aprovado) {
        Table table = dynamoDB.getTable(pointTable);
        Map<String, Object> expressionMap = new HashMap<>();
        expressionMap.put(":ativo", aprovado);
        //ItemCollection<QueryOutcome> query = table.query("aprovado","false");
       // table.scan("aprovado = :ativo",null,null,  expressionMap).iterator().forEachRemaining(System.out::println);
        ItemCollection<ScanOutcome> outcome =  table.scan("aprovado = :ativo",null,null,  expressionMap);
        return outcome;
    }

}