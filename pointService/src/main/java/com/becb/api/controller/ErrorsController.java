package com.becb.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@ControllerAdvice
public class ErrorsController {


    private final Logger log = LoggerFactory.getLogger(getClass());
    @ResponseBody
    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public String handleHttpMediaTypeNotAcceptableException() {
        return "acceptable MIME type:" + MediaType.APPLICATION_JSON_VALUE;
    }

    @ResponseBody
    @ExceptionHandler(MultipartException.class)
    public ResponseEntity handleFileUploadingError(MultipartException exception) {
        log.warn("Failed to upload attachment", exception);
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

    }


    @ResponseBody
    @ExceptionHandler(IOException.class)
    public ResponseEntity handleFileUploadingError(IOException exception) {
        log.warn("IOException error: {}", exception);
        return new ResponseEntity<>(exception.getCause(), HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @ResponseBody
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity runtimeExceptionError(IOException exception) {
        log.warn("IOException error: {}", exception);
        return new ResponseEntity<>(exception.getCause(), HttpStatus.INTERNAL_SERVER_ERROR);

    }

        @ResponseBody
        @ExceptionHandler({HttpServerErrorException.class, HttpStatusCodeException.class})
        public ResponseEntity HttpServerErrorExceptionError(HttpServerErrorException exception) {
            log.warn("HttpServerErrorException error: {}", exception);
            return new ResponseEntity<>(exception.getCause(), HttpStatus.INTERNAL_SERVER_ERROR);

        }


    @ResponseBody
    @ExceptionHandler({URISyntaxException.class})
    public ResponseEntity URISyntaxExceptionErrorExceptionError(URISyntaxException exception) {
        log.warn("URISyntaxException error: {}", exception);
        return new ResponseEntity<>(exception.getCause(), HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @Value("${page.endpoint}")
    String pageEndpoint;

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity handleNoHandlerFoundException(
            NoHandlerFoundException ex, HttpServletRequest httpServletRequest) {
        System.out.println("No handler found exception");

        URI start = UriComponentsBuilder.fromUriString(pageEndpoint).build().toUri();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(start);
        return new ResponseEntity<>(httpHeaders, HttpStatus.TEMPORARY_REDIRECT);
    }
        @ResponseBody
        @ExceptionHandler({Exception.class})
        public ResponseEntity ErrorExceptionError(Exception exception) {
            log.warn("Exception error: {}", exception);
            return new ResponseEntity<>(exception.getCause(), HttpStatus.INTERNAL_SERVER_ERROR);

        }






}
