package com.musica.musica_api.dto;
import lombok.Getter;

@Getter

public class CancionDTO {
	
	private Integer id;
    private String titulo;
    private String duracion;
    private Long reproducciones;
    private String url;
    private String miniaturaUrl;

    public CancionDTO(Integer id, String titulo, String duracion, Long reproducciones, String url, String miniaturaUrl) {
        this.id = id;
        this.titulo = titulo;
        this.duracion = duracion;
        this.reproducciones = reproducciones;
        this.url = url;
        this.miniaturaUrl = miniaturaUrl;
    }
}
