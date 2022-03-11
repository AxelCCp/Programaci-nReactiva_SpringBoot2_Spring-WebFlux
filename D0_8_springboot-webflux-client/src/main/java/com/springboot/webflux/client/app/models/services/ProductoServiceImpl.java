package com.springboot.webflux.client.app.models.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.springboot.webflux.client.app.models.Producto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//CLASE90
//1.0-client.get().accept(MediaType.APPLICATION_JSON) : 
		//GET(): ESPECIFICA QUE ES UNA PETICIÓN DEL TIPO GET.
		//LA URI YA ESTÁ ESPECIFICADA EN EL APPLICATION.PROPERTIES.
		//ACCEPT(): SE ESPECIFICA QUE SE ACEPTARÁ UN TIPO DETERMINADO.  DEFINE EL TIPO DE CONTENIDO QUE SE ACEPTA EN EL RESPONSE.
		//EXCHAGE()  : SE ENVIA LA REQUEST.
//1.1.-AHORA SE RECIBEN LOS DATOS:
		//flatMapMany() : COMO FINDALL RETORNA UN FLUX<PRODUCTO>, SE USA flatMapMany(), YA QUE SE RECIBIRÁN VARIOS ELEMENTOS EN UN FLUX.
		//response -> response.bodyToFlux(Producto.class):TODOS ESTOS ELEMENTOS QUE SE RECIBEN DESDE EL OTRO MICROSERVICIO AL HABERLE HECHO EL REQUEST SE PASAN AHORA AL RESPONSE CON bodyToFlux(Producto.class) ...COMO UN FLUX DE PRODUCTO.
//2.0.-.retrieve().bodyToMono(Producto.class); EN REMPLAZO DE   .exchange().flatMapMany(response -> response.bodyToFlux(Producto.class));
//CLASE91
//3.0.-accept(MediaType.APPLICATION_JSON) : DEFINE EL TIPO DE CONTENIDO QUE SE ACEPTA EN EL RESPONSE.
//3.1.-contentType(MediaType.APPLICATION_JSON) : ES EL TIPO DE CONTENIDO QUE SE ESTÁ ENVIANDO EN EL REQUEST AL GUARDAR EL PRODUCTO.
//3.2.-body(BodyInserters.fromObject(producto)): SE PASA EL PRODUCTO AL BODY DEL REQUEST. CAMBIO fromObject POR fromValue, YA QUE ESTÁ DEPRECATED.
//3.3.-retrieve().bodyToMono(Producto.class) : SE EJECUTA EL EXCHANGE POR DEBAJO Y SE PASA EL BODY A MONO DE PRODUCTO.
//4
//5.-exchange().then() : SE MANDA EL REQUEST CON EL EXCHANGE Y CON EL THEN RETORNA UN MONO<VOID> .
	//.retrieve().bodyToMono(Void.class) : ES UNA ALTERNATIVA A exchange().then().


@Service
public class ProductoServiceImpl implements IProductoService {
	
	//1
	@Override
	public Flux<Producto> findAll() {
		//1
		return  client.get().accept(MediaType.APPLICATION_JSON)
				//.exchange()
				//.flatMapMany(response -> response.bodyToFlux(Producto.class));
				.retrieve().bodyToFlux(Producto.class);
	}

