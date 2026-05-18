package com.musica.musica_api.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter

public class CancionUsuarioDTO {
	
	 private Integer id;
	 private String titulo;
	 private String duracion;
	 private Long reproducciones;
	 private String url;
	 private String miniaturaUrl;
	 private LocalDateTime fechaVista;

}
