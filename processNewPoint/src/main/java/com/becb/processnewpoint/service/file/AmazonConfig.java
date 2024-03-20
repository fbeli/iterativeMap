package com.becb.processnewpoint.service.file;


import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.becb.processnewpoint.storage.StorageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmazonConfig {

    @Autowired
    private StorageProperties storageProperties;

    @Bean
    public AWSCredentials AmazonCredential(){
        var credentials = new BasicAWSCredentials(storageProperties.getAccessKey(), storageProperties.getSecretKey());
        return credentials;
    }
}
