package com.becb.api.service.user;

import com.becb.api.dto.LoginDto;
import com.becb.api.dto.LoginResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class UserService {

    @Value("${auth.server.url}")
    String auth_url;

    private final Logger logger = LoggerFactory.getLogger(getClass());


    private HttpURLConnection getConnection(String urlConnection, String method) throws IOException {

        URL url = new URL(urlConnection);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setDoOutput(true);
        return connection;
    }

    public LoginDto getUserById(String id) throws IOException {

       return  getUser(auth_url+"/user/get_by_id/"+id);

    }
    public LoginDto getUserByEmail(String email) throws IOException {
        return  getUser(auth_url+"/user/get_by_email/"+email);

    }

    public LoginDto getUser(String connectionEndpoint) throws IOException {
        HttpURLConnection con =  getConnection(connectionEndpoint, "GET");
        int responseCode = con.getResponseCode();
        LoginDto loginResponse = new LoginDto();
        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            loginResponse.setupFromString(response.toString());
        } else {
            logger.info("user not found at {}", connectionEndpoint);
        }

            return loginResponse;

    }

    public LoginResponse resetPassword(LoginDto requestLoginDto) throws IOException {
        String connectionEndpoint = auth_url+"/user/reset_password";
        HttpURLConnection con =  getConnection(connectionEndpoint, "PUT");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);
        con.setDoInput(true);

        //String params = "code="+requestLoginDto.getCode()+"&email=" + requestLoginDto.getEmail() + "&password=" + requestLoginDto.getPassword();
        String requestBody = "{ \"code\":\""+requestLoginDto.getCode()+"\" ,"
                + "\"email\":\""+requestLoginDto.getEmail()+"\" ,"
                + "\"password\":\""+requestLoginDto.getPassword()+"\" }";
       // byte[] postData = params.getBytes(StandardCharsets.UTF_8);

        OutputStream outputStream = con.getOutputStream() ;
        outputStream.write(requestBody.getBytes());
        outputStream.flush();
        outputStream.close();

        LoginResponse loginResponse = new LoginResponse();
        int responseCode = con.getResponseCode();

    //    if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            loginResponse.setupFromString(response.toString());

        return loginResponse;
    }

}
