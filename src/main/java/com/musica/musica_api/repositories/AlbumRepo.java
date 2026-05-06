package com.musica.musica_api.repositories;

import com.musica.musica_api.models.Album;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlbumRepo extends JpaRepository<Album, Integer> {
}
