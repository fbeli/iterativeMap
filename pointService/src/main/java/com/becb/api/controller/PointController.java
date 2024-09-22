package com.becb.api.controller;

import com.becb.api.core.Properties;
import com.becb.api.dto.PointDto;
import com.becb.api.dto.PointResponse;
import com.becb.api.service.ArquivoService;
import com.becb.api.service.PointService;
import com.becb.api.service.file.FileService;
import com.becb.api.service.sqs.SqsService;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.github.f4b6a3.ulid.UlidCreator;
import org.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping(produces = "application/json;charset=UTF-8", consumes = {"application/json;charset=UTF-8", "multipart/form-data"})
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
public class PointController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public PointController(@Autowired ArquivoService arquivoService,@Autowired FileService fileService, @Autowired Properties becbProperties,@Autowired SqsService sqsService,@Autowired PointService pointService) {
        this.arquivoService = arquivoService;
        this.fileService = fileService;
        this.becbProperties = becbProperties;
        this.sqsService = sqsService;
        this.pointService = pointService;
    }

    public PointController() {
    }
    @Autowired
    Properties becbProperties;

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

    @Value("${sqs.queue.translate_point}")
    String translateQueue;

    @Value("${file.endpoint}")
    String fileEndpoint;


    @Autowired
    ArquivoService arquivoService;

    @Autowired
    FileService fileService;

    @Autowired
    PointService pointService;

    /**
     * Cadastrar um ponto
     */

    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    @RequestMapping(value = "/point")
    public PointResponse cadastro(@RequestBody PointDto pointDto, HttpServletRequest request) throws IOException {

        if (pointDto.getPointId() == null)
            pointDto.setPointId(UlidCreator.getUlid().toString());

        //if audio is recorded at time.
        if (pointDto.getAudio() != null && !pointDto.getAudio().isEmpty()) {
            String filePath = fileService.saveAudio(pointDto.getAudio().replace("data:audio/ogg code=opus;base64,", ""),
                    pointDto.getPointId());
            pointDto.setAudio(filePath);
        }

        String formatedPoint = configPoint(pointDto, request);

        logger.info("Point to Add: " + formatedPoint);
        try {
            sqsService.sendMessage(formatedPoint);
            sqsService.sendMessage(pointDto.getPointIdJson(), translateQueue);

        } catch (Exception e) {
            logger.error("Error to add point: {}", e.getMessage());
            return new PointResponse("500", "Error to add point" + e.getMessage());
        }
        PointResponse response = new PointResponse("Point added successfully");
        response.setPointId(pointDto.getPointId());
        logger.info("Point added: {}", response);
        return response;
    }

    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    @PutMapping("/point/{pointId}")
    public PointResponse uploadFile(@PathVariable String pointId, @RequestParam("files") MultipartFile files, HttpServletRequest request) throws IOException {

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

    @ResponseBody
    @GetMapping("/point/{pointId}")
    public PointDto getPoint(@PathVariable String pointId) throws IOException {
        logger.info("Get point by id: {}", pointId);
        return pointService.getPointById(pointId);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping( value = "/point/users")
    @ResponseBody
    public List<PointDto> getByUser(@RequestParam(value = "page", defaultValue = "0") int page,
                                 @RequestParam(value = "size", defaultValue = "10") int size,
                                 @RequestParam("userId") String userId) {


        return pointService.getPointsByUser(userId, size, page);
    }

    private boolean addfile( String pointId, MultipartFile files) throws IOException {

        String queue;
        String filePath;
        String message;

        InputStream inputStream ;
        if (files != null)
            inputStream = new BufferedInputStream(files.getInputStream());
        else
            return false;

        if (files.getOriginalFilename()!= null && files.getOriginalFilename().endsWith(".mp3")) {

            filePath = fileService.saveFileMp3(inputStream, pointId);
            queue = addAudioPointQueueName;

        } else {
            filePath = fileService.savePointPhoto(inputStream, pointId);
            queue = addPhotoPointQueueName;
        }

        message = "{\"point_id\" : \"" + pointId + "\", \"file_path\" : \"" + filePath + "\"}";
        sqsService.sendMessage(message, queue);

        return true;
    }

    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    @PutMapping(value="/v2/point")
    /**
     * {{endpoint_service}}/v2/point/01HSF8GPB8DWWH4FW3K7Z753WR
     */
    public PointResponse updatePoint( @RequestParam String pointId, @RequestParam PointDto pointDto, HttpServletRequest request) throws IOException {

        pointDto.setPointId(pointId);
        sqsService.sendMessage(pointDto.toString(), becbProperties.sqs.update_point);
        pointService.sendMessageToReadFromQueue();
        return new PointResponse("point updated successfully");
    }

    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    @PutMapping("/point/upload_file_link/{pointId}")
    public PointResponse uploadFilelink(@PathVariable String pointId, @RequestParam("file") String link, HttpServletRequest request){


        String queue = addPhotoPointQueueName;
        String message;

        message = "{\"point_id\" : \"" + pointId + "\", \"link\" : \"" + link + "\"}";
        sqsService.sendMessage(message, queue);

        return new PointResponse("Uploading to " + queue);
    }

    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    @PutMapping("/v2/point/translate")
    /**
     * https://rapidapi.com/gofitech/api/nlp-translation/
     */
    public PointResponse translate(@RequestParam String pointId,
                              @RequestParam(value = "language", defaultValue = "EN") String language
                                ,HttpServletRequest request)
            throws IOException {
        PointDto pointDto = new PointDto();
        pointDto.setPointId(pointId);
        pointDto.setLanguage(language);

        String formatedPoint = configPoint(pointDto, request);

        logger.info("Point to Add: " + formatedPoint);

        sqsService.sendMessage(pointDto.getPointIdJson(), translateQueue);

        return new PointResponse("point updated successfully");
    }


    private JSONObject getToken(HttpServletRequest request){
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        String[] chunks = token.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payload = new String(decoder.decode(chunks[1]));

        return new JSONObject(payload);
    }

    private String configPoint(PointDto pointDto, HttpServletRequest request) {

        JSONObject jsonObject = getToken(request);

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
