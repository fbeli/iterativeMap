package com.becb.processnewpoint.controller;




import com.becb.processnewpoint.domain.Point;
import com.becb.processnewpoint.domain.User;
import com.becb.processnewpoint.service.PointService;
import com.becb.processnewpoint.service.translate.TranslateService;
import org.json.JSONArray;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

@WebMvcTest(EditPointController.class)
@TestPropertySource(
        locations = {"classpath:application-test.properties"},
        properties = {"key=value"})
public class TestEndpoint {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    PointService pointService;

    @MockBean
    TranslateService translateService;

    @Test
    public void testeEndpointExample() throws Exception {

        when(pointService.getPointsByUserId(any(), any())).thenReturn(createPageResult());
        when(pointService.getFatherPointsByUserId(any(), any())).thenReturn(createPageResultWithChild());
        when(pointService.convertPointToDto(any())).thenCallRealMethod();

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                        .get("/point/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("userId", "testUser"))
                .andExpect(status().is2xxSuccessful())
                .andReturn();


        System.out.println(mvcResult.getResponse().getContentAsString());
        JSONArray jsonArray = new JSONArray(mvcResult.getResponse().getContentAsString());
        Assert.assertEquals("point1", jsonArray.getJSONObject(0).optString("pointId"));

    }

    private Page createPageResult(){
        String userId = "testUser";

        Point point = new Point();
        Point point1 = new Point();
        User user = new User();
        user.setUserId(userId);
        point.setUser(user);
        point1.setUser(user);
        Page<Point> pageResult = new PageImpl<>(Arrays.asList(point1, point));

        return pageResult;
    }

    private Page createPageResultWithChild(){
        String userId = "testUser";


        Point point0 = new Point();
        point0.setPointId("point0");
        Point point1 = new Point();
        point1.setPointId("point1");
        Point point2 = new Point();
        User user = new User();
        point2.setPointId("point2");
        user.setUserId(userId);
        point0.setUser(user);
        point1.setUser(user);
        point2.setUser(user);
        point2.setPointParent(point1);
        Page<Point> pageResult = new PageImpl<>(Arrays.asList(point1, point0,point2));

        return pageResult;
    }
}


