package com.springboot.webflux.app;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import com.springboot.webflux.app.models.dao.IProductoDao;
import com.springboot.webflux.app.models.documents.Producto;

import reactor.core.publisher.Flux;
//CLASE31
//1.-AQUI VA LA LÓGICA PARA INSERTAR LOS PRODUCTOS DE EJEMPLO USANDO FLUX.
	//EN JUST() SE PONENE LOS PRODUCTOS CON TODOS SUS DATOS.
//X.- SE USA FLAPMAP() Y NO MAP(), YA QUE EL MAP DEVOLVERÍA UN FLUJO MONO DE PRODUCTO. MIENTRAS QUE FLATMAP LO QUE HACE ES OBTENER EL FLUJO, MONO O FLUX, Y LO APLANA HASTA CONVERTIRLO EN UN OBJ PRODUCTO.
//2.-METODO PARA OBTENER LOS PRODUCTOS DEL FLUJO Y METERLOS EN UNA BASE DE DATOS.
//CLASE32
//3.-PARA BORRAR LA COLECCIÓN CADA VEZ QUE SE ECHE A ANDAR AL APP.
//CLASE33
//4.-SE PONE LA FECHA.

@SpringBootApplication
public class SpringbootWebfluxApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootWebfluxApplication.class, args);
	}

	//CLASE31 //1
	@Override
	public void run(String... args) throws Exception {
		//3
		mongoTemplate.dropCollection("productos").subscribe();
		//1.1
		Flux.just(new Producto("TV panasonic LCD",234.23),
				new Producto("Camara Sony Digital HD",256.23),
				new Producto("Apple ipad ",876.23),
				new Producto("HP notebook",294.23),
				new Producto("TV samsung 4k",834.23))
		//x.-
		.flatMap(producto -> {
			//4
			producto.setCreateAt(new Date());
			return dao.save(producto);
			})
				.subscribe(producto -> log.info("Insert: " + producto.getId() + producto.getNombre()));
		
	}

	
	@Autowired
	private IProductoDao dao;
	private static final Logger log = LoggerFactory.getLogger(SpringbootWebfluxApplication.class);
	@Autowired //3
	private ReactiveMongoTemplate mongoTemplate;
	
}
