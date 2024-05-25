package com.becb.processnewpoint.controller;

import com.becb.processnewpoint.domain.Point;
import com.becb.processnewpoint.service.translate.TranslateService;
import com.becb.processnewpoint.service.translate.TranslateServiceInterface;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping//( produces = "application/json;charset=UTF-8", consumes = "application/json;charset=UTF-8")
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.NONE)
public class TranslateController {

    @Autowired
    TranslateService translateService;


    @GetMapping ( value = "/translate/")
    @ResponseBody
    public Point getByUser(@RequestParam String pointId, @RequestParam String language)  throws IOException {

        //return translateService.translate(pointId, language);
        return null;

    }

    @GetMapping ( value = "/hi/")
    @ResponseBody
    public String xx()  throws IOException {

        return "ok";

    }

}
