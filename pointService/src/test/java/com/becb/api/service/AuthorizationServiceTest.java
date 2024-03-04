package com.becb.api.service;

import com.becb.api.controller.PointController;
import com.becb.api.dto.LoginDto;
import com.becb.api.dto.LoginResponse;
import com.becb.api.service.file.AmazonS3Config;
import com.becb.api.service.file.AmazonS3Service;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class AuthorizationServiceTest {

    @Autowired
    private AuthorizationService authorizationService;


    @MockBean
    PointController pc;
    @MockBean
    private AmazonS3Config amazonS3Config;

    @MockBean
    private AmazonS3Service amazonS3Service;


    @Test
    void loginThrowsException() {
        authorizationService.setAuth_url("xxxx");

        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("test@gmail.com");

        LoginResponse response =  authorizationService.login(loginDto);
        Assert.assertEquals(500, response.getStatus());


    }
}