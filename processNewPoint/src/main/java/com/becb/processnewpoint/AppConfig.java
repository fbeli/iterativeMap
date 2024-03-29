package com.becb.processnewpoint;

import com.becb.processnewpoint.service.email.SendEmailService;
import com.becb.processnewpoint.service.email.SendEmailServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean("sendEmailService")
    public SendEmailService sendEmailService() {
        return new SendEmailServiceImpl();
    }
}
