package com.springboot.webflux.client.app;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

//CLASE88
//AQUÍ SE CONFIGURA EL MICROSERVICIO CLIENTE QUE SE CONECTA CON OTRO MICROSERVICIO
//SE PONE LA RUTA DE CONEXIÓN AL MICROSERVICIO. ESTA ESTÁ EN EL APPLICATION.PROPERTIES.

//CLASE102 : SE HACEN ALGUNAS MODIFICACIONES PARA EL BALANCEO DE CARGA.
	//SE CAMBIA EL TIPO A WebClient.Builder.
	//SE HACEN CAMBIOS EN LA RUTA config.base.endpoint EN EL APPLICATION.PROPERTIES. SE CAMBIA EL LOCALHOST POR EL NOMBRE DEL SERVICIO APIREST.
	//SE HACE CAMBIO EN PRODUCTOSERVICEIMPL DE ESTE MICROSERVICIO. SE CAMBIA EL TIPO DEL BEAN WebClient A WebClient.Builder. TAMBN SE AGREGÓ EL MÉTODO BUILD DESPUES DE CADA BEAN "CLIENT" DE WebClient.Builder. 

@Configuration
public class AppConfig {
	
	/*
	@Bean
	public WebClient registrarWebClient() {
		return WebClient.create(url);
	}*/
	
	//CLASE102
	@Bean
	@LoadBalanced
	public WebClient.Builder registrarWebClient() {
		return WebClient.builder().baseUrl(url);
	}
	
	
	@Value("${config.base.endpoint}")
	private String url;
}
