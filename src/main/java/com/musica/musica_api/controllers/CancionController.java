package com.musica.musica_api.controllers;

import com.musica.musica_api.models.Cancion;
import com.musica.musica_api.repositories.CancionRepo;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
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
			@RequestParam(defaultValue="0") int page,
			@RequestParam(required = false) String titulo){
		Pageable pageable = PageRequest.of(page, limit);
		if (titulo != null && !titulo.trim().isEmpty()) {
            return cancionRepo.findByTituloContainingIgnoreCase(titulo, pageable).getContent();
		}
		
		return cancionRepo.findAll(pageable).getContent();
	}
	
	@GetMapping("/random")
    public List<Cancion> listarAleatorias(
            @RequestParam(defaultValue = "6") int limit) {
        return cancionRepo.findRandom(limit);
    }
	
	@GetMapping("/{id}")
	public ResponseEntity<Cancion> buscarPorId(@PathVariable Integer id){
		return cancionRepo.findById(id)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}
	
	@GetMapping("/artista/{idArtista}")
	public List<Cancion> listarCancionesPorArtista(@PathVariable Integer idArtista) {
	    return cancionRepo.findByArtistaOrderByReproduccionesDesc(idArtista);
	}

}
