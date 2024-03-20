package com.becb.processnewpoint.service.file;

import com.amazonaws.services.s3.model.PutObjectResult;
import com.becb.processnewpoint.domain.LanguageEnum;
import com.becb.processnewpoint.domain.Point;
import com.becb.processnewpoint.domain.User;
import com.becb.processnewpoint.service.AprovedEnum;
import net.minidev.json.JSONValue;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.validation.constraints.AssertTrue;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class FileServiceTest {

    @Autowired
    FileService fileService;

    @MockBean
    AmazonS3Service amazonS3Service;

    @Test
    void configFilename() {

        String filename = fileService.configFilename("fileMap_.json", "eng");
        assertEquals("fileMap_eng_.json", filename);

    }

    @Test
    void createFileToMap() throws IOException {
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
        point.setLatitude("0000");        point.setLanguage(LanguageEnum.EN);
        return point;
    }

    @Test
    void getBodyHtml() {
        String str = fileService.getBodyJson(createPointEN());
        JSONObject jsonObject = new JSONObject(str);

        JSONObject object = new JSONObject(jsonObject.get("properties").toString());

        assertEquals("null", object.get("user_guide"));

        assertEquals( createPointEN().getPointId(), object.get("pointId"));

    }
}