package com.musica.musica_api.repositories;

import com.musica.musica_api.models.CancionUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CancionUsuarioRepo extends JpaRepository<CancionUsuario, Integer> {

    List<CancionUsuario> findTop10ByIdUsuarioOrderByFechaVistaDesc(Integer idUsuario);
}
