package com.musica.musica_api.controllers;

import com.musica.musica_api.models.Album;
import com.musica.musica_api.dto.AlbumDTO;
import com.musica.musica_api.repositories.AlbumRepo;
import com.musica.musica_api.repositories.ArtistaRepo;
import com.musica.musica_api.repositories.GeneroRepo;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/albumes")

public class AlbumController {
	
	private final AlbumRepo albumRepo;
	private final ArtistaRepo artistaRepo;
	private final GeneroRepo generoRepo;
	
	public AlbumController(AlbumRepo albumRepo, ArtistaRepo artistaRepo, GeneroRepo generoRepo) {
	    this.albumRepo = albumRepo;
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
	
	@GetMapping("/genero/{idGenero}")
	public List<AlbumDTO> listarAlbumesPorGenero(@PathVariable Integer idGenero) {

	    List<Album> albumes = albumRepo.findByIdGenero(idGenero);

	    List<AlbumDTO> albumesDTO = new ArrayList<>();

	    for (Album album : albumes) {
	        AlbumDTO albumDTO = convertirADTO(album);
	        albumesDTO.add(albumDTO);
	    }

	    return albumesDTO;
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
	
	@GetMapping("/{id}/imagen")
	public ResponseEntity<byte[]> obtenerImagenAlbum(@PathVariable Integer id) {

	    Optional<Album> albumOptional = albumRepo.findById(id);

	    if (albumOptional.isEmpty()) {
	        return ResponseEntity.notFound().build();
	    }

	    Album album = albumOptional.get();

	    if (album.getImagen() == null || album.getImagenTipo() == null) {
	        return ResponseEntity.notFound().build();
	    }

	    return ResponseEntity.ok()
	            .contentType(MediaType.parseMediaType(album.getImagenTipo()))
	            .body(album.getImagen());
	}
	
	@PostMapping("/importar-imagenes")
	public ResponseEntity<String> importarImagenesAlbumes() throws IOException {

	    List<Album> albumes = albumRepo.findAll();

	    int importadas = 0;
	    int noEncontradas = 0;

	    for (Album album : albumes) {

	        Path rutaImagen = Paths.get(
	                "C:/Users/crish/Desktop/Prácticas/proyecto-angular/img1/" 
	                + album.getId() 
	                + ".jpg"
	        );

	        System.out.println("Buscando imagen en: " + rutaImagen.toAbsolutePath());

	        if (Files.exists(rutaImagen)) {
	            byte[] bytesImagen = Files.readAllBytes(rutaImagen);

	            album.setImagen(bytesImagen);
	            album.setImagenTipo("image/jpeg");

	            albumRepo.save(album);

	            importadas++;
	            System.out.println("Imagen importada para album ID: " + album.getId());
	        } else {
	            noEncontradas++;
	            System.out.println("NO encontrada imagen para album ID: " + album.getId());
	        }
	    }

	    return ResponseEntity.ok("Imágenes importadas: " + importadas + ". Imágenes no encontradas: " + noEncontradas);
	}

}
