package com.springboot.webflux.app.handler;

import java.io.File;
import java.net.URI;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.springboot.webflux.app.models.documents.Categoria;
import com.springboot.webflux.app.models.documents.Producto;
import com.springboot.webflux.app.models.service.IProductoService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//CLASE70
//CLASE71
//1.- 
//1.1.-SE OBTIENE EL PATHVARIABLE.
//1.2.-productoService.findById(id) : DEVUELVE UN MONO<PRODUCTO> POR LO TANTO SE USA FLATMAP() PARA CONVERTIRLO A Mono<ServerResponse>
//1.3.-DEVUELVE STATUS 200, TIPO DE CONTENIDO JSON, SE INSERTA EL PRODUCTO EN EL BODY.
//CLASE72
//2.- 2.1.- Mono<Producto> producto = request.bodyToMono(Producto.class): OBTIENE EL OBJ PRODUCTO QUE VIENE EN EL BODY DEL REQUEST. ESTE ES EL REMPLAZO DEL @REQUESTBODY, PERO DE MANERA REACTIVA. CON request.bodyToMono(Producto.class) SE CONVIERTE EL BODY A REACTIVO.
//2.2.-flatMap(p->) : CON FLATMAP SE EMITE EL PRODUCTO Y SE GUARDA EN LA BASE DE DATOS. PRODUCE UN FLUJO DE PRODUCTO PERO CON EL ID PARA GUARDARLO EN LA BD.
//2.3.-SE CONVIERTE EL MONO<PRODUCTO> A UN Mono<ServerResponse>. SE GUARDA LA URI CON SU RESPECTIVO ID PARA VER DETALLE. SE GUARDA EL PRODUCTO EN EL BODY DEL RESPONSE.
//CLASE73
//3 3.1.-SE OBTIENE EL PRODUCTO DESDE EL BODY DEL REQUEST.
//3.2.-SE OBTIENE EL PATH VARIABLE ID.
//3.3.-SE BUSCA LE MONO<PRODUCTO> DESDE LA BASE DE DATOS.
//3.4.-SE VAN A COMBINAR: PRODUCTO DESDE EL BODY DEL REQUEST + MONO<PRODUCTO> DESDE LA BASE DE DATOS. SE PARTE CON LA INFORMACION DEL PRODUCTO DE LA BASE DE DATOS Y LUEGO SE COMBINA CON LA INFORMACIÓN NUEVA QUE VIENE EN EL BODY REQUEST.
	//productoDb.zipWith(producto, (pDb,pReq) -> {}
//3.5.-SE MODIFICA A UN Mono<ServerResponse>. flatMap() RECIBE EL PRODUCTO MODIFICADO "p" Y SE GUARDA EN LA BASE DE DATOS.  CON BODY(): SE PASA "p" AL CUERPO DEL RESPONSE Y ASÍ APARECERÁ EN EL JSON DEL POSTMAN, EN LA RESPUESTA.
//3.6.-YA SI LA WEA NO EXISTE SEGÚN EL ID DEL PATHVARIABLE, VA A LANZAR UN ERROR.
//CLASE74
//4 4.1.- productoDb.flatMap(p -> productoService.delete(p)) : ESTO DEVUELVE UN MONO<VOID> POR LO TANTO HAY QUE CAMBIAR EL TIPO DE LA RESPUESTA. PARA ESTO SE USA EL THEN().
//4.2.- then(ServerResponse.noContent().build()) : NOCONTENT(): NOTIFICA QUE SE ELIMINÓ CORRECTAMENTE CON STATUS 204. EL BUILD() GENERA LA RESPUESTA SIN BODY.
//4.3.- SE MANEJA EL ERROR. 
//CLASE75
//5 5.1.-SE NECESITA ACCEDER AL PRODUCTO EN LA BASE DE DATOS DE MONGO. PARA PODER ACTUALIZARLO CON EL NOMBRE DE LA IMAGEN DEL PRODUCTO. 
		//PARA ESTO SE NECESITA EL OBJ FILEPART, PARA TRANSFERIR EL ARCHIVO DEL DIRECTORIO DE LAS IMAGENES, PARA OBTENER EL NOMBRE DE LA FOTO.
		//multipartData(): DEVULEVE UN Mono<MultiValueMap<String, Part>>  : CONTIENE LOS NOMBRES DE PARÁMETROS DEL REQUEST Q SE ESTÁ ENVIANDO. CON UN CONTENTTYPE = MULTIPART/FORM-DATA. TAMBIEN UN CAMPO DE TIPO PART : CON ESTE OBJ SE PUEDE OBTENER EL NOMBRE DE LA IMAGEN Y MOVER LA IMAGEN AL DIRECTORIO. 
		//MAP() : SE CONVIERTE EL FLUJO DE Mono<MultiValueMap<String, Part>> A PART
		//multipart.toSingleValueMap().get("file") : CON ESTO SE OBTIENE UN "PART".  "file" : HACE REFERENCIA AL NOMBRE DEL PARÁMETRO. DEBE SER EL MISMO NOMBRE.
		//cast(): SE HACE UN CAST PARA OBTENER UN TIPO MÁS ESPECIFICO DEL TIPO FILEPART.
		//flatMap(): YA CON EL FILEPART, SE COMBIERTE ESTE FLUJO A U MONO<PRODUCTO>. "file" HACE REFERENCIA AL FILEPART.
