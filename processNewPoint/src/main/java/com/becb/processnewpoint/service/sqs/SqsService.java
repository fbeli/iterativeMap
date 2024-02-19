package com.becb.processnewpoint.service.sqs;

import com.becb.processnewpoint.service.AprovedEnum;
import com.becb.processnewpoint.service.PointService;
import com.becb.processnewpoint.service.SuportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Map;

@Slf4j
@Component
public class SqsService {

    @Autowired
    private PointService pointService;

    @SqsListener(value = "${sqs.queue.new_point}", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void listenNewPointQueue(@Headers Map<String, Object> headers, String message) {
        log.info("Received message on  queue new_point : {}", message);
        if(SuportService.isValid(message)){
            pointService.savePoint(message);
        }else{
            log.error("Invalid message received on queue new_point : {}: ", message);
        }
    }

    @SqsListener(value = "${sqs.queue.new_file_to_map}", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void gerarArquivoParaMapa(@Headers Map<String, Object> headers, String message) {
        log.info("Received message to create mapfile!");
        pointService.gerarArquivoParaMapa(message);
    }

    @SqsListener(value = "${sqs.queue.not_approved}", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void gerarArquivoParaAprovacao(@Headers Map<String, Object> headers, String message) {
        log.info("Received message on  queue not_approved: {}", message);
        pointService.gerarArquivoParaAprovacao(message);
    }

    @SqsListener(value = "${sqs.queue.aprovar_point}", deletionPolicy = SqsMessageDeletionPolicy.ALWAYS)
    public void listenAproveQueue(@Headers Map<String, Object> headers, String message) {
        log.info("Received message on first queue 'aprovar_point': {}",  message);
        try {
            pointService.aprovePoint(message, AprovedEnum.asTrue.getValue());
        } catch (Exception e) {
            log.error("Error occurred while aproving point: {} . \nErro: {}", message, e.getMessage());
        }
    }

    @SqsListener(value = "${sqs.queue.bloquear_point}", deletionPolicy = SqsMessageDeletionPolicy.ALWAYS)
    public void listenBlockQueue(@Headers Map<String, Object> headers, String message) {
        log.info("Received message on  queue bloquear_point: {}", message);
        try {
            pointService.aprovePoint(message, AprovedEnum.asForget.getValue());
        } catch (Exception e) {
            log.error("Error occurred while aproving point: {} . \nErro: {}", message, e.getMessage());
        }
    }

}