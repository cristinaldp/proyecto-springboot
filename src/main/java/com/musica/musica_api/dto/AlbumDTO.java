package com.musica.musica_api.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import lombok.Getter;
import lombok.Setter;
import java.sql.Date;

@Getter
@Setter
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)

public class AlbumDTO {

	private Integer id;
	private String titulo;
	private Date ano;
	private Integer idArtista;
	private String nombreArtista;
	private Integer idGenero;
	private String nombreGenero;
	private String spotifyUrl;
	
	 public AlbumDTO(Integer id, String titulo, Date ano, Integer idArtista, String nombreArtista, Integer idGenero, String nombreGenero, String spotifyUrl) {
		this.id = id;
		this.titulo = titulo;
		this.ano = ano;
		this.idArtista = idArtista;
		this.nombreArtista = nombreArtista;
		this.idGenero = idGenero;
        this.nombreGenero = nombreGenero;
        this.spotifyUrl = spotifyUrl;
	 }
	 
}