//5.2.- flatMap() : SEGUNDO FLATMAP, PARA SUBIR EL ARCHIVO A TRAVÉS DEL FILE Y GUARDARLO EN LA BASE DE DATOS CON LA FOTO.
//5.3.- SE MUEVE LA IMAGEN AL DIRECTORIO Y SE GUARDA EL PRODUCTO EN LA BBDD CON LA IMAGEN.
		//then(productoService.save(p)) : ACTUALIZA LA IMAGEN EN MONGO. EL THEN() EMITE OTRO FLUJO UNA VEZ QUE SE HAYA SUBIDO EL PRODUCTO CON LA FOTO.
//5.4.- SE MODIFICA EL FLUJO. EN ESTE PUNTO EL FLUJO ES DEL TIPO MONO<PRODUCTO> Y HAY Q PASARLO AL TIPO Mono<ServerResponse> .
//CLASE76
	//6.1.- MEDIANTE MULTIPARTDATA SE CREA UN NUEVO PRODUCTO CON NOMBRE, PRECIO Y CATEGORÍA.
	//6.2.- SE CONTRUYE EL OBJ CATEGORÍA. value() : RETORNA UN VALOR STRING.
	//6.3.- SE CONSTRUYÓ EL PRODUCTO DESDE EL MULTIPARTDATA
	//6.4.- (ESTA ES UNA COPIA DEL MÉTODO UNPLOAD) SE CAMBIA productoService.findById(id) POR EL NUEVO PRODUCTO MONO. 
	//6.5.- SE LE AGREGA LA FECHA.
//CLASE77
	//7.1.-AUTOWIRED PARA VALIDAR CON LA INTERFAZ VALIDATOR.
	//7.2.-SE HACE COPIA DEL MÉTODO CREAR PARA INTEGRAR VALIDACIONES.
	//7.3.-OBJETO QUE PERMITE VALIDAR Y ENCONTRAR LOS ERRORES DE LA VALIDACIÓN. BeanPropertyBindingResult : IMPLEMENTACIÓN DE LA INTERFAZ ERRORS. SE LE PASA EL OBJ QUE SE QUIERE VALIDAR EL NOMBRE DE LA CLASE A LA QUE PERTENECE.
	//7.4.-SE VALIDA CON VALIDATOR.
	//7.5.- LUEGO SE PREGUNTA CON IF SI EL OBJ ERRORS CONTIENE ERRORES REGISTRADOS. SE MANEJA LA RESPUESTA:
		//EN EL CASO DE ERROR, SE CONSIGUE UN FLUX CON UNA LISTA DE ERRORES. LUEGO CON EL MAP() SE OBTIENE UN FLUX DE STRINGS CON LOS ERRORES.
		//CON COLLECT() SE VUELVE A UN TIPO MONO LIST STRING. 
		//flatMap(): LUEGO ESTA LISTA SE CONBIERTE AL TIPO  Mono<ServerResponse> CON ServerResponse.badRequest(). LUEGO CON BODY(BodyInserters.fromValue(list)) SE PASAN LOS MENSAJES AL CUERPO PARA QUE SE VEAN EN EL JSON.
	
@Component
public class ProductoHandler {

	//0
	public Mono<ServerResponse> listar(ServerRequest request){
		return ServerResponse.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(productoService.findAll(),Producto.class);
	}
	
	//1
	public Mono<ServerResponse>ver(ServerRequest request){
		//1.1
		String id = request.pathVariable("id");
		//1.2
		return productoService.findById(id).flatMap(p-> 
		//1.3
		ServerResponse.ok()
		.contentType(MediaType.APPLICATION_JSON)
		.body(BodyInserters.fromValue(p)))
		.switchIfEmpty(ServerResponse.notFound().build());
	}
	
	/*
	//2
	public Mono<ServerResponse>crear(ServerRequest request){
		//2.1
		Mono<Producto> producto = request.bodyToMono(Producto.class);
		//2.2
		return producto.flatMap(p->{
			if(p.getCreateAt()==null) {
				p.setCreateAt(new Date());
			}
			return productoService.save(p);
			//2.3
		}).flatMap(p-> ServerResponse
				.created(URI.create("/api/v2/productos/".concat(p.getId())))
				.contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromValue(p)));
	}
	*/
	
