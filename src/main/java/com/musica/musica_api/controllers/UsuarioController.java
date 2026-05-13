package com.musica.musica_api.controllers;

import com.musica.musica_api.dto.LoginRequest;
import com.musica.musica_api.models.Usuario;
import com.musica.musica_api.repositories.UsuarioRepo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioRepo usuarioRepo;

    public UsuarioController(UsuarioRepo usuarioRepo) {
        this.usuarioRepo = usuarioRepo;
    }

    @GetMapping
    public List<Usuario> listarUsuarios() {
        return usuarioRepo.findAll();
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) {

        String identificador = loginRequest.getIdentificador();
        String contrasena = loginRequest.getContrasena();

        Optional<Usuario> usuario = usuarioRepo.findByNicknameAndContrasena(identificador, contrasena);

        if (usuario.isEmpty()) {
            usuario = usuarioRepo.findByEmailAndContrasena(identificador, contrasena);
        }

        Map<String, Object> respuesta = new HashMap<>();

        if (usuario.isPresent()) {
            Usuario usuarioEncontrado = usuario.get();

            respuesta.put("mensaje", "Inicio de sesión correcto");
            respuesta.put("id", usuarioEncontrado.getId());
            respuesta.put("nickname", usuarioEncontrado.getNickname());
            respuesta.put("email", usuarioEncontrado.getEmail());
            respuesta.put("rol", usuarioEncontrado.getRol());

            return ResponseEntity.ok(respuesta);
        } else {
            respuesta.put("mensaje", "Inicio de sesión no válido");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(respuesta);
        }
    }
}
