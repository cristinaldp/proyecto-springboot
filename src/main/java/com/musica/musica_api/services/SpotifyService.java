package com.musica.musica_api.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class SpotifyService {

    @Value("${spotify.client.id}")
    private String clientId;

    @Value("${spotify.client.secret}")
    private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    private String obtenerToken() {

        String credenciales = clientId + ":" + clientSecret;

        String credencialesBase64 = Base64.getEncoder()
                .encodeToString(credenciales.getBytes(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + credencialesBase64);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> request = new HttpEntity<>("grant_type=client_credentials", headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://accounts.spotify.com/api/token",
                request,
                Map.class
        );

        return (String) response.getBody().get("access_token");
    }

    public String obtenerUrlAlbumSpotify(String tituloAlbum, String nombreArtista) {

        String token = obtenerToken();

        String busqueda = tituloAlbum + " " + nombreArtista;

        String url = UriComponentsBuilder
                .fromUriString("https://api.spotify.com/v1/search")
                .queryParam("q", busqueda)
                .queryParam("type", "album")
                .queryParam("market", "ES")
                .queryParam("limit", 10)
                .encode()
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                Map.class
        );

        Map body = response.getBody();

        if (body == null) {
            return null;
        }

        Map albums = (Map) body.get("albums");
        List items = (List) albums.get("items");

        if (items == null || items.isEmpty()) {
            return null;
        }

        String mejorUrl = null;
        int mejorPuntuacion = 0;

        for (Object item : items) {
            Map album = (Map) item;

            String nombreAlbumSpotify = (String) album.get("name");
            String albumType = (String) album.get("album_type");
            List artistas = (List) album.get("artists");

            if (contienePalabraProhibida(nombreAlbumSpotify)) {
                continue;
            }

            int puntuacion = 0;

            String tituloBD = normalizar(tituloAlbum);
            String tituloSpotify = normalizar(nombreAlbumSpotify);

            if (tituloSpotify.equals(tituloBD)) {
                puntuacion += 60;
            } else if (tituloSpotify.contains(tituloBD) || tituloBD.contains(tituloSpotify)) {
                puntuacion += 40;
            }

            if (contieneArtista(artistas, nombreArtista)) {
                puntuacion += 40;
            }

            if ("album".equalsIgnoreCase(albumType)) {
                puntuacion += 20;
            }

            if ("single".equalsIgnoreCase(albumType)) {
                puntuacion += 5;
            }

            if (puntuacion > mejorPuntuacion) {
                Map externalUrls = (Map) album.get("external_urls");

                if (externalUrls != null) {
                    mejorUrl = (String) externalUrls.get("spotify");
                    mejorPuntuacion = puntuacion;
                }
            }
        }

        if (mejorPuntuacion >= 70) {
            return mejorUrl;
        }

        return null;
    }
    
    private String normalizar(String texto) {
        if (texto == null) {
            return "";
        }

        return java.text.Normalizer.normalize(texto, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase()
                .replaceAll("\\(.*?\\)", "")
                .replaceAll("\\[.*?\\]", "")
                .replaceAll("deluxe", "")
                .replaceAll("edition", "")
                .replaceAll("version", "")
                .replaceAll("remastered", "")
                .replaceAll("explicit", "")
                .replaceAll("clean", "")
                .replaceAll("[^a-z0-9 ]", "")
                .replaceAll("\\s+", " ")
                .trim();
    }
    
    private boolean contieneArtista(List artistas, String nombreArtista) {
        if (artistas == null) {
            return false;
        }

        for (Object artistaObj : artistas) {
            Map artista = (Map) artistaObj;
            String nombreSpotify = (String) artista.get("name");

            if (normalizar(nombreSpotify).equals(normalizar(nombreArtista))) {
                return true;
            }
        }

        return false;
    }
    
    private boolean contienePalabraProhibida(String texto) {
        String textoNormalizado = normalizar(texto);

        return textoNormalizado.contains("karaoke")
                || textoNormalizado.contains("tribute")
                || textoNormalizado.contains("cover")
                || textoNormalizado.contains("instrumental")
                || textoNormalizado.contains("originally performed")
                || textoNormalizado.contains("made famous by");
    }
}
