package com.springboot.webflux.app.models.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springboot.webflux.app.models.dao.IProductoDao;
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

	@Autowired
	private IProductoDao productoDao;



	
	
}
