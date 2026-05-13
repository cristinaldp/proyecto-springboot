package com.musica.musica_api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class LoginRequest {
	
	private String identificador;
	private String contrasena;

}
