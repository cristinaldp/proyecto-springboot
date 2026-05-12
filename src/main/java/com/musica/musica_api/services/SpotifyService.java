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
                .queryParam("limit", 1)
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
        Map albums = (Map) body.get("albums");
        List items = (List) albums.get("items");

        if (items.isEmpty()) {
            return null;
        }

        Map album = (Map) items.get(0);
        Map externalUrls = (Map) album.get("external_urls");

        return (String) externalUrls.get("spotify");
    }
}
