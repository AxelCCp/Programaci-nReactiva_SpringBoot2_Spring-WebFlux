package com.springboot.webflux.app.models.dao;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.springboot.webflux.app.models.documents.Categoria;

import reactor.core.publisher.Mono;

public interface ICategoriaDao extends ReactiveMongoRepository<Categoria,String>{

	
	//CLASE82
	//NOMBRE VENDRÍA SIENDO EL NOMBRE DE LA CATEGORIA, POR LO TANTO ESTARÍA OK.
	
	public Mono<Categoria> findByNombre(String nombre); 
	
	
}
