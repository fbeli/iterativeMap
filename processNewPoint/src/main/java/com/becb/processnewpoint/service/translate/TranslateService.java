package com.becb.processnewpoint.service.translate;

import com.becb.processnewpoint.domain.LanguageEnum;
import com.becb.processnewpoint.domain.Point;
import com.becb.processnewpoint.service.SuportService;
import net.suuft.libretranslate.Language;
import net.suuft.libretranslate.Translator;
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
public class TranslateService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Value("${becb.rapidapi.url}")
    String url;

    @Value("${becb.rapidapi.host}")
    String host;

    @Value("${becb.rapidapi.key}")
    String key;

    @Autowired
    SuportService suporteService;

    private Language getLanguage(String language){
        if(language.equals("EN"))
            return Language.ENGLISH;
        if(language.equals("PT"))
            return Language.PORTUGUESE;
        if(language.equals("ES"))
            return Language.SPANISH;
        if(language.equals("DE"))
            return Language.DUTCH;
        if(language.equals("FR"))
            return Language.FRENCH;
        return null;
    }
    public Point translate(Point parentPoint, Point point, String languageDestino) throws IOException {

        Language languageTo = getLanguage(languageDestino);
        point.setLanguage(languageDestino);

        point.setTitle(Translator.translate(getLanguage(parentPoint.getLanguage().getValue()),
                                             languageTo,
                                             point.getTitle()));
        if(point.getDescription() == null)
            point.setDescription(point.getTitle());
        else{
            point.setDescription(Translator.translate(getLanguage(parentPoint.getLanguage().getValue()),
                    languageTo, point.getDescription()));
        }
        point.setPointParent(parentPoint);
        //TODO create audio
        parentPoint.addChildPoint(point);
        point.setAudio(null);


        return point;
    }

    public String translateText(String text, String languageDestino) throws IOException {

        HttpURLConnection connection = suporteService.getConnection(url+"text="+text+"&to="+languageDestino.toLowerCase());
        connection.setRequestProperty("X-RapidAPI-Key", key);
        connection.setRequestProperty("X-RapidAPI-Host", host);

        log.info("Connecting in Translate Endpoint, URL: {}", url);

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

            return getTranslateFromJason(response.toString(), languageDestino);
        }
       return "";
    }

    public String getTranslateFromJason(String str, String lang){
        if(str == null || str.isBlank())
            return "";
        JSONObject jsonObject = new JSONObject(str);
        JSONObject translatedTextObject = jsonObject.getJSONObject("translated_text");
        return translatedTextObject.getString(lang.toLowerCase());

    }

    public boolean canChildForThatLanguage(Point point, String language){
        if(point.getLanguage().equals(LanguageEnum.valueOf(language)))
            return false;

        return point.getChildrenPoints().stream()
                .filter(p-> p.getLanguage().getValue().equals(LanguageEnum.valueOf(language).getValue()))
                .count() > 0 ? false:  true;


    }
}
