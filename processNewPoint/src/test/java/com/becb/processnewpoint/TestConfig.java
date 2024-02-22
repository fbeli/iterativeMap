package com.becb.processnewpoint;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.becb.processnewpoint.service.sqs.SqsConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.aws.messaging.config.SimpleMessageListenerContainerFactory;
import org.springframework.cloud.aws.messaging.listener.QueueMessageHandler;
import org.springframework.cloud.aws.messaging.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.mock;

@TestConfiguration
@EnableAutoConfiguration(exclude = {SqsConfiguration.class})
public class TestConfig {

    @Bean
    public SimpleMessageListenerContainer simpleMessageListenerContainer() {
        final SimpleMessageListenerContainerFactory factory = new SimpleMessageListenerContainerFactory();
        factory.setAutoStartup(false);
        factory.setAmazonSqs(amazonSQSAsync());
        final SimpleMessageListenerContainer simpleMessageListenerContainer = factory
                .createSimpleMessageListenerContainer();
        simpleMessageListenerContainer.setMessageHandler(messageHandler());
        return simpleMessageListenerContainer;
    }

    @Bean(name = "messageHandler")
    public QueueMessageHandler messageHandler() {
        return mock(QueueMessageHandler.class);
    }

    @Bean(name = "amazonSQSAsync")
    public AmazonSQSAsync amazonSQSAsync() {
        return mock(AmazonSQSAsync.class);
    }
}