	//2
	@Override
	public Mono<Producto> findById(String id) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("id", id);
		return client.get().uri("/{id}",params).accept(MediaType.APPLICATION_JSON)
				//.exchange()
				//.flatMap(response -> response.bodyToMono(Producto.class));
				//2.0
				.retrieve().bodyToMono(Producto.class);
	}
	
	//3
	@Override
	public Mono<Producto> save(Producto producto) {
		return client.post()
		//3.0
		.accept(MediaType.APPLICATION_JSON)
		//3.1
		.contentType(MediaType.APPLICATION_JSON)
		//3.2
		.body(BodyInserters.fromValue(producto))		
		//3.3
		.retrieve()
		.bodyToMono(Producto.class);
	}
	
	//4
	@Override
	public Mono<Producto> update(Producto producto, String id) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("id", id);
		return client.put()
			.uri("/{id}",params)
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON)
			.body(BodyInserters.fromValue(producto))		
			.retrieve()
			.bodyToMono(Producto.class);
	}

	//5
	@Override
	public Mono<Void> delete(String id) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("id", id);
		return client.delete().uri("/{id}",params)
				//.exchange().then();
				.retrieve().bodyToMono(Void.class);                      //PARA LA VALIDACIÓN  USA ESTE.
	}
	
	//6
	//6.0.-SE CONFIGURA EL BODY DEL REQUEST PARA QUE SEA MULTIPARTBODY. PARA ESTO SE USA LA CLASE MULTIPARTBODYBUILDER.
	//*NOTA: EN EL MICROSERVICIO APIREST, LA IMAGEN SE OBTIENE DEL REQUEST, PERO DEL TIPO MULTIPARTFORMDATA Y SE OBTIENE EL ARCHIVO COMO UN FILEPART.
	//6.0.-EN EL MICROSERVICIO CLIENTE, HAY QUE OBTENER LA IMAGEN FILEPART DEL REQUEST Y PASARLO EN AL WEBCLIENT EN EL REQUEST PARA EMVIARLO AL MICROSERVICIO APIREST. LA IMAGEN SE LE PASA AL MICROSERVICIO APIREST COMO MULTIPARTFORMDATA.
	//6.1.-SE AGREGA, LA PARTE, EL "FILE" EN ESTE MultipartBodyBuilder.
		//FilePart file : ES UN OBSERVABLE DEL TIPO FLUX DE DATABUFFER. POR LO TANTO ES ASINCRONO. POR ESTO SE USA EL MÉTODO ASYNCPART().
		//asyncPart(NOMBRE DEL CAMPO, EL FILEPART CON EL CONTENIDO FLUX<DATABUFFER>, EL TIPO DEL CONTENIDO);   ... content() : DEVUELVE UN FLUX<DATABUFFER>.  
		//.CLASS : FIJATE QUE EL .CLASS QUE IMPORTES SEA EL QUE DICE SPRINFRAMEWORK.CORE.IO.BUFFER.DATABUFFER.
	//6.2.-AHORA EL HEADER. AQUÍ SE HACEN MODIFICACIONES.
		//SE MODIFICA PARA AGREGAR EL ARCHIVO FILE COMO NOMBRE DE PARÁMETRO CON EL NOMBRE DEL ARCHIVO EN LA CABECERA. DEL TIPO CONTENTDISPOSITIONFORMDATA.
	//6.3.-HASTA AQUÍ YA SE CONFIGURÓ EL MultipartBodyBuilder.
	//6.4.-AHORA SE REALIZA LA REQUEST. LA PETICION HTTP AL APIREST.
	//6.5.-SE LE PASA AL BODY DEL REQUEST, EL MultipartBodyBuilder. Y CON BUILD SE CONSTRUYE EN EL BODY.   MultipartBodyBuilder PART,  ES UN TIPO SINCRONO, UN TIPO COMÚN Y CORRIENTE. NO ES FLUX NI MONO.  
		//build() : ESTA WEA DEVUELVE UN MultiValueMap<String,httpEntity>
		//retrieve() : ESTA WEA EJECUTA EL MÉTODO EXCHANGE() PARA QUE SE EJECUTE LA REQUEST Y SE HAGA EL INTERCAMBIO ENTRE EL REQUEST Y EL RESPONSE. 
		//bodyToMono(Producto.class) : LUEGO SE COMBIERTE LA RESPUESTA A UN MONO DEL TIPO PRODUCTO.
	@Override
	public Mono<Producto> upload(FilePart file, String id) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("id", id);
		
		MultipartBodyBuilder parts = new MultipartBodyBuilder();
		//6.1
		parts.asyncPart("file", file.content(), DataBuffer.class)
		//6.2
		.headers(h-> {
			h.setContentDispositionFormData("file", file.filename());
		}); //6.3
		//6.4
		return client.post()
				.uri("/upload/{id}",params)
				.contentType(MediaType.MULTIPART_FORM_DATA)
				//6.5
				.body(BodyInserters.fromValue(parts.build()))
				.retrieve()
				.bodyToMono(Producto.class);
	}
	
	

	//ESTE BEAN VIENE DE LA CLASE APPCONFIG
	@Autowired
	private WebClient client;

	
	
}
