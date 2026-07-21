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

import com.generation.blogpessoal.repository.TemaRepository;

import jakarta.validation.Valid;

@RestController

@RequestMapping("/postagens")

@CrossOrigin(origins = "*", allowedHeaders = "*") // liberar requisições de servidores diferentes

public class PostagemController {

	@Autowired

	private TemaRepository temaRepository;

	@Autowired

	private PostagemRepository postagemRepository;

	@GetMapping

	public ResponseEntity<List<Postagem>> getAll() {

		return ResponseEntity.ok(postagemRepository.findAll());

	}

	@GetMapping("/{id}")

	// Resposta HTTP carrega uma Postagem e um status

	public ResponseEntity<Postagem> getById(@PathVariable Long id) {

		return postagemRepository.findById(id) // busca no banco e retorna um Optional<Postagem>

				.map(resposta -> ResponseEntity.ok(resposta)) // SE

				.orElse(ResponseEntity.notFound().build()); // SENAO

	}

	@GetMapping("titulo/{titulo}") // (etiqueta/ titulo)

	public ResponseEntity<List<Postagem>> getAllByTitulo(@PathVariable String titulo) {

		return ResponseEntity.ok(postagemRepository.findAllByTituloContainingIgnoreCase(titulo));

	}

	@PostMapping

	public ResponseEntity<Postagem> post(@Valid @RequestBody Postagem postagem) {

		if (temaRepository.existsById(postagem.getTema().getId())) {

			postagem.setId(null);

			return ResponseEntity.status(HttpStatus.CREATED)

					.body(postagemRepository.save(postagem));

		}

		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O tema não existe !", null);

	}

	@PutMapping

	public ResponseEntity<Postagem> put(@Valid @RequestBody Postagem postagem) {

		if (temaRepository.existsById(postagem.getTema().getId())) {

			if (postagemRepository.existsById(postagem.getId())) {

				return ResponseEntity.ok(postagemRepository.save(postagem));

				// UPDATE tb_postagens SET titulo = ?, texto = ? WHERE id = ?;

			}

			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O tema não existe !", null);

		}

		return ResponseEntity.notFound().build();

	}

	@ResponseStatus(HttpStatus.NO_CONTENT)

	@DeleteMapping("/{id}")

	public void delete(@PathVariable Long id) {

		Optional<Postagem> postagem = postagemRepository.findById(id);

		if (postagem.isEmpty())

			throw new ResponseStatusException(HttpStatus.NOT_FOUND);

		postagemRepository.deleteById(id);

		// DELETE FROM tb_postagens WHERE id = ?;

	}

}
