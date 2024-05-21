package com.becb.processnewpoint.service;

import com.becb.processnewpoint.domain.Point;
import com.becb.processnewpoint.service.dynamodb.DynamoDbClient;
import com.becb.processnewpoint.service.sqs.SqsChronClient;
import com.becb.processnewpoint.service.sqs.SqsConfiguration;
import com.becb.processnewpoint.service.sqs.SqsService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestPropertySource(
        locations = {"classpath:application-test.properties"},
        properties = {"key=value"})
class MapServiceTest {

    @InjectMocks
    MapService mapService;


    @Mock
    SuportService suporteService;

    @Test
    void getPlace() throws IOException {

        HttpURLConnection mockHttpURLConnection = mock(HttpURLConnection.class);
        when(suporteService.getConnection(any())).thenReturn(mockHttpURLConnection);
        when(mockHttpURLConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);
        when(mockHttpURLConnection.getInputStream()).thenReturn(new ByteArrayInputStream(getResponse().getBytes()));

        StringBuffer response = new StringBuffer();

        Point point = new Point();
        point.setLatitude("-9.999");
        point.setLongitude("-11.1111");
        point.setTitle("Teste");
        assertNull(point.getCountry());
        mapService.setPlace(point);
        assertTrue(point.getCountry().equals("Portugal"));
        assertTrue(point.getState().equals("Lisbon"));
        assertTrue(point.getCity().equals("Cascais"));

    }

    private String getResponse() {
        String filePath = "src/test/resources/response_map.txt";
        Path path = Paths.get(filePath);
        System.out.println(path.toAbsolutePath());
        try {
            return new String(Files.readAllBytes(path));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}