package com.becb.processnewpoint.controller;

import com.becb.processnewpoint.domain.Point;
import com.becb.processnewpoint.service.PointService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.TestPropertySource;


import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestPropertySource(
        locations = {"classpath:application-test.properties"},
        properties = { "key=value" })
class EditPointControllerTest {

    @InjectMocks
    private EditPointController editPointController;

    @Mock
    private PointService pointService;

    @Test
    void testGetByUser() {
        String userId = "testUser";
        int page = 0;
        int size = 10;
        Page<Point> pageResult = new PageImpl<>(Arrays.asList(new Point(), new Point()));

        when(pointService.getPointsByUserId(any(Pageable.class), eq(userId))).thenReturn(pageResult);

        List<Point> result = editPointController.getByUser(page, size, userId);

        assertEquals(2, result.size());
        verify(pointService, times(1)).getPointsByUserId(any(Pageable.class), eq(userId));
    }

    @Test
    void testGetByPointId() {
        String pointId = "testPoint";
        Point point = new Point();
        point.setPointId(pointId);

        when(pointService.getPointById(any())).thenReturn(point);

        Point result = editPointController.getByPointId(pointId);

        assertEquals(pointId, result.getPointId());
        verify(pointService, times(1)).getPointById(pointId);
    }


    @Test
    void testAddChron() {
        ResponseEntity<String> response = editPointController.addChron();

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
