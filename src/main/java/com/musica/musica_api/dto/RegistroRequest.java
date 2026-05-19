package com.musica.musica_api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class RegistroRequest {
	
	private String email;
	private String nickname;
	private String contrasena;

}
