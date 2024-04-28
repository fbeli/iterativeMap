package com.becb.api.controller;

import com.becb.api.service.ArquivoService;
import com.becb.api.service.sqs.SqsService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@RunWith(SpringRunner.class)
@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    SqsService sqsService;

    @MockBean
    ArquivoService arquivoService;
    @MockBean
    private JwtDecoder jwtDecoder;




    @Test
    //@WithMockUser(username = "pascal", roles = "USER")
 //   @WithMockUser
   // @WithM
    void aprovar() throws Exception {

        doNothing().when(sqsService).sendMessage(any());


        mockMvc.perform(get("/aprovar/{id}/{email}", "HWD0XZSG5TDC2E69Z9tR59YEV","come.vive.viaja@gmail.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("200"));



    }
}