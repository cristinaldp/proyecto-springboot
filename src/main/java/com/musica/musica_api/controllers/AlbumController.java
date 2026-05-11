package com.musica.musica_api.controllers;

import com.musica.musica_api.models.Album;
import com.musica.musica_api.dto.AlbumDTO;
import com.musica.musica_api.repositories.AlbumRepo;
import com.musica.musica_api.repositories.ArtistaRepo;
import com.musica.musica_api.repositories.GeneroRepo;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.ArrayList;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/albumes")

public class AlbumController {
	
	private final AlbumRepo albumRepo;
	private final ArtistaRepo artistaRepo;
	private final GeneroRepo generoRepo;
	
	public AlbumController(AlbumRepo albumRepo, ArtistaRepo artistaRepo, GeneroRepo generoRepo) {
		this.albumRepo=albumRepo;
		this.artistaRepo = artistaRepo;
		this.generoRepo = generoRepo;
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
		
		List<AlbumDTO> albumesDTO = new ArrayList<>();

		for (Album album : albumes) {
		    AlbumDTO dto = convertirADTO(album);
		    albumesDTO.add(dto);
		}

		return albumesDTO;
	}
	
	@GetMapping("/random")
    public List<AlbumDTO> listarAleatorios(@RequestParam(defaultValue = "6") int limit) {
		
		 List<Album> albumes = albumRepo.findRandom(limit);

		 List<AlbumDTO> albumesDTO = new ArrayList<>();

		 for (Album album : albumes) {
		     AlbumDTO albumDTO = convertirADTO(album);
		     albumesDTO.add(albumDTO);
		 }

		 return albumesDTO;
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
        
        String nombreGenero = "Género desconocido";

        if (album.getIdGenero() != null) {
            nombreGenero = generoRepo.findById(album.getIdGenero())
                    .map(genero -> genero.getNombre())
                    .orElse("Género desconocido");
        }
        
        return new AlbumDTO(
                album.getId(),
                album.getTitulo(),
                album.getAno(),
                album.getIdArtista(),
                nombreArtista,
                album.getIdGenero(),
                nombreGenero
        );
    }
	
	@GetMapping("/novedades")
	public List<AlbumDTO> listarNovedades(@RequestParam(defaultValue = "6") int limit) {

	    List<Album> albumes = albumRepo.findNovedades(limit);

	    List<AlbumDTO> albumesDTO = new ArrayList<>();

	    for (Album album : albumes) {
	        AlbumDTO albumDTO = convertirADTO(album);
	        albumesDTO.add(albumDTO);
	    }

	    return albumesDTO;
	}

}
