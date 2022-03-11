package com.springboot.webflux.app.models.dao;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.springboot.webflux.app.models.documents.Producto;

import reactor.core.publisher.Mono;

public interface IProductoDao extends ReactiveMongoRepository<Producto,String>{

	
	//CLASE81
	//ESTE MÉTODO ES ESPECIAL Y SE USA PARA LAS PRUEBAS UNITARIAS EN class SpringbootWebfluxApirestApplicationTests 
	
	public Mono<Producto> findByNombre(String nombre);	
	
	//LO MISMO QUE EL MÉTODO findByNombre PERO CON CONSULTA MONGO.
	@Query("{ 'nombre: ?0' }")
	public Mono<Producto> obtenerPorNombre(String nombre);	
	
	
}
