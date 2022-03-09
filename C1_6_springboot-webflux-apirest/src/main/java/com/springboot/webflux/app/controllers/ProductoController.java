package com.springboot.webflux.app.controllers;

import java.io.File;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.support.WebExchangeBindException;

import com.springboot.webflux.app.models.documents.Producto;
import com.springboot.webflux.app.models.service.IProductoService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


//CLASE59
//1.-CON RESPONSE ENTITY SE GUARDA EL FLUX DE PRODUCTO EN EL BODY DEL RESPONSE. RETORNA UN MONO DE UN RESPONSE ENTITY Y DENTRO DEL RESPONSE ENTITY SE GUARDA EL FLUX DE PRODUCTO. RESPONSE ENTITY NO ES REACTIVO, POR LO TANTO SE USA MONO.JUST(). DENTRO DE JUST() SE DEVUELVE UN RESPONSE ENTITY CON STATUS 200 ok(). Y DENTRO DEL OK() SE DEVUELVE LA INFORMACIÓN.
//2.-SE DEFINE EL contentType CON JSON Y CON BODY() SE GUARDA LA LISTA DE PRODUCTOS EN EL CUERPO DE LA RESPUESTA.
//CLASE60
//3.-MÉTODO DE DETALLE
	//productoService.findById(id) : ESTO RETORNA UN MONO DE PRODUCTO. Y DEBE RETORNAR UN Mono<ResponseEntity<Producto>>. POR LO TANTO  SE USA MAP() PARA MODIFICAR EL FLUJO. SE LE PASA EL PRODUCTO A ResponseEntity CON STATUS 200 OK().
	//defaultIfEmpty(ResponseEntity.notFound().build() : POR SI ES QUE NO EXISTE EL PRODUCTO, DEVUELVE UN SUATUS 404. EL BUILD() CONSTUYE UNA RESPOESTA SIN CONTENIDO.
//4.-FORMA2
//CLASE61
//5.-MÉTODO CREAR
	//5.1.-SE CAMBIA EL MONO DE TIPO PRODUCTO A UN MONO DE TIPO RESPONSE ENTITY.
	//ResponseEntity.created(): DEVUELVE UN SYTATUS 201.
	//.created(URI.create("/api/productos/".concat(p.getId()))) : SE PASA LA RUTA DE DETALLE DE PRODUCTO.
	//SE DEFINE EL CONTENT TYPE Y SE AGREGA EL PRODUCTO AL BODY.
//CLASE62
//6.-MÉTODO PUT PARA ACTUALIZAR
//CLASE63
//7.-MÉTODO PARA ELIMINAR
//CLASE64
//MÉTODO PARA CARGAR LA FOTO
//8.-flatMap(): DENTRO DEL FLATMAP SE ACTUALIZA EL PRODUCTO CON LA FOTO Y TAMBN SE SUBE LA FOTO.
//8.1.-EL FLATMAP DEBE RETORNAR UN FLUJO MONO, POR LO TANTO:
	//file.transferTo(new File(path + p.getFoto())) : SE ASIGNA LA RUTA DE LA FOTO.
	//THEN(): PERMITE EJECUTAR OTRO FLUJO. then(productoService.save(p) ...CON EL CUAL SE GUARDA LA EL PRODUCTO CON LA FOTO.
//8.2.-map(): SE CAMBIA EL FLUJO A UN Mono<ResponseEntity<Producto>>
	//ResponseEntity.ok(p) : SE LE PASA EL PRODUCTO CON UN STATUS 200.
//8.3.-SE VALIDA POR SI ES QUE EL ID NO EXISTE.
//CLASE65 ...SEGUNDA VERSIÓN DEL MÉTODO UPLOAD SE COPIÓ DEL MÉTODO CREAR. ESTE SERÁ UN MÉTODO CREAR CON FOTO.
//9.-9.1-.
	//SE QUITA LA ANOTACION @RequestBody, YA QUE LAS FOTOS NO SE PUEDEN SUBIR COMO JSON, SINO QUE HAY QUE SUBIRLAS COMO FORM-DATA.
	//SI SE AGREGA EL @RequestPart FilePart file.
