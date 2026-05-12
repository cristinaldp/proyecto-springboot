package com.musica.musica_api.controllers;

import com.musica.musica_api.dto.CancionDTO;
import com.musica.musica_api.models.Cancion;
import com.musica.musica_api.repositories.CancionRepo;
import com.musica.musica_api.services.YoutubeService;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.ArrayList;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/canciones")
public class CancionController {
	
	private final CancionRepo cancionRepo;
	private final YoutubeService youtubeService;

	public CancionController(CancionRepo cancionRepo, YoutubeService youtubeService) {
	    this.cancionRepo = cancionRepo;
	    this.youtubeService = youtubeService;
	}
	
	@GetMapping
	public List<CancionDTO> listarCanciones(
	        @RequestParam(defaultValue = "5") int limit,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(required = false) String titulo) {

	    Pageable pageable = PageRequest.of(page, limit);

	    List<Cancion> canciones;

	    if (titulo != null && !titulo.trim().isEmpty()) {
	        canciones = cancionRepo.findByTituloContainingIgnoreCase(titulo, pageable).getContent();
	    } else {
	        canciones = cancionRepo.findAll(pageable).getContent();
	    }

	    List<CancionDTO> cancionesDTO = new ArrayList<>();

	    for (Cancion cancion : canciones) {
	        cancionesDTO.add(convertirADTO(cancion));
	    }

	    return cancionesDTO;
	}
	
	@GetMapping("/random")
	public List<CancionDTO> listarAleatorias(@RequestParam(defaultValue = "6") int limit) {

	    List<Cancion> canciones = cancionRepo.findRandom(limit);

	    List<CancionDTO> cancionesDTO = new ArrayList<>();

	    for (Cancion cancion : canciones) {
	        cancionesDTO.add(convertirADTO(cancion));
	    }

	    return cancionesDTO;
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<CancionDTO> buscarPorId(@PathVariable Integer id) {
	    return cancionRepo.findById(id)
	            .map(cancion -> ResponseEntity.ok(convertirADTO(cancion)))
	            .orElse(ResponseEntity.notFound().build());
	}
	
	@GetMapping("/artista/{idArtista}")
	public List<CancionDTO> listarCancionesPorArtista(@PathVariable Integer idArtista) {

	    List<Cancion> canciones = cancionRepo.findByArtistaOrderByReproduccionesDesc(idArtista);

	    List<CancionDTO> cancionesDTO = new ArrayList<>();

	    for (Cancion cancion : canciones) {
	        cancionesDTO.add(convertirADTO(cancion));
	    }

	    return cancionesDTO;
	}
	
	@GetMapping("/top")
	public List<CancionDTO> listarTopCanciones(@RequestParam(defaultValue = "5") int limit) {

	    List<Cancion> canciones = cancionRepo.findTopByReproducciones(limit);

	    List<CancionDTO> cancionesDTO = new ArrayList<>();

	    for (Cancion cancion : canciones) {
	        cancionesDTO.add(convertirADTO(cancion));
	    }

	    return cancionesDTO;
	}
	
	private CancionDTO convertirADTO(Cancion cancion) {

	    String miniaturaUrl = youtubeService.obtenerMiniaturaDesdeUrl(cancion.getUrl());

	    return new CancionDTO(
	            cancion.getId(),
	            cancion.getTitulo(),
	            cancion.getDuracion(),
	            cancion.getReproducciones(),
	            cancion.getUrl(),
	            miniaturaUrl
	    );
	}

}
