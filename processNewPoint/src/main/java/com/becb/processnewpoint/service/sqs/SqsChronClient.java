package com.becb.processnewpoint.service.sqs;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.becb.processnewpoint.service.PointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SqsChronClient {

    @Autowired
    public SqsChronClient(PointService pointService, AmazonSQS sqs) {
        this.pointService = pointService;
        this.sqs = sqs;
    }

    PointService pointService;

    @Autowired
    @Qualifier("amazonSQS")
    AmazonSQS sqs;



    @Value("${sqs.queue.url}")
    String queueUrl;

    @Value("${sqs.queue.dlq}")
    String queue;



  //  @Scheduled(cron = "10 * * * * *") // Cron expression for running every 2 minutes
    public void receberMensagensChron() {
        receberMensagens();
    }

    public void receberMensagens() {

        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl + queue);
        receiveMessageRequest.setMaxNumberOfMessages(5);
        List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();

        if (!messages.isEmpty()) {
            messages.forEach(message -> {
                pointService.addFileToPoint(message.getBody());
                sqs.deleteMessage(queueUrl + queue, message.getReceiptHandle());
            });
        }
    }


}
