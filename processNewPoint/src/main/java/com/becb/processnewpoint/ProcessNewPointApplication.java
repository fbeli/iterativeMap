package com.becb.processnewpoint;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.becb.processnewpoint.service.MigrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.becb.processnewpoint.service.dynamodb.DynamoDbClient;

import javax.annotation.PostConstruct;
import java.io.IOException;


@EnableScheduling
@SpringBootApplication
public class ProcessNewPointApplication {


	@Value("${becb.sqs.access-key}")
	String sqsAccessKey;

	@Value("${becb.sqs.secret-key}")
	String sqsSecretKey;

	@Value("${becb.sqs.region}")
	String region;

	@Value("${env}")
	String env;

	public static void main(String[] args) {
		SpringApplication.run(ProcessNewPointApplication.class, args);
	}
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Value("${dynamodb.point.table}")
	private String pointTable;

	@Bean("dynamoDbClient")
	public DynamoDbClient getDynamoDbClient(@Value("${dynamodb.host}")String dynamoDbHost) {
		AmazonDynamoDBClient client ;


		if ( env.equals("docker") || env.equals("dev") ) {
			client = new AmazonDynamoDBClient();
			logger.info("DynamoDbHost: {}", dynamoDbHost);
			client.setEndpoint(dynamoDbHost);
		}else {
			final AWSCredentials credentials = new BasicAWSCredentials(sqsAccessKey, sqsSecretKey);
			final AWSCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(credentials);
			client = (AmazonDynamoDBClient) AmazonDynamoDBClient.builder().withCredentials(credentialsProvider).build();

		}
		DynamoDbClient dynamoDbClient = new DynamoDbClient();
		dynamoDbClient.setDynamoDB(new DynamoDB(client));
		dynamoDbClient.setPointTable(pointTable);
		return dynamoDbClient;
	}


	@Autowired
	MigrationService migrationService;
	//@PostConstruct
	public void migrate() throws IOException {
		migrationService.migrate();
	}
}
