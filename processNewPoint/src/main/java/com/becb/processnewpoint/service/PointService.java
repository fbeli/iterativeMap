package com.becb.processnewpoint.service;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.becb.processnewpoint.domain.AprovedEnum;
import com.becb.processnewpoint.domain.LanguageEnum;
import com.becb.processnewpoint.domain.Point;
import com.becb.processnewpoint.domain.User;
import com.becb.processnewpoint.dto.PointDto;
import com.becb.processnewpoint.repository.PointRepository;
import com.becb.processnewpoint.service.audio.AudioService;
import com.becb.processnewpoint.service.dynamodb.DynamoDbClient;
import com.becb.processnewpoint.service.file.FileService;
import com.becb.processnewpoint.service.sqs.SqsChronClient;
import com.becb.processnewpoint.service.translate.TranslateService;
import com.github.f4b6a3.ulid.UlidCreator;
import org.hibernate.ObjectNotFoundException;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

@Service
public class PointService {


    private final Logger logger = LoggerFactory.getLogger(getClass());
    //@Autowired
    SqsChronClient chron;

    private final TranslateService translateService;
    private final FileService fileService;
    private final DynamoDbClient dynamoDbClient;
    private final PointRepository pointRepository;
    private final MapService mapService;
    private final AudioService audioService;

    public PointService(@Autowired FileService fileService,
                        @Autowired DynamoDbClient dynamoDbClient,
                        @Autowired PointRepository pointRepository,
                        @Autowired MapService mapService,
                        @Autowired TranslateService translateService,
                        @Autowired AudioService audioService) {
        this.fileService = fileService;
        this.dynamoDbClient = dynamoDbClient;
        this.pointRepository = pointRepository;
        this.mapService = mapService;
        this.translateService = translateService;
        this.audioService = audioService;
    }

    public Point messageToPoint(String message) {
        Point point = new Point();
        JSONObject jsonObject = new JSONObject(message);

        if (!jsonObject.has("pointId") || jsonObject.isNull("pointId") || jsonObject.getString("pointId").isBlank()) {
            point.setPointId(UlidCreator.getUlid().toString());
        } else {
            point.setPointId(jsonObject.getString("pointId"));
        }
        try {
            point.setTitle(jsonObject.optString("title"));
            point.setDescription(jsonObject.optString("description").replace("\"", "'").replace("\n", "'"));
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

            if (!jsonObject.optString("photo").equals(""))
                point.addPhoto(jsonObject.optString("photo"));

            point.getUser().setGuide(jsonObject.optBoolean("guide"));

        } catch (JSONException jsonException) {
            logger.error("Essential field not present: \n{}", jsonException.getMessage());
            return null;
        }
        return point;
    }

   /* public Item savePoint(String message) {
        Point point = messageToPoint(message);
        if (point != null)
            return savePointDynamo(point);
        return null;
    }*/

    /*@Deprecated
    public Item savePointDynamo(Point point) {
        return dynamoDbClient.savePoint(point);
    }*/

    public Point savePointDb(Point point) {

        if(point.getDescription().length()>5000 )
            point.setDescription(point.getDescription().substring(0, 5000));
        if (point != null) {
            if (point.getAproved() == null)
                point.setAproved("true");
            if (point.getCountry() == null)
                mapService.setPlace(point);
            pointRepository.save(point);
        }

        return point;
    }

