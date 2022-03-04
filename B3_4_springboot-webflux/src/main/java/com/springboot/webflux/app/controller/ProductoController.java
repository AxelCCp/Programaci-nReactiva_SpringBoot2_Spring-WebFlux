package com.springboot.webflux.app.controller;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Date;
import java.util.UUID;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;

import com.springboot.webflux.app.models.documents.Categoria;
import com.springboot.webflux.app.models.documents.Producto;
import com.springboot.webflux.app.models.services.IProductoService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//CLASE34
//1.-CON MODEL SE LE PASAN DATOS A LA VISTA.
//CLASE35
//2.-SE PASA EL NOMBRE A MAYUSCULA
//CLASE36
//3.-SE USA REACTIVE DATA DRIVER PARA MANEJAR LA CONTRAPRESIÓN.
//4.-SE PONE UN DELAY PARA RETRAZAR LOS ELEMENTOS DEL FLUJO. CON ofSeconds(1) SE VA A DEMORAR 1 SEGUNDO POR CADA ELEMENTO DEL FLUJO.
//5.-ReactiveDataDriverContextVariable() : AL MODELO SE LE PASA UN OBJ ReactiveDataDriverContextVariable, EL CUAL RECIVE POR PARÁMETRO "PRODUCTOS" Y UN "2" QUE SE REFIERE AL TAMAÑO DEL BUFFER Y  LA CANTIDAD DE ELEMENTOS QUE VA A IR CARGANDO A LA VEZ.
//CLASE37
//6.-EJEMPLO DE FULL, NO SE CONFIGURA NADA CON RESPECTO A PROCESO DEL LOS ELEMENTOS Y LA CONTRAPRESION.
	//repeat(5000) : SE SIMULA UN FLUJO MUY GRANDE, MULTIPLICANDO LA BBDD POR 5000.
//7.-SE USA EL MODO CHUNKED, OTRA MANERA DE MANEJAR LA CONTRAPRESIÓN. AQUÍ EL BUFFER SE MIDE EN BYTES Y NO EN CANTIDAD DE ELEMENTOS COMO DATA DRIVER. SE USA CUANDO EL FLUJO ES MUY GRANDE.
//CLASE38
//8.-SE CONFIGURA CHUNKED EN EL APPLICATION.PROPERTIES. SE CONFIGURÓ EN 1024 BYTES.
//9.-EJEMPLO DE CHUNKED 
//------------
//CLASE43
//10.-METODO PARA MONTRAR FORMULARIO
//11.-METODO PARA GUARDAR LOS DATOS DEL FORMULARIO. ESTE MÉTODO SE ACTIVA AL PULSAR EL BOTON DE GUARDAR.
	//thenReturn(): DEVUELVE UN MONO DE STRING. POR LO TANTO SE CONBIERTE EL FLUJO DE PRODUCTOS DE SAVE() EN UN STRING Y ESTE STRING CONTIENE LA RESPUESTA. EL REDIRECT. 
