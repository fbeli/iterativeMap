package com.becb.processnewpoint.service;

import com.becb.processnewpoint.domain.Point;
import com.becb.processnewpoint.service.dynamodb.DynamoDbClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Service
public class MigrationService {

    @Autowired
    DynamoDbClient dynamoDbClient;

    @Autowired
    PointService pointService;

    @Autowired
    UserService userService;

    public void migrate() throws IOException {
        ArrayList<Point> points = pointService.getApprovedPoints();
        for (Point point : points) {
          migrateAll(point);
        }
    }
    Set<String> users = new HashSet<>();
    public void migrateAll(Point point) throws IOException {

        System.out.println("User: "+point.getUser());
        if(!users.contains(point.getUser().getUserId())){
            userService.saveUser(point.getUser());
            users.add(point.getUser().getUserId());
        }else{
            //User user = userService.userRepository.findByUserId(point.getUser().getUserId());
            //user.addPoint(point);
            point.setUser(userService.userRepository.findByUserId(point.getUser().getUserId()));
        }
        point.setCreateTime(SuportService.ulidToLocalDateTime(point.getPointId()));
        point.setPhoto(point.getPhoto());
        System.out.println("Point: "+ point);

        pointService.savePointDb(point);
    }
}
