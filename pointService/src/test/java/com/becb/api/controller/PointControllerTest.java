package com.becb.api.controller;

import com.becb.api.dto.ArquivoDto;
import com.becb.api.service.ArquivoService;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class PointControllerTest {


    @Autowired
    private ArquivoService arquivoService;

    @Test
    void gerarArquivoAprovacaoWithoutHeader() throws JSONException {

        HttpServletRequest request = new MockHttpServletRequest();

        String str = arquivoService.configArquivo(new ArquivoDto(), request, "filename");
        JSONObject jsonObject = new JSONObject(str);
        assertNotNull(jsonObject.getString("user_id"));
    }
}