package com.becb.processnewpoint.domain;

import lombok.Data;

@Data
public class User {

    private String userId;
    private String userName;
    private String userEmail;
    private String instagram;
    private Boolean share;

    public String getInstagram(){
        if (instagram == null)
            return "";
        return instagram;
    }

}
