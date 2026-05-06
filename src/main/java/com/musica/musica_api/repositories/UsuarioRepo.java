package com.musica.musica_api.repositories;

import com.musica.musica_api.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepo extends JpaRepository<Usuario, Integer> {
	Optional<Usuario> findByNicknameAndContrasena(String nickname, String contrasena);
}
