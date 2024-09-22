package com.becb.api.service;

import com.becb.api.dto.PointDto;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.util.ArrayList;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PointService {

    private static final Logger log = LoggerFactory.getLogger(PointService.class);
    @Value("${newpoint.server.url}")
    String newPointServerUrl;

    String getPointByIdEndpoint = "/point/?pointId=";
    String getPointByIdUser = "/point/users?userId=";
    String lerDaQueueEndpoint = "/point/reload_queue";

    public PointDto getPointById(String id) throws IOException {

        String url = newPointServerUrl + getPointByIdEndpoint + id;
        HttpURLConnection connection = PointSupport.getConnection(url);
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json");

        log.info("Connecting in getPointById, URL: {}", url);

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
            if(response.toString().isBlank()) { return null; }
            JSONObject jsonResponse = new JSONObject(response.toString());

            return getPointDtoFromJson(jsonResponse);
        }
        return null;
    }

    public void sendMessageToReadFromQueue() throws IOException {

            String url = newPointServerUrl + lerDaQueueEndpoint;
            HttpURLConnection connection = PointSupport.getConnection(url);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");

            log.info("Connecting with readFromQueue, URL: {}", url);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                log.info("Message sent to read from queue");
            }


    }
    public List<PointDto> getPointsByUser(String userId, Integer size, Integer page) {

        String url = newPointServerUrl + getPointByIdUser + userId;
        if (size != null)
            url += "&size=" + size;
        if (page != null)
            url += "&page=" + page;
        try {
            HttpURLConnection connection = PointSupport.getConnection(url);

            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");

            log.info("Connecting in getPointByUser, URL: {}", url);

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

                JSONArray array = new JSONArray(response.toString());
                JSONObject jsonResponse;
                List<PointDto> points = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    jsonResponse = array.getJSONObject(i);
                    points.add(getPointDtoFromJson(jsonResponse));
                }
                return points;
            }
        } catch (ProtocolException e) {
            log.error("Error  to set endpoint method{}", e);
            throw new RuntimeException(e);
        } catch (IOException e) {
            log.error("Erro to connect with endpoint: {}", newPointServerUrl);
            log.error("Error while trying to get points by user. \n{}", e);
            throw new RuntimeException(e);
        }
        return null;
    }

    public PointDto getPointDtoFromJson(JSONObject jsonObject) {
        PointDto pointDto = new PointDto();
        pointDto.setPointId(jsonObject.optString("pointId"));
        pointDto.setInstagram(jsonObject.optString("instagram"));
        pointDto.setPhoto(jsonObject.optString("photo"));
        pointDto.setAudio(jsonObject.optString("audio"));
        pointDto.setDescription(jsonObject.optString("description"));
        pointDto.setGuide(jsonObject.optBoolean("guide"));
        pointDto.setLanguage(jsonObject.optString("language"));
        pointDto.setLatitude(jsonObject.optString("latitude"));
        pointDto.setLongitude(jsonObject.optString("longitude"));
        pointDto.setShare(jsonObject.optBoolean("share"));
        pointDto.setTitle(jsonObject.optString("title"));
        pointDto.setType(jsonObject.optString("type"));
        pointDto.setUser_email(jsonObject.optString("user_email"));
        pointDto.setUser_id(jsonObject.optString("user_id"));
        pointDto.setUser_name(jsonObject.optString("user_name"));
        pointDto.setCountry(jsonObject.optString("country"));
        pointDto.setCity(jsonObject.optString("city"));
        pointDto.setState(jsonObject.optString("state"));
        pointDto.setPhoto(jsonObject.optString("photo"));
        pointDto.setAproved(jsonObject.optString("aproved"));
        pointDto.setCreatedAt(jsonObject.optString("createTime"));
        pointDto.setChildren(setChildren(jsonObject));



        return pointDto;
    }
    private List<String> setChildren(JSONObject jsonObject) {
        JSONArray childrensArray = jsonObject.optJSONArray("childrenPoints");

        Set<String> children = new HashSet<>();
        if (childrensArray != null) {
            for (int i = 0; i < childrensArray.length(); i++) {
                children.add(childrensArray.optString(i));
            }
        }

        return children.stream().collect(Collectors.toList());

    }


}
