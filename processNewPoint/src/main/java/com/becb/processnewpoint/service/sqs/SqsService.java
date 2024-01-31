package com.becb.processnewpoint.service.sqs;

import com.becb.processnewpoint.service.PointService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class SqsService {



    @Autowired
    private PointService pointService;

    @SqsListener(value = "${sqs.queue.newpoint_name}", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void listenToFirstQueue(@Headers Map<String, Object> headers, String message) {
        log.info("Received a subject: {} and message on first queue: {}", headers.getOrDefault("NOTIFICATION_SUBJECT_HEADER", "Subject N/A"), message);

        pointService.savePoint(message);
    }
}