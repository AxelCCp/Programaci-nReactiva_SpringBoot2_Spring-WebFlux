package com.springboot.webflux.app.models.services;

import com.springboot.webflux.app.models.documents.Producto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//CLASE42
//1.-MÉTODO QUE RETORNA UN FLUX DE PRODUCTOS


public interface IProductoService {
	
	public Flux<Producto> findAll();
	public Flux<Producto> findAllConNombreUpperCase();
	public Flux<Producto> findAllConNombreUpperCaseRepeat();
	
	public Mono<Producto> findById(String id);
	public Mono<Producto> save(Producto producto);
	public Mono<Void> delete(Producto producto);
	
}