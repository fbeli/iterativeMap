package com.becb.processnewpoint.service.dynamodb;

import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.becb.processnewpoint.domain.Point;
import lombok.Setter;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
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
        logger.info("Audit data being inserted for event: {} ", point);

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
                .withBoolean("aprovado", false)
                .withString("language", "Portuguese");

        PutItemSpec itemSpec = new PutItemSpec();
        itemSpec.withItem(item);

        dynamoDB.getTable(pointTable).putItem(itemSpec);

       // dynamoDB.getTable(pointTable).scan().iterator().forEachRemaining(System.out::println);
       // getPointsNotAproved().iterator();
       //  getAuditData(point.getLatitude());



    }

    public Item getPoint(String pointId) {
        logger.info("Getting Value from DynamoDb ");

        Table table = dynamoDB.getTable("points");

        return table.getItem(new PrimaryKey("pointId", pointId));

    }

    public boolean updateNotApprovedPoint(String pointId, String userEmail){

        logger.info("Updating aproved point {}",pointId);

        UpdateItemOutcome updateItemOutcome = dynamoDB.getTable(pointTable)
                .updateItem(
                        new UpdateItemSpec().withPrimaryKey("pointId", pointId)
                                .withUpdateExpression("SET aprovado = :val0, usuario_aprovador = :val1")
                                .withValueMap(new ValueMap().with(":val1", userEmail)
                                        .with(":val0", "forget"))
                                .withReturnValues(ReturnValue.ALL_NEW)
                );
        if(!updateItemOutcome.getItem().get("aprovado").equals("forget")){
            logger.error("Error updating point {} to aproved", pointId);
            throw new RuntimeException("Error updating point to aproved");
        }
        return true;
    }

    public boolean updatePointToAproved(String pointId, String userEmail) {
        logger.info("Updating aproved point {}",pointId);

        UpdateItemOutcome updateItemOutcome = dynamoDB.getTable(pointTable)
                .updateItem(
                        new UpdateItemSpec().withPrimaryKey("pointId", pointId)
                                .withUpdateExpression("SET aprovado = :val0, usuario_aprovador = :val1")
                                .withValueMap(new ValueMap().with(":val1", userEmail)
                                    .with(":val0", "true"))
                                .withReturnValues(ReturnValue.ALL_NEW)
                );
        if(!updateItemOutcome.getItem().get("aprovado").equals("true")){
            logger.error("Error updating point {} to aproved", pointId);
            throw new RuntimeException("Error updating point to aproved");
        }
        return true;
    }



    public ItemCollection<ScanOutcome> getPointsNotAproved() {

        logger.info("Getting not aproved Value from DynamoDb ");

        Table table = dynamoDB.getTable("points");

        Map<String, Object> expressionAttributeValues = new HashMap<String, Object>();
        expressionAttributeValues.put(":aproved", false);

        ItemCollection<ScanOutcome> items = table.scan (
                "aprovado = :aproved",                                  //FilterExpression
                null,     //ProjectionExpression
                null,                                           //No ExpressionAttributeNames
                expressionAttributeValues);

        items.iterator().forEachRemaining(System.out::println);
        return items;

    }

}

