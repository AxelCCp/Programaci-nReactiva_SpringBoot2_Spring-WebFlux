package com.springboot.webflux.app;

import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.springboot.webflux.app.models.documents.Categoria;
import com.springboot.webflux.app.models.documents.Producto;
import com.springboot.webflux.app.models.service.IProductoService;

import reactor.core.publisher.Mono;

//CLASE79
//1.-INDICA QUE SE VAA TRABAJAR CON PRUEBAS UNITARIAS CON JUNIT4
//1.1-INDICA QUE TOME LA CONFIGURACIÓN PRINCIPAL DE LA APPLICACION. O SEA LA CLASE PRINCIPAL DONDE ESTÁ EL MAIN. VA A TOMAR EL APPLICATION CONTEXT DE SPRING Y LO INCLUYE AQUÍ. GRACIAS A ESTO SE PUEDEN USAR TODOS LOS COMPONENTES QUE ESTÉN REGISTRADOS. 
	//SE DEFINE EL MODO CON EL QUE SE VAN A REALIZAR LA PRUEBAS, SE VA A USAR UN MOCK.
//1.2.-COMPONENTE PRINCIPAL PARA REALIZAR PUEBAS.
//1.3.-MÉTODO POR DEFECTO. PARA REALIZAR UNA PETICIÓN GET AL REQUEST.
	//uri("/api/v2/productos") : APUNTA A HANDLER DE LISTAR.
	//exchange() : ES PARA ENVIAR EL REQUEST Y CONSUMIR EL ENDPOINT. ESTE MÉTODO REVUELVE UN RESPONSE SPEC. Y CON ESTO SE PUEDE REALIZAR PRUEBAS.
	//expectStatus().isOk() : PRIMERA PRUEBA UNITARIA. EL HANDLER EN LA URI "/api/v2/productos" DEVUELVE UN STATUS().OK() UN 200. Y CON isOk(); SE ESTÁ PREGUNTANDO SI ESTÁ DEVOLVIENDO ESTE VALOR.
	//expectHeader().contentType(MediaType.APPLICATION_JSON): SE COMPRUEBA EL HEADER. EL CONTENT TYPE.
	//expectBodyList(Producto.class) : SE COMPRUEBA SI DEVUELVE EL BODY UNA LISTA DEL TIPO PRODUCTO.CLASS.
	//.hasSize(7) : SE COMPRUEBA SI LA LISTA TIENE ELEMENTOS EN SU INTERIOR. EN ESTE CASO LOS 7 PRODUCTOS INDICADOS AGREGADOS EN LA CLASE PRINCIPAL DEL MAIN.
//CLASE80
//2.-CON ESTE MÉTODO SE RECIBE EL RESPONSE. SE PUEDE TRABAJAR CON LA RESPUESTA. REVISAR EL LISTADO DE PRODUCTOS, ETC.
	//List<Producto>productos = response.getResponseBody() : SE OBTIENE LA LISTA DE PRODUCTOS.
	//SE REALIZA PRUEBAS UNITARIAS A LA LISTA DE PRODUCTOS. SE EVALÚA SI ES MAYOR QUE 0 Y DEVUELVE TRUE OR FALSE.
//CLASE81
//3.- MÉTODO PARA EVALUAR EL DETALLE DE PRODUCTOS
	//3.1.-Producto producto = productoService.findByNombre("TV panasonic LCD").block(); : SE OBTIENE UN PRODUCTO CON EL NUEVO MÉTODO QUE BUSCA POR NOMBRE. ESTE MÉTODO SE DESARROLLA EN LA CLASE 81, EN LAS SIGUIENTES CLASES E INTEFACES: IPRODUCTODAO, IPRODUCTOSERVICE, PRODUCTOSERVICEIMPL.
		//block(): SE DEBE USAR BLOCK PQ LAS PRUEBAS UNITARIAS NO SE PUEDEN REALIZAR DENTRO DE UN OBSERVABLE. LAS PRUEBAS SE DEBEN EJECUTAR DENTRO DEL MÉTODO verTest(){}. CON BLOCK() SE BLOQUEAN LOS MONO, POR LO TANTO DEBE SER SINCRONO. POR ESO SE CAMBIÓ EL MONO<PRODUCTOS>PRODUCTOS ...POR... Productos producto.
	//3.2.- Collections.singletonMap() : SE LE PASA EL PATH VARIABLE A LA RUTA. // CON  producto.block().getId() : CON BLOCK() SE OBTIENE EL PRODUCTO Y CON GETID() EL ID.
	//3.3.-SE HACEN PRUEBAS SOBRE EL CUERPO DEL JSON QUE RETORNA LA RESPUESTA. AL DEJAR EL expectBody() SIN PARÁMETROS, POR DEFECTO SE ESPERA UN EXPECT BODY DE JSON.
	//3.4.-OTRA MANERA HAER PRUEBAS UNITARIAS, PERO ESTA VEZ A TRAVÉS DE LA CLASE PRODUCTO.
