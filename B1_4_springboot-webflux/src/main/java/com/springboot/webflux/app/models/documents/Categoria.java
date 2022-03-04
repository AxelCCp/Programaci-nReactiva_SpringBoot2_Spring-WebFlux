package com.springboot.webflux.app.models.documents;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


//CLASE50

@Document(collection="categorias")
public class Categoria {
	
	public Categoria() {
	}

	public Categoria(String nombre) {
		this.nombre = nombre;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	@Id
	private String id;
	private String nombre;
}
