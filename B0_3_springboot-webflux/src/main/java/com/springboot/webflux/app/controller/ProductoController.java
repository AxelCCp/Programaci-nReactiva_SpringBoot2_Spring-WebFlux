package com.springboot.webflux.app.controller;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;

import com.springboot.webflux.app.models.dao.IProductoDao;
import com.springboot.webflux.app.models.documents.Producto;

import reactor.core.publisher.Flux;

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


@Controller
public class ProductoController {

	//1
	@GetMapping({"/listar","/"})
	public String listar(Model model) {
		Flux<Producto> productos = productoDao.findAll().map(producto -> {
			//2
			producto.setNombre(producto.getNombre().toUpperCase());
			return producto;
		});
		
		productos.subscribe(prod -> log.info(prod.getNombre()));
		
		model.addAttribute("productos",productos);
		model.addAttribute("titulo", "listado de productos");
		return "listar";
	}
	
	//3
	@GetMapping("/listar-dataDriver")
	public String listarDataDriver(Model model) {
		Flux<Producto> productos = productoDao.findAll().map(producto -> {
			producto.setNombre(producto.getNombre().toUpperCase());
			return producto;
			//4
		}).delayElements(Duration.ofSeconds(1));
		
		productos.subscribe(prod -> log.info(prod.getNombre()));
		//5
		model.addAttribute("productos",new ReactiveDataDriverContextVariable(productos,2));
		model.addAttribute("titulo", "listado de productos");
		return "listar";
	}
	
	//6.-EJEMPLO DE FULL, 
	@GetMapping("/listar-full")
	public String listarFull(Model model) {
		Flux<Producto> productos = productoDao.findAll().map(producto -> {
			
			producto.setNombre(producto.getNombre().toUpperCase());
			return producto;
			//6.1
		}).repeat(5000);
		
	
		model.addAttribute("productos",productos);
		model.addAttribute("titulo", "listado de productos");
		return "listar";
	}
	
	//9.-EJEMPLO DE CHUNKED 
		@GetMapping("/listar-chunked")
		public String listarchunked(Model model) {
			Flux<Producto> productos = productoDao.findAll().map(producto -> {
				
				producto.setNombre(producto.getNombre().toUpperCase());
				return producto;
				//6.1
			}).repeat(5000);
			
		
			model.addAttribute("productos",productos);
			model.addAttribute("titulo", "listado de productos");
			return "listar-chunked";
		}
	
	
	
	@Autowired
	private IProductoDao productoDao;
	
	private static final Logger log = LoggerFactory.getLogger(ProductoController.class);
	
}
