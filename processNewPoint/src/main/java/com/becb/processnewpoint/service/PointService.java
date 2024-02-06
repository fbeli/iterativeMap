package com.becb.processnewpoint.service;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.becb.processnewpoint.domain.Point;
import com.becb.processnewpoint.domain.User;
import com.becb.processnewpoint.service.dynamodb.DynamoDbClient;
import com.becb.processnewpoint.service.file.FileService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class PointService {

    @Autowired
    FileService fileService;

    @Autowired
    private DynamoDbClient dynamoDbClient;

    public DynamoDbClient getDynamoDbClient() {
        return dynamoDbClient;
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());

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
    public void aprovePoint(String message, String aprovedValue){
        dynamoDbClient.updatePointToAproved(getUpdatePointValue(message), aprovedValue) ;
    }

    private Point getUpdatePointValue(String message){
        Point point = new Point();
        JSONObject jsonObject = new JSONObject(message);
        point.setPointId(jsonObject.getString("pointId"));
        point.setUser(new User());
        point.getUser().setUserEmail(jsonObject.getString("user_email"));

        return point;
    }


    public void gerarArquivoParaMapa(String message){

        Point point = new Point();
        JSONObject jsonObject = new JSONObject(message);
        point.setUser(new User());
        point.getUser().setUserId(jsonObject.getString("user_id"));
        point.getUser().setUserName(jsonObject.getString("user_name"));
        point.getUser().setUserEmail(jsonObject.getString("user_email"));

        ArrayList<Point>  points =
                convertItemsToPoints(dynamoDbClient.getPointsByAproved(AprovedEnum.asTrue.getValue()));

        try {
            fileService.createFileToMap(points, jsonObject.getString("file_name"));
        } catch (Exception e) {
            logger.info(
                    "Error to create file for not approved points: " + e.getMessage());

        }

    }

    public void gerarArquivoParaAprovacao(String message){

        Point point = new Point();
        JSONObject jsonObject = new JSONObject(message);
        point.setUser(new User());
        point.getUser().setUserId(jsonObject.getString("user_id"));
        point.getUser().setUserName(jsonObject.getString("user_name"));
        point.getUser().setUserEmail(jsonObject.getString("user_email"));

        ArrayList<Point>  points =
                convertItemsToPoints(dynamoDbClient.getPointsByAproved(AprovedEnum.asFalse.getValue()));

        try {
            fileService.createNotApprovedFile(points, jsonObject.getString("file_name"));
        } catch (Exception e) {
            logger.info(
                    "Error to create file for not approved points: " + e.getMessage());

        }
    }

    public ArrayList<Point> convertItemsToPoints(ItemCollection<ScanOutcome> ic)  {
        ArrayList<Point> points = new ArrayList<Point>();

        ic.iterator().forEachRemaining(item -> {
            points.add(convertItemToPoint(item));
        });

        return points;
    }

    public Point convertItemToPoint(Item item){
        Point   point = new Point();
        point.setPointId(item.getString("pointId"));
        point.setTitle(item.getString("point_title"));
        point.setDescription(item.getString("point_description"));
        point.setShortDescription(item.getString("point_short_description"));
        point.setLatitude(item.getString("point_latitude"));
        point.setLongitude(item.getString("point_longitude"));
        point.setUser(new User());
        point.getUser().setUserId(item.getString("user_id"));
        point.getUser().setUserName(item.getString("user_name"));
        point.getUser().setUserEmail(item.getString("user_email"));
        point.setAproved(item.getString("aprovado"));
        point.setLanguage(item.getString("language"));
        return point;

    }
}
