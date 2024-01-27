package com.becb.processnewpoint;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ProcessNewPointApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProcessNewPointApplication.class, args);
	}

}
