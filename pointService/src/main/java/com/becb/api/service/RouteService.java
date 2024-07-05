package com.becb.api.service;

import org.apache.http.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.List;

@Service
public class RouteService {

    private final Logger log = LoggerFactory.getLogger(getClass());
    @Value("${newpoint.server.url}")
    String newPointServerUrl;

    public String getRoute(int page,
                                           int size,
                                           String instagram,
                                           String title,
                                           String city) throws Exception {

        String url = newPointServerUrl + "/routes";
        url+="?page="+page+"&size="+size+"&instagram="+instagram+"&title="+title+"&city="+city;
        HttpURLConnection connection = Support.getConnection(url);
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json");


        log.info("Connecting to GetRoute , URL: {}", url);

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

            return response.toString();
        }
        return null;

    }
        public String getQuery(List<NameValuePair> params) throws Exception
        {
            StringBuilder result = new StringBuilder();
            boolean first = true;

            for (NameValuePair pair : params) {
                if (first){
                    first = false;
                    result.append("?");
                }else
                    result.append("&");

                result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
            }

            return result.toString();
        }



}
