package com.becb.api.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginDto {
    private String email;
    private String password;

    private String country;

    private String phone;
    private String name;
}