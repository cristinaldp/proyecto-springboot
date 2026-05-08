package com.musica.musica_api.repositories;
import com.musica.musica_api.models.Album;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AlbumRepo extends JpaRepository<Album, Integer> {
	
	Page<Album> findByTituloContainingIgnoreCase(String titulo, Pageable pageable);
	
	@Query(value = "SELECT * FROM albumes ORDER BY RAND() LIMIT ?1", nativeQuery = true)
    List<Album> findRandom(int limit);
	
	@Query(value = "SELECT * FROM albumes ORDER BY ano DESC LIMIT ?1", nativeQuery = true)
	List<Album> findNovedades(int limit);
}