//CLASE45
//12.-MÉTODO PARA EDITAR UN PRODUCTO
//13.-CONTIENE EL NOMBRE DEL OBJ DEL FORMULARIO CON EL CUAL SE PASA A LA VISTA. CON ESTO EL OBJ PRODUCTO SE GUARDA MOMENTANEAMENTE EN LA SESIÓN HTTP, CUANDO SE PASA A LA VISTA EN EL MÉTODO CREAR Y EDITAR. SE USA ESTO, YA QUE AL EDITAR UN PRODUCTO, ESTE SE DUPLICA POR NO CONTENER EN LA SESIÓN HTTP EL ID DEL PRODUCTO. ENTONCES CON ESTA ANOTACIÓN SE EVITA ESTO. 
//13.1.-SE ELIMINA EL OBJ PRODUCTO DE LA SESSIÓN HTTP.
//14.-defaultIfEmpty(new Producto()): SI EL PRODUCTO NO SE ENCONTRÓ SEGÚN EL ID QUE SE PASA POR HTTP, ENTREGA UN FORMULARIO VACÍO QUE PERMITE CREAR EL PRODUCTO. DE ESTA MANERA SE EVITA UN ERROR.
//15.-OTRO METODO EDITAR MÁS REACTIVO.
//16.-@VALID:  LE DICE A SPRING QUE TIENE QUE VALIDAR SEGÚN LAS ANOTACIONES QUE SE PUSIERON EN LA CLASE ENTITY PRODUCTO.
//16.1.-SE PREGUNTA SI SE PRODUJERON ERRORES EN LA VALIDACIÓN CON BINDING RESULT. BindingResult result : ALMACENA INFORMACIÓN DE ERRORES QUE PUEDAN HABER.
//16.2.-SI HAY ERRORES TE DEVUELVE A LA VISTA DEL FORMULARIO.
//17.-SE ASIGNA UNA FECHA POR DEFECTO SI ES QUE ESTA ES NULA.
//18.-MÉTODO ELIMINAR
//19.-METODO EDITAR MÁS REACTIVO 
//CLASE52
//20.-MÉTODO QUE BUSCA EL LISTADO DE CATEGORÍAS
	//@ModelAttribute("categorias") : CON ESTA NOTACIÓN, EL OBJ QUE RETORNA EL MÉTODO SE PASA AUTOMÁTICAMENTE A LA VISTA.
	//LA REFERENCIA "categorias" ESTÁ EN EL FORM.HTML, EN <option th:each="categoria: ${categorias}" th:value="${categoria.id}" th:text="${categoria.nombre}"></option>   EN EL DIV DE CATEGORIA.
//CLASE53
//21.-SE MODIFICA EL MÉTODO GUARDAR, PARA PODER INSERTAR EL OBJ "CATEGORIA" EN EL OBJ PRODUCTO.
	//EN EL PARÁMETRO DEL MÉTODO GUARDAR ESTÁ EL OBJ PRODUCTO. EL OBJ PRODUCTO CONTIENE LA CATEGORÍA Y LA CATEGORÍA SU RESPECTIVO ID. POR LO TANTO SE EXTRAE ESTA INFORMACIÓN.
	//UNA VEZ QUE SE OBTIENE EL MODO DE CATEGORIA, ESTE FLUJO SE CONVIERTE A PRODUCTO CON FLAPMAP(). DE ESTA MANERA SE LE PASA LA CATEGORÍA A PRODUCTO.
//CLASE54
//22.-SE CARGA FOTO DEL PRODUCTO
//22.1.-@RequestPart(name="file") FilePart file : PARA OBTENER EL PARÁMETRO DE TIPO FILE. (name="file") :EL NOMBRE "file" HACE REFERENCIA EL NOMBRE QUE SE LE PUSO EN EL FORMULARIO, EN LA LINEA <div><input type="file" name="file"/></div>.
//22.2.-if(!file.filename().isEmpty()) : SI file.filename() ES DISTINTO DE EMPTY, SE LE PASA LA FOTO AL PRODUCTO.
	//UUID.randomUUID().toString(): GENERA UN STRING RANDOM, PARA QUE LOS NOMBRES DE LAS FOTOS NO SE REPITAN.	
	//replace(" ", ""): SE ELIMINAN LOS ESPACIOS EN BLANCO QUE SE PUEDAN GENERAR.
	//replace(":", "").replace("\\", "") : SON HACKS PARA QUE FUNCIONES EN MICROSOFT EDGE Y EXPLORER.
//22.3.-OTRO FLATMAP PARA SUBIR LA FOTO.
	//return file.transferTo("") : SI VIENE LA FOTO CON SU NOMBRE, RETORNA UN  file.transferTo(""), QUE DEVULEVE UN MONO<VOID> CON LA RUTA DONDE SE QUIERE SUBIR LA IMAGEN.
