package com.becb.processnewpoint.exception;

public class SQSMessageException extends Exception{

    public SQSMessageException(String SQSErrorExecption, Exception e) {
        super(SQSErrorExecption, e);
    }
}
