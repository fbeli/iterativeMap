package com.becb.processnewpoint.controller;

import com.becb.processnewpoint.RoteiroTests;
import com.becb.processnewpoint.domain.User;
import com.becb.processnewpoint.repository.RoteiroRepository;
import com.becb.processnewpoint.repository.UserRepository;
import com.becb.processnewpoint.service.RoteiroService;
import com.becb.processnewpoint.service.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RoteiroController.class)
@TestPropertySource(
        locations = {"classpath:application-test.properties"},
        properties = {"key=value"})

@EntityScan(basePackages = {"com.becb.processnewpoint.domain"})
class RoteiroControllerTest extends RoteiroTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    RoteiroRepository roteiroRepository;
    @MockBean
    RoteiroService roteiroService;
    @MockBean
    UserService userService;
    @MockBean
    UserRepository userRepository;




    @Test
    void getRoteirosByUser() throws Exception {


        when(userRepository.save(any(User.class)))
                .thenAnswer(i -> i.getArguments()[0]);
        when(roteiroService.createRoteiro(any(),any(), any(), any(), any(), any(), any())).thenCallRealMethod();
        when(roteiroRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
        when(roteiroService.saveRoteiro(any())).thenReturn(null);


        roteiroService.setRoteiroRepository(roteiroRepository);
        setServices(userService, roteiroService);
        saveRoteiro("Lisbon", "userId_1");
        saveRoteiro("Lisbon", "userId_2");
        saveRoteiro("Porto", "userId_1");

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                        .get("/rotas/instagram")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("instagram", "@userId_1"))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        mvcResult.getResponse();
    }
    @Test
    void getRoteirosByRotes() throws Exception {


        when(userRepository.save(any(User.class)))
                .thenAnswer(i -> i.getArguments()[0]);
        when(roteiroService.createRoteiro(any(),any(), any(), any(), any(), any(), any())).thenCallRealMethod();
        when(roteiroRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
        when(roteiroService.saveRoteiro(any())).thenReturn(null);

        roteiroService.setRoteiroRepository(roteiroRepository);
        setServices(userService, roteiroService);
        saveRoteiro("Lisbon", "userId_1");
        saveRoteiro("Lisbon", "userId_2");
        saveRoteiro("Porto", "userId_1");

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                                                      .get("/routes")
                                                      .contentType(MediaType.APPLICATION_JSON)
                                                      .accept(MediaType.APPLICATION_JSON)
                                                      .param("instagram", "@userId_1"))
                                     .andExpect(status().is2xxSuccessful())
                                     .andReturn();

        mvcResult.getResponse();
    }

}