//23.-MÉTODO  VER PARA MOSTRAR INFORMACIÓN DETALLADA DEL PRODUCTO.
//23.1.-switchIfEmpty() : ES CASI IGUAL AL defaultIfEmpty().  switchIfEmpty(): CON ESTE PARA DEVOLVER UN PRODUCTO SE USA MONO.JUST(NEW PRODUCTO). MIENTRAS QUE CON defaultIfEmpty(): SE PONE DIRECTAMENTE ... defaultIfEmpty(NEW PRODUCTO()).
//CLASE56
//24.-MÉTODO PARA MOSTRAR LA IMAGEN.
	//ResponseEntity<X> : PUEDE ALBERGAR CUALQUIER TIPO.
//24.1.-ResponseEntity.ok() : DEVUELVE STATUS 200.
	//header() : SE MODIFICA LA CABECERA. PQ HAY QUE INDICAR QUE EL CONTENT_DISPOSITION, ES UNA IMAGEN. UN ATTACHMENT, POR LO TANTO HAY QUE ADJUNTAR LA IMAGEN. 

@SessionAttributes("producto")  //13 ...producto HACE REFERENCIA A LA PALABRA "producto" DEL MÉTODO CREAR y EDITAR.
@Controller
public class ProductoController {

	//1
	@GetMapping({"/listar","/"})
	public Mono<String>listar(Model model) {
		Flux<Producto> productos = productoService.findAllConNombreUpperCase();
		productos.subscribe(prod -> log.info(prod.getNombre()));
		model.addAttribute("productos",productos);
		model.addAttribute("titulo", "listado de productos");
		return Mono.just("listar");
	}
	
	//10
	@GetMapping("/form")	
	public Mono<String>crear(Model model){
		model.addAttribute("producto",new Producto());
		model.addAttribute("titulo","Formulario de producto");
		model.addAttribute("boton","crear");
		return Mono.just("form");
	}
	//11
	@PostMapping("/form")       //16                      //16.1                //16.2         //22.1                                    //13.1
	public Mono<String>guardar(@Valid Producto producto, BindingResult result, Model model, @RequestPart(name="file") FilePart file, SessionStatus status){
		//16.1    //16.2
		if(result.hasErrors()) {
			model.addAttribute("titulo","Errores en el formulario de productos");
			model.addAttribute("boton","guardar");
			return Mono.just("form");
		}else {
			//13.1
			status.setComplete();
			
			//21
			Mono<Categoria>categoria = productoService.findCategoriaById(producto.getCategoria().getId());
			return categoria.flatMap(c-> {
				
				//17
				if(producto.getCreateAt()==null) {
					producto.setCreateAt(new Date());
				}
				
				//22.2
				if(!file.filename().isEmpty()) {
					producto.setFoto(UUID.randomUUID().toString() + "-" + file.filename().replace(" ", "").replace(":", "").replace("\\", ""));
				}
				
				producto.setCategoria(c);
				return productoService.save(producto);
			})
			
			.doOnNext(p-> {
				log.info("Categoría asignada: " + p.getCategoria().getNombre() + "...Id categoría: " + p.getCategoria().getId());
				log.info("Producto guardado: " + p.getNombre() + "...Id: " + p.getId());
			})
			
			//22.3
			.flatMap(p-> {
				if(!file.filename().isEmpty()) {
					return file.transferTo(new File(path + p.getFoto()));
				}
				return Mono.empty();
			})
			
			.thenReturn("redirect:/listar");
		}
	}
	//12
	@GetMapping("/form/{id}")
	public Mono<String>editar(@PathVariable String id, Model model){
		Mono<Producto>productoMono = productoService.findById(id).doOnNext(p-> {	
			log.info("Producto: " + p.getNombre());
		}).defaultIfEmpty(new Producto());               //14
		model.addAttribute("titulo","Editar producto");
		model.addAttribute("producto",productoMono);
		model.addAttribute("boton","editar");
		return Mono.just("form");
	}
	//19
	@GetMapping("/form2/{id}")
	public Mono<String>editar2(@PathVariable String id, Model model){
		return productoService.findById(id).doOnNext(p-> {	
			log.info("Producto: " + p.getNombre());
			model.addAttribute("titulo","Editar producto");
			model.addAttribute("boton","editar");
			model.addAttribute("producto",p);
		}).defaultIfEmpty(new Producto())
				.flatMap(p->{
					if(p.getId()==null) {
						return Mono.error(new InterruptedException("No existe el producto"));
					}
					return Mono.just(p);
				})
				.then(Mono.just("form"))
				.onErrorResume(ex-> Mono.just("redirect:/listar?error=no+existe+el+producto"));		
	}
	
