package com.musica.musica_api.controllers;

import com.musica.musica_api.models.Usuario;
import com.musica.musica_api.repositories.UsuarioRepo;
import com.musica.musica_api.models.LoginRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
	
	private final UsuarioRepo usuarioRepo;
	
	public UsuarioController(UsuarioRepo usuarioRepo) {
		this.usuarioRepo=usuarioRepo;
	}
	
	@GetMapping
	public List<Usuario> listarUsuarios(){
		return usuarioRepo.findAll();
	}
	
	@PostMapping("/login")
	public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest loginRequest) {
	    Map<String, String> respuesta = new HashMap<>();

	    Optional<Usuario> usuario = usuarioRepo.findByNicknameAndContrasena(
	        loginRequest.getNickname(),
	        loginRequest.getContrasena()
	    );

	    if (usuario.isPresent()) {
	        respuesta.put("mensaje", "Inicio de sesión correcto");
	        return ResponseEntity.ok(respuesta);
	    } else {
	        respuesta.put("mensaje", "Inicio de sesión no válido");
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(respuesta);
	    }
	}
}
