package com.becb.processnewpoint.service.sqs;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.config.QueueMessageHandlerFactory;
import org.springframework.cloud.aws.messaging.listener.QueueMessageHandler;
import org.springframework.cloud.aws.messaging.listener.SimpleMessageListenerContainer;
import org.springframework.cloud.aws.messaging.support.NotificationMessageArgumentResolver;
import org.springframework.cloud.aws.messaging.support.converter.NotificationRequestConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.CompositeMessageConverter;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.converter.SimpleMessageConverter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SqsConfiguration {

    private final ObjectMapper objectMapper;

    @Value("${sqs.queue.url}")
    String endpoint;

    @Value("${becb.sqs.access-key}")
    String sqsAccessKey;

    @Value("${becb.sqs.secret-key}")
    String sqsSecretKey;

    @Value("${becb.sqs.region}")
    String region;

    @Value("${env}")
    String env;

    @Bean
    public AmazonSQSAsync amazonSQS() {
        if (env.equals("docker") || env.equals("dev")) {
            return AmazonSQSAsyncClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(new AnonymousAWSCredentials()))
                    .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, "elasticmq"))
                    .build();
        } else {
            final AWSCredentials credentials = new BasicAWSCredentials(sqsAccessKey, sqsSecretKey);
            final AWSStaticCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(credentials);
            return AmazonSQSAsyncClientBuilder.standard()
                    .withCredentials(credentialsProvider)
                    //.withRegion(region)
                    .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, region))
                    .build();
        }
    }

    @Bean
    public QueueMessageHandler queueMessageHandler() {
        QueueMessageHandlerFactory queueMessageHandlerFactory = new QueueMessageHandlerFactory();
        queueMessageHandlerFactory.setAmazonSqs(amazonSQS());

        MappingJackson2MessageConverter messageConverter = new MappingJackson2MessageConverter();
        messageConverter.setSerializedPayloadClass(String.class);
        messageConverter.setObjectMapper(objectMapper);

        messageConverter.setStrictContentTypeMatch(false);

        List<MessageConverter> messageConverterList = new ArrayList<>();
        messageConverterList.add(messageConverter);
        messageConverterList.add(new NotificationRequestConverter(messageConverter));
        messageConverterList.add(new SimpleMessageConverter());

        CompositeMessageConverter compositeMessageConverter = new CompositeMessageConverter(messageConverterList);

        queueMessageHandlerFactory.setArgumentResolvers(Collections.singletonList(new NotificationMessageArgumentResolver(compositeMessageConverter)));
        QueueMessageHandler queueMessageHandler = queueMessageHandlerFactory.createQueueMessageHandler();
        return queueMessageHandler;
    }

    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(10);
        executor.initialize();
        return executor;
    }

    @Bean
    public SimpleMessageListenerContainer simpleMessageListenerContainer(QueueMessageHandler queueMessageHandler) {
        SimpleMessageListenerContainer simpleMessageListenerContainer = new SimpleMessageListenerContainer();
        simpleMessageListenerContainer.setAmazonSqs(amazonSQS());
        simpleMessageListenerContainer.setMessageHandler(queueMessageHandler);
        simpleMessageListenerContainer.setMaxNumberOfMessages(10);
        simpleMessageListenerContainer.setTaskExecutor(threadPoolTaskExecutor());
        return simpleMessageListenerContainer;
    }
}