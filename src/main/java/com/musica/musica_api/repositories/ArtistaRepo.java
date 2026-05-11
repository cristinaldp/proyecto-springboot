package com.musica.musica_api.repositories;

import com.musica.musica_api.models.Artista;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ArtistaRepo extends JpaRepository<Artista, Integer> {
	
	Page<Artista> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);

    @Query(value = "SELECT * FROM artistas ORDER BY RAND() LIMIT ?1", nativeQuery = true)
    List<Artista> findRandom(int limit);
    
    @Query(
    	    value = "SELECT a.* FROM artistas a " +
    	            "JOIN artista_genero ag ON a.id = ag.id_artista " +
    	            "WHERE ag.id_genero = :idGenero",
    	    nativeQuery = true
    	)
    	List<Artista> findByGenero(@Param("idGenero") Integer idGenero);
}