//9.2.-SE DEFINE EL NOMBRE DE LA FOTO.
//9.3.- file.transferTo(new File(path + producto.getFoto())): SE ESTABLECE EL DIRECTORIO QUE YA ESTÁ CONFIGURADO PARA GUARDAR LA FOTO.
//9.4.- THEN() : SE GUARDA EL PRODUCTO CON LA FOTO.
//9.5.-SE CAMBIA EL TIPO DEL FLUJO MONO A UN RESPONSE ENTITY.
//9.6.-created(): SE ESTABLECE LA URI PARA EL DETALLE DEL PRODUCTO. Y DESPUÉS SE GUARDA EL PRODUCTO EN EL BODY.
//CLASE67
//10.-SE CAMBIA EL TIPO PRODUCTO A UN TIPO REACTIVO MONO<PRODUCTO> PQ CUANDO SE VALIDA Y FALLA, SE PUEDE MANEJAR EL ERROR EN EL OPERADOR ON ERROR RESUME.
//10.1.-ENVIA UN RESPONSE ENTITY CON LOS MENSAJES DE ERROR.
	//t :REPRESENTA UN ERROR THROWABLE Y SE PASA A UN TIPO REACTIVO CON MONO.JUST(t)
	//CAST() : CON ESTE MÉTODO "t" SE PASA A UN ERROR MÁS CONCRETO PARA ESTE CASO, DEL TIPO  WebExchangeBindException.
	//FLATMAP(): AHORA EL ERROR WebExchangeBindException ES DENOMINADO "e" DENTRO DEL FLATMAP() Y SE CONVIERTE A UN TIPO LIST DEL TIPO FIELDERROR.
	//DENTRO DE FLATMAP SE USA MONO.JUST(), YA QUE FIELDERROR ES UN TIPO COMUN  CORRIENTE Y FLATMAP DEBE RETORNAR UN FLUJO.
	//HASTA AQUÍ SE TIENE UN MONO<LIST> CON LOS CAMPOS DE ERROR.
	//flatMapMany() : CON ESTE MÉTODO AHORA SE CONVIERTE EN UN FLUX PARA PODER ITERAR CADA ELEMENTO. PARA QUE EN VEZ DE QUE SEA UN FIELDERROR, SEA DEL TIPO STRING QUE CONTENGA EL MENSAJE DE ERROR. 
		//flatMapMany(errors -> Flux.fromIterable(errors)) : ERRORS ES UNA LISTA DE FIELDERROR QUE ES RECORRIDA.
	//MAP(): SE CONVIERTE CADA ELEMENTO FIELDERROR DE "ERRORS" EN UN TIPO STRING, CON EL NOMBRE DEL CAMPO Y EL TEXTO DEL ERROR.
	//collectList() : AHORA SE TOMA EL FLUX DE STRING Y SE CONVIERTE A UN MONO<LIST<STRING>> CON LOS MENSAJES DE ERROR.
	//flatMap(): AHORA SE CONVIERTE ESTE MONO A UN TIPO RESPONSE ENTITY.
//10.2.- Map<String,Object>respuesta = new HashMap<String,Object>() : SE CREA PARA UNIFICAR LAS RESPUESTAS DE LOS RESTURN'S: productoService.save(producto)... Y EL RETURN DEL ERROR.	
	//Map<String,Object: SE CAMBIO EL TIPO DEL MÉTODO PARA USAR EL Map<String,Object>respuesta.
//10.3.-SE PASA EL PRODUCTO AL MAP.
//10.4.-SE PASA LA LISTA DE ERRORES AL MAP.
	
@RestController
@RequestMapping("api/productos")
public class ProductoController {
	
	/*
	@GetMapping
	public Flux<Producto> lista (){
		return productoService.findAll(); 
	}*/
	
	/*
	//1.-FORMA1 SIMPLIFICADA
	@GetMapping
	public Mono<ResponseEntity<Flux<Producto>>> lista (){
		return Mono.just(ResponseEntity.ok(productoService.findAll()));
	}*/
	
	//2.-FORMA2
	@GetMapping
	public Mono<ResponseEntity<Flux<Producto>>> lista (){
		return Mono.just(
				ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(productoService.findAll())
				);
	}
	
	/*
	//3.- FORMA1 SIMPLIFICADA
	@GetMapping("/{id}")
	public Mono<ResponseEntity<Producto>>ver(@PathVariable String id){
		return productoService.findById(id).map(p-> ResponseEntity.ok(p));
	}*/
	
	//4.-FORMA2
	@GetMapping("/{id}")
	public Mono<ResponseEntity<Producto>>ver(@PathVariable String id){
		return productoService.findById(id).map(p-> ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(p))
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}
	
