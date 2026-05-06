package com.musica.musica_api.repositories;

import com.musica.musica_api.models.Artista;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ArtistaRepo extends JpaRepository<Artista, Integer> {
	
	Page<Artista> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);

    @Query(value = "SELECT * FROM artistas ORDER BY RAND() LIMIT ?1", nativeQuery = true)
    List<Artista> findRandom(int limit);
}
