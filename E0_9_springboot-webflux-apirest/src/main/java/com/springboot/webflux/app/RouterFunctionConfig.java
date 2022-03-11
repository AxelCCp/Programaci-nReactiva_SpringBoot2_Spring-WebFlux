package com.springboot.webflux.app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import com.springboot.webflux.app.handler.ProductoHandler;



//ServerResponse :SE PARECE AL ResponseEntity, PERO ES 100% REACTIVO.


@Configuration
public class RouterFunctionConfig {

	@Bean
	public RouterFunction<ServerResponse>routes(ProductoHandler handler){
		return RouterFunctions.route(RequestPredicates.GET("/api/v2/productos")
				.or(RequestPredicates.GET("/api/v3/productos")), request-> handler.listar(request))
				.andRoute(RequestPredicates.GET("/api/v2/productos/{id}"), request2-> handler.ver(request2))
				.andRoute(RequestPredicates.POST("/api/v2/productos"), request3-> handler.crear(request3))
				.andRoute(RequestPredicates.PUT("/api/v2/productos/{id}"), request4-> handler.editar(request4))
				.andRoute(RequestPredicates.DELETE("/api/v2/productos/{id}"), request5-> handler.eliminar(request5))
				.andRoute(RequestPredicates.POST("/api/v2/productos/upload/{id}"), request6-> handler.upload(request6))
				.andRoute(RequestPredicates.POST("/api/v2/productos/crear"), request7-> handler.crearConFoto(request7));
	}

}
