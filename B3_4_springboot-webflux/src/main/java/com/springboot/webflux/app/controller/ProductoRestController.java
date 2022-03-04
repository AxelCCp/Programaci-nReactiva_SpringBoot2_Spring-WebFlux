package com.springboot.webflux.app.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.webflux.app.models.dao.IProductoDao;
import com.springboot.webflux.app.models.documents.Producto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//CLASE39
//1.-SE OBTIENE UN FLUX CON TODOS LOS PRODUCTOS.
//2.-SE BUSCA CON EL FILTRO DENTRO DEL FLUX PRODUCTOS, EL ID PASADO POR PARAMETRO EN EL METODO. LUEGO CON NEXT(), EL PRODUCTO ENCONTRADO EN EL FLUJO SE PASA A MONO.
//3.-SE IMPRIME EN CONSOLA.

@RestController
@RequestMapping("/api/productos")
public class ProductoRestController {
	
	
	@GetMapping
	public Flux<Producto>index(){
		Flux<Producto> productos = productoDao.findAll().map(producto -> {
			
			producto.setNombre(producto.getNombre().toUpperCase());
			return producto;
			
		}).doOnNext(prod -> log.info(prod.getNombre()));
		return productos;
	}
	
	@GetMapping("/{id}")
	public Mono<Producto>show(@PathVariable String id){
		//Mono<Producto>productos = productoDao.findById(id);
		//1
		Flux<Producto> productos = productoDao.findAll();
		//2
		Mono <Producto> producto = productos.filter(p->p.getId().equals(id))
				.next()
				//3
				.doOnNext(prod -> log.info(prod.getNombre()));
		return producto;
	}
	
	@Autowired
	private IProductoDao productoDao;
	private static final Logger log = LoggerFactory.getLogger(ProductoController.class);
	
}
