package com.becb.processnewpoint.service;


import com.becb.processnewpoint.domain.LanguageEnum;
import com.github.f4b6a3.ulid.Ulid;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SuportService {

    public static boolean isValid(String json) {
        try {
            new JSONObject(json);
        } catch (JSONException e) {
            return false;
        }
        return true;
    }

    public static LanguageEnum getLanguage(String language){
        language = language.toUpperCase();
        switch (language) {
            case "ENGLISH":
            case "EN":
                return  LanguageEnum.EN;

            case "ESPANHOL":
            case "ESPAÑOL":
            case "SPANISH":
            case "SP":
            case "ES":
               return LanguageEnum.ES;



            default:
               return LanguageEnum.PT;

        }

    }
    public static String unicodeEscapeToUtf8(String unicodeEscapeString) {
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
            e.printStackTrace();
            return null;
        }
    }

    public static String getCode(){
        Random gerador=  new Random();

        String resp = Integer.toString(gerador.nextInt(9999));
        while (resp.length() < 4) {
            resp = "0" + resp;
        }
        return resp;
    }
    public static LocalDateTime ulidToLocalDateTime(String ulidString) {
        //String ulid = "01HTX5G45H9BACTSAPYTRXF8NC";

        long time = Ulid.getTime(ulidString);

        return LocalDateTime.ofInstant(Instant.ofEpochMilli(time),
                TimeZone.getDefault().toZoneId());
    }
    private static final Pattern ULID_PATTERN = Pattern.compile("^[0-9A-Fa-f]{10}-?[0-9A-Fa-f]{1,13}(-[0-9A-Fa-f]{1,13})?$");

    public static boolean isValidUlid(String ulid) {
        return ULID_PATTERN.matcher(ulid).matches();
    }

    public  HttpURLConnection getConnection(String fullUrl) throws IOException {
        URL url = new URL(fullUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        return connection;
    }
}
