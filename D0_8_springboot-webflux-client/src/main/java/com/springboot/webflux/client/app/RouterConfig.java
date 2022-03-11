package com.springboot.webflux.client.app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.springboot.webflux.client.app.handler.ProductoHandler;


//CLASE92

@Configuration
public class RouterConfig {

	@Bean
	public RouterFunction<ServerResponse>rutas(ProductoHandler handler){
		return RouterFunctions
				.route(RequestPredicates.GET("/api/client"), request1 -> handler.listar(request1))
				.andRoute(RequestPredicates.GET("/api/client/{id}"), request2 -> handler.ver(request2))
				.andRoute(RequestPredicates.POST("/api/client"), request3 -> handler.crear(request3))
				.andRoute(RequestPredicates.PUT("/api/client/{id}"), request4 -> handler.editar(request4))
				.andRoute(RequestPredicates.DELETE("/api/client/{id}"), request5 -> handler.eliminar(request5))
				.andRoute(RequestPredicates.POST("/api/client/upload/{id}"), request6 -> handler.upload(request6));
	}
	
}
