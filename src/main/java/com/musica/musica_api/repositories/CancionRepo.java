package com.musica.musica_api.repositories;
import com.musica.musica_api.models.Cancion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CancionRepo extends JpaRepository<Cancion, Integer> {
	
	Page<Cancion> findByTituloContainingIgnoreCase(String titulo, Pageable pageable);
	
	@Query(value = "SELECT * FROM canciones ORDER BY RAND() LIMIT ?1", nativeQuery = true)
    List<Cancion> findRandom(int limit);
	
	@Query(
		    value = "SELECT c.* FROM canciones c " +
		            "JOIN artista_cancion ac ON c.id = ac.id_cancion " +
		            "WHERE ac.id_artista = :idArtista " +
		            "ORDER BY c.reproducciones DESC",
		    nativeQuery = true
	)
	List<Cancion> findByArtistaOrderByReproduccionesDesc(@Param("idArtista") Integer idArtista);
}
