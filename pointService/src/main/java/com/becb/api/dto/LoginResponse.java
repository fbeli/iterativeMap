package com.becb.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;


@Getter
@Setter

public class LoginResponse {
    private String token;
    private String error;
    private int status = 200;

}
