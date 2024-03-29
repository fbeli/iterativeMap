package com.becb.processnewpoint.service;

import com.becb.processnewpoint.service.email.SendEmailService;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class UserService {

    @Value("${auth.server.url}")
    String auth_url;

    @Autowired
    @Qualifier("sendMailCourier")
    SendEmailService sendEmailService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

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

    public boolean saveCode(String message, String code) {
        return sendCode(new ResetRequestDto(message), code);
    }
    private boolean sendCode(ResetRequestDto message, String code) {

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
                    return false;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
@Getter
@Setter
 class ResetRequestDto {

    ResetRequestDto(String message){
        JSONObject jsonObject = new JSONObject(message);
        this.name = jsonObject.optString("name");
        this.email = jsonObject.optString("email");
        this.userId = jsonObject.optString("user_id");
    }
    String email;
    String userId;
    String name;

}