//CLASE82
//4.-MÉTODO PARA EVALUAR CREAR.
	//4.0.-SE VA A BUSCAR UNA CATEGORIA. PARAA ESTO SE CREA EL MÉTODO findCategoriaByNombre() EN ICategoriaDao, ProductoServiceImpl, IProductoService, IProductoDao.
	//4.1.-SE CREA UN PRODUCTO CON SUS DATOS
	//4.2.-EL CONTENTTYPE() ES EL MEDIATYPE DEL REQUEST, MIENTRAS QUE  EL ACCEPT() ES EL TIPO DE CONTENIDO EN EL MEDIATYPE QUE SE QUIERE MANEJAR EN EL RESPONSE.
	//4.3.-SE ENVIA LA REQUEST CON EXCHANGE().
		//expectStatus().isCreated() : SE CONFIRMA EL STATUS DE LA RESPUESTA.
		//expectHeader().contentType(MediaType.APPLICATION_JSON) : SE CONFIRMA EL CONTENTYPE DE LA CABECERA DEL RESPONSE.
	//4.- expectBody(): SE CONFIRMA EL JSON DEL BODY DE LA RESPUESTA..
//5.- 
	//5.0.- PRUEBAS UNITARIAS A TRAVÉS DE LA CLASE PRODUCTO.
	//5.1.- SE OBTIENE EL PRODUCTO DESDE EL RESPONSE.
//CLASE83
//6
	//6.0.-SE BUSCA UN PRODUCTO POR EL NOMBRE PARA OBTENER EL ID.
	//6.1.-SE AGREGA UNA CATEGORIA. SE CAMBIA LA CATEGORÍA DE COMPUTACIÓN A ELECTRÓNICO.
	//6.2.-SE CREA PRODUCTO EDITADO.
	//6.3.-SE ENVÍA Y EDITA EL PRODUCTO YA EXISTENTE.SE LE PASA EL ID QUE SE CONSIGUE A TRAVÉS DEL OBJ PRODUCTO QUE SE OBTUVO POR MEDIO DEL NOMBRE.
	//6.4.-SE CONFIGURA EL RESQUEST.
	//6.5.-SE ENVIA EL REQUEST Y SE HACEN LAS PRUEBAS.
//CLASE84
//7
	//7.0.-TEST1 : SE COMPRUEBA QUE SE BORRO EL PRODDUCTO.
	//7.1.-TEST2 : SE COMPRUEBA QUE EL PRODUCTO BUSCADO ES NOT FOUND.
	

//@AutoConfigureWebTestClient   //ESTA ES PARA USARLA CON MOCK. SE CAMBIA RANDOM_PORT POR MOCK.
@RunWith(SpringRunner.class) //1
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) //1.1
class SpringbootWebfluxApirestApplicationTests {

	//1.3
	@Test
	public void listarTest() {
		client.get()
		.uri("/api/v2/productos")
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus().isOk()
		.expectHeader().contentType(MediaType.APPLICATION_JSON)
		.expectBodyList(Producto.class)
		//2
		.consumeWith(response-> {
			List<Producto>productos = response.getResponseBody();
			productos.forEach(p-> {
			System.out.println(p.getNombre());
		});
		Assertions.assertThat(productos.size()>0).isTrue();
		});
		//.hasSize(7);
	}
	
