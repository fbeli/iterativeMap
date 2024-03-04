package com.becb.api.dto;


import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

@Getter
@Setter
public class LoginDto {
    private String email;
    private String password;
    private String country;
    private String phone;
    private String name;
    private String userId;
    private String guide;
    private String born_date;
    private String instagram;
    private String share;
    private String description;

    public void setupFromString(String json) {

        JSONObject jsonObject = new JSONObject(json);

        if(jsonObject.get("share") != null || !jsonObject.get("share").toString().equals("null")) {
            this.setEmail(jsonObject.get(email).toString());
        }
    }
    public void setDescription(String description){
        if(description.equals("Descript Yourself"))
            this.description = null;
        this.description = description;
    };
}