	//7 
	public Mono<ServerResponse>crear(ServerRequest request){
		
		Mono<Producto> producto = request.bodyToMono(Producto.class);
		
		return producto.flatMap(p-> {
		
			//7.3
			Errors errors = new BeanPropertyBindingResult(p, Producto.class.getName());
			//7.4
			validator.validate(p, errors);
			//7.5
			if(errors.hasErrors()) {
				return Flux.fromIterable(errors.getFieldErrors())
						.map(fieldError -> "El campo " + fieldError.getField() + " " + fieldError.getDefaultMessage())
						.collectList()
						.flatMap(list-> ServerResponse.badRequest().body(BodyInserters.fromValue(list)));
			}else {	
				if(p.getCreateAt()==null) {
					p.setCreateAt(new Date());
				}
				return productoService.save(p).flatMap(pdb-> ServerResponse
						.created(URI.create("/api/v2/productos/".concat(pdb.getId())))
						.contentType(MediaType.APPLICATION_JSON)
						.body(BodyInserters.fromValue(pdb)));
			}
		});
	}
	
	
	//3
	public Mono<ServerResponse>editar(ServerRequest request){
		//3.1
		Mono<Producto> producto = request.bodyToMono(Producto.class);
		//3.2
		String id = request.pathVariable("id");
		//3.3
		Mono<Producto> productoDb = productoService.findById(id);
		//3.4
		return productoDb.zipWith(producto, (pDb,pReq) -> {
			pDb.setNombre(pReq.getNombre());
			pDb.setPrecio(pReq.getPrecio());
			pDb.setCategoria(pReq.getCategoria());
			return pDb;
			//3.5
		}).flatMap(p-> ServerResponse.created(URI.create("/api/v2/productos/".concat(p.getId())))
				.contentType(MediaType.APPLICATION_JSON)
				.body(productoService.save(p),Producto.class))
				//3.6
				.switchIfEmpty(ServerResponse.notFound().build());
	}
	
	//4
	public Mono<ServerResponse>eliminar(ServerRequest request){
		String id = request.pathVariable("id");
		Mono<Producto> productoDb = productoService.findById(id);
		return productoDb.flatMap(p -> productoService.delete(p)
				.then(ServerResponse.noContent().build()))
				.switchIfEmpty(ServerResponse.notFound().build());	
	}
	
	//5
	public Mono<ServerResponse>	upload(ServerRequest request){
		String id = request.pathVariable("id");
		//5.1
		return request.multipartData().map(multipart -> multipart.toSingleValueMap().get("file"))
				.cast(FilePart.class)
				.flatMap(file -> productoService.findById(id)
						//5.2
						.flatMap(p -> {
					p.setFoto(UUID.randomUUID().toString() + "-" + file.filename()
					.replace(" ", "-")
					.replace(":", "")
					.replace("\\", ""));
					//5.3
					return file.transferTo(new File(path + p.getFoto())).then(productoService.save(p));
				})).flatMap(p -> ServerResponse
						.created(URI.create("/api/v2/productos/".concat(p.getId())))
						.contentType(MediaType.APPLICATION_JSON)
						.body(BodyInserters.fromValue(p)))
				.switchIfEmpty(ServerResponse.notFound().build());
	}
	
	//6
	public Mono<ServerResponse>	crearConFoto(ServerRequest request){
		//6.1
		Mono<Producto>producto = request.multipartData().map(multipart -> {
			FormFieldPart nombre = (FormFieldPart)multipart.toSingleValueMap().get("nombre");
			FormFieldPart precio = (FormFieldPart)multipart.toSingleValueMap().get("precio");
			FormFieldPart categoriaId = (FormFieldPart)multipart.toSingleValueMap().get("categoria.id");
			FormFieldPart categoriaNombre = (FormFieldPart)multipart.toSingleValueMap().get("categoria.nombre");
			//6.2
			Categoria categoria = new Categoria(nombre.value());
			categoria.setId(categoriaId.value());
			return new Producto(nombre.value(), Double.parseDouble(precio.value()),categoria);
		});
		
		return request.multipartData().map(multipart -> multipart.toSingleValueMap().get("file"))
				.cast(FilePart.class)
				//6.4
				.flatMap(file -> producto
						
						.flatMap(p -> {
					p.setFoto(UUID.randomUUID().toString() + "-" + file.filename()
					.replace(" ", "-")
					.replace(":", "")
					.replace("\\", ""));
					//6.5
					p.setCreateAt(new Date());
					
					return file.transferTo(new File(path + p.getFoto())).then(productoService.save(p));
				})).flatMap(p -> ServerResponse
						.created(URI.create("/api/v2/productos/".concat(p.getId())))
						.contentType(MediaType.APPLICATION_JSON)
						.body(BodyInserters.fromValue(p)));
	}
	
	
	@Autowired
	private IProductoService productoService;
	
	//5.3
	@Value("${config.uploads.path}")
	private String path;
	
	//7.1
	@Autowired
	private Validator validator;
}
