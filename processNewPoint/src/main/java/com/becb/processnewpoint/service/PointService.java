package com.becb.processnewpoint.service;

import com.becb.processnewpoint.domain.Point;
import com.becb.processnewpoint.domain.User;
import com.becb.processnewpoint.service.dynamodb.DynamoDbClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PointService {

    @Autowired
    private DynamoDbClient dynamoDbClient;

    public void savePoint(String message){
        Point point = new Point();
        JSONObject jsonObject = new JSONObject(message);
        point.setTitle( jsonObject.getString("title"));
        point.setDescription(jsonObject.getString("description"));
        point.setLongitude(jsonObject.getString("longitude"));
        point.setLatitude(jsonObject.getString("latitude"));
        point.setUser(new User());
        point.getUser().setUserId(jsonObject.getString("user_id"));
        point.getUser().setUserName(jsonObject.getString("user_name"));
        point.getUser().setUserEmail(jsonObject.getString("user_email"));

        dynamoDbClient.savePoint(point);
    }
    public void aprovePoint(String pointId, String userId){


        dynamoDbClient.updatePointToAproved(pointId, userId);
    }
}
