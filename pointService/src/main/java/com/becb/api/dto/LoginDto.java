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

    public void setupFromString(String json) {
        if(json != null && json.startsWith("{")) {
            JSONObject jsonObject = new JSONObject(json);

            if (jsonObject.has("share")  && !jsonObject.get("share").toString().equals("null")) {
                this.setName( jsonObject.has("name") ? jsonObject.get("name").toString() : "");
                this.setEmail(jsonObject.has("email") ?jsonObject.get("email").toString(): "");
                this.setBorn_date(jsonObject.has("born_date")  ?jsonObject.get("born_date").toString(): "");
                this.setDescription(jsonObject.has("description")  ?jsonObject.get("description").toString(): "");
                this.setGuide(jsonObject.has("guide") ?jsonObject.get("guide").toString(): "");
                this.setPhone(jsonObject.has("telefone") ?jsonObject.get("telefone").toString(): "");
                this.setCountry(jsonObject.has("country")  ?jsonObject.get("country").toString(): "");
                this.setInstagram(jsonObject.has("instagram") ?jsonObject.get("instagram").toString(): "");
                this.setShare(jsonObject.has("share") ?jsonObject.get("share").toString(): "");
            }
        }
    }
    public void setDescription(String description){
        if(description.equals("Descript Yourself"))
            this.description = null;
        this.description = description;
    }
}