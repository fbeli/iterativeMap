package com.becb.processnewpoint.domain;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.document.Item;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@DynamoDBTable(tableName = "user_points")
@Setter
@Getter
public class UserPoint {

    public UserPoint(){}
    public UserPoint(Item item  ){

    }
    @DynamoDBHashKey(attributeName = "userId")
    private String userId;
    private String stringPointIds;
    private List<String> listOfPoints;



}