	/*
	//5
	@PostMapping()                              
	public Mono<ResponseEntity<Producto>>crear(@RequestBody Producto producto){
		if(producto.getCreateAt()==null) {
			producto.setCreateAt(new Date());
		}//5.1
		return productoService.save(producto).map(p-> ResponseEntity
				.created(URI.create("/api/productos/".concat(p.getId())))
				.contentType(MediaType.APPLICATION_JSON)
				.body(p));
	}*/
	
	//10.- MISMO METODO CREAR, PERO CON ALGUNAS MODIFICACIONES PARA IMPLEMENTAR VALIDACIONES.
	@PostMapping()                              
	public Mono<ResponseEntity<	Map<String,Object>>>crear(@Valid @RequestBody Mono<Producto> monoProducto){
		//10.2
		Map<String,Object>respuesta = new HashMap<String,Object>();
		
		return monoProducto.flatMap(producto -> {
			if(producto.getCreateAt()==null) {
				producto.setCreateAt(new Date());
			}
			
			return productoService.save(producto).map(p-> {
				//10.3
				respuesta.put("producto", p);
				respuesta.put("mensaje", "Producto creado con éxito");
				respuesta.put("timestamp", new Date());  //SE LE PASA UNA FECHA.
				return ResponseEntity
			
					.created(URI.create("/api/productos/".concat(p.getId())))
					.contentType(MediaType.APPLICATION_JSON)
					.body(respuesta);		
			});
			//10.1
		}).onErrorResume(t-> {
			return Mono.just(t).cast(WebExchangeBindException.class)
					.flatMap(e -> Mono.just(e.getFieldErrors()))
					.flatMapMany(Flux::fromIterable)
					.map(fieldError -> "El campo "+fieldError.getField() + " " + fieldError.getDefaultMessage())
					.collectList()
					.flatMap(list -> {
						respuesta.put("errors", list);
						respuesta.put("timestamp", new Date());
						respuesta.put("status", HttpStatus.BAD_REQUEST.value());
						return Mono.just(ResponseEntity.badRequest().body(respuesta));
					});	
		});
	}
	
	
	//6
	@PutMapping("/{id}")
	public Mono<ResponseEntity<Producto>> editar(@RequestBody Producto producto, @PathVariable String id){
		return productoService.findById(id).flatMap(p-> {
			p.setNombre(producto.getNombre());
			p.setPrecio(producto.getPrecio());
			p.setCategoria(producto.getCategoria());
			return productoService.save(p);
		}).map(p-> ResponseEntity
				.created(URI.create("api/productos/".concat(p.getId())))
				.contentType(MediaType.APPLICATION_JSON)
				.body(p))
		.defaultIfEmpty(ResponseEntity.notFound().build());
		
	}
	
	
	//7
	@DeleteMapping("/{id}")
	public Mono<ResponseEntity<Void>>eliminar(@PathVariable String id){
		return productoService.findById(id).flatMap(p-> {
			return productoService.delete(p).then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)));
		}).defaultIfEmpty(new ResponseEntity<Void>(HttpStatus.NOT_FOUND));
	}
	
	
	//8
	@PostMapping("/upload/{id}")
	public Mono<ResponseEntity<Producto>>upload(@PathVariable String id, @RequestPart FilePart file){
		return productoService.findById(id).flatMap(p->{
			p.setFoto(UUID.randomUUID().toString() + "-" + file.filename()
			.replace(" ","")
			.replace(":","")
			.replace("\\",""));
			//8.1
			return file.transferTo(new File(path + p.getFoto())).then(productoService.save(p));
			//8.2
		}).map(p-> ResponseEntity.ok(p))
				//8.3
				.defaultIfEmpty(ResponseEntity.notFound().build());	
	}
	
	//9
	@PostMapping("/v2") //9.1
	public Mono<ResponseEntity<Producto>>crearConFoto(Producto producto, @RequestPart FilePart file){
		if(producto.getCreateAt()==null) {
			producto.setCreateAt(new Date());
		}
		//9.2
		producto.setFoto(UUID.randomUUID().toString() + "-" + file.filename()
		.replace(" ","")
		.replace(":","")
		.replace("\\",""));
		//9.3                                                          //9.4
		return file.transferTo(new File(path + producto.getFoto())).then(productoService.save(producto))
				//9.5    //9.6
				.map(p-> ResponseEntity
				.created(URI.create("/api/productos/".concat(p.getId())))
				.contentType(MediaType.APPLICATION_JSON)
				.body(p));
	}
	
	
	@Autowired
	private  IProductoService productoService;
	@Value("${config.uploads.path}")
	private String path;
}
