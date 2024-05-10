package com.becb.processnewpoint.service.sqs;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SqsChronClient {

    @Autowired
    public SqsChronClient(SqsService sqsService, AmazonSQS sqs) {
        this.sqsService = sqsService;
        this.sqs = sqs;
    }


    SqsService sqsService;
    AmazonSQS sqs;

  /*  @Value("${sqs.queue.not_approved}")
    String notApprovedQueueUrl;
    @Value("${sqs.queue.new_file_to_map}")
    String newFileToMapQueueUrl;
    @Value("${sqs.queue.aprovar_point}")
    String aprovarPointQueueUrl;
    @Value("${sqs.queue.bloquear_point}")
    String bloquearPointQueueUrl;
    @Value("${sqs.queue.add_photo_point}")
    String addPhotoPointQueueUrl;
    @Value("${sqs.queue.add_audio_point}")
    String addAudioPointQueueUrl;
    @Value("${sqs.queue.reset_password}")
    String resetPasswordQueueUrl;
    @Value("${sqs.queue.update_point}")
    String updatePointQueueUrl;

*/

    //@Value("${sqs.queue.not_approved}")


    String bloquearPointQueueUrl = "bloquear-point-queue";
    String newFileToMapQueueUrl = "new-file-to-map-queue";

    String uploadPhotoQueueUrl = "upload-photo-queue";
    String uploadAudioQueueUrl = "upload-audio-queue";
    String resetPasswordQueueUrl = "reset-password-queue";
    String pointUpdateQueueUrl = "point-update-queue";


    String newPointQueueUrl = "new-point-queue";
    String aprovarPointQueueUrl = "aprovar-point-queue";
    String notApprovedQueueUrl = "not-approved-queue";
    String pointVoteQueueUrl = "point-vote-queue";
    List<String> queues = List.of(notApprovedQueueUrl, pointVoteQueueUrl, newPointQueueUrl, aprovarPointQueueUrl,
            pointUpdateQueueUrl, resetPasswordQueueUrl, uploadAudioQueueUrl, uploadPhotoQueueUrl, newFileToMapQueueUrl, bloquearPointQueueUrl);

    @Value("${sqs.queue.url}")
    String queueUrl;


    @Scheduled(cron = "2 * * * * *") // Cron expression for running every minute
    public void receberMensagens() {
        queues.stream().forEach(queue -> {
            //System.out.println("Recebendo mensagens... from queue: " + queueUrl + queue);
            ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl + queue);
            receiveMessageRequest.setMaxNumberOfMessages(5);
            List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
            if (messages.size() > 0) {
                switch (queue) {
                    case "not-approved-queue":
                        messages.forEach(message -> {
                            sqsService.gerarArquivoParaAprovacao(null, message.getBody());
                            sqs.deleteMessage(queueUrl + queue, message.getReceiptHandle());
                        });
                        break;
                    case "new-point-queue":
                        messages.forEach(message -> {
                            sqsService.listenNewPointQueue(null, message.getBody());
                            sqs.deleteMessage(queueUrl + queue, message.getReceiptHandle());
                        });
                        break;
                    case "point-vote-queue":
                        messages.forEach(message -> {
                            sqsService.addVote(null, message.getBody());
                            sqs.deleteMessage(queueUrl + queue, message.getReceiptHandle());
                        });
                        break;
                    case "point-update-queue":
                        messages.forEach(message -> {
                            sqsService.updadePoint(null, message.getBody());
                            sqs.deleteMessage(queueUrl + queue, message.getReceiptHandle());
                        });
                        break;
                    case "reset-password-queue":
                        messages.forEach(message -> {
                            sqsService.sendMailToResetPass(null, message.getBody());
                            sqs.deleteMessage(queueUrl + queue, message.getReceiptHandle());
                        });
                        break;

                    case "upload-photo-queue":
                        messages.forEach(message -> {
                            sqsService.addPhotoToPoint(null, message.getBody());
                            sqs.deleteMessage(queueUrl + queue, message.getReceiptHandle());
                        });
                        break;

                    case "upload-audio-queue":
                        messages.forEach(message -> {
                            sqsService.addAudioToPoint(null, message.getBody());
                            sqs.deleteMessage(queueUrl + queue, message.getReceiptHandle());
                        });
                        break;
                    case "new-file-to-map-queue":
                        messages.forEach(message -> {
                            sqsService.gerarArquivoParaMapa(null, message.getBody());
                            sqs.deleteMessage(queueUrl + queue, message.getReceiptHandle());
                        });
                    case "bloquear-point-queue":
                        messages.forEach(message -> {
                            sqsService.listenBlockQueue(null, message.getBody());
                            sqs.deleteMessage(queueUrl + queue, message.getReceiptHandle());
                        });
                }
            }
        });


    }
}


