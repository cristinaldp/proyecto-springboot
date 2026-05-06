package com.musica.musica_api.controllers;

import com.musica.musica_api.models.Album;
import com.musica.musica_api.repositories.AlbumRepo;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/albumes")

public class AlbumController {
	
	private final AlbumRepo albumRepo;
	
	public AlbumController(AlbumRepo albumRepo) {
		this.albumRepo=albumRepo;
	}
	
	@GetMapping
	public List<Album> listarAlbumes(
			@RequestParam(defaultValue="5") int limit,
			@RequestParam(defaultValue="0") int page){
		Pageable pageable = PageRequest.of(page, limit);
		return albumRepo.findAll(pageable).getContent();
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Album> buscarPorId(@PathVariable Integer id){
		return albumRepo.findById(id)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

}
