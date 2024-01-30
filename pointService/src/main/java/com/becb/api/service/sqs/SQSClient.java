package com.becb.api.service.sqs;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;

import java.util.List;
import java.util.logging.Logger;

public class SQSClient {
    private final String queueUrl;
    private final String sqsServiceEndpoint = "http://localhost:9324/queue/";

    private static final Logger logger = Logger.getLogger(SQSClient.class.getName());

    private final AmazonSQS sqs = this.defaultClient();
    private final SendMessageRequest sendMessageRequest = new SendMessageRequest();
    private ReceiveMessageRequest receiveMessageRequest;

    public SQSClient(String queueName) {
        String env = System.getenv("ENVIRONMENT");
        if ( env.equals("docker") || env.equals("dev") ) {
            this.queueUrl = sqsServiceEndpoint + queueName;
        } else {
            this.queueUrl = this.sqs.getQueueUrl(queueName).getQueueUrl();
        }
        logger.info("Queue URL: " + this.queueUrl);
    }

    public AmazonSQS defaultClient() {
        return AmazonSQSClientBuilder.defaultClient();
    }

    public List<Message> receiveMessage(int count) {
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(this.queueUrl);
        receiveMessageRequest.setMaxNumberOfMessages(count);
        return this.sqs.receiveMessage(receiveMessageRequest).getMessages();
    }

    public void deleteMessage(String messageHandle) {
        this.sqs.deleteMessage(new DeleteMessageRequest(this.queueUrl, messageHandle));
    }

    public void changeMessageVisibility(String messageHandle) {
        this.sqs.changeMessageVisibility(this.queueUrl, messageHandle, 120);
    }

    public void sendMessage(String messageBody) {
        SendMessageRequest request = (new SendMessageRequest()).withQueueUrl(this.queueUrl).withMessageBody(messageBody);
        this.sqs.sendMessage(request);
    }
}
