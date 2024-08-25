package com.becb.processnewpoint.service.sqs;

import com.becb.processnewpoint.core.BecbProperties;
import com.becb.processnewpoint.domain.AprovedEnum;
import com.becb.processnewpoint.domain.Point;
import com.becb.processnewpoint.domain.Roteiro;
import com.becb.processnewpoint.exception.SQSMessageException;
import com.becb.processnewpoint.service.PointService;
import com.becb.processnewpoint.service.RoteiroService;
import com.becb.processnewpoint.service.SuportService;
import com.becb.processnewpoint.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class SqsService {

    private final PointService pointService;

    private final UserService userService;

    private final BecbProperties becbProperties;
    private final RoteiroService roteiroService;

    @Autowired
    public SqsService(PointService pointService, UserService userService, BecbProperties becbProperties, RoteiroService roteiroService) {
        this.pointService = pointService;
        this.userService = userService;
        this.becbProperties = becbProperties;
        this.roteiroService = roteiroService;
    }

    @JmsListener(destination = "new-point-queue")
    public void listenNewPointQueue(@Headers Map<String, Object> headers, String message) {
        log.info("Received message on queue new_point : {}", message);
        if (SuportService.isValid(message)) {
            Point point0 = pointService.messageToPoint(message);
            if (point0.getCreateTime() == null)
                point0.setCreateTime(LocalDateTime.now());


            userService.saveUser(point0.getUser());
            pointService.savePointDb(point0);

            if (point0.getUser().getInstagram() != null) {
                Runnable runnable = () -> {
                    try {
                        Thread.sleep(100000); // pausa por 10 segundos
                        userService.createUserMap(point0.getUser());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        log.error("Error to save file to user map. Error: {}", e.getMessage());
                    }
                };
                Thread thread = new Thread(runnable);
                thread.start();

            }
        } else {
            log.error("Invalid message received on queue new_point : {}: ", message);
        }
    }

    /**
     * Create new maps
     *
     * @param headers
     * @param message
     */
    @JmsListener(destination = "new-file-to-map-queue")
    public void gerarArquivoParaMapa(@Headers Map<String, Object> headers, String message) {
        log.info("Received message to create mapfile!");
        log.info("Files created: {}", pointService.gerarArquivoParaMapa(message));
    }

    /**
     * Queue with not reviewd points
     *
     * @param message
     */
    @JmsListener(destination = "not-approved-queue")
    public void gerarArquivoParaAprovacao(@Headers Map<String, Object> headers, String message) {
        log.info("Received message on  queue not_approved: {}", message);
        pointService.gerarArquivoParaAprovacao(message);
    }

    @JmsListener(destination = "aprovar-point-queue")
    public void listenAproveQueue(@Headers Map<String, Object> headers, String message) {
        log.info("Received message on first queue 'aprovar_point': {}", message);
        try {
            pointService.aprovePoint(message, AprovedEnum.asTrue.getValue());
        } catch (Exception e) {
            log.error("Error occurred while aproving point: {} . \nErro: {}", message, e.getMessage());
        }
    }

    @JmsListener(destination = "bloquear-point-queue")
    public void listenBlockQueue(@Headers Map<String, Object> headers, String message) {
        log.info("Received message on  queue bloquear_point: {}", message);
        try {
            pointService.aprovePoint(message, AprovedEnum.asForget.getValue());
        } catch (Exception e) {
            log.error("Error occurred while aproving point: {} . \nErro: {}", message, e.getMessage());
        }
    }

    @JmsListener(destination = "upload-photo-queue")
    public void addPhotoToPoint(@Headers Map<String, Object> headers, String message) throws SQSMessageException {
        log.info("Received message on sqs.queue.add_photo_point: {}", message);
        if (message.contains("http")) {
            if (!pointService.addFileLinkToPoint(message))
                log.error("Message on sqs.queue.add_photo_point: {} not saved", message);
        } else {
            if (!pointService.addFileToPoint(message)) {
                String exc = "Message on sqs.queue.add_audio_point: " + message + " not saved";
                log.error(exc);
                throw new SQSMessageException(exc, null);
            }
        }
    }

    @JmsListener(destination = "upload-audio-queue")
    public void addAudioToPoint(@Headers Map<String, Object> headers, String message) throws SQSMessageException {
        log.info("Received message on sqs.queue.add_audio_point: {}", message);

        if (!pointService.addFileToPoint(message)) {
            String exc = "Message on sqs.queue.add_audio_point: " + message + " not saved";
            log.error(exc);
            throw new SQSMessageException(exc, null);
        }
    }

    @JmsListener(destination = "reset-password-queue")
    public void sendMailToResetPass(@Headers Map<String, Object> headers, String message) {
        log.info("Received message on sqs.queue.password.sendmail: {}", message);
        String code = SuportService.getCode();
        if (userService.sendCode(message, code)) {
            log.info("Code saved to message: {} code to  password : {}", message, code);
            userService.sendEmailResetPassword(message, code);
        } else
            log.error("Error ask for reset password: {}", message);
    }

    @JmsListener(destination = "point-update-queue")
    public void updadePoint(@Headers Map<String, Object> headers, String message) {
        log.info("Received message on sqs.queue.update_point: {}", message);

        if (SuportService.isValid(message)) {
            Point point0 = pointService.messageToPoint(message);
            if (point0.getPointId() != null) {
                log.info("Received message to updade Point_id: {} Point Title: {}", point0.getPointId(), point0.getTitle());
                Point point = pointService.getPointById(point0.getPointId());
                pointService.updatePointObject(point0, point);
                point.setUser(userService.getUserByPointId(point.getPointId()));
                pointService.savePointDb(point);

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
    }

    @JmsListener(destination = "translate-queue")
    public void traslatePoint(@Headers Map<String, Object> headers, String message) throws Exception {
        try{
            pointService.createPointsFromParent(message);
        }catch (ObjectNotFoundException objE){
            TimeUnit.SECONDS.sleep(3);
            throw objE;
        }
        catch (Exception e){
            log.info("Erro para a mensagem: {}", message);
            throw e;
        }

    }

    @JmsListener(destination = "add-roteiro-queue")
    public void addRoteiro(String message){
        log.info("Received message on sqs.add-roteiro-queue: {}", message);

        Roteiro roteiro = roteiroService.addNewRoute(message);

        if (roteiro != null)
            log.info("Roteiro atualizado: "+roteiro.getTitle());
        else
            log.error("Erro para incluir ponto a rota : {}", message);

    }

    @JmsListener(destination = "point-vote-queue")
    public void addVote(@Headers Map<String, Object> headers, String message) {
        log.info("Received message on sqs.queue.point_vote: {}", message);
        String code = SuportService.getCode();
        if (userService.sendCode(message, code)) {
            log.info("Code saved to message: {} code to  password : {}", message, code);
            userService.sendEmailResetPassword(message, code);
        } else
            log.error("Erro receiving vote: {}", message);

    }

}