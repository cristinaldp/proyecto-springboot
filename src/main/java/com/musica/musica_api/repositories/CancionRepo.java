package com.musica.musica_api.repositories;

import com.musica.musica_api.models.Cancion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CancionRepo extends JpaRepository<Cancion, Integer> {
}
