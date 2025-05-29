package com.microservices.collection;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CollectionApplication {

	public static void main(String[] args) {
		SpringApplication.run(CollectionApplication.class, args);
	}

}
