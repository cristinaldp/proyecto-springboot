package com.musica.musica_api.controllers;

import com.musica.musica_api.models.Artista;
import com.musica.musica_api.repositories.ArtistaRepo;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/artistas")

public class ArtistaController {
	
	private final ArtistaRepo artistaRepo;
	
	public ArtistaController(ArtistaRepo artistaRepo) {
		this.artistaRepo=artistaRepo;
	}
	
	@GetMapping
	public List<Artista> listarArtistas(
			@RequestParam(defaultValue="5") int limit,
			@RequestParam(defaultValue="0") int page,
			@RequestParam(required = false) String nombre) {
		Pageable pageable = PageRequest.of(page, limit);
		
		if (nombre != null && !nombre.trim().isEmpty()) {
            return artistaRepo.findByNombreContainingIgnoreCase(nombre, pageable).getContent();
        }
		
		return artistaRepo.findAll(pageable).getContent();
	}
	
	@GetMapping("/random")
    public List<Artista> listarAleatorios(
            @RequestParam(defaultValue = "6") int limit) {
        return artistaRepo.findRandom(limit);
    }
	
	@GetMapping("/{id}")
	public ResponseEntity<Artista> buscarPorId(@PathVariable Integer id){
		return artistaRepo.findById(id)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}
	
	@GetMapping("/genero/{idGenero}")
	public List<Artista> listarArtistasPorGenero(@PathVariable Integer idGenero) {
	    return artistaRepo.findByGenero(idGenero);
	}

}
