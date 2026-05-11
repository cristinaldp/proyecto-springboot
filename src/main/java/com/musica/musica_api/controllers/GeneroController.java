package com.musica.musica_api.controllers;

import com.musica.musica_api.models.Genero;
import com.musica.musica_api.repositories.GeneroRepo;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/generos")
public class GeneroController {

    private final GeneroRepo generoRepo;

    public GeneroController(GeneroRepo generoRepo) {
        this.generoRepo = generoRepo;
    }

    @GetMapping
    public List<Genero> listarGeneros() {
        return generoRepo.findAll();
    }

    @GetMapping("/artista/{idArtista}")
    public List<Genero> listarGenerosPorArtista(@PathVariable Integer idArtista) {
        return generoRepo.findGenerosByArtista(idArtista);
    }
}