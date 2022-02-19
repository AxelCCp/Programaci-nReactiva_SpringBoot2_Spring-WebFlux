package com.springboot.reactor.app;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.springboot.reactor.app.models.Comentarios;
import com.springboot.reactor.app.models.Usuario;
import com.springboot.reactor.app.models.UsuarioConComentarios;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//1.-collectList(): CONVIERTE EN UN MONO A UN SOLO OBJ QUE EN ESTE CASO ES LA LISTA DE USUARIOS. SE PASA DE FLUX A MONO.


@SpringBootApplication
public class SpringbootReactorApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootReactorApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		//ejemploIterable();
		//ejemploFlapMap();
		//ejemploToString();
		//ejemploConvertirAListaMono();
		//ejemploConvertirAListaMono2();
		//ejemploUsuarioComentariosFlatMap();
		//ejemploUsuarioComentariosZipWith();
		ejemploUsuarioComentariosZipWith2();
	}

	
	
	//CLASE18: 
	public void ejemploUsuarioComentariosZipWith2() {
		//FLUJO DE USUARIO
		Mono<Usuario>usuarioMono = Mono.fromCallable(()-> new Usuario("Jhon","Doe"));
		//FLUJO DE COMENTARIOS
		Mono<Comentarios>comentariosUsuarioMono = Mono.fromCallable(()->{
			Comentarios comentarios = new Comentarios();
			comentarios.addComentarios("Hola!!");
			comentarios.addComentarios("Mañana voy a la playa.");
			comentarios.addComentarios("Estoy usando el curso de Spring con reactor.");
			return comentarios;
		});
		Mono<UsuarioConComentarios> usuarioConComentarios = usuarioMono.zipWith(comentariosUsuarioMono).map(tuple -> {
			Usuario u = tuple.getT1();
			Comentarios c = tuple.getT2();
			return new UsuarioConComentarios(u,c);
		});
		usuarioConComentarios.subscribe(uc -> log.info(uc.toString()));
	}
	
	
	//CLASE18: SE CONBINAN 2 FLUJOS EN UNO CON zipWith().
	public void ejemploUsuarioComentariosZipWith() {
		//FLUJO DE USUARIO
		Mono<Usuario>usuarioMono = Mono.fromCallable(()-> new Usuario("Jhon","Doe"));
		//FLUJO DE COMENTARIOS
		Mono<Comentarios>comentariosUsuarioMono = Mono.fromCallable(()->{
			Comentarios comentarios = new Comentarios();
			comentarios.addComentarios("Hola!!");
			comentarios.addComentarios("Mañana voy a la playa.");
			comentarios.addComentarios("Estoy usando el curso de Spring con reactor.");
			return comentarios;
		});
		Mono<UsuarioConComentarios> usuarioConComentarios = usuarioMono.zipWith(comentariosUsuarioMono, (u,c) -> new UsuarioConComentarios(u,c));
		usuarioConComentarios.subscribe(uc -> log.info(uc.toString()));
	}
	
	
	
	//CLASE17:  SE UNEN 2 FLUJOS PARA CREAR UN SOLO FLUJO CON flatMap().
	public void ejemploUsuarioComentariosFlatMap() {
		//FLUJO DE USUARIO
		Mono<Usuario>usuarioMono = Mono.fromCallable(()-> new Usuario("Jhon","Doe"));
		//FLUJO DE COMENTARIOS
		Mono<Comentarios>comentariosUsuarioMono = Mono.fromCallable(()->{
			Comentarios comentarios = new Comentarios();
			comentarios.addComentarios("Hola!!");
			comentarios.addComentarios("Mañana voy a la playa.");
			comentarios.addComentarios("Estoy usando el curso de Spring con reactor.");
			return comentarios;
		});
		usuarioMono.flatMap(u -> comentariosUsuarioMono.map(c -> new UsuarioConComentarios(u, c)))
		.subscribe(uc -> log.info(uc.toString()));
	}
	
	
	//SE CONVIERTE UN FLUX DE USUARIO EN UN MONO DEL TIPO LIST DE USUARIO + LA LISTA SE IMPRIME CON UN FOR EACH DEL API STREAM:
	public void ejemploConvertirAListaMono2() throws Exception {
		List<Usuario> usuariosList = new ArrayList<>();
		usuariosList.add(new Usuario("Andres", "Guzman"));
		usuariosList.add(new Usuario("Pedro", "Fulano"));
		usuariosList.add(new Usuario("María", "Fulana"));
		usuariosList.add(new Usuario("Diego", "Sultano"));
		usuariosList.add(new Usuario("Bruce", "Lee"));
		usuariosList.add(new Usuario("Bruce", "Wayne"));
		Flux.fromIterable(usuariosList)
		.collectList() //1
		.subscribe(lista -> {
			lista.forEach(item -> log.info(item.toString()));
		});
	}
	
	//SE CONVIERTE UN FLUX DE USUARIO EN UN MONO DEL TIPO LIST DE USUARIO.
	public void ejemploConvertirAListaMono() throws Exception {

		List<Usuario> usuariosList = new ArrayList<>();
		usuariosList.add(new Usuario("Andres", "Guzman"));
		usuariosList.add(new Usuario("Pedro", "Fulano"));
		usuariosList.add(new Usuario("María", "Fulana"));
		usuariosList.add(new Usuario("Diego", "Sultano"));
		usuariosList.add(new Usuario("Bruce", "Lee"));
		usuariosList.add(new Usuario("Bruce", "Wayne"));

		Flux.fromIterable(usuariosList)
		.collectList() //1
		.subscribe(lista -> log.info(lista.toString()));
	}
		
	public void ejemploToString() throws Exception {

		List<Usuario> usuariosList = new ArrayList<>();
		usuariosList.add(new Usuario("Andres", "Guzman"));
		usuariosList.add(new Usuario("Pedro", "Fulano"));
		usuariosList.add(new Usuario("María", "Fulana"));
		usuariosList.add(new Usuario("Diego", "Sultano"));
		usuariosList.add(new Usuario("Bruce", "Lee"));
		usuariosList.add(new Usuario("Bruce", "Wayne"));

		Flux.fromIterable(usuariosList)
				.map(usuario -> usuario.getNombre().toUpperCase().concat(" ").concat(usuario.getApellido().toUpperCase()))
				.flatMap(nombre -> {
					if (nombre.contains("bruce".toUpperCase())) {
						return Mono.just(nombre);
					} else {
						return Mono.empty();
					}
				})

				.map(nombre -> {
					return nombre.toLowerCase();	
				}).subscribe(u -> log.info(u.toString()));
	}

	public void ejemploFlapMap() throws Exception {

		List<String> usuariosList = new ArrayList<>();
		usuariosList.add("Andres Guzman");
		usuariosList.add("Pedro Fulano");
		usuariosList.add("María Fulana");
		usuariosList.add("Diego Sultano");
		usuariosList.add("Bruce Lee");
		usuariosList.add("Bruce Wayne");

		Flux.fromIterable(usuariosList)
				.map(nombre -> new Usuario(nombre.split(" ")[0].toUpperCase(), nombre.split(" ")[1].toUpperCase()))
				.flatMap(usuario -> {
					if (usuario.getNombre().equalsIgnoreCase("bruce")) {
						return Mono.just(usuario);
					} else {
						return Mono.empty();
					}
				})

				.map(usuario -> {
					String nombre = usuario.getNombre().toLowerCase();
					usuario.setNombre(nombre);
					return usuario;
				}).subscribe(u -> log.info(u.toString()));
	}

	public void ejemploIterable() throws Exception {

		List<String> usuariosList = new ArrayList<>();
		usuariosList.add("Andres Guzman");
		usuariosList.add("Pedro Fulano");
		usuariosList.add("María Fulana");
		usuariosList.add("Diego Sultano");
		usuariosList.add("Bruce Lee");
		usuariosList.add("Bruce Wayne");
		Flux<String> nombres = Flux.fromIterable(usuariosList);

		Flux<Usuario> usuarios = nombres
				.map(nombre -> new Usuario(nombre.split(" ")[0].toUpperCase(), nombre.split(" ")[1].toUpperCase()))
				.filter(usuario -> usuario.getNombre().equalsIgnoreCase("bruce")).doOnNext(usuario -> {
					if (usuario == null) {
						throw new RuntimeException("Nombres no pueden ser vacíos");
					}
					System.out.println(usuario.getNombre().concat(" ").concat(usuario.getApellido()));
				}).map(usuario -> {
					String nombre = usuario.getNombre().toLowerCase();
					usuario.setNombre(nombre);
					return usuario;
				});

		usuarios.subscribe(e -> log.info(e.toString()), error -> log.error(error.getMessage()), new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				log.info("Ha finalizado la ejecución del observable con éxito");
			}
		});
	}

	private static final Logger log = LoggerFactory.getLogger(SpringbootReactorApplication.class);
}