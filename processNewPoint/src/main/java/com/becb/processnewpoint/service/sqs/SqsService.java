package com.becb.processnewpoint.service.sqs;

import com.becb.processnewpoint.service.AprovedEnum;
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

    @SqsListener(value = "${sqs.queue.new_point}", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void listenNewPointQueue(@Headers Map<String, Object> headers, String message) {
        log.info("Received a subject: {} and message on first queue: {}", headers.getOrDefault("NOTIFICATION_SUBJECT_HEADER", "Subject N/A"), message);

        pointService.savePoint(message);
    }

    @SqsListener(value = "${sqs.queue.new_file_to_map}", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void gerarArquivoParaMapa(@Headers Map<String, Object> headers, String message) {
        log.info("Received a subject: {} and message on first queue: {}", headers.getOrDefault("NOTIFICATION_SUBJECT_HEADER", "Subject N/A"), message);
        pointService.gerarArquivoParaMapa(message);
    }

    @SqsListener(value = "${sqs.queue.not_approved}", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void gerarArquivoParaAprovacao(@Headers Map<String, Object> headers, String message) {
        log.info("Received a subject: {} and message on first queue: {}", headers.getOrDefault("NOTIFICATION_SUBJECT_HEADER", "Subject N/A"), message);
        pointService.gerarArquivoParaAprovacao(message);
    }

    @SqsListener(value = "${sqs.queue.aprovar_point}", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void listenAproveQueue(@Headers Map<String, Object> headers, String message) {
        log.info("Received a subject: {} and message on first queue: {}", headers.getOrDefault("NOTIFICATION_SUBJECT_HEADER", "Subject N/A"), message);
        pointService.aprovePoint(message, AprovedEnum.asTrue.getValue());
    }

    @SqsListener(value = "${sqs.queue.bloquear_point}", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void listenBlockQueue(@Headers Map<String, Object> headers, String message) {
        log.info("Received a subject: {} and message on first queue: {}", headers.getOrDefault("NOTIFICATION_SUBJECT_HEADER", "Subject N/A"), message);
        pointService.aprovePoint(message, AprovedEnum.asForget.getValue());
    }


}