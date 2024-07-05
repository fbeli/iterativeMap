package com.becb.api.controller;

import com.becb.api.service.RouteService;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping//( produces = "application/json;charset=UTF-8", consumes = "application/json;charset=UTF-8")
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.NONE)
public class RouterController {


    @Autowired
    RouteService routeService;

    @GetMapping(value = "/routes")
    @ResponseBody
    public ResponseEntity<Object> getRoutes(@RequestParam(value = "page", defaultValue = "0") int page,
                                                @RequestParam(value = "size", defaultValue = "10") int size,
                                                @RequestParam(value ="instagram" ,  defaultValue = "") String instagram,
                                                @RequestParam(value ="title",  defaultValue = "") String title,
                                                @RequestParam(value ="city",  defaultValue = "") String city) throws Exception {




        return new ResponseEntity<>(routeService.getRoute(page, size, instagram, title, city), HttpStatus.valueOf(200));

    }

}