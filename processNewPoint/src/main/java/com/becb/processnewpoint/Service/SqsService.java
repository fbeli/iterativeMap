package com.becb.processnewpoint.Service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class SqsService {


    private static final String queueName ="new-point-queue";

    @SqsListener(value = queueName, deletionPolicy = SqsMessageDeletionPolicy.ALWAYS)
    public void listenToFirstQueue(@Headers Map<String, Object> headers, String message) {
        log.info("Received a subject: {} and message on first queue: {}", headers.getOrDefault("NOTIFICATION_SUBJECT_HEADER", "Subject N/A"), message);
    }
}