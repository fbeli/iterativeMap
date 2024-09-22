package com.becb.api.controller;

import com.becb.api.dto.PointDto;
import com.becb.api.service.PointService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Controller
public class RedirectController {


    @Value("${page.endpoint}")
    String pageEndpoint;



    private final Logger logger = LoggerFactory.getLogger(getClass());

    @RequestMapping("/usermap/{insta}")
    public ResponseEntity<Object> redirectToExternalUrl(@PathVariable String insta) throws URISyntaxException {
        URI yahoo = new URI("https://m.guidemapper.com/users/" + insta +"/index.html");
        logger.info("Access from usermap/insta to user : {}",insta);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(yahoo);
        return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
    }

    //@RequestMapping("/{insta}")
    public ResponseEntity<Object> redirectToRoot(@PathVariable String insta) throws URISyntaxException {
        URI usersUri = new URI("https://m.guidemapper.com/users/" + insta +"/index.html");
        logger.info("Access from /insta to user : {}",insta);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(usersUri);
        return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
    }

    @Autowired
    PointService pointService;
    @RequestMapping("/")
    public ResponseEntity<Object> startPage(@RequestParam(value = "point", defaultValue = "") String point) {
        PointDto pointDto;
        String redirectPage = pageEndpoint;

        if(!point.isBlank()){
            String zoom = "16.5";
            try {
                pointDto = pointService.getPointById(point);
                if(pointDto!=null) {
                    redirectPage = pageEndpoint + "#" + zoom + "/" + pointDto.getLatitude() + "/" + pointDto.getLongitude() + "/pointId=" + pointDto.getPointId();
                }
            } catch (IOException e) {
                logger.error("Point {} not fount.", point);
            }
        }

        URI start = UriComponentsBuilder.fromUriString(redirectPage).build().toUri();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(start);
        ResponseEntity response = new ResponseEntity<>(httpHeaders, HttpStatus.TEMPORARY_REDIRECT);

        return response;

}
/*
    @RequestMapping("/")
    public ResponseEntity<Object> startPage()  {

            String redirectPage = pageEndpoint;

            logger.info("redirecting to  page : {}", redirectPage);
        try {
            URI start = UriComponentsBuilder.fromUriString(redirectPage).build().toUri();
            logger.info("start");
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setLocation(start);

            ResponseEntity response = new ResponseEntity<>(httpHeaders, HttpStatus.TEMPORARY_REDIRECT);

            logger.info("Response Created: {}", response.toString());
            return response;
        } catch (Exception e) {
            logger.error("Exception:::: {}", e);
            throw new RuntimeException(e);
        }
    }*/

}
