package com.springboot.reactor.app;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
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
//2.-SE ESTABLECE UN RANGO DE 4. 
//3.-CON "i" SE OBTIENE EL VALOR DE LOS ENTEROS Y SE MULTIPLICAN POR 2.
//4.-zipWith(): SE COMBINA EL FLUJO CON OTRO FLUJO QUE CONTIENE LOS NÚMEROS 0,1,2 3. // "uno" : ES ESTE FLUJO "Flux.just(1,2,3,4)"... "dos": ES ESTE FLUJO "Flux.range(0, 4)".
//5.-SE CREA FLUJO CON RANGO DEL 1 AL 12.
//6.-SE CREA OTRO FLUJO CON RETRASO DE 1 SEGUNDO.
//7.-SE JUNTAN LOS FLUJOS. (ra,re) -> ra) SE JUNTAN LOS FLUJOS PERO SOLO SE PASA EL FLUX RANGO.
//8.-doOnNext(i ->log.info(i.toString())) : SE HACE ALGO CON LOS VALORES                                  // doOnNext(): ES PARA APLICAR UNA TAREA. 
//9.-blockLast(): VA BLOQUEANDO LOS ELEMENTOS DEL FLUJO. SE USA ESTE MÉTODO PARA IR VIENDO EL FLUJO EN LA CONSOLA. CON SUBSCRIBE() NO SE VE.
//10.-EMITE UN VALOR LONG CADA UN SEGUNDO.
//11.-EL VALOR LONG CADA DE CADA UN SEGUNDO SE PASA A UN STRING.
//12.-SE EMITE EL STRING EN EL LOG.
//13.-SE USA UN CONTADOR, PARA QUE LA INFO SE VEA EN EL LOG. AL FINAL SE CONSIGUE LO MISMO QUE CON EL MÉTODO BLOCKLAST(). new CountDownLatch(1) : CONTADOR VA DE 1 EN 1.
//14.-SE ESPERA A QUE EL CONTADOR DEL THREAD LLEGUE A 0. CON AWAIT(), SE AGREGA AL MÉTODO "throws InterruptedException".
//15.-SE ESTABLECE DONDE TIENE QUE EMPEZAR LA CUENTA REGRESIVA, PARA BLOCKEAR EL THREAD. 
//16.-CON FLATMAP() SE LIMITA EL CONTADOR HASTA 5, SINO EL CONTADOR ES INFINNITO. SE OBTIENE CADA VALOR Y SE CAMBIA POR OTRO FLUJO OBSERVABLE. ESTE OBSERVABLE ES DE TIPO ERROR PARA QUE LANCE UNA EXCEPTION E INTERRUMPA EL OBSERVABLE.
//17.-SI i<5 SIGUE DEVOLVIENDO EL FLUJO ANTERIOR. 
//18.-SE COMENTA DOONNEXT() Y SE DESARROLLA EN EL SUBSCRIBE(), PARA MANEJAR EL ERROR QUE VA A SALIR UNA VEZ QUE EL CONTADOR >=5.
		//subscribe(s -> log.info(s), e -> log.error(e.getMessage())) : (IMPRIME EN LA CONSOLA EN CASO DE ÉXITO, MANEJA EL ERROR).
