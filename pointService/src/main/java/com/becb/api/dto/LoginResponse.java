package com.becb.api.dto;


import lombok.Getter;
import lombok.Setter;



@Getter
@Setter
public class LoginResponse {
    private String token;
    private String error;
    private int status = 200;

}
