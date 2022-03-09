package com.springboot.webflux.app;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import com.springboot.webflux.app.models.documents.Categoria;
import com.springboot.webflux.app.models.documents.Producto;
import com.springboot.webflux.app.models.service.IProductoService;

import reactor.core.publisher.Flux;



@SpringBootApplication
public class SpringbootWebfluxApirestApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootWebfluxApirestApplication.class, args);
	}

	
	@Override
	public void run(String... args) throws Exception {
		
		mongoTemplate.dropCollection("productos").subscribe();
		mongoTemplate.dropCollection("categorias").subscribe();

		Categoria electronico = new Categoria("Electrónico");
		Categoria deporte = new Categoria("Deporte");
		Categoria computacion = new Categoria("Computación");
		Categoria muebles = new Categoria("Muebles");


		Flux.just(electronico,deporte,computacion,muebles)
		.flatMap(c-> productoService.saveCategoria(c))
		.doOnNext(c-> {
			log.info("Categoría creada: " + c.getNombre() + ", ID: " + c.getId());
		}).thenMany(
				
				Flux.just(new Producto("TV panasonic LCD",234.23,electronico),
						new Producto("Camara Sony Digital HD",256.23,electronico),
						new Producto("Apple ipad ",876.23,electronico),
						new Producto("HP notebook",294.23,computacion),
						new Producto("TV samsung 4k",834.23,electronico),
						new Producto("Mica cómoda 5 cajones",84.23,muebles),
						new Producto("Bicicleta",354.23,deporte))
				
				.flatMap(producto -> {
				
					producto.setCreateAt(new Date());
					return productoService.save(producto);
					})
				)
		.subscribe(producto -> log.info("Insert: " + producto.getId() + producto.getNombre()));
	}
	
	
	@Autowired
	private IProductoService productoService;
	private static final Logger log = LoggerFactory.getLogger(SpringbootWebfluxApirestApplication.class);
	@Autowired //3
	private ReactiveMongoTemplate mongoTemplate;
	
	
	
}
