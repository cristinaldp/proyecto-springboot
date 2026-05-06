package com.musica.musica_api.models;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="albumes")
@Getter
@Setter
@NoArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)

public class Album {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String titulo;
	private java.sql.Date ano;
	
	@Column(name = "id_artista")
	private Integer idArtista;

}
