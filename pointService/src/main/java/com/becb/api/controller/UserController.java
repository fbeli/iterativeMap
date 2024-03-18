package com.becb.api.controller;


import com.becb.api.dto.LoginDto;
import com.becb.api.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    UserService userService;

    @GetMapping("/user/{id}")
    public ResponseEntity<LoginDto> getUser(@PathVariable String id) throws IOException {

        LoginDto loginDto;
        if(id.contains("@"))
            loginDto =  userService.getUserByEmail(id);
        else
            loginDto =  userService.getUserById(id);
        return new ResponseEntity<LoginDto>(loginDto, HttpStatus.valueOf(200));

    }

    @GetMapping("/user/email/{email}")
    public LoginDto getUserByEmail(@RequestParam String email) throws IOException {

        return userService.getUser(email);

    }


}
