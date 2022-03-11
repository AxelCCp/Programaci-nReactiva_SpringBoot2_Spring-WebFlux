package com.springboot.webflux.client.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
@EnableEurekaClient
@SpringBootApplication
public class SpringbootWebfluxClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootWebfluxClientApplication.class, args);
	}

}
