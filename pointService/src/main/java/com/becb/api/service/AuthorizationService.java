package com.becb.api.service;

import com.becb.api.dto.LoginDto;

import com.becb.api.dto.LoginResponse;
import com.becb.api.exception.UsuarioAlreadyExistsException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class AuthorizationService {

    @Value("${auth.server.url}")
    String auth_url;

    private static final Logger logger = Logger.getLogger(AuthorizationService.class.getName());

    public LoginResponse login(LoginDto loginDto) {

        LoginResponse loginResponse = new LoginResponse();
        try {

            HttpURLConnection connection = getConnection(auth_url+"/oauth/token");

            String params = "grant_type=password&username=" + loginDto.getEmail() + "&password=" + loginDto.getPassword();
            byte[] postData = params.getBytes(StandardCharsets.UTF_8);
            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(postData);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                JSONObject jsonResponse = new JSONObject(response.toString());
                loginResponse.setToken(jsonResponse.getString("access_token")) ;
                loginResponse.setStatus(responseCode);
                logger.info("token: "+jsonResponse.getString("access_token"));
                return loginResponse;

            } else {
                loginResponse.setError(""+responseCode);
                loginResponse.setStatus(responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private HttpURLConnection getConnection(String urlConnection) throws IOException {
        String clientAuth = Base64.getEncoder().encodeToString(("xpto:xpto").getBytes());
        URL url = new URL(urlConnection);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Authorization", "Basic " + clientAuth);
        return connection;
    }

    public String addUsuarioAuthServer(LoginDto loginDto) {
        try {
            HttpURLConnection connection = getConnection(auth_url+"/cadastro");
            connection.setRequestProperty("Content-Type", "application/json");

            String params =
                    "email=" + loginDto.getEmail() + "&password=" + loginDto.getPassword()+ "&name=" + loginDto.getName() + "&telefone=" + loginDto.getPhone();
            byte[] postData = params.getBytes(StandardCharsets.UTF_8);
            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(postData);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                JSONObject jsonResponse = new JSONObject(response.toString());

                return jsonResponse.getString("id");
            }else {
                System.out.println("Erro ao gerar access key");
                return null;
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String adicionarUsuario(LoginDto loginDto) throws UsuarioAlreadyExistsException {
        LoginResponse loginResponse = new LoginResponse();
        try {
            // Specify the URL of the REST API endpoint
            URL url = new URL(auth_url+"/cadastro");

            // Open a connection to the URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set the request method to POST
            connection.setRequestMethod("POST");

            // Set the request headers (if required)
            connection.setRequestProperty("Content-Type", "application/json");

            // Enable output and input streams for sending and receiving data
            connection.setDoOutput(true);
            connection.setDoInput(true);

            // Create the request body
            String requestBody = "{\"name\": \""+loginDto.getName()+"\"," +
                    "\"email\": \""+loginDto.getEmail()+"\"," +
                    "\"password\": \""+loginDto.getPassword()+"\"," +
                    "\"telefone\": \""+loginDto.getPhone()+"\"}";

            // Write the request body to the connection's output stream
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(requestBody.getBytes());
            outputStream.flush();
            outputStream.close();

            // Get the response code
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read the response from the connection's input stream
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONObject jsonResponse = new JSONObject(response.toString());
                connection.disconnect();
                return jsonResponse.getString("id");
            }
            else {
                throw new UsuarioAlreadyExistsException("Create new user failed. Response code: " + responseCode  +
                        ". Verify the user already exists or try again later.");

            }

            // Close the connection

        }
        catch (UsuarioAlreadyExistsException e) {
            throw e;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}