	//3
	@Test
	public void verTest() {
		//3.1
		Producto producto = productoService.findByNombre("TV panasonic LCD").block();
		
		client.get()
		//3.2
		.uri("/api/v2/productos/{id}", Collections.singletonMap("id", producto.getId()))
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus().isOk()
		.expectHeader().contentType(MediaType.APPLICATION_JSON)
		
		/*
		//3.3
		.expectBody()
		.jsonPath("$.id").isNotEmpty()
		.jsonPath("$.nombre").isEqualTo("TV panasonic LCD");
		*/
		
		//3.4
		.expectBody(Producto.class)
		.consumeWith(response-> {
			Producto p = response.getResponseBody();
			Assertions.assertThat(p.getId()).isNotEmpty();
			Assertions.assertThat(p.getId().length()>0).isTrue();	
			Assertions.assertThat(p.getNombre()).isEqualTo("TV panasonic LCD");
		});
	}
	
	//4
	@Test
	public void crearTest() {
		//4.0
		Categoria categoria = productoService.findCategoriaByNombre("Muebles").block();
		//4.1
		Producto producto = new Producto("Mesa comedor X",100.00,categoria);
		client.post().uri("/api/v2/productos")
		//4.2
		.contentType(MediaType.APPLICATION_JSON)
		.accept(MediaType.APPLICATION_JSON)
		.body(Mono.just(producto), Producto.class)
		//4.3
		.exchange()
		.expectStatus().isCreated()
		.expectHeader().contentType(MediaType.APPLICATION_JSON)
		//4.4
		.expectBody()
		.jsonPath("$.id").isNotEmpty()
		.jsonPath("$.nombre").isEqualTo("Mesa comedor X")
		.jsonPath("$.categoria.nombre").isEqualTo("Muebles");
	}
	
	//5
	@Test
	public void crearTest2() {
	
		Categoria categoria = productoService.findCategoriaByNombre("Muebles").block();
	
		Producto producto = new Producto("Mesa comedor X",100.00,categoria);
		client.post().uri("/api/v2/productos")
		
		.contentType(MediaType.APPLICATION_JSON)
		.accept(MediaType.APPLICATION_JSON)
		.body(Mono.just(producto), Producto.class)
	
		.exchange()
		.expectStatus().isCreated()
		.expectHeader().contentType(MediaType.APPLICATION_JSON)
		//5.0
		.expectBody(Producto.class)
		.consumeWith(response -> {
			//5.1
			Producto p = response.getResponseBody();
			Assertions.assertThat(p.getId()).isNotEmpty();
			Assertions.assertThat(p.getNombre()).isEqualTo("Mesa comedor X")	;
			Assertions.assertThat(p.getCategoria().getNombre()).isEqualTo("Muebles");
			
		});
	}
	
	//6
	@Test
	public void editarTest() {
		//6.0
		Producto producto = productoService.findByNombre("HP notebook").block();
		//6.1
		Categoria categoria = productoService.findCategoriaByNombre("Electrónico").block();
		//6.2
		Producto productoEditado = new Producto("Asus notebook",700.00,categoria);
		//6.3
		client.put().uri("/api/v2/productos/{id}",Collections.singletonMap("id", producto.getId()))
		//6.4
		.contentType(MediaType.APPLICATION_JSON)
		.accept(MediaType.APPLICATION_JSON)
		.body(Mono.just(productoEditado), Producto.class)
		//6.5
		.exchange()
		.expectStatus().isCreated()
		.expectHeader().contentType(MediaType.APPLICATION_JSON)
		.expectBody()
		.jsonPath("$.id").isNotEmpty()
		.jsonPath("$.nombre").isEqualTo("Asus notebook") //VERIFICA SI REALMENTE SE HIZO EL CAMBIO.
		.jsonPath("$.categoria.nombre").isEqualTo("Electrónico");
	}
	
	//7
	@Test
	public void eliminarTest() {
		Producto producto = productoService.findByNombre("Mica cómoda 5 cajones").block();
		
		client.delete()
		.uri("/api/v2/productos/{id}",Collections.singletonMap("id", producto.getId()))
		.exchange()
		.expectStatus().isNoContent()   //SE COMPRUEBA QUE EL CÓDIGO DE LA RESPUESTA ES UN 204 (SIN CONTENIDO).
		.expectBody().isEmpty();
		
		client.get()
		.uri("/api/v2/productos/{id}",Collections.singletonMap("id", producto.getId()))
		.exchange()
		.expectStatus().isNotFound()  
		.expectBody().isEmpty();
	}
	
	
	//1.2
	@Autowired 
	private WebTestClient client;
	
	@Autowired 
	private IProductoService productoService;
	

}
