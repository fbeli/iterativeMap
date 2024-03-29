package com.becb.api.dto;


import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

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
    private String code;

    public void setupFromString(String json) {
        if(json != null && json.startsWith("{")) {
            JSONObject jsonObject = new JSONObject(json);

           this.setUserId( jsonObject.optString("id"));
                this.setName(  jsonObject.optString("name"));
                this.setEmail(jsonObject.optString("email"));
                this.setBorn_date(jsonObject.optString("born_date"));
                this.setDescription(jsonObject.optString("description"));
                this.setGuide(jsonObject.optString("guide"));
                this.setPhone(jsonObject.optString("telefone"));
                this.setCountry(jsonObject.optString("country"));
                this.setInstagram(jsonObject.optString("instagram"));
                this.setShare(jsonObject.optString("share"));

        }
    }
    public void setDescription(String description){
        if(description.equals("Descript Yourself"))
            this.description = null;
        this.description = description;
    }
}