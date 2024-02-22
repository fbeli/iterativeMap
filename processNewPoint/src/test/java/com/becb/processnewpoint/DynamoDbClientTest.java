package com.becb.processnewpoint;

import com.becb.processnewpoint.domain.LanguageEnum;
import com.becb.processnewpoint.domain.Point;
import com.becb.processnewpoint.domain.User;
import com.becb.processnewpoint.service.AprovedEnum;
import com.becb.processnewpoint.service.dynamodb.DynamoDbClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

import java.util.Locale;

@SpringBootTest
@ComponentScan({"com.becb.processnewpoint", "com.becb.processnewpoint.service", "com.becb.processnewpoint.domain"})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))

public class DynamoDbClientTest{

    @Autowired
    DynamoDbClient client;

    public void contextLoads() {
    }


    //@Test
    public void getNotApproved() {

        client.getPointsByAproved(AprovedEnum.asFalse.toString());
    }


    public void setNotApproved(){
        String id = "01HNG094DXF7A4HQPD8QKWHCBW";
        Point point = new Point();
        point.setPointId(id);

        client.updatePointToAproved(point, AprovedEnum.asFalse.toString());
        System.out.println(client.getPoint(id).toString());
    }

    //@Test
    public void setPointAproved(){
        System.out.println(client.getPoint("01HNG094DXF7A4HQPD8QKWHCBW").toString());
        Point point = new Point();

        point.setPointId("01HNG094DXF7A4HQPD8QKWHCBW");
        point.setUser(new User());
        point.getUser().setUserEmail("fred.belisario@gmail.com");
        client.updatePointToAproved(point, AprovedEnum.asTrue.toString());
        System.out.println(client.getPoint("01HNG094DXF7A4HQPD8QKWHCBW").toString());

    }

    //@Test
    public void savePointTest(){
        Point point = new Point();
        point.setPointId("01HNG094DXF7A4HQPD8QKWHCBW");
        point.setUser(new User());
        point.getUser().setUserEmail("fred.belisario@gmail.com");
        point.setTitle("Title");
                point.setDescription("Description");
                 point.setShortDescription("ShortDescription");
                 point.setLatitude("10.0");
                point.setLongitude("-11.0");
               point.getUser().setUserId("200");
                point.getUser().setUserName("fulano");
                point.setAproved( AprovedEnum.asFalse.getValue());
                point.setLanguage(LanguageEnum.PT);

        client.savePoint(point);
    }
}


