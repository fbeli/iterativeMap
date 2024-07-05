package com.becb.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartException;

import java.io.IOException;

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



}
