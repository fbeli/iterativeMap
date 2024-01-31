package com.becb.processnewpoint;

import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.becb.processnewpoint.service.dynamodb.DynamoDbClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.util.Collection;

@SpringBootTest
public class DynamoDbClientTest{

    @Autowired
    DynamoDbClient client;

    public void contextLoads() {
    }


    public void getNotApproved() {
        ItemCollection<ScanOutcome> items =  client.getPointsNotAproved();

        items.iterator().forEachRemaining(System.out::println);
        Assert.notEmpty((Collection<?>) items);
    }


    public void setNotApproved(){
        String id = "01HNG094DXF7A4HQPD8QKWHCBW";
        System.out.println(client.getPoint(id).toString());

        client.updateNotApprovedPoint(id, "fred.belisario@gmail.com");
        System.out.println(client.getPoint(id).toString());
    }

    //@Test
    public void setPointAproved(){
        System.out.println(client.getPoint("01HNG094DXF7A4HQPD8QKWHCBW").toString());
        client.updatePointToAproved("01HNG094DXF7A4HQPD8QKWHCBW", "fred.belisario@gmail.com");
        System.out.println(client.getPoint("01HNG094DXF7A4HQPD8QKWHCBW").toString());
    }
}


