package com.becb.processnewpoint.service.sqs;

import com.becb.processnewpoint.core.BecbProperties;
import com.becb.processnewpoint.domain.AprovedEnum;
import com.becb.processnewpoint.domain.Point;
import com.becb.processnewpoint.service.PointService;
import com.becb.processnewpoint.service.SuportService;
import com.becb.processnewpoint.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Component
public class SqsService {



    private PointService pointService;

    private UserService userService;

    private BecbProperties becbProperties;

    @Autowired
    public SqsService(PointService pointService, UserService userService, BecbProperties becbProperties) {
        this.pointService = pointService;
        this.userService = userService;
        this.becbProperties = becbProperties;
    }

    @SqsListener("new-point-queue")
    public void listenNewPointQueue(@Headers Map<String, Object> headers, String message){
        log.info("Received message on queue new_point : {}", message);
        if(SuportService.isValid(message)){
            Point point0 = pointService.messageToPoint(message);
            if(point0.getCreateTime() ==null)
                point0.setCreateTime(LocalDateTime.now());
            Point point = pointService.convertItemToPoint(pointService.savePointDynamo(point0));

            userService.saveUser(point0.getUser());
            pointService.savePointDb(point0);

            if(point.getUser().getInstagram()!=null){
                Runnable runnable = () -> {
                        try {
                            Thread.sleep(100000); // pausa por 10 segundos
                            userService.createUserMap(point.getUser());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            log.error("Error to save file to user map. Error: {}", e.getMessage());
                        }
                };
                Thread thread = new Thread(runnable);
                thread.start();

            }
        }else{
            log.error("Invalid message received on queue new_point : {}: ", message);
        }
    }

    /**
     * Create new maps
     * @param headers
     * @param message
     */
    @SqsListener(value = "new-file-to-map-queue", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void gerarArquivoParaMapa(@Headers Map<String, Object> headers, String message) {log.info("Received message to create mapfile!");
        pointService.gerarArquivoParaMapa(message);
    }

    /**
     *
     * Queue with not reviewd points
     * @param message
     */
    @SqsListener(value = "not-approved-queue", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void gerarArquivoParaAprovacao(@Headers Map<String, Object> headers, String message) {
        log.info("Received message on  queue not_approved: {}", message);
        pointService.gerarArquivoParaAprovacao(message);
    }

    @SqsListener(value = "aprovar-point-queue", deletionPolicy = SqsMessageDeletionPolicy.ALWAYS)
    public void listenAproveQueue(@Headers Map<String, Object> headers, String message) {
        log.info("Received message on first queue 'aprovar_point': {}",  message);
        try {
            pointService.aprovePoint(message, AprovedEnum.asTrue.getValue());
        } catch (Exception e) {
            log.error("Error occurred while aproving point: {} . \nErro: {}", message, e.getMessage());
        }
    }

    @SqsListener(value = "bloquear-point-queue", deletionPolicy = SqsMessageDeletionPolicy.ALWAYS)
    public void listenBlockQueue(@Headers Map<String, Object> headers, String message) {
        log.info("Received message on  queue bloquear_point: {}", message);
        try {
            pointService.aprovePoint(message, AprovedEnum.asForget.getValue());
        } catch (Exception e) {
            log.error("Error occurred while aproving point: {} . \nErro: {}", message, e.getMessage());
        }
    }

    @SqsListener(value = "upload-photo-queue", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void addPhotoToPoint(@Headers Map<String, Object> headers, String message)  {
        log.info("Received message on sqs.queue.add_photo_point: {}", message);
        if(message.contains("http")) {
            if (!pointService.addFileLinkToPoint(message))
                log.error("Message on sqs.queue.add_photo_point: {} not saved", message);
        }else {
            if (!pointService.addFileToPoint(message)) {
                String exc = "Message on sqs.queue.add_audio_point: " + message + " not saved";
                log.error(exc);

            }
        }
    }

    @SqsListener(value = "upload-audio-queue", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void addAudioToPoint(@Headers Map<String, Object> headers, String message) {
        log.info("Received message on sqs.queue.add_audio_point: {}", message);

        if(!pointService.addFileToPoint(message)) {
            String exc = "Message on sqs.queue.add_audio_point: "+message+" not saved";
            log.error(exc);

        }
    }

    @SqsListener(value = "reset-password-queue", deletionPolicy = SqsMessageDeletionPolicy.ALWAYS)
    public void sendMailToResetPass(@Headers Map<String, Object> headers, String message) {
        log.info("Received message on sqs.queue.password.sendmail: {}", message);
        String  code = SuportService.getCode();
        if(userService.sendCode(message, code)) {
            log.info("Code saved to message: {} code to  password : {}", message, code  );
            userService.sendEmailResetPassword(message, code);
        }else
            log.error("Error ask for reset password: {}", message);
    }

    @SqsListener(value = "point-update-queue", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void updadePoint(@Headers Map<String, Object> headers, String message) {
        log.info("Received message on sqs.queue.update_point: {}", message);

        if(SuportService.isValid(message)) {
            Point point0 = pointService.messageToPoint(message);
            Point point = pointService.getPointById(point0.getPointId());
            pointService.updatePointObject(point0, point);
            pointService.savePointDb(point);
            pointService.savePointDynamo(point);

            if (point.getUser().getInstagram() != null) {
                Runnable runnable = () -> {
                    try {
                        Thread.sleep(100000); // pausa por 10 segundos
                        userService.createUserMap(point.getUser());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        log.error("Error to save file to user map. Error: {}", e.getMessage());
                    }
                };
                Thread thread = new Thread(runnable);
                thread.start();

            }
        }
    }


    @SqsListener(value = "point-vote-queue", deletionPolicy = SqsMessageDeletionPolicy.ALWAYS)
    public void addVote(@Headers Map<String, Object> headers, String message) {
        log.info("Received message on sqs.queue.point_vote: {}", message);
        String  code = SuportService.getCode();
        if(userService.sendCode(message, code)) {
            log.info("Code saved to message: {} code to  password : {}", message, code  );
            userService.sendEmailResetPassword(message, code);
        }else
            log.error("Erro receiving vote: {}", message);

    }

}