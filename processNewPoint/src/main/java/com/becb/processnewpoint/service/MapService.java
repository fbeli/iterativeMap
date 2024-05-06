package com.becb.processnewpoint.service;

import com.becb.api.dto.PointDto;
import com.becb.processnewpoint.domain.Point;
import com.becb.processnewpoint.dto.PlaceDto;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

@Service
@Setter
public class MapService {


    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${becb.mapbox.geocode.url}")
    private String geocodeUrl;

    @Value("${becb.mapbox.token}")
    private String token;

    @Autowired
    SuportService suporteService;

    public Point setPlace(Point point )  {
        String url = geocodeUrl + "&longitude=" + point.getLongitude() + "&latitude=" + point.getLatitude();
        //String response
        try {

            HttpURLConnection connection = suporteService.getConnection(url);
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                configPoint(response.toString(), point);

            }
        } catch (IOException e) {
            logger.error("Error getting place:  {} ", e.getMessage());
            logger.error("trying connect with:  {} ", url);

        }
        finally {
            return point;
        }
    }

    private void configPoint(String json, Point point) {
       PlaceDto placeDto = getPlaceDto(json);
       if(placeDto != null){
               if(placeDto.getState() != null && !placeDto.getState().isEmpty())
                    point.setState(placeDto.getState());
               if (placeDto.getCountry() != null && !placeDto.getCountry().isEmpty())
                    point.setCountry(placeDto.getCountry());
                if(placeDto.getCity() != null && !placeDto.getCity().isEmpty())
                    point.setCity(placeDto.getCity());
        }
    }

    private PlaceDto getPlaceDto(String json) {

        if (json != null && json.startsWith("{")) {
            JSONObject jsonObject = new JSONObject(json);

            JSONArray featuresArray = jsonObject.getJSONArray("features");


            JSONObject featureObject = featuresArray.getJSONObject(0);
            JSONObject propertiesObject = featureObject.getJSONObject("properties");
            JSONObject contextObject = propertiesObject.getJSONObject("context");

            String region = contextObject.getJSONObject("region").getString("name");
            String country = contextObject.getJSONObject("country").getString("name");
            String place = contextObject.getJSONObject("place").getString("name");

            return new PlaceDto(country, region, place);
        }

        return null;
    }
}
