package com.becb.api.dto;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;


@Getter
@Setter
@Data
public class LoginResponse {
    private String token;
    private String error;
    private int status = 200;

    public void setupFromString(String json) {
        if(json != null && json.startsWith("{")) {
            JSONObject jsonObject = new JSONObject(json);


            this.status =  jsonObject.optInt("status");
            this.error = jsonObject.optString("message");
            this.token = jsonObject.optString("token");

        }
    }
}
