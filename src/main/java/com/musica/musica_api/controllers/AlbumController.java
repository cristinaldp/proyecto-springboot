package com.musica.musica_api.controllers;

import com.musica.musica_api.models.Album;
import com.musica.musica_api.models.Artista;
import com.musica.musica_api.dto.AlbumDTO;
import com.musica.musica_api.repositories.AlbumRepo;
import com.musica.musica_api.repositories.ArtistaRepo;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/albumes")

public class AlbumController {
	
	private final AlbumRepo albumRepo;
	private final ArtistaRepo artistaRepo;
	
	public AlbumController(AlbumRepo albumRepo, ArtistaRepo artistaRepo) {
		this.albumRepo=albumRepo;
		this.artistaRepo = artistaRepo;
	}
	
	@GetMapping
	public List<AlbumDTO> listarAlbumes(
			@RequestParam(defaultValue="9") int limit,
			@RequestParam(defaultValue="0") int page,
			@RequestParam(required = false) String titulo){
		
		Pageable pageable = PageRequest.of(page, limit);
		List<Album> albumes;
		
		if (titulo != null && !titulo.trim().isEmpty()) {
            albumes = albumRepo.findByTituloContainingIgnoreCase(titulo, pageable).getContent();
		} else{
			albumes = albumRepo.findAll(pageable).getContent();
		}
		
		return albumes.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
	}
	
	@GetMapping("/random")
    public List<AlbumDTO> listarAleatorios(@RequestParam(defaultValue = "6") int limit) {
        return albumRepo.findRandom(limit).stream()
        		.map(this::convertirADTO)
        		.collect(Collectors.toList());
    }
	
	@GetMapping("/{id}")
	public ResponseEntity<AlbumDTO> buscarPorId(@PathVariable Integer id){
		return albumRepo.findById(id)
				.map(album -> ResponseEntity.ok(convertirADTO(album)))
				.orElse(ResponseEntity.notFound().build());
	}
	
	private AlbumDTO convertirADTO(Album album) {
        String nombreArtista = artistaRepo.findById(album.getIdArtista())
                .map(artista -> artista.getNombre())
                .orElse("Artista desconocido");
        
        return new AlbumDTO(
                album.getId(),
                album.getTitulo(),
                album.getAno(),
                album.getIdArtista(),
                nombreArtista
        );
    }

}
