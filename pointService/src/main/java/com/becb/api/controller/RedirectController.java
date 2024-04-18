package com.becb.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URI;
import java.net.URISyntaxException;

@Controller
public class RedirectController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @RequestMapping("/usermap/{insta}")
    public ResponseEntity<Object> redirectToExternalUrl(@PathVariable String insta) throws URISyntaxException {
        URI yahoo = new URI("https://www.guidemapper.com/users/" + insta +"/index.html");
        logger.info("Access from usermap/insta to user : {}",insta);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(yahoo);
        return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
    }

    //@RequestMapping("/{insta}")
    public ResponseEntity<Object> redirectToRoot(@PathVariable String insta) throws URISyntaxException {
        URI yahoo = new URI("https://www.guidemapper.com/users/" + insta +"/index.html");
        logger.info("Access from /insta to user : {}",insta);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(yahoo);
        return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
    }
}
