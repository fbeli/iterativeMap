package com.becb.processnewpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.becb.processnewpoint.service.dynamodb.DynamoDbClient;


@EnableScheduling
@SpringBootApplication
public class ProcessNewPointApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProcessNewPointApplication.class, args);
	}
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Value("${dynamodb.point.table}")
	private String pointTable;
	@Bean("dynamoDbClient")
	public DynamoDbClient getDynamoDbClient(//@Value("${spring.profiles.active}")String activeProfile,
											@Value("${dynamodb.host}")String dynamoDbHost) {
		AmazonDynamoDBClient client = new AmazonDynamoDBClient();

		logger.info("DynamoDbHost: {}", dynamoDbHost);
		//TODO: created docker, need check it to run.
		//if ("local".equals(activeProfile) || "docker".equals(activeProfile)) { //Is it correct?
			client.setEndpoint(dynamoDbHost);
//		}
		DynamoDbClient dynamoDbClient = new DynamoDbClient();
		dynamoDbClient.setDynamoDB(new DynamoDB(client));
		dynamoDbClient.setPointTable(pointTable);
		//dynamoDbClient.setReconAuditTable(reconAuditTable);
		return dynamoDbClient;
	}
}
