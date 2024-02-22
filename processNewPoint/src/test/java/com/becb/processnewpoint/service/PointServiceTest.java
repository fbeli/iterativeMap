package com.becb.processnewpoint.service;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.becb.processnewpoint.service.dynamodb.DynamoDbClient;
import com.becb.processnewpoint.service.sqs.SqsConfiguration;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@SpringBootTest
@ComponentScan
@ExtendWith(MockitoExtension.class)
@TestPropertySource(locations="classpath:test.properties")

class PointServiceTest {

    @MockBean
    private SqsConfiguration conf;

    @MockBean
    DynamoDbClient dynamodbClient;

    @Autowired
    PointService pointService;
    //@Test
    void gerarArquivoParaMapa() {

        pointService.gerarArquivoParaMapa("{" +
                "  \"user_id\": \"01HNG094DXF7A4HQPD8QKWHCBW\"," +
                "  \"user_name\": \"Fred\"," +
                "  \"user_email\": \"fred.belisario@gmail.com\","+
                "  \"file_name\": \"arquivo_"+ LocalDateTime.now() +".html\"} ");
    }

   // @Test
    void gerarArquivoParaAprovacao() {
        pointService.gerarArquivoParaAprovacao("{" +
                "  \"user_id\": \"01HNG094DXF7A4HQPD8QKWHCBW\"," +
                "  \"user_name\": \"Fred\"," +
                "  \"user_email\": \"fred.belisario@gmail.com\","+
                "  \"file_name\": \"arquivo_"+ LocalDateTime.now() +".html\"} ");
    }

    @Test
    void savePoint() {
        when(dynamodbClient.savePoint(any())).thenReturn(new Item());
        Assert.assertNotNull(pointService.savePoint(getMessageFromStack()));
    }

    @Test
    void savePointAudioEmpty() {
        when(dynamodbClient.savePoint(any())).thenReturn(new Item());
        Assert.assertNotNull(pointService.savePoint(getMessageFromStackAudioEmpty()));

    }

    @Test
    void savePointWithAudio() {
        when(dynamodbClient.savePoint(any())).thenReturn(new Item());
        Assert.assertNotNull(pointService.savePoint(getMessageFromStackWithAudio()));
    }

    @Test
    void savePointWrongJson() {
        when(dynamodbClient.savePoint(any())).thenReturn(new Item());
        assertThrows(Exception.class,
                ()->{
                    pointService.savePoint("[B@20976331");
                });
    }
    @Test
    void testSavePoint() {
    }

    @Test
    void aprovePoint() {
    }

    @Test
    void testGerarArquivoParaMapa() {
    }

    @Test
    void testGerarArquivoParaAprovacao() {
    }

    @Test
    void convertItemsToPoints() {
    }

    @Test
    void convertItemToPoint() {
    }

    private String getMessageFromStack(){

    return "{"+
        "\"description\": \"Marquês de Pombal era muito bom\","+
        "\"latitude\": \"Latitude: 38.72524959265044\","+
        "\"lngLat\": null,"+
        "\"longitude\": \"Longitude: -9.15007687024712\","+
        "\"pointId\": null,"+
        "\"s3Voice\": null,"+
        "\"title\": \"Marqu\u00E9s de Pombal\","+
        "\"user_email\": \"frederico@gmail.com\","+
        "\"user_id\": \"335254e4-73e6-4c9d-a271-904cb3bf320a\","+
        "\"user_name\": \"Frederico\""+
        "}" ;
    }

    private String getMessageFromStackWithAudio(){

        return "{"+
                "\"description\": \"Marquês de Pombal era muito bom\","+
                "\"latitude\": \"Latitude: 38.72524959265044\","+
                "\"lngLat\": null,"+
                "\"longitude\": \"Longitude: -9.15007687024712\","+
                "\"pointId\": XXXXXX,"+
                "\"s3Voice\": null,"+
                "\"title\": \"Marqu\u00E9s de Pombal\","+
                "\"user_email\": \"frederico@gmail.com\","+
                "\"user_id\": \"335254e4-73e6-4c9d-a271-904cb3bf320a\","+
                "\"user_name\": \"Frederico\","+
                "\"audio\": \"AUDIOHER\""+
                "}" ;
    }
    private String getMessageFromStackAudioEmpty(){

        return "{"+
                "\"description\": \"Marquês de Pombal era muito bom\","+
                "\"latitude\": \"Latitude: 38.72524959265044\","+
                "\"lngLat\": null,"+
                "\"longitude\": \"Longitude: -9.15007687024712\","+
                "\"pointId\": null,"+
                "\"s3Voice\": null,"+
                "\"title\": \"Marqus de Pombal\","+
                "\"user_email\": \"frederico@gmail.com\","+
                "\"user_id\": \"335254e4-73e6-4c9d-a271-904cb3bf320a\","+
                "\"user_name\": \"Frederico\","+
                "\"audio\": \"\""+
                "}" ;
    }
}