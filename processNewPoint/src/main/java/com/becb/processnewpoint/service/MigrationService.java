package com.becb.processnewpoint.service;

import com.becb.processnewpoint.domain.Point;
import com.becb.processnewpoint.service.dynamodb.DynamoDbClient;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class MigrationService {

    @Autowired
    DynamoDbClient dynamoDbClient;

    @Autowired
    PointService pointService;

    @Autowired
    UserService userService;

    @Autowired
    MapService mapService;

    private List<Point> pointsDb;
    Set<String> users = new HashSet<>();

    @SneakyThrows
    public void migrate() {
        List<Point> points = pointService.getApprovedPoints();
        pointsDb = pointService.getApprovedPointsDb();

        points.parallelStream().forEach(
                    this::migrateAll
            );

    }

    Logger logger = LoggerFactory.getLogger(getClass());

    public void migrateAll(Point point){

        logger.info("User: "+point.getUser().getInstagram());

        Point pointDb;
        if(pointsDb != null) {
            pointDb = pointsDb.stream().filter(p -> p.getPointId().equals(point.getPointId())).findFirst().orElse(null);
            if (pointDb != null && point.getPhotos()!=null && point.getPhotos().size() > 0) {
                pointDb.setPhoto(point.getPhotos().get(0));
            }
            //if (pointDb != null && pointDb.getCountry() == null) {
                //mapService.setPlace(pointDb);
            //}
            pointService.savePointDb(pointDb);
            logger.info("PointDb: " + pointDb.toString());
        }else {

            if (!users.contains(point.getUser().getUserId())) {
                userService.saveUser(point.getUser());
                users.add(point.getUser().getUserId());
            } else {
                //User user = userService.userRepository.findByUserId(point.getUser().getUserId());
                //user.addPoint(point);
                point.setUser(userService.userRepository.findByUserId(point.getUser().getUserId()));
            }
            point.setCreateTime(SuportService.ulidToLocalDateTime(point.getPointId()));
            point.setPhoto(point.getPhoto());
            logger.info("Point: " + point);

            pointService.savePointDb(point);
        }
    }
}
