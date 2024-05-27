package com.becb.processnewpoint.service.sqs;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.connection.CachingConnectionFactory;

import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import org.springframework.jms.annotation.EnableJms;


import com.amazon.sqs.javamessaging.ProviderConfiguration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;

import javax.jms.Session;

@Configuration
@EnableJms
public class SqsConfiguration {

    @Value("${becb.sqs.region}")
    private String region;

    @Value("${becb.sqs.access-key}")
    private String accessKey;

    @Value("${becb.sqs.secret-key}")
    private String secretKey;


    @Value("${sqs.queue.url}")
    String queueEndpoint;

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
        SQSConnectionFactory connectionFactory;
                if(System.getenv("ENVIRONMENT").toLowerCase().trim().equals("dev")) {
                    connectionFactory = SQSConnectionFactory.builder()
                            .withRegion(Region.getRegion(Regions.EU_CENTRAL_1))
                            .withEndpoint(queueEndpoint)
                            .withAWSCredentialsProvider(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                            .withNumberOfMessagesToPrefetch(0)
                            .build();
                }else {
                    connectionFactory = SQSConnectionFactory.builder()
                            .withRegion(Region.getRegion(Regions.EU_CENTRAL_1))
                            .withAWSCredentialsProvider(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                            .withNumberOfMessagesToPrefetch(0)
                            .build();
                }

        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
        cachingConnectionFactory.setTargetConnectionFactory(connectionFactory);
        cachingConnectionFactory.setReconnectOnException(true);

        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        //factory.setErrorHandler(getErrorHandler());
        factory.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
        return factory;
    }

    @Bean
    public AmazonSQSAsync amazonSQS() {

            final AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
            final AWSStaticCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(credentials);
            return AmazonSQSAsyncClientBuilder.standard()
                    .withCredentials(credentialsProvider)
                    .withRegion(region)
                    //.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, region))
                    .build();

    }

}