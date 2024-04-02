package com.becb.processnewpoint.core;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties("becb.mail.aws")
public class EmailProperties {
    public String user;
    public String pass;
    public String smtp;
    public String port;
    public String remetente;
}