    public void aprovePoint(String message, String aprovedValue) {
        Point point = getUpdatePointValue(message);
        dynamoDbClient.updatePointToAproved(point, aprovedValue);
        Optional<Point> optionalPoint = pointRepository.findPointByPointId(point.getPointId());
        if (optionalPoint.isPresent()) {
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

    public List<String>  gerarArquivoParaMapa(String message) {
        List<String> fileNames = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(message);

        var langs = Arrays.stream(LanguageEnum.values());
        langs.forEach(lang -> {
            List<Point> points = getApprovedPoints(lang);
            points.parallelStream().forEach(p -> {
                if (p.getCountry() == null) {
                    this.savePointDb(p);
                }
            });
            fileNames.add(fileService.createFileToMap(points, jsonObject.getString("file_name")).get(0));
        });

        return fileNames;
    }

    public List<Point> getApprovedPoints() {
        return pointRepository.findAllByAproved(AprovedEnum.asTrue.getValue());
    }
    public List<Point> getApprovedPoints(LanguageEnum languageEnum) {
        return pointRepository.findAllByAprovedAndLanguage(AprovedEnum.asTrue.getValue(), languageEnum);
    }

    public void gerarArquivoParaAprovacao(String message) {

        JSONObject jsonObject = new JSONObject(message);

        List<Point> points = pointRepository.findAllByAproved(AprovedEnum.asFalse.getValue());

        logger.info("File :  {} is been created with {} points", jsonObject.getString("file_name"), points.size());
        try {
            fileService.createNotApprovedFile(points, jsonObject.getString("file_name"));
        } catch (Exception e) {
            logger.info(
                    "Error to create file for not approved points: {}", e.getMessage());
        }
    }

    public ArrayList<Point> convertItemsToPoints(ItemCollection<ScanOutcome> ic) {
        ArrayList<Point> points = new ArrayList<>();

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

        point.getUser().setShare(item.getString("share") == null || Boolean.parseBoolean(item.getString("share")));
        point.getUser().setGuide(item.getString("guide") != null && Boolean.parseBoolean(item.getString("guide")));

        point.setAproved(item.getString("aprovado"));
        point.setAudio(item.getString("audio"));
        if (item.getString("instagram") != null) {
            point.getUser().setInstagram(item.getString("instagram"));
        } else
            point.getUser().setInstagram("");
        if (item.getString("language") != null)
            point.setLanguage(Enum.valueOf(LanguageEnum.class, item.getString("language").toUpperCase()));
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
        List<String> list = new ArrayList<>();
        Collections.addAll(list, array);
        return list;
    }

    public boolean addFileToPoint(String message) {
        JSONObject jsonObject = new JSONObject(message);
        String pointId = (String) jsonObject.get("point_id");
        String path = (String) jsonObject.get("file_path");

        return addFileToPointDb(pointId, path);
    }

    @Deprecated
    public boolean addFileToPointDynamo(String pointId, String path) {
        Item item = dynamoDbClient.getPoint(pointId);
        Point point = this.convertItemToPoint(item);
        if (point == null || path == null) {
            logger.info("PointId not found {}, trying again....", pointId);
            return false;
        }
        if (path.endsWith("jpg") || path.endsWith("jpeg") || path.endsWith("png")) {
            point.addPhoto(path);
            return dynamoDbClient.addPhotoToPoint(point);
        }

        point.setAudio(path);
        return dynamoDbClient.addAudioToPoint(point);
    }

    public boolean addFileToPointDb(String pointId, String path) {
        Optional<Point> opPoint = pointRepository.findPointByPointId(pointId);
        if (opPoint.isPresent()) {
            Point point = opPoint.get();
            if (path.endsWith("jpg") || path.endsWith("jpeg") || path.endsWith("png") || path.endsWith("gif")) {
                point.addPhoto(path);
            } else {
                point.setAudio(path);
            }
            pointRepository.save(point);
            return true;
        }
        return false;
    }

    public boolean addFileLinkToPoint(String message) {
        JSONObject jsonObject = new JSONObject(message);

        String pointId = (String) jsonObject.get("point_id");
        String path = (String) jsonObject.get("link");

        return addFileToPointDb(pointId, path);

    }

    @Deprecated
    public boolean addFileLinkToPointDynamo(String pointId, String path) {
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


    public Page<Point> getPointsByUserId(Pageable pageable, String userId) {
        Page<Point> page = pointRepository.findAllByUserNotBlocked(pageable, userId);
        page.stream().filter(p -> p.getCountry() == null)
                .forEach(p -> this.savePointDb(p));
        return page;
    }
    public List<Point> getPointsByUserId(String userId) {
        List<Point> page = pointRepository.findAllByUser(userId);
        page.stream().filter(p -> p.getCountry() == null)
                .forEach(p -> this.savePointDb(p));
        return page;
    }

    public Page<Point> getFatherPointsByUserId(Pageable pageable, String userId) {
        Page<Point> page = pointRepository.findFathersByUserNotBlocked(pageable, userId);
        page.stream().filter(p -> p.getCountry() == null)
                .forEach(p -> this.savePointDb(p));
        return page;
    }

    public Page<PointDto> convertPointToDto(List<Point> points) {
        List<PointDto> pointsDto = new ArrayList();
        points.forEach(p -> pointsDto.add(new PointDto(p)));
        return new PageImpl<>(pointsDto);
    }

    public Point getPointById(String pointId) {
        return pointRepository.findPointByPointId(pointId).orElse(null);
    }

    public Point updatePointObject(Point newPoint, Point oldPoint) {

        if (newPoint == null || oldPoint == null || !newPoint.getPointId().equals(oldPoint.getPointId()))
            return null;
        return copyPoint(newPoint, oldPoint);
    }

    @Transactional
    public Point copyPoint(Point pointFrom, Point pointTo) {
        Field[] fields = Point.class.getDeclaredFields();
        String fieldsXvalue = "";
        try {
            for (Field field : fields) {
                if (isUpdatedField(field)) {
                    field.setAccessible(true);
                    Object newValue = field.get(pointFrom);
                    if (newValue != null && !newValue.toString().isBlank()) {
                        fieldsXvalue += field.getName() + "=" + newValue + " \n";
                        field.set(pointTo, newValue);
                    }
                }
            }
        } catch (IllegalAccessException e) {
            logger.info("Erro em point parent: {}: ", pointFrom.getPointId());
            logger.info(e.getMessage());
            logger.info(fieldsXvalue);
            throw new RuntimeException(e);
        } catch (Exception e) {
            logger.info("Erro em point parent: {}: ", pointFrom.getPointId());
            logger.info(e.getMessage());
            logger.info(fieldsXvalue);
            throw new RuntimeException(e);
        }

        return pointTo;
    }

    private boolean isUpdatedField(Field field) {
        if (field.getName().equals("id")
                || field.getName().equals("pointId") || field.getName().equals("childrenPoints"))
            return false;
        return true;
    }

    public List<Point> createPointsFromParent(String message) throws Exception {
        String pointId;
        JSONObject jsonObject;

        jsonObject = new JSONObject(message);
        pointId = jsonObject.optString("pointId");
        Point parentPoint = this.getPointById(pointId);
        if (parentPoint != null)
            return createPointsFromParent(parentPoint);
        return null;

    }

    public List<Point> createPointsFromParent(Point parentPoint) throws Exception {
        List<Point> createdPoints = new ArrayList<>();
        createdPoints.add(createAudioToParent(parentPoint));

        Point localPoint;
        String audioEndpoint;
        for (LanguageEnum language : LanguageEnum.values()) {
            localPoint = translate(parentPoint, language.getValue());
            if (localPoint != null && !localPoint.getDescription().equals(parentPoint.getTitle())) {
                if (audioService.needCreateAudio(localPoint)) {
                    audioEndpoint = audioService.saveAudio(localPoint.getPointId(), localPoint.getDescription(), localPoint.getLanguage());
                    localPoint.setAudio(audioEndpoint);
                }
                createdPoints.add(this.savePointDb(localPoint));
            }
        }
        return createdPoints;
    }

    public Point createAudioToParent(String pointId) {
        Point parentPoint = this.getPointById(pointId);
        return createAudioToParent(parentPoint);
    }

    public Point createAudioToParent(Point parentPoint) {

        if (audioService.needCreateAudio(parentPoint)) {
            parentPoint.setAudio(audioService.saveAudio(parentPoint.getPointId(), parentPoint.getDescription(), parentPoint.getLanguage()));
        }
        if (parentPoint != null)
            this.savePointDb(parentPoint);
        return parentPoint;
    }


    public Point translate(Point parentPoint, String languageDestino) throws IOException {

        if (parentPoint == null)
            throw new ObjectNotFoundException(Point.class,
                    "Parent point not found, try again latter ");

        parentPoint.setChildrenPoints(getChildren(parentPoint.getPointId()));
        Point childPoint = new Point(UlidCreator.getUlid().toString());
        copyPoint(parentPoint, childPoint);
        childPoint.setChildrenPoints(null);

        if (!translateService.canChildForThatLanguage(parentPoint, languageDestino))
            return null;

        childPoint = translateService.translate(parentPoint, childPoint, languageDestino);

        this.savePointDb(childPoint);
        return childPoint;
    }

    public List<Point> getChildren(String pointId){
        return pointRepository.findPointByParentId(pointId);
    }

    public void adiantarChron() {
        //chron.receberMensagens();
    }

}
