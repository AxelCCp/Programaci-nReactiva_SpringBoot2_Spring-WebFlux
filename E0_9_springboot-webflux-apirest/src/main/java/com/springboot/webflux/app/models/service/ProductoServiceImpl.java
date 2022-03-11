package com.springboot.webflux.app.models.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springboot.webflux.app.models.dao.ICategoriaDao;
import com.springboot.webflux.app.models.dao.IProductoDao;
import com.springboot.webflux.app.models.documents.Categoria;
import com.springboot.webflux.app.models.documents.Producto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductoServiceImpl implements IProductoService {

	@Override
	public Flux<Producto> findAll() {
		// TODO Auto-generated method stub
		return productoDao.findAll();
	}
	
	@Override
	public Flux<Producto> findAllConNombreUpperCase() {
		// TODO Auto-generated method stub
		return productoDao.findAll().map(producto -> {
			producto.setNombre(producto.getNombre().toUpperCase());
			return producto;
		});
	} 
	
	@Override
	public Flux<Producto> findAllConNombreUpperCaseRepeat() {
		// TODO Auto-generated method stub
		return findAllConNombreUpperCase().repeat(5000);
	}
	

	@Override
	public Mono<Producto> findById(String id) {
		// TODO Auto-generated method stub
		return productoDao.findById(id);
	}

	@Override
	public Mono<Producto> save(Producto producto) {
		// TODO Auto-generated method stub
		return productoDao.save(producto);
	}

	@Override
	public Mono<Void> delete(Producto producto) {
		// TODO Auto-generated method stub
		return productoDao.delete(producto);
	}

	
	@Override
	public Flux<Categoria> findAllCategoria() {
		// TODO Auto-generated method stub
		return categoriaDao.findAll();
	}

	@Override
	public Mono<Categoria> findCategoriaById(String id) {
		// TODO Auto-generated method stub
		return categoriaDao.findById(id);
	}

	@Override
	public Mono<Categoria> saveCategoria(Categoria categoria) {
		// TODO Auto-generated method stub
		return categoriaDao.save(categoria);
	}
	
	
	//CLASE81
	@Override
	public Mono<Producto> findByNombre(String nombre) {
		// TODO Auto-generated method stub
		return productoDao.findByNombre(nombre);
	}
	
	//CLASE82
	@Override
	public Mono<Categoria> findCategoriaByNombre(String nombre) {
		// TODO Auto-generated method stub
		return categoriaDao.findByNombre(nombre);
	}
	
	@Autowired
	private IProductoDao productoDao;
	@Autowired
	private ICategoriaDao categoriaDao;
	
	





	
	
}
