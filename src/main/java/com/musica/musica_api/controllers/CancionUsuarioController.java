package com.musica.musica_api.controllers;

import com.musica.musica_api.dto.CancionDTO;
import com.musica.musica_api.dto.CancionUsuarioRequest;
import com.musica.musica_api.models.Cancion;
import com.musica.musica_api.models.CancionUsuario;
import com.musica.musica_api.repositories.CancionRepo;
import com.musica.musica_api.repositories.CancionUsuarioRepo;
import com.musica.musica_api.services.YoutubeService;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/canciones-vistas")

public class CancionUsuarioController {
	
	private final CancionUsuarioRepo cancionUsuarioRepo;
    private final CancionRepo cancionRepo;
    private final YoutubeService youtubeService;
    
    @PostMapping
    public CancionUsuario guardarVista(@RequestBody CancionUsuarioRequest request) {
        CancionUsuario vista = new CancionUsuario(
                request.getIdUsuario(),
                request.getIdCancion()
        );

        return cancionUsuarioRepo.save(vista);
    }
    
    @GetMapping("/usuario/{idUsuario}")
    public List<CancionDTO> listarVistasPorUsuario(@PathVariable Integer idUsuario) {

        List<CancionUsuario> vistas = cancionUsuarioRepo.findTop10ByIdUsuarioOrderByFechaVistaDesc(idUsuario);

        List<CancionDTO> canciones = new ArrayList<>();

        for (CancionUsuario vista : vistas) {
            Optional<Cancion> cancionOptional = cancionRepo.findById(vista.getIdCancion());

            if (cancionOptional.isPresent()) {
                Cancion cancion = cancionOptional.get();

                String miniaturaUrl = youtubeService.obtenerMiniaturaDesdeUrl(cancion.getUrl());

                CancionDTO dto = new CancionDTO(
                        cancion.getId(),
                        cancion.getTitulo(),
                        cancion.getDuracion(),
                        cancion.getReproducciones(),
                        cancion.getUrl(),
                        miniaturaUrl
                );

                canciones.add(dto);
            }
        }

        return canciones;
    }

}
