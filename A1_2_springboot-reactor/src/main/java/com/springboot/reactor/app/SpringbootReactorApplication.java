package com.springboot.reactor.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.springboot.reactor.app.models.Usuario;

import reactor.core.publisher.Flux;

@SpringBootApplication
public class SpringbootReactorApplication implements CommandLineRunner	 {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootReactorApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Flux<Usuario> nombres = Flux.just("Andres Guzman","Pedro Fulano","María Fulana","Diego Sultano","Juan Mengano","Bruce Lee","Bruce wayne")
				
				.map(nombre-> new Usuario(nombre.split(" ")[0].toUpperCase(),nombre.split(" ")[1].toUpperCase()))
				
				.filter(usuario ->  usuario.getNombre().equalsIgnoreCase("bruce"))
				
				.doOnNext(usuario -> {
					if(usuario==null) {
						throw new RuntimeException("Nombres no pueden ser vacíos");
					}
					
					System.out.println(usuario.getNombre().concat(" ").concat(usuario.getApellido()));
				})
				.map(usuario->{
					String nombre = usuario.getNombre().toLowerCase();
					usuario.setNombre(nombre);
					return usuario;
				}); 
			
		
		nombres.subscribe(e->log.info(e.toString()), 
				error->log.error(error.getMessage()),
				new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						log.info("Ha finalizado la ejecución del observable con éxito");
					}
					
				});
	}

	private static final Logger log = LoggerFactory.getLogger(SpringbootReactorApplication.class); 
}
