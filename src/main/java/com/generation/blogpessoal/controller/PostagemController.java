package com.generation.blogpessoal.controller;
 
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.generation.blogpessoal.model.Postagem;
import com.generation.blogpessoal.repository.PostagemRepository;

import jakarta.validation.Valid;
 
@RestController // Indica que esta classe é um controlador REST. Ou seja, ela vai responder requisições HTTP (GET, POST, PUT, DELETE). e retornar dados geralmente em formato JSON.
 
@RequestMapping("postagens") // Define o caminho base da URL para acessar os métodos desta classe. Exemplo: http://localhost:8080/postagens
 
@CrossOrigin(origins = "*", allowedHeaders = "*") // Permite requisições de diferentes origens (CORS). Aqui está liberado para qualquer domínio (*) acessar a API. Útil quando o frontend está em outro servidor.
public class PostagemController {
	
	@Autowired //Injeta o repository automaticamente.Transferencia de responsabilidade.
	private PostagemRepository postagemRepository; //terei acesso a todos os métodos de repository
	
	@GetMapping
	public ResponseEntity<List<Postagem>> getAll(){
		return ResponseEntity.ok(postagemRepository.findAll()); // Equivalente: SELECT * FROM t_postagens
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Postagem> getById(@PathVariable Long id){ //PathVariabl acha o ID no caminho e
		return postagemRepository.findById(id) // SELECT * FROM tb_postagens WHERE id = ?
				.map(resposta -> ResponseEntity.ok(resposta)) //Guarda o que vc achou dentro de resposta.
				.orElse(ResponseEntity.notFound().build()); //Não achou nada, está vazio o optional, então ele devolve o notFound que seria o método 404
	}
	
	@GetMapping("/titulo/{titulo}")
	public ResponseEntity<List<Postagem>> getAllByTitulo(@PathVariable String titulo){
		return ResponseEntity.ok(postagemRepository.findAllByTituloContainingIgnoreCase(titulo)); // SELECT * FROM t_postagens
	//Equivalente: SELECT * FROM tb_postangens WHERE titulo LIKE "%?%"
	}
	
	@PostMapping
	public ResponseEntity<Postagem> post(@Valid @RequestBody Postagem postagem) {//@Valid: aplica as validações definidas na entidade (ex: @NotBlank, @Size). @RequestBody: indica que os dados no corpo da requisição serão HTTP (em formato JSON)
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(postagemRepository.save(postagem));
	//Equivalente: INSERT INTO tb_postagens(titulo, texto) VALUES(?, ?);
	}
	
	@PutMapping
	public ResponseEntity<Postagem> put(@Valid @RequestBody Postagem postagem) {
		if(postagemRepository.existsById(postagem.getId()))
		return ResponseEntity.ok(postagemRepository.save(postagem));
		
	//Equivalente: UPDATE tb_postagens SET titulo = ?, texto = ? WHERE id + ?;
		
		return ResponseEntity.notFound().build();
	}
	
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@DeleteMapping("/{id}")
	public void delete(@PathVariable Long id){
		
		Optional<Postagem> postagem = postagemRepository.findById(id);
		
		if(postagem.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		
		postagemRepository.deleteById(id);
		
		//DELETE FROM tb_postagens WHERE id = ?;
		
	}
}
 
 