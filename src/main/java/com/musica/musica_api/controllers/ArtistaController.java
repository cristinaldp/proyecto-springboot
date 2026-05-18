package com.musica.musica_api.controllers;

import com.musica.musica_api.models.Artista;
import com.musica.musica_api.repositories.ArtistaRepo;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

import java.util.Optional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
	
	@GetMapping("/{id}/imagen")
	public ResponseEntity<byte[]> obtenerImagenArtista(@PathVariable Integer id) {

	    Optional<Artista> artistaOptional = artistaRepo.findById(id);

	    if (artistaOptional.isEmpty()) {
	        return ResponseEntity.notFound().build();
	    }

	    Artista artista = artistaOptional.get();

	    if (artista.getImagen() == null || artista.getImagenTipo() == null) {
	        return ResponseEntity.notFound().build();
	    }

	    return ResponseEntity.ok()
	            .contentType(MediaType.parseMediaType(artista.getImagenTipo()))
	            .body(artista.getImagen());
	}
	
	@PostMapping("/importar-imagenes")
	public ResponseEntity<String> importarImagenesArtistas() throws IOException {

	    List<Artista> artistas = artistaRepo.findAll();

	    int importadas = 0;
	    int noEncontradas = 0;

	    for (Artista artista : artistas) {

	        Path rutaImagen = Paths.get(
	                "C:/Users/crish/Desktop/Prácticas/proyecto-angular/img/" 
	                + artista.getId() 
	                + ".jpg"
	        );

	        System.out.println("Buscando imagen en: " + rutaImagen.toAbsolutePath());

	        if (Files.exists(rutaImagen)) {
	            byte[] bytesImagen = Files.readAllBytes(rutaImagen);

	            artista.setImagen(bytesImagen);
	            artista.setImagenTipo("image/jpeg");

	            artistaRepo.save(artista);

	            importadas++;
	            System.out.println("Imagen importada para artista ID: " + artista.getId());
	        } else {
	            noEncontradas++;
	            System.out.println("NO encontrada imagen para artista ID: " + artista.getId());
	        }
	    }

	    return ResponseEntity.ok("Imágenes importadas: " + importadas + ". Imágenes no encontradas: " + noEncontradas);
	}
}
