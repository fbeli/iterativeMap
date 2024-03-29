package com.becb.processnewpoint.exception;

public class EmailException extends Exception{

    public EmailException(String emailDidNotSend, Exception e) {
        super(emailDidNotSend, e);
    }
}
