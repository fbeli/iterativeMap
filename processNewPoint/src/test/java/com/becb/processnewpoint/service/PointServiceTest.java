package com.becb.processnewpoint.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Date;


@SpringBootTest
class PointServiceTest {

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
        System.out.println("Total points: " + pointService.getDynamoDbClient().getTotalPoints());
        pointService.gerarArquivoParaAprovacao("{" +
                "  \"user_id\": \"01HNG094DXF7A4HQPD8QKWHCBW\"," +
                "  \"user_name\": \"Fred\"," +
                "  \"user_email\": \"fred.belisario@gmail.com\","+
                "  \"file_name\": \"arquivo_"+ LocalDateTime.now() +".html\"} ");
    }
}