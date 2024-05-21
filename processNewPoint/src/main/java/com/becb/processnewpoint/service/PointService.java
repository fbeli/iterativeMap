package com.becb.processnewpoint.service;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.becb.processnewpoint.domain.AprovedEnum;
import com.becb.processnewpoint.domain.LanguageEnum;
import com.becb.processnewpoint.domain.Point;
import com.becb.processnewpoint.domain.User;
import com.becb.processnewpoint.repository.PointRepository;
import com.becb.processnewpoint.service.dynamodb.DynamoDbClient;
import com.becb.processnewpoint.service.file.FileService;
import com.becb.processnewpoint.service.sqs.SqsChronClient;
import com.github.f4b6a3.ulid.UlidCreator;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PointService {


    public PointService(@Autowired FileService fileService,
                        @Autowired DynamoDbClient dynamoDbClient,
                        @Autowired PointRepository pointRepository,
                        @Autowired MapService mapService) {
        this.fileService = fileService;
        this.dynamoDbClient = dynamoDbClient;
        this.pointRepository = pointRepository;
        this.mapService = mapService;
    }

    private FileService fileService;
    private DynamoDbClient dynamoDbClient;
    private PointRepository pointRepository;
    private MapService mapService;

    public DynamoDbClient getDynamoDbClient() {
        return dynamoDbClient;
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public Point  messageToPoint(String message){
        Point point = new Point();
        JSONObject jsonObject = new JSONObject(message);

        if (!jsonObject.has("pointId") || jsonObject.isNull("pointId") || jsonObject.getString("pointId").isBlank()) {
            point.setPointId(UlidCreator.getUlid().toString());
        } else {
            point.setPointId(jsonObject.getString("pointId"));
        }
        try {
            point.setTitle(jsonObject.optString("title"));
            point.setDescription(jsonObject.optString("description").replace("\"","'").replace("\n","'"));
            point.setLongitude(jsonObject.optString("longitude"));
            point.setLatitude(jsonObject.optString("latitude"));
            point.setUser(new User());
            point.getUser().setUserId(jsonObject.optString("user_id"));
            point.getUser().setUserName(jsonObject.optString("user_name"));
            point.getUser().setUserEmail(jsonObject.optString("user_email"));
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
        return point;
    }

    public Item savePoint(String message) {
        Point point = messageToPoint(message);
        if(point != null)
            return savePointDynamo(point);
        return null;
    }

    @Deprecated
    public Item savePointDynamo(Point point) {
        return dynamoDbClient.savePoint(point);
    }

    public Point savePointDb(Point point) {

        if(point != null) {
            if(point.getAproved() == null)
                point.setAproved("false");
            if(point.getCountry() == null)
                mapService.setPlace(point);
            pointRepository.save(point);
        }

        return point;
    }



    public void aprovePoint(String message, String aprovedValue) {
        Point point = getUpdatePointValue(message);

        dynamoDbClient.updatePointToAproved(point, aprovedValue);

        Optional<Point> optionalPoint = pointRepository.findPointByPointId(point.getPointId());
        if(optionalPoint.isPresent()) {
            optionalPoint.get().setAproved(aprovedValue);
            savePointDb(optionalPoint.get());
        }


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

        List<Point> points = getApprovedPoints();

        points.parallelStream().forEach(p -> {
            if (p.getCountry() == null) {
                this.savePointDb(p);
            }
        });

        try {
            fileService.createFileToMap(points, jsonObject.getString("file_name"));
        } catch (Exception e) {
            logger.info(
                    "Error to create file for not approved points: {}", e.getMessage());
        }
    }

    //TODO: fix it
    public  List<Point> getApprovedPoints() {
        List<Point> pointdb = pointRepository.findAllByAproved(AprovedEnum.asTrue.getValue());
        //List<Point> pointdy = convertItemsToPoints(dynamoDbClient.getPointsByAproved(AprovedEnum.asTrue.getValue()));

        return pointdb;
    }
    public  List<Point> getApprovedPointsDb() {
        List<Point> pointdb = pointRepository.findAllByAproved(AprovedEnum.asTrue.getValue());
        //List<Point> pointdy = convertItemsToPoints(dynamoDbClient.getPointsByAproved(AprovedEnum.asTrue.getValue()));

        return pointdb;
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


        addFileToPointDb( pointId, path);
        return addFileToPointDynamo(pointId,path);
    }

    @Deprecated
    public boolean addFileToPointDynamo(String pointId,  String path){
        Item item = dynamoDbClient.getPoint(pointId);
        Point point = this.convertItemToPoint(item);
        if (point == null || path == null) {
            logger.info("PointId not found{}, trying again....", pointId);
            return false;
        }
        if (path.endsWith("jpg") || path.endsWith("jpeg") || path.endsWith("png")) {
            point.addPhoto(path);
            return dynamoDbClient.addPhotoToPoint(point);
        }

        point.setAudio(path);
        return dynamoDbClient.addAudioToPoint(point);
    }

        public void addFileToPointDb(String pointId,  String path){

            Optional<Point> opPoint = pointRepository.findPointByPointId(pointId);
            if (opPoint.isPresent()) {
                Point point = opPoint.get();
                if (path.endsWith("jpg") || path.endsWith("jpeg") || path.endsWith("png") || path.endsWith("gif")) {
                    point.addPhoto(path);
                }else {
                    point.setAudio(path);
                }
                pointRepository.save(point);
            }
    }

    public boolean addFileLinkToPoint(String message) {
        JSONObject jsonObject = new JSONObject(message);

        String pointId = (String) jsonObject.get("point_id");
        String path = (String) jsonObject.get("link");

        addFileToPointDb(pointId, path);
        return addFileLinkToPointDynamo(pointId,path);

    }

     @Deprecated
    public boolean addFileLinkToPointDynamo(String pointId,  String path){
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

    public Page<Point>  getPointsByUserId(Pageable pageable, String userId){
        Page<Point> page = pointRepository.findAllByUserNotBlocked(pageable, userId);
        page.stream().filter( p -> p.getCountry() == null)
                .forEach(p -> this.savePointDb(p));
        return page;
    }
    public Point getPointById(String pointId){
        return pointRepository.findPointByPointId(pointId).orElse(null);
    }

    public Point updatePointObject(Point newPoint, Point oldPoint) {

        if(newPoint == null || oldPoint == null || !newPoint.getPointId().equals(oldPoint.getPointId()) )
            return null;
        Field[] fields = Point.class.getDeclaredFields();
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                Object newValue = field.get(newPoint);

                if (newValue != null && !newValue.toString().isBlank()) {

                        field.set(oldPoint, newValue);

                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return oldPoint;
    }

    //@Autowired
    SqsChronClient chron;
    public void adiantarChron(){
        //chron.receberMensagens();
    }

}
