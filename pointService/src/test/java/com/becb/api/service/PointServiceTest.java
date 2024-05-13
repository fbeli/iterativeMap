package com.becb.api.service;

import com.amazonaws.services.s3.AmazonS3;
import com.becb.api.controller.PointController;
import com.becb.api.dto.PointDto;
import com.becb.api.service.file.AmazonS3Config;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
class PointServiceTest {

    @MockBean
    PointController pc;
    @MockBean
    private AmazonS3Config amazonS3Config;

    @MockBean
    private AmazonS3 amazonS3;

    @Autowired
    PointService pointService;
   @Test
   void getPointDtoFromJson() throws JSONException {
        PointDto pointDto = pointService.getPointDtoFromJson(new JSONObject(json));
        assertEquals(pointDto.getPointId(), "01HRD6ZVXMVBX0CHWC4S7Z4FTA");
   }
   private String json = "{ \"id\": 0,\"pointId\": \"01HRD6ZVXMVBX0CHWC4S7Z4FTA\",\"title\": \"King Sebastian Statue\" }";
}