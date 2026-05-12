package com.musica.musica_api.controllers;

import com.musica.musica_api.services.SpotifyService;

import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/spotify")
public class SpotifyController {

    private final SpotifyService spotifyService;

    public SpotifyController(SpotifyService spotifyService) {
        this.spotifyService = spotifyService;
    }

    @GetMapping("/album/url")
    public String obtenerUrlAlbum(
            @RequestParam String titulo,
            @RequestParam String artista) {

        return spotifyService.obtenerUrlAlbumSpotify(titulo, artista);
    }
}
