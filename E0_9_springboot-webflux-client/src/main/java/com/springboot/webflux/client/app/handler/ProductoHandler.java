package com.springboot.webflux.client.app.handler;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.springboot.webflux.client.app.models.Producto;
import com.springboot.webflux.client.app.models.services.IProductoService;
import reactor.core.publisher.Mono;

//CLASE92
	//1.- ServerResponse.ok().contentType(null) : DEVUELVE UN STATUS 200. ESTE CONTENT TYPE ES DE LA RESPUESTA.
//CLASE97
	//* SE COMENTA return ServerResponse.notFound().build(); , PARA AGREGAR MENSAJE PERSONALIZADO EN EL ERROR.
	//2.- PARA ESTO SE PONE UN MAP COMO BOBY.
@Component
public class ProductoHandler {

	public Mono<ServerResponse>listar(ServerRequest request){
		//1
		return ServerResponse.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(productoService.findAll(),Producto.class);	
	}
	
	
	public Mono<ServerResponse>ver(ServerRequest request){
		String id = request.pathVariable("id");
		
		return productoService.findById(id).flatMap(p -> ServerResponse.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromValue(p)))
				//.syncBody(p))
				.switchIfEmpty(ServerResponse.notFound().build())
				
				.onErrorResume(error-> {
					WebClientResponseException errorResponse = (WebClientResponseException) error;
					if(errorResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
						//return ServerResponse.notFound().build();
						//2
						Map<String,Object>body = new HashMap<>();
						body.put("error", "No existe el producto según el id ".concat(errorResponse.getMessage()));
						body.put("timestamp",new Date());
						body.put("status", errorResponse.getStatusCode().value());
						return ServerResponse.status(HttpStatus.NOT_FOUND).body(BodyInserters.fromValue(body));
					}
					//SI EL ERROR NO ES BAD REQUEST, LANZA EL ERROR QUE SEA.
					return Mono.error(errorResponse);
			});
	}
	
	
	public Mono<ServerResponse>crear(ServerRequest request){
		Mono<Producto> producto = request.bodyToMono(Producto.class);
		return producto.flatMap(p-> {
			if(p.getCreateAt()==null) {
				p.setCreateAt(new Date());
			}
			return productoService.save(p);
		}).flatMap(p-> ServerResponse
				.created(URI.create("/api/client/".concat(p.getId())))
				.contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromValue(p)))
			//SE MANEJA EL ERROR EN CASO DE UN BAD REQUEST AL CREAR EL PRODUCTO.
			.onErrorResume(error-> {
					WebClientResponseException errorResponse = (WebClientResponseException) error;
					if(errorResponse.getStatusCode() == HttpStatus.BAD_REQUEST) {
						return ServerResponse.badRequest()
								.contentType(MediaType.APPLICATION_JSON)
								.body(BodyInserters.fromValue(errorResponse.getResponseBodyAsString()));
					}
					//SI EL ERROR NO ES BAD REQUEST, LANZA EL ERROR QUE SEA.
					return Mono.error(errorResponse);
			});
	}
	
	
	public Mono<ServerResponse>editar(ServerRequest request){
		Mono<Producto> producto = request.bodyToMono(Producto.class);
		String id = request.pathVariable("id");
		
		return producto
				.flatMap(p-> productoService.update(p, id))
				.flatMap(p-> ServerResponse
					.created(URI.create("/api/client/".concat(p.getId())))
					.contentType(MediaType.APPLICATION_JSON)
					//AQUÍ SE USA UN BODY QUE GUARDA UN PUBLISHER. PQ SE USARÁ EL PRODUCTOSERVICE PARA MODIFICAR EL PRODUCTO. (SE HICIERON UN PAR DE CAMBIOS EN LA CLASE 97, POR ESO QUE YA NO VA LO QUE ESTÁ  COMENTADO.)
					//.body(productoService.update(p, id),Producto.class));	
					.body(BodyInserters.fromValue(p)))
				
				.onErrorResume(error-> {
					WebClientResponseException errorResponse = (WebClientResponseException) error;
					if(errorResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
						return ServerResponse.notFound().build();
					}
					//SI EL ERROR NO ES NOT FOUND, LANZA EL ERROR QUE SEA.
					return Mono.error(errorResponse);
			});
	}
	
	public Mono<ServerResponse>eliminar(ServerRequest request){
		String id = request.pathVariable("id");
		return productoService.delete(id).then(ServerResponse.noContent().build())
				
				.onErrorResume(error-> {
					WebClientResponseException errorResponse = (WebClientResponseException) error;
					if(errorResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
						return ServerResponse.notFound().build();
					}
					//SI EL ERROR NO ES  NOT FOUND, LANZA EL ERROR QUE SEA.
					return Mono.error(errorResponse);
			});
	}
	
	//CLASE96
	//1.-SE ONTIENE EL MULTIPART Y CON MAP() SE HACE UN CAST A FILEPART. "multipart" ES UN OBSERVABLE Y CON toSingleValueMap().get("file") SE OBTIENE EL FILE. Y LUEGO ESTE FILE SE PASA A FILEPART.
	//2.-flatMap(): EN ESTE MÉTODO SE EMITE EL ARCHIVO FILE IMAGEN QUE SE TIENE QUE SUBIR. 
	//3.-flatMap(): PARA CONVERTIR EL PRODUCTO A UN MONO<SERVERRESPONSE>
	public Mono<ServerResponse> upload(ServerRequest request){
		String id = request.pathVariable("id");
		//1
		return request.multipartData().map(multipart -> multipart.toSingleValueMap().get("file"))
				.cast(FilePart.class)
				//2
				.flatMap(file-> productoService.upload(file, id))
				//3
				.flatMap(p-> ServerResponse
						.created(URI.create("/api/client/".concat(p.getId())))
						.contentType(MediaType.APPLICATION_JSON)
						.body(BodyInserters.fromValue(p)))
				
				.onErrorResume(error-> {
					WebClientResponseException errorResponse = (WebClientResponseException) error;
					if(errorResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
						return ServerResponse.notFound().build();
					}
					//SI EL ERROR NO ES BAD REQUEST, LANZA EL ERROR QUE SEA.
					return Mono.error(errorResponse);
			});
	}
	
	
	@Autowired
	private IProductoService productoService;
}
