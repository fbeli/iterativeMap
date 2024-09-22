package com.becb.processnewpoint.service;

import com.becb.processnewpoint.domain.Point;
import com.becb.processnewpoint.domain.User;
import com.becb.processnewpoint.repository.UserRepository;
import com.becb.processnewpoint.service.dynamodb.DynamoDbClient;
import com.becb.processnewpoint.service.email.SendEmailService;
import com.becb.processnewpoint.service.file.FileService;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@Service
public class UserService {

    @Value("${auth.server.url}")
    String auth_url;

    SendEmailService sendEmailService;
    PointService pointService;
    FileService fileService;
    UserRepository userRepository;

    @Autowired
    DynamoDbClient dynamoDbClient;


    private final Logger logger = LoggerFactory.getLogger(getClass());


    public UserService( @Autowired @Qualifier("sendMailCourier") SendEmailService sendEmailService,
                        @Autowired PointService pointService,
                        @Autowired FileService fileService,
                        @Autowired UserRepository userRepository) {
        this.sendEmailService = sendEmailService;
        this.pointService = pointService;
        this.fileService = fileService;
        this.userRepository = userRepository;
    }

    public boolean sendEmailResetPassword(String message, String code) {
        try {
            ResetRequestDto resetDto = new ResetRequestDto(message);
            String html = sendEmailService.createHtmlContentForgotPasswod(resetDto.name, resetDto.email, code);
            sendEmailService.sendEmail(resetDto.email, "Password Reset", html);
            return true;
        } catch (Exception e) {
            logger.error("Email did not send:  {} ", e.getMessage());
        }
        return false;
    }

    public boolean sendCode(String inputMessage, String code) {
        ResetRequestDto message = new ResetRequestDto(inputMessage);

        try {
            String fullUrl = auth_url + "/user/reset_create_code";
            URL url = new URL(fullUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            connection.setDoInput(true);

            String requestBody = "{\"email\": \"" + message.email + "\"," +
                    "\"name\": \"" + message.name + "\"," +
                    "\"code\": \"" + code + "\"," +
                    "\"userId\": \"" + message.userId + "\"}";

            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(requestBody.getBytes());
            outputStream.flush();
            outputStream.close();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                connection.disconnect();
                return true;
            } else {
                logger.error("Email courier error, Response Code: {} trying to connect with {} \n ", responseCode, fullUrl);
                return false;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }

    }
//TODO - change to db
    public boolean createUserMap(User user) throws IOException {

        List<Point> points = pointService.getPointsByUserId(user.getUserId());
        Point point = points.stream().sorted((p1,p2) -> p2.getPointId().compareTo(p2.getPointId())).findFirst().get();
        fileService.createFileToUserMap(points, user.getInstagram());
        fileService.copyUserFiles(user.getInstagram());
        fileService.createConstFile(user.getInstagram(), point.getLatitude(), point.getLongitude());
        return true;
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Getter
    @Setter
    class ResetRequestDto {

        ResetRequestDto(String message) {
            JSONObject jsonObject = new JSONObject(message);
            this.name = jsonObject.optString("name");
            this.email = jsonObject.optString("email");
            this.userId = jsonObject.optString("user_id");
        }

        String email;
        String userId;
        String name;

    }

    public User getUserByPointId(String pointId) {
        return userRepository.findUserByPoints(pointId);
    }
    public User getUserByUserId(String userId) {
        return userRepository.findByUserId(userId);
    }

    public User getUserByInstagramId(String instagramId) {
        List<User> lista = userRepository.findByUserByInstgram(instagramId);
        if (lista != null && !lista.isEmpty()) {
            return lista.get(0);
        }
        return null;
    }


}
