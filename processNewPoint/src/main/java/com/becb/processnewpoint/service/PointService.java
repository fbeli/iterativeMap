package com.becb.processnewpoint.service;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.becb.processnewpoint.domain.AprovedEnum;
import com.becb.processnewpoint.domain.LanguageEnum;
import com.becb.processnewpoint.domain.Point;
import com.becb.processnewpoint.domain.User;
import com.becb.processnewpoint.service.dynamodb.DynamoDbClient;
import com.becb.processnewpoint.service.file.FileService;
import com.github.f4b6a3.ulid.UlidCreator;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    public Item savePoint(String message) {
        Point point = new Point();
        JSONObject jsonObject = new JSONObject(message);

        if (!jsonObject.has("pointId") || jsonObject.isNull("pointId") || jsonObject.getString("pointId").isBlank()) {
            point.setPointId(UlidCreator.getUlid().toString());
        } else {
            point.setPointId(jsonObject.getString("pointId"));
        }
        try {
            point.setTitle(jsonObject.getString("title"));
            point.setDescription(jsonObject.getString("description").replace("\"","'"));
            point.setLongitude(jsonObject.getString("longitude"));
            point.setLatitude(jsonObject.getString("latitude"));
            point.setUser(new User());
            point.getUser().setUserId(jsonObject.getString("user_id"));
            point.getUser().setUserName(jsonObject.getString("user_name"));
            point.getUser().setUserEmail(jsonObject.getString("user_email"));
            point.getUser().setInstagram(jsonObject.optString("instagram"));
            point.getUser().setShare(jsonObject.optBoolean("share"));
            point.setAudio(jsonObject.optString("audio"));
            point.setLanguage(jsonObject.optString("language"));
            point.setType(jsonObject.optString("type"));

            if(jsonObject.optString("photo") != "")
                point.addPhoto(jsonObject.optString("photo"));

            point.getUser().setGuide(jsonObject.optBoolean("guide"));

        } catch (JSONException jsonException) {
            logger.error("Essential field not present: \n{}", jsonException.getMessage());
            return null;
        }
        return savePoint(point);
    }

    public Item savePoint(Point point) {
        return dynamoDbClient.savePoint(point);
    }

    public void aprovePoint(String message, String aprovedValue) {
        dynamoDbClient.updatePointToAproved(getUpdatePointValue(message), aprovedValue);
    }

    private Point getUpdatePointValue(String message) {
        Point point = new Point();
        JSONObject jsonObject = new JSONObject(message);
        point.setPointId(jsonObject.getString("pointId"));
        point.setUser(new User());
        point.getUser().setUserEmail(jsonObject.getString("user_email"));

        return point;
    }

    public void gerarArquivoParaMapa(String message) {

        Point point = new Point();
        JSONObject jsonObject = new JSONObject(message);
        point.setUser(new User());
        point.getUser().setUserId(jsonObject.getString("user_id"));
        point.getUser().setUserName(jsonObject.getString("user_name"));
        point.getUser().setUserEmail(jsonObject.getString("user_email"));

        ArrayList<Point> points =
                convertItemsToPoints(dynamoDbClient.getPointsByAproved(AprovedEnum.asTrue.getValue()));

        try {
            fileService.createFileToMap(points, jsonObject.getString("file_name"));
        } catch (Exception e) {
            logger.info(
                    "Error to create file for not approved points: " + e.getMessage());
        }
    }

    public void gerarArquivoParaAprovacao(String message) {

        JSONObject jsonObject = new JSONObject(message);

        ArrayList<Point> points =
                convertItemsToPoints(dynamoDbClient.getPointsByAproved(AprovedEnum.asFalse.getValue()));

        logger.info("File :  {} is been created with {} points", jsonObject.getString("file_name"), points.size());
        try {
            fileService.createNotApprovedFile(points, jsonObject.getString("file_name"));
        } catch (Exception e) {
            logger.info(
                    "Error to create file for not approved points: {}", e.getMessage());
        }
    }

    public ArrayList<Point> convertItemsToPoints(ItemCollection<ScanOutcome> ic) {
        ArrayList<Point> points = new ArrayList<Point>();

        ic.iterator().forEachRemaining(item -> {
            points.add(convertItemToPoint(item));
        });

        return points;
    }

    public Point convertItemToPoint(Item item) {
        if (item == null)
            return null;
        Point point = new Point();
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
        point.setType(item.getString("type"));

        point.getUser().setShare((item.getString("share") != null) ? Boolean.parseBoolean(item.getString("share")) : true);
        point.getUser().setGuide(item.getString("guide") != null && Boolean.parseBoolean(item.getString("guide")));

        point.setAproved(item.getString("aprovado"));
        point.setAudio(item.getString("audio"));
        if (item.getString("instagram") != null) {
            point.getUser().setInstagram(item.getString("instagram"));
        } else
            point.getUser().setInstagram("");
        if (item.getString("language") != null)
            point.setLanguage(Enum.valueOf(LanguageEnum.class, item.getString("language")));
        if (item.getString("photos") != null)
            point.setPhotos(addPhotos(item.getString("photos")));
        return point;

    }

    public List<String> addPhotos(String photosArray) {

        if (!photosArray.startsWith("{") && !photosArray.endsWith("}")) {
            return null;
        }
        photosArray = photosArray.replace("{", "");
        photosArray = photosArray.replace("}", "");

        String[] array = photosArray.split(",");
        List<String> list = new ArrayList<String>();
        for (String element : array) {
            list.add(element);
        }
        return list;
    }

    public boolean addFileToPoint(String message) {
        JSONObject jsonObject = new JSONObject(message);

        String pointId = (String) jsonObject.get("point_id");
        String path = (String) jsonObject.get("file_path");
        Point point = this.convertItemToPoint(dynamoDbClient.getPoint(pointId));

        if (point == null || path == null) {
            return false;
        }
        if (path.endsWith("jpg") || path.endsWith("jpeg") || path.endsWith("png")) {
            point.addPhoto(path);
            return dynamoDbClient.addPhotoToPoint(point);
        }

        point.setAudio(path);
        return dynamoDbClient.addAudioToPoint(point);
    }
    public boolean addFileLinkToPoint(String message) {
        JSONObject jsonObject = new JSONObject(message);

        String pointId = (String) jsonObject.get("point_id");
        String path = (String) jsonObject.get("link");
        Point point = this.convertItemToPoint(dynamoDbClient.getPoint(pointId));

        if (point == null || path == null) {
            return false;
        }
        if (path.endsWith("jpg") || path.endsWith("jpeg") || path.endsWith("png")) {
            point.addPhoto(path);
            return dynamoDbClient.addPhotoToPoint(point);
        }

        point.setAudio(path);
        return dynamoDbClient.addAudioToPoint(point);
    }

    public void addVotetoPoint(String message) {
        JSONObject jsonObject = new JSONObject(message);

        String pointId =  jsonObject.optString("pointId");
        Integer vote = jsonObject.optInt("vote");


    }
}
