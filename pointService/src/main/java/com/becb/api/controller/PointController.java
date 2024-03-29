package com.becb.api.controller;

import com.becb.api.dto.PointDto;
import com.becb.api.dto.PointResponse;
import com.becb.api.service.ArquivoService;
import com.becb.api.service.file.FileService;
import com.becb.api.service.sqs.SqsService;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.github.f4b6a3.ulid.UlidCreator;
import org.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.fileupload.FileUploadException;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping(produces = "application/json;charset=UTF-8", consumes = {"application/json;charset=UTF-8", "multipart/form-data"})
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
public class PointController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    SqsService sqsService;

    @Value("${sqs.queue.aprovar_point}")
    String aproveQueueName;

    @Value("${sqs.queue.bloquear_point}")
    String bloquearQueueName;

    @Value("${sqs.queue.new_point}")
    String newPointQueueName;

    @Value("${sqs.queue.new_file_to_map}")
    String newFileToMapQueueName;

    @Value("${sqs.queue.add_photo_point}")
    String addPhotoPointQueueName;

    @Value("${sqs.queue.add_audio_point}")
    String addAudioPointQueueName;

    @Value("${sqs.queue.not_approved}")
    String notApprovedQueueName;

    @Value("${file.endpoint}")
    String fileEndpoint;

    @Autowired
    ArquivoService arquivoService;

    @Autowired
    FileService fileService;

    /**
     * Cadastrar um ponto
     */

    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    @RequestMapping(value = "/point")
    public PointResponse cadastro(@RequestBody PointDto pointDto, HttpServletRequest request) throws IOException, InterruptedException {

        if (pointDto.getPointId() == null)
            pointDto.setPointId(UlidCreator.getUlid().toString());

        if (pointDto.getAudio() != null && !pointDto.getAudio().isEmpty()) {

            String filePath = fileService.saveAudio(pointDto.getAudio().replace("data:audio/ogg code=opus;base64,", ""),
                    pointDto.getPointId());

            pointDto.setAudio(filePath);
        }

        String formatedPoint = configPoint(pointDto, request);

        logger.info("Point to Add: " + formatedPoint);
        try {
            sqsService.sendMessage(formatedPoint);
        } catch (Exception e) {
            logger.error("Error to add point: {}", e.getMessage());
            return new PointResponse("500", "Error to add point" + e.getMessage());
        }
        PointResponse response = new PointResponse("Point added successfully");
        response.setPointId(pointDto.getPointId());
        logger.info("Point added: {}", response.toString());
        return response;
    }

    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    @PutMapping("/point/{pointId}")
    public PointResponse uploadFile(@PathVariable String pointId, @RequestParam("files") MultipartFile files, HttpServletRequest request) throws IOException, InterruptedException {


        String queue;
        String filePath;
        String message;
        InputStream inputStream = null;
        if (files != null)
            inputStream = new BufferedInputStream(files.getInputStream());
        else
            return new PointResponse("500", "Error to upload, file null");

        if (files.getOriginalFilename().endsWith(".mp3")) {

            filePath = fileService.saveFileMp3(inputStream, pointId);
            queue = addAudioPointQueueName;

        } else {
            filePath = fileService.savePointPhoto(inputStream, pointId);
            queue = addPhotoPointQueueName;
        }

        message = "{\"point_id\" : \"" + pointId + "\", \"file_path\" : \"" + filePath + "\"}";
        sqsService.sendMessage(message, queue);

        return new PointResponse("Uploading to " + queue);

    }

    private String configPoint(PointDto pointDto, HttpServletRequest request) {

        String token = request.getHeader("Authorization").replace("Bearer ", "");

        String[] chunks = token.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payload = new String(decoder.decode(chunks[1]));
        JSONObject jsonObject = new JSONObject(payload);

        pointDto.setUser_id(jsonObject.getString("usuario_id"));
        pointDto.setUser_email(jsonObject.getString("user_name"));
        pointDto.setUser_name(jsonObject.getString("nome_completo"));
        pointDto.setShare(jsonObject.getBoolean("share"));
        pointDto.setInstagram(jsonObject.getString("usuario_instagram"));
        pointDto.setShare(jsonObject.getBoolean("guide"));

        return unicodeEscapeToUtf8(pointDto.toString());
    }

    private static String unicodeEscapeToUtf8(String unicodeEscapeString) {
        Pattern pattern = Pattern.compile("\\\\u([0-9a-fA-F]{4})");
        Matcher matcher = pattern.matcher(unicodeEscapeString);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            String hexString = matcher.group(1);
            int codePoint = Integer.parseInt(hexString, 16);
            matcher.appendReplacement(sb, new String(Character.toChars(codePoint)));
        }
        matcher.appendTail(sb);
        try {
            byte[] utf8Bytes = sb.toString().getBytes("UTF-8");
            return new String(utf8Bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Logger logger = LoggerFactory.getLogger(PointController.class);
            logger.error(e.getMessage());

            return null;
        }
    }

}