//19.-REPITE EL FLUJO "N" CANTIDAD DE VECES.
//20.-EMITER VENDRÍA SIENDO UN OBJ QUE PERMITE CREAR UN OBSERVABLE. CONTIENE Y ASIGNA EL VALOR QUE VA TENER EL FLUJO.
//21.-SCHEDULE(): PARA ASIGNAR UNA TAREA. (TAREA, EL DELAY, MOMENTO EN QUE SE VA A REPETIR LA TAREA.)
//22.-SE MUESTRA EN EL LOG. EL CONTADOR SE PASA A STRING, PARA QUE NO DÉ ERROR. YA QUE EL LOG MUESTRA STRINGS.
//23.-SE CANCELA EL TIMER Y SE CIERRA EL FLUJO AL LLEGAR EL CONTADOR A 10.
//24.-doOnComplete() : HACER ALGO CUANDO HAYA TERMINADO. EN ESTE CASO SE PASA UN MENSAJE POR CONSOLA. 
//25.-log(): ES UN OPERADOR QUE MUESTRA LA TRAZA COMPLETA DE LOS FLUX.
//26.-EL SUBSCRIBE(), ES UN SUSCRIPTOR Y ESTE HACE UN REQUEST AL FLUJO PARA QUE LE LLEGUE LA PRODUCCIÓN DEL FLUJO. AHORA, PARA MANEJAR LA CONTRAPRESIÓN, HAY QUE MODIFICAR EL REQUEST. ESTA MODIFICACIÓN SE HACE CON "onSubscribe()"
//27.-SE IMPLEMENTA INTERFAZ. <Integer> : SE LE PONE ESTE TIPO YA QUE EL RANGO ES DE TIPO INTEGER.
//28.-SE CREA VARIABLE Subscription Y POR PARÁMETRO SE GUARDA LA INFO RECIBIDA POR (), USANDO "this".  s.request(Long.MAX_VALUE): LE DICE AL FLUJO QUE MANDE TODO.
//29.-PARA MOSTRAR CADA ELEMENTO JUNTO A LA TRAZA ONNEXT.
//30.-SE AGREGAN OTROS ATRIBUTOS. LÍMITE QUE SE QUIERE DAR.
//31.-SE CONFIGURA PARA QUE EL FLUJO VAYA ENTREGANDO DE 2 EN 2.

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
		//ejemploUsuarioComentariosZipWith2();
		//ejemploZipWith3Rangos();
		//ejemploZipWith3Rangos2();
		//interval(); 
		//ejemploDelayElements();
		//ejemploIntervalInfinito();
		//ejemploIntervalInfinito();
		//ejemploIntervalDesdeCreate();
		//ejemploContraPresion();
		ejemploContraPresion2();
	}
	
	//CLASE23 : SE USA limitRate() EN VEZ DE LA INTERFAZ "new Subscriber()".
	public void ejemploContraPresion2() {
		Flux.range(1, 10)
		.log() 
		.limitRate(2)
		.subscribe();
	}
	
	
	//CLASE23: MANEJANDO LA CONTRAPRESIÓN.  //26
	public void ejemploContraPresion() {
		Flux.range(1, 10)
		//25
		.log() //27
		.subscribe(new Subscriber<Integer>() {

			//CREA UN OBJ Subscription
			private Subscription s;
			//30
			private Integer limite = 2;
			private Integer consumido = 0; 
			
			
			@Override //LO QUE SE PUEDE PEDIR AL FLUJO
			public void onSubscribe(Subscription s) {
				//28
				this.s = s;
				//s.request(Long.MAX_VALUE);
				s.request(limite);
			}

			@Override //PARA CADA VEZ QUE SE RECIBE UN OBJ
			public void onNext(Integer t) {
				//29
				log.info(t.toString());
				//31
				consumido++;
				if(consumido == limite) {
					consumido=0;
					s.request(limite);
				}
			}

			@Override //PARA MANEJAR ERROR
			public void onError(Throwable t) {
				
			}

			@Override //PARA CUANDO TERMINA
			public void onComplete() {
				
			}
		});
	}
	
	
	//CLASE22: SE CREA UN FLUX DESDE 0 CON CREATE().SE USA UNA CLASE INTERNA ABSTRACTA TimerTask
	public void ejemploIntervalDesdeCreate() {
		//20
		Flux.create(emitter ->{
			Timer timer = new Timer();
			//21
			timer.schedule(new TimerTask() {
				private Integer contador = 0;
				@Override
				public void run() {
					emitter.next(++contador);
					//23
					if(contador==10) {
						timer.cancel();
						emitter.complete();
					}
					if(contador==5) {
						timer.cancel();
						emitter.error(new InterruptedException("Error, se ha detenido el flux en 5!!"));
					}
				}
				
			},1000, 1000);
		}) 
		//22
		.doOnNext(next -> log.info(next.toString()))
		.doOnComplete(()-> log.info("Hemos terminado"))
		.subscribe();
	}
	
	//CLASE21: INTERVALO INFINITO
	public void ejemploIntervalInfinito() throws InterruptedException {
		//13
		CountDownLatch latch = new CountDownLatch(1);
		//10
		Flux.interval(Duration.ofSeconds(1))
		//15
		//.doOnTerminate(()-> latch.countDown())
		.doOnTerminate(latch :: countDown)
		//16
		.flatMap(i -> {
			if(i>=5) {
				return Flux.error(new InterruptedException("SOLO HASTA 5!!"));
			}//17
			return Flux.just(i);
		})
		
		//11
		.map(i -> "Hola " + i)
		//19
		.retry(2)
		//12
		//.doOnNext(s -> log.info(s))
		//18
		.subscribe(s -> log.info(s), e -> log.error(e.getMessage()));
		//14
		latch.await();
	}	
	
	
	
	//CLASE20: 
		public void ejemploDelayElements() {
			Flux<Integer>rango = Flux.range(1, 12)
					.delayElements(Duration.ofSeconds(1))
					.doOnNext(i ->log.info(i.toString()));
			rango.blockLast();
	
		}
	
	
	//CLASE20: CON ZIPWITH() SE COMBINAN INTERVALOS DE TIEMPO.
	public void interval() {
		//5
		Flux<Integer>rango = Flux.range(1, 12);
		//6
		Flux<Long>retraso = Flux.interval(Duration.ofSeconds(1));
		//7
		rango.zipWith(retraso,(ra,re) -> ra)
		//8   //9
		.doOnNext(i ->log.info(i.toString())).blockLast();
		
	}
	

	//CLASE19: SE COMBINAN 2 FLUX CON RANGOS DIFERENTES. CON OPERADOR RANGO PERMITE CREAR UN FLUX DE UN RANGO. SE CREA EL FLUJO DOS FUERA DE ZIPWITH()
		public void ejemploZipWith3Rangos2() {
			Flux<Integer> rangos = Flux.range(0, 4);
			Flux.just(1,2,3,4)
			.map(i-> (i*2))
			.zipWith(rangos,(uno,dos) -> String.format("Primer flux: %d, Segundo flux: %d", uno, dos))
			.subscribe(texto->log.info(texto));
		}
	
	
	//CLASE19: SE COMBINAN 2 FLUX CON RANGOS DIFERENTES. CON OPERADOR RANGO PERMITE CREAR UN FLUX DE UN RANGO. 
	public void ejemploZipWith3Rangos() {
		//2
		Flux.just(1,2,3,4)
		//3 
		.map(i-> (i*2))
		//4
		.zipWith(Flux.range(0, 4),(uno,dos) -> String.format("Primer flux: %d, Segundo flux: %d", uno, dos))
		.subscribe(texto->log.info(texto));
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
