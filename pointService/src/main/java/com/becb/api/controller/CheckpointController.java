package com.becb.api.controller;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping//( produces = "application/json;charset=UTF-8", consumes = "application/json;charset=UTF-8")
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.NONE)
public class CheckpointController {


    @GetMapping(value = "health")
    @ResponseBody
    public String health() {
        return "true";
    }
}