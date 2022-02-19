package com.springboot.reactor.app.models;

public class UsuarioConComentarios {
	
	public UsuarioConComentarios(Usuario usuario, Comentarios comentarios) {
		this.usuario =  usuario;
		this.comentarios = comentarios;
	}
	
	
	
	
	
	@Override
	public String toString() {
		return "UsuarioConComentarios [usuario=" + usuario + ", comentarios=" + comentarios + "]";
	}





	private Usuario usuario;
	private Comentarios comentarios;
}
