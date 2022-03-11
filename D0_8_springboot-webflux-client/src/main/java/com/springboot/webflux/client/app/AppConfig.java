package com.springboot.webflux.client.app;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

//CLASE88
//AQUÍ SE CONFIGURA EL MICROSERVICIO CLIENTE QUE SE CONECTA CON OTRO MICROSERVICIO
//SE PONE LA RUTA DE CONEXIÓN AL MICROSERVICIO. ESTA ESTÁ EN EL APPLICATION.PROPERTIES.

@Configuration
public class AppConfig {

	@Bean
	public WebClient registrarWebClient() {
		return WebClient.create(url);
	}
	
	
	@Value("${config.base.endpoint}")
	private String url;
}
