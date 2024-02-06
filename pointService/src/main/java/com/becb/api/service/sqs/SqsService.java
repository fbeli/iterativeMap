package com.becb.api.service.sqs;


import org.springframework.stereotype.Service;

import java.util.logging.Logger;


@Service
public class SqsService {

    private static final Logger logger = Logger.getLogger(SqsService.class.getName());
    private String queueName = "new-point-queue";
    public void sendMessage(String message, String queueName) {
        logger.info("Adding message: "+message+" , to queue:" + queueName);
        SQSClient sqsClient = new SQSClient(queueName);
        sqsClient.sendMessage(message);
    }

    public void sendMessage(String message) {
        this.sendMessage(message, this.queueName);
    }

}