	//18
	@GetMapping("/eliminar/{id}")
	public Mono<String> eliminar(@PathVariable String id){
		return productoService.findById(id)
				
				.defaultIfEmpty(new Producto())
				.flatMap(p->{
					if(p.getId()==null) {
						return Mono.error(new InterruptedException("No existe el producto a eliminar"));
					}
					return Mono.just(p);
				})
				
				.flatMap(p-> {
					log.info("Eliminando el producto: " + p.getNombre());
					log.info("Eliminando el producto Id: " + p.getId());
			return productoService.delete(p);
		}).then(Mono.just("redirect:/listar?success=producto+eliminado+con+exito"))
		.onErrorResume(ex-> Mono.just("redirect:/listar?error=no+existe+el+producto+a+eliminar"));		
	}
	
	//20
	@ModelAttribute("categorias")
	public Flux<Categoria>categorias(){
		return productoService.findAllCategoria();
	}
	
	//23
	@GetMapping("/ver/{id}")
	public Mono<String> ver(Model model, @PathVariable String id){
		return productoService.findById(id)
				.doOnNext(p-> {
					model.addAttribute("producto",p);
					model.addAttribute("titulo","Detalle producto");
				}).switchIfEmpty(Mono.just(new Producto()))               //23.1
				//SI SALE MAL
				.flatMap(p-> {
					if(p.getId()==null) {
						return Mono.error(new InterruptedException("No existe el producto"));
					}
					return Mono.just(p);
					//SI SALE BIEN THEN()...
				}).then(Mono.just("ver"))
				//AHORA SI SE PRODUCE UN ERROR PARA IR A "VER"
				.onErrorResume(ex-> Mono.just("redirect:/listar?error=no+existe+el+producto"));		
	} 
	
	//24
	@GetMapping("/uploads/img/{nombreFoto:.+}")
	public Mono<ResponseEntity<Resource>>verFoto(@PathVariable String nombreFoto) throws MalformedURLException{
		Path ruta = Paths.get(path).resolve(nombreFoto).toAbsolutePath();
		Resource imagen = new UrlResource(ruta.toUri());
		return Mono.just(
				//24.1
				ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + imagen.getFilename() + "\"")
				.body(imagen)
				);
	}
	
	//3
	@GetMapping("/listar-dataDriver")
	public String listarDataDriver(Model model) {
		Flux<Producto> productos = productoService.findAllConNombreUpperCase().delayElements(Duration.ofSeconds(1));
		productos.subscribe(prod -> log.info(prod.getNombre()));
		//5
		model.addAttribute("productos",new ReactiveDataDriverContextVariable(productos,2));
		model.addAttribute("titulo", "listado de productos");
		return "listar";
	}
	
	//6.-EJEMPLO DE FULL, 
	@GetMapping("/listar-full")
	public String listarFull(Model model) {
		Flux<Producto> productos = productoService.findAllConNombreUpperCaseRepeat();
		model.addAttribute("productos",productos);
		model.addAttribute("titulo", "listado de productos");
		return "listar";
	}
	
	//9.-EJEMPLO DE CHUNKED 
		@GetMapping("/listar-chunked")
		public String listarchunked(Model model) {
			Flux<Producto> productos = productoService.findAllConNombreUpperCaseRepeat();
			model.addAttribute("productos",productos);
			model.addAttribute("titulo", "listado de productos");
			return "listar-chunked";
		}
	
	
	
	@Autowired
	private IProductoService productoService;

	//CLASE 54 DEL PUNTO 22.3
	@Value("${config.uploads.path}")
	private String path;
	
	
	private static final Logger log = LoggerFactory.getLogger(ProductoController.class);
	
}
