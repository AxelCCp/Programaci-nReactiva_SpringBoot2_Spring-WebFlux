package com.springboot.reactor.app.models;

import java.util.ArrayList;
import java.util.List;

public class Comentarios {
	
	
	public Comentarios() {
		this.comentarios = new ArrayList<>();
	}
	

	public void addComentarios(String comentario) {
		this.comentarios.add(comentario);
	}


	@Override
	public String toString() {
		return "Comentarios= " + comentarios;
	}




	private List<String>comentarios; 
	
}
