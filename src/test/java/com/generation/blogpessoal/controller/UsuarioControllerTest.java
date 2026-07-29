package com.generation.blogpessoal.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.repository.UsuarioRepository;
import com.generation.blogpessoal.service.UsuarioService;
import com.generation.blogpessoal.util.JwtHelper;
import com.generation.blogpessoal.util.TestBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment. RANDOM_PORT)
@AutoConfigureTestRestTemplate
@TestInstance(TestInstance. Lifecycle. PER_CLASS)
@TestMethodOrder(MethodOrderer.DisplayName. class)

public class UsuarioControllerTest {

	

	@Autowired
	private TestRestTemplate testRestTemplate;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private UsuarioService usuarioService;

	private static final String BASE_URL = "/usuarios";
	private static final String USUARIO = "root@root.com";
	private static final String SENHA = "rootroot";

	
	@BeforeAll
	void inicio() {
	usuarioRepository.deleteAll();
	usuarioService.cadastrarUsuario(TestBuilder.criarUsuario(null, "Root", USUARIO, SENHA));
	
	}
	
	@Test
	@DisplayName ("01 - Deve Cadastrar um novo usuário com sucesso")
	void deveCadastrarUsuario () {
		
	
	
	//Given
	Usuario usuario = TestBuilder.criarUsuario(null, "Edson Nascimento",
			"edshow.nexus@email.com", "edson1234");
	
	// when
	// Corpo da Requisição
	HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(usuario);
	
	// Enviar a Requisição
	ResponseEntity<Usuario> resposta = testRestTemplate.exchange(
			BASE_URL + "/cadastrar", HttpMethod.POST, corpoRequisicao, Usuario.class);
	
	// then

	//  trocar para OK , vai mostrar 201, e vai dar um ERRO , pois voce esperava o 200 CREATED
	assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
	assertNotNull(resposta.getBody());

	
}
	@Test
	@DisplayName ("02 - NÃO deve Cadastrar usuário duplicado")
	void naoDeveCadastrarUsuarioDuplicado () {
		
	
	
	//Given
	Usuario usuario = TestBuilder.criarUsuario(null, " Thais Santana",
			"thais.nexus@email.com", "thais1234");
	usuarioService.cadastrarUsuario(usuario);
	
	// when
	// Corpo da Requisição
	HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(usuario);
	
	// Enviar a Requisição
	ResponseEntity<Usuario> resposta = testRestTemplate.exchange(
			BASE_URL + "/cadastrar", HttpMethod.POST, corpoRequisicao, Usuario.class);
	
	// then

	//  Retornar Usuario ja cadastrado
	assertEquals(HttpStatus.BAD_REQUEST, resposta.getStatusCode());
	assertNull(resposta.getBody());

	
}
	@Test
	@DisplayName ("03 - Deve listar todos os usuários")
	void deveListarTodosUsuarios () {
		
	//Given
		usuarioService.cadastrarUsuario(TestBuilder.criarUsuario
			(null, "Nayara Porto", "nayara.nexus@email.com", "gatinhos1234"));
		usuarioService.cadastrarUsuario(TestBuilder.criarUsuario
				(null, "Guilherme Sandoli", "guilherme.nexus@email.com", "guilherme1234"));
	
	// when
		// Obter o Token
		String token = JwtHelper.obterToken(testRestTemplate, USUARIO, SENHA);

		// Cabeçalho da Requisição
		HttpEntity<Void> cabecalhoRequisicao= JwtHelper.criarRequisicaoComToken(token);

		// Enviar a Requisição
		ResponseEntity<Usuario[]> resposta = testRestTemplate. exchange(
		BASE_URL + "/all", HttpMethod.GET, cabecalhoRequisicao, Usuario[].class);
	// then

	//  
	assertEquals(HttpStatus.OK, resposta.getStatusCode());
	assertNotNull(resposta.getBody());

	
}@Test
	@DisplayName("04 - Deve Atualizar os dados do usuário com sucesso")
	void deveAtualizarUsuario() {
		// Given
 
		// Objeto para fazer o cadastro
		Usuario usuario = TestBuilder.criarUsuario(null, "Daniel", "daniel@email.com.br", "daniel1234");
 
		// Fiz o cadastro e guardei os dados objeto
		Optional<Usuario> usuarioCadastrado = usuarioService.cadastrarUsuario(usuario);
 
		// Preparar o objeto com a atualização
		Usuario usuarioUpdate = TestBuilder.criarUsuario(usuarioCadastrado.get().getId(), "Daniel ",
				"daniel_araujo@email.com.br", "abcd1234");
 
		// When
 
		// Obter o Token
		String token = JwtHelper.obterToken(testRestTemplate, USUARIO, SENHA);
 
		// Cabeçalho da Requisição
		HttpEntity<Usuario> cabecalhoRequisicao = JwtHelper.criarRequisicaoComToken(usuarioUpdate, token);
 
		
		// Enviar a Requisição
		ResponseEntity<Usuario> resposta = testRestTemplate.exchange(BASE_URL + "/atualizar", HttpMethod.PUT,
				cabecalhoRequisicao, Usuario.class);
 
		// Then
 
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
		assertNotNull(resposta.getBody());
 
	}
}