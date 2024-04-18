package com.becb.api.controller;


import com.becb.api.dto.LoginDto;
import com.becb.api.dto.LoginResponse;
import com.becb.api.service.sqs.SqsService;
import com.becb.api.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    UserService userService;

    @Autowired
    SqsService sqsService;

    @Value("${sqs.queue.reset_password}")
    private String resetPasswordQueue;


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


    @PostMapping("/user/forget")
    @ResponseBody
    public LoginResponse forgetPassword(@RequestBody LoginDto requestLoginDto,  HttpServletResponse response) throws IOException {

        LoginDto loginDto =  userService.getUserByEmail(requestLoginDto.getEmail());
        LoginResponse loginResponse = new LoginResponse();

        if(loginDto.getName()!=null) {
            String message = "{ \"name\" : \"" + loginDto.getName() + "\", \"email\" : \"" + loginDto.getEmail() + "\" ," +
                    " \"user_id\" : \"" + loginDto.getUserId() + "\" }";

            sqsService.sendMessage(message, resetPasswordQueue);
            loginResponse.setStatus(response.getStatus());
        } else {
            logger.info("Email not found: {}", requestLoginDto.getEmail());
            loginResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            loginResponse.setError("email not found.");
        }


        return loginResponse;

    }

    @PutMapping("/user/reset_password")
    @ResponseBody
    public LoginResponse resetPassword(@RequestBody LoginDto requestLoginDto,  HttpServletResponse response) throws IOException {

        return userService.resetPassword(requestLoginDto);

    }

    @GetMapping("/token/{token}")
    @ResponseBody
    public LoginDto testToken(@PathVariable String token) throws IOException {
        return new LoginDto();
    }

    @PostMapping("/token")
    @ResponseBody
    public LoginDto postTestToken(@RequestBody String token) throws IOException {
        return new LoginDto();
    }

}
