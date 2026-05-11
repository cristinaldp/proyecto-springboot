package com.musica.musica_api.repositories;

import com.musica.musica_api.models.Genero;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface GeneroRepo extends JpaRepository<Genero, Integer> {
	
	  @Query(
		        value = "SELECT g.* FROM generos g " +
		                "JOIN artista_genero ag ON g.id = ag.id_genero " +
		                "WHERE ag.id_artista = :idArtista",
		        nativeQuery = true
		    )
		    List<Genero> findGenerosByArtista(@Param("idArtista") Integer idArtista);

}
