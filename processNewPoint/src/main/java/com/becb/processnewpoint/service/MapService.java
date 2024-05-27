package com.becb.processnewpoint.service;

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


    @Value("${becb.mapbox.geocode.url}")
    private String geocodeUrl;

    @Value("${becb.mapbox.token}")
    private String token;

    @Autowired
    SuportService suporteService;
    Logger logger = LoggerFactory.getLogger(getClass());

    public Point setPlace(Point point )  {
        Logger logger = LoggerFactory.getLogger(getClass());
        logger.info("Getting Country to point: {} long:  {}  lat: {}", point.getTitle(), point.getLongitude(), point.getLatitude());

        String url = geocodeUrl + "&longitude=" + point.getLongitude() + "&latitude=" + point.getLatitude();
        logger.info("resquest endpoint: \n {}", url);
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

            }else{
                logger.info("Impossible find country to point: {}", point.getTitle());
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
       if(placeDto != null && placeDto.getCountry() != null && !placeDto.getCountry().isEmpty()){
               if(placeDto.getState() != null && !placeDto.getState().isEmpty())
                    point.setState(placeDto.getState());
               if (placeDto.getCountry() != null && !placeDto.getCountry().isEmpty())
                    point.setCountry(placeDto.getCountry());
                if(placeDto.getCity() != null && !placeDto.getCity().isEmpty())
                    point.setCity(placeDto.getCity());
        }else{

            logger.info("Impossible find place to {} coordenate {} {} in json: {} to ", point.getTitle(), point.getLatitude(), point.getLongitude(), json);
        }
    }

    private PlaceDto getPlaceDto(String json) {

        if (json != null && json.startsWith("{")) {
            JSONObject jsonObject = new JSONObject(json);

            JSONArray featuresArray = jsonObject.getJSONArray("features");

            if(featuresArray.length() > 0) {
                JSONObject featureObject = featuresArray.getJSONObject(0);
                JSONObject propertiesObject = featureObject.getJSONObject("properties");
                JSONObject contextObject = propertiesObject.getJSONObject("context");

                String region = (contextObject.opt("region")!=null?contextObject.getJSONObject("region").getString("name"):"");
                String country = (contextObject.opt("country")!=null?contextObject.getJSONObject("country").getString("name"):"");
                String place = (contextObject.opt("place")!=null?contextObject.getJSONObject("place").getString("name"):"");

                return new PlaceDto(country, region, place);
            }

        }

        return null;
    }
}
