package com.becb.processnewpoint.service.file;

import com.amazonaws.services.s3.model.PutObjectResult;
import com.becb.processnewpoint.domain.LanguageEnum;
import com.becb.processnewpoint.domain.Point;
import com.becb.processnewpoint.domain.User;
import com.becb.processnewpoint.service.sqs.SqsChronClient;
import com.becb.processnewpoint.service.sqs.SqsConfiguration;
import com.becb.processnewpoint.service.sqs.SqsService;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestPropertySource(
        locations = {"classpath:application-test.properties"},
        properties = { "key=value" })
class FileServiceTest {

    @Autowired
    FileService fileService;

    @MockBean
    AmazonS3Service amazonS3Service;

    @MockBean
    SqsConfiguration sqsConfiguration;

    @MockBean
    SqsService sqsService;

    @MockBean
    SqsChronClient sqsChronClient;

    @Test
    void configFilename() {

        String filename = fileService.configFilename("fileMap_.json", "eng");
        assertEquals("fileMap_eng_.json", filename);

    }

    @Test
    void createFileToMap(){
        ArrayList<Point> points = new ArrayList<>();

        PutObjectResult result = new PutObjectResult();
        result.setVersionId("xxxx");
        when(amazonS3Service.saveAdminFile( any(), any())).thenReturn(result);

        points.add(createPointEN());
        points.add(createPointLisboaSecreta());
        points.add(createPointPT());
        List<String> lista = fileService.createFileToMap(points, "fileMap_.json");
        assertEquals(4, lista.size());
        List listaRetorno = lista.stream().filter(f -> {
            return f.contains("- 1");
        }).collect(Collectors.toList());
        assertEquals(3, listaRetorno.size());

    }

    private Point createPointLisboaSecreta(){
        Point point = new Point();
        point.setPointId("01HNG094DXF7A4HQPD8QKWHCBW");
        point.setUser(new User());
        point.getUser().setUserEmail("fred.belisario@gmail.com");
        point.getUser().setInstagram("@lisboasecreta");
        point.setLongitude("0000");
        point.setLatitude("0000");
        return point;
    }
    private Point createPointPT(){
        Point point = new Point();
        point.setPointId("01HNG094DXF7A4dHQPD8QKWHCBW");
        point.setUser(new User());
        point.getUser().setUserEmail("fred.belisario@gmail.com");
        point.setLongitude("0000");
        point.setLatitude("0000");        point.setLanguage(LanguageEnum.PT);
        return point;
    }
    private Point createPointEN(){
        Point point = new Point();
        point.setPointId("01HNG094DXF7A4HQPDd8QKWHCBW");
        point.setUser(new User());
        point.getUser().setUserEmail("fred.belisario@gmail.com");
        point.setLongitude("0000");
        point.setLatitude("0000");
        point.setLanguage(LanguageEnum.EN);

        return point;
    }

    @Test
    void getBodyJson() throws JSONException {
        String str = fileService.getBodyJson(createPointEN());
        JSONObject jsonObject = new JSONObject(str);

        JSONObject object = new JSONObject(jsonObject.get("properties").toString());

        assertEquals("false", object.get("user_guide"));

        assertEquals( createPointEN().getPointId(), object.get("pointId"));
        assertEquals("", object.get("photo"));


    }


    @Test
    void getBodyWithPhotoJson() throws JSONException {
        Point point = createPointEN();
        List<String> listaPhotos = List.of("photo/vista_mouros.jpeg","photo/cruz.jpeg");
        point.setPhotos(listaPhotos);
        String photo = "photo/this_one.jpeg";
        point.setPhoto(photo);
        String str = fileService.getBodyJson(point);
        JSONObject jsonObject = new JSONObject(str);
        JSONObject object = new JSONObject(jsonObject.get("properties").toString());

        assertEquals("false", object.get("user_guide"));

        assertEquals( createPointEN().getPointId(), object.get("pointId"));
        assertTrue(object.getString("photo").contains(photo) );

    }
}