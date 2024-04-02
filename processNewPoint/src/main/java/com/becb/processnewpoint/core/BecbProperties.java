package com.becb.processnewpoint.core;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class BecbProperties {

    public EmailProperties email;
    public SQSProperties sqs;
}

