package com.becb.processnewpoint.core;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class BecbProperties {

    @Autowired
    public EmailProperties email;

    @Autowired
    public SQSProperties sqs;
}

