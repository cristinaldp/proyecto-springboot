package com.musica.musica_api.controllers;

import com.musica.musica_api.models.Cancion;
import com.musica.musica_api.repositories.CancionRepo;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/canciones")
public class CancionController {
	
	private final CancionRepo cancionRepo;
	
	public CancionController(CancionRepo cancionRepo) {
		this.cancionRepo=cancionRepo;
	}
	
	@GetMapping
	public List<Cancion> listarCanciones(
			@RequestParam(defaultValue="5") int limit,
			@RequestParam(defaultValue="0") int page){
		Pageable pageable = PageRequest.of(page, limit);
		return cancionRepo.findAll(pageable).getContent();
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Cancion> buscarPorId(@PathVariable Integer id){
		return cancionRepo.findById(id)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

}
