package com.musica.musica_api.controllers;

import com.musica.musica_api.services.YoutubeService;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/youtube")
public class YoutubeController {

    private final YoutubeService youtubeService;

    public YoutubeController(YoutubeService youtubeService) {
        this.youtubeService = youtubeService;
    }

    @GetMapping("/miniatura")
    public String obtenerMiniatura(@RequestParam String url) {
        return youtubeService.obtenerMiniaturaDesdeUrl(url);
    }
}
