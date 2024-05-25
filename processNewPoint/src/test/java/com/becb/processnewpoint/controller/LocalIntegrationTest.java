package com.becb.processnewpoint.controller;


import com.becb.processnewpoint.domain.LanguageEnum;
import com.becb.processnewpoint.domain.Point;
import com.becb.processnewpoint.domain.User;
import com.becb.processnewpoint.service.PointService;
import com.becb.processnewpoint.service.UserService;
import com.becb.processnewpoint.service.sqs.SqsService;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@TestPropertySource(
        locations = {"classpath:application-test.properties"},
        properties = {"key=value"})
public class LocalIntegrationTest {

    private String userId = "335254e4-73e6-4c9d-a271-904cb3bf320a";

    @Autowired
    @InjectMocks
    PointService pointService;

    @Autowired
    UserService userService;

    @MockBean
    SqsService sqsService;

    @Test
    @Transactional
    void testSimpleSavePoint(){

        userService.saveUser(getUser());
        pointService.savePointDb(createNewPoint());
        Pageable pageable = PageRequest.of(0, 10);
        List<Point> points = pointService.getPointsByUserId(pageable, userId).toList();
        Assert.assertEquals(1, points.size());
        Point point = points.get(0);
        Assert.assertEquals(0,point.getChildrenPoints().size());

    }

    private String retornoFromTranlationService(){
        return "{ \"status\": 200, \"from\": \"en\",\"to\": \"pt\",\"original_text\": \"Hello, world!!\",\"translated_text\": {\"pt\": \"Ola\"},\"translated_characters\": 14}";
    }

    @Test
    @Transactional
    void testSimpleSavePointAndChild() throws IOException {

        userService.saveUser(getUser());
        pointService.savePointDb(createNewPoint());
        pointService.translate(createNewPoint().getPointId(),"PT");

        Pageable pageable = PageRequest.of(0, 10);
        List<Point> points = pointService.getPointsByUserId(pageable, userId).toList();
        Assert.assertEquals(2, points.size());


    }

    private Point createNewPoint() {
        Point point = new Point();
        point.setUser(getUser());
        point.setPointId("AAAAAAA");
        point.setTitle("Title");
        point.setDescription("This is a test point.");
        point.setLatitude("37.7749");
        point.setLongitude("-122.4194");
        point.setCity("San Francisco");
        point.setState("CA");
        point.setCountry("USA");
        point.setCreateTime(LocalDateTime.now());
        point.setLanguage(LanguageEnum.EN);


        return point;
    }

    private String createNewPointMessage() {

        return "{" +
                "\"description\": \"Marquês de Pombal era muito bom\"," +
                "\"latitude\": \"Latitude: 38.72524959265044\"," +
                "\"lngLat\": null," +
                "\"longitude\": \"Longitude: -9.15007687024712\"," +
                "\"pointId\": XXXXXX," +
                "\"s3Voice\": null," +
                "\"title\": \"Marqu\u00E9s de Pombal\"," +
                "\"user_email\": \"frederico@gmail.com\"," +
                "\"user_id\": \""+userId+"\"," +
                "\"user_name\": \"Frederico\"," +
                "\"audio\": \"AUDIOHER\"" +
                "}";
    }

    private User getUser(){
        User user = new User();
        user.setUserId(userId);
        user.setUserName("Frederico");
        user.setUserEmail("frederico@gmail.com");
        return user;
    }

}
