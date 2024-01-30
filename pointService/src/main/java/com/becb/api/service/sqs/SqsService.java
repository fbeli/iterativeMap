package com.becb.api.service.sqs;


import java.util.logging.Logger;


public class SqsService {

    private static final Logger logger = Logger.getLogger(SqsService.class.getName());
    private String queueName = "new-point-queue";
    public void sendMessage(String message) {
        logger.info("Adding message: "+message+" , to queue:" + queueName);
        SQSClient sqsClient = new SQSClient(queueName);
        sqsClient.sendMessage(message);
    }

}
