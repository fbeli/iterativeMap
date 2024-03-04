package com.becb.api.controller;


import com.becb.api.dto.LoginDto;
import com.becb.api.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    UserService userService;

    @GetMapping("/user/{id}")
    public LoginDto getUser(@PathVariable String id) throws IOException {


        LoginDto loginDto = userService.getUser(id);

        return loginDto;

    }

}
