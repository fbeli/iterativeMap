package com.becb.processnewpoint.service;


import org.json.JSONException;
import org.json.JSONObject;

public class SuportService {

    public static boolean isValid(String json) {
        try {
            new JSONObject(json);
        } catch (JSONException e) {
            return false;
        }
        return true;
    }
}
