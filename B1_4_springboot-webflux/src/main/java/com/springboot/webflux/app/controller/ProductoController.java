package com.springboot.webflux.app.controller;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;

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
	@PostMapping("/form")                                       //13.1
	public Mono<String>guardar(Producto producto, SessionStatus status){
		//13.1
		status.setComplete();
		return productoService.save(producto).doOnNext(p-> {
			log.info("Producto guardado: " + p.getNombre() + "...Id: " + p.getId());
		}).thenReturn("redirect:/listar");
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
	
	private static final Logger log = LoggerFactory.getLogger(ProductoController.class);
	
}
