package com.becb.api.service.sqs;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;


@Service
public class SqsService {

    private static final Logger logger = Logger.getLogger(SqsService.class.getName());

    @Value("${sqs.queue.new_point}")
    private String queueName;

    @Value("${sqs.queue.url}")
    private String sqsServiceEndpoint;

    public SqsService() {
    }

    public void sendMessage(String message, String queueName) {

        SQSClient sqsClient;

        String env = System.getenv("ENVIRONMENT").trim();
        if ( env.equals("docker") || env.equals("dev") ) {
            sqsClient = new SQSClient(queueName, sqsServiceEndpoint);
        } else {
            sqsClient = new SQSClient(queueName);
        }
        sqsClient.sendMessage(message);
    }

    public void sendMessage(String message) {
        this.sendMessage(message, this.queueName);
    }

}
