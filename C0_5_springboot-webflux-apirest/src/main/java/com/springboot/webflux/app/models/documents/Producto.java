package com.springboot.webflux.app.models.documents;

import java.util.Date;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

@Document(collection="productos")
public class Producto {
	
	public Producto() {
		
	}

	public Producto(String nombre, Double precio) {
		this.nombre = nombre;
		this.precio = precio;
	}
	
	public Producto(String nombre, Double precio, Categoria categoria) {
		this.nombre = nombre;
		this.precio = precio;
		this.categoria = categoria;
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
	public Double getPrecio() {
		return precio;
	}
	public void setPrecio(Double precio) {
		this.precio = precio;
	}
	public Date getCreateAt() {
		return createAt;
	}
	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}
	public Categoria getCategoria() {
		return categoria;
	}
	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}
	public String getFoto() {
		return foto;
	}
	public void setFoto(String foto) {
		this.foto = foto;
	}


	@Id
	private String id;
	@NotEmpty //VALIDACION, SE HABILITA EN EL CONTROLADOR
	private String nombre;
	@NotNull  //VALIDACION, SE HABILITA EN EL CONTROLADOR
	private Double precio;
	@DateTimeFormat(pattern="yyyy-MM-dd") //CLASE44: PARA QUE NO DÉ ERROR EN LA FECHA, CUANDO SE GUARDE UN PRODUCTO A TRAVÉS DEL FORMULARIO.
	private Date createAt;
	@Valid
	@NotNull
	private Categoria categoria;
	private String foto;
}
