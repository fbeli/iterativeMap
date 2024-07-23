package com.becb.api.service;

import com.becb.api.controller.PointController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PointSupport {

    private PointSupport() {}
    public static HttpURLConnection getConnection(String urlConnection) throws IOException {

        URL url = new URL(urlConnection);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        return connection;
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
            Logger logger = LoggerFactory.getLogger(PointController.class);
            logger.error(e.getMessage());

            return null;
        }
    }
}
