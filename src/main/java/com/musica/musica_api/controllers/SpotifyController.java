package com.musica.musica_api.controllers;

import com.musica.musica_api.services.SpotifyService;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/spotify")
public class SpotifyController {

    private final SpotifyService spotifyService;

    public SpotifyController(SpotifyService spotifyService) {
        this.spotifyService = spotifyService;
    }

    @GetMapping("/album/url")
    public Map<String, String> obtenerUrlAlbum(
            @RequestParam String titulo,
            @RequestParam String artista) {

        String spotifyUrl = spotifyService.obtenerUrlAlbumSpotify(titulo, artista);

        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("url", spotifyUrl);

        return respuesta;
    }
}
