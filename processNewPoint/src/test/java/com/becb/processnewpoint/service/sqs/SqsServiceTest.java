package com.becb.processnewpoint.service.sqs;

import com.becb.processnewpoint.domain.Point;
import com.becb.processnewpoint.exception.SQSMessageException;
import com.becb.processnewpoint.service.PointService;
import com.becb.processnewpoint.service.dynamodb.DynamoDbClient;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jms.config.JmsListenerContainerFactory;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

//@SpringBootTest
//@ComponentScan
//@ExtendWith(MockitoExtension.class)
//@TestPropertySource(locations="classpath:test.properties")
class SqsServiceTest {

    @InjectMocks
    SqsService sqsService;

    @MockBean
    SqsChronClient sqsChronClient;

    @MockBean
    DynamoDbClient dynamodbClient;

    @MockBean
    JmsListenerContainerFactory jmsListenerContainerFactory;

    @MockBean
    SqsConfiguration sqsConfiguration;

    @MockBean
    PointService pointService;


    //@Test
    void addPhotoToPoint() throws SQSMessageException {
        sqsService.addPhotoToPoint(null, null);
    }

    @Test
    void addAudioToPoint() {
    }
//TODO:criar o método em pointService e testar por lá.
    //@Test
    void updadePoint() {
        String message = "{\"aproved\":null,\"audio\":null,\"city\":null,\"country\":null,\"description\":\"Vista do Castelo da Pena para o Castelo dos Mouros\",\"guide\":null,\"instagram\":null,\"language\":\"PT\",\"latitude\":null,\"lngLat\":null,\"longitude\":null,\"photo\":null,\"pointId\":\"01HVEEJ5AZ1AFSC7363ZYTSPG8\",\"share\":null,\"state\":null,\"title\":\"Vista para o Castelo dos Mouros\",\"type\":null,\"user_email\":null,\"user_id\":null,\"user_name\":null}";

        Point point = getPoint();
        when(pointService.messageToPoint(any())).thenCallRealMethod();
        when(pointService.getPointById(any())).thenReturn(point);
        when(pointService.updatePointObject(any(), any())).thenCallRealMethod();
        when(pointService.savePointDb(any())).thenReturn(point);


        sqsService.updadePoint(null, message);
        assertTrue(point.getDescription().contains("Vista do Castelo da Pena para o Castelo dos Mouros"));

    }
    private Point getPoint() {
        Point point = new Point();
        point.setPointId("01HVEEJ5AZ1AFSC7363ZYTSPG8");

        return point;
    }
}