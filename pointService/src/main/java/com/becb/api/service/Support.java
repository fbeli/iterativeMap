package com.becb.api.service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class Support {

    public static HttpURLConnection getConnection(String urlConnection) throws IOException {

        URL url = new URL(urlConnection);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        return connection;
    }
}
