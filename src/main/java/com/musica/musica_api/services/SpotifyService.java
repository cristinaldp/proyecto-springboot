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
import java.net.URI;

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

        String mejorUrl = null;
        int mejorPuntuacion = 0;
        String fechaMejorResultado = null;

        int[] offsets = {0, 10};

        for (int offset : offsets) {

        	URI url = UriComponentsBuilder
        	        .fromUriString("https://api.spotify.com/v1/search")
        	        .queryParam("q", busqueda)
        	        .queryParam("type", "album")
        	        .queryParam("market", "ES")
        	        .queryParam("limit", "10")
        	        .queryParam("offset", "0")
        	        .build()
        	        .encode()
        	        .toUri();

            System.out.println("URL SPOTIFY: " + url);

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.set("User-Agent", "PostmanRuntime/7.43.0");

            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    Map.class
            );
            
            System.out.println("RESPUESTA COMPLETA SPOTIFY:");
            System.out.println(response.getBody());

            Map body = response.getBody();

            if (body == null) {
                continue;
            }

            Map albums = (Map) body.get("albums");

            if (albums == null) {
                continue;
            }

            List items = (List) albums.get("items");

            if (items == null || items.isEmpty()) {
                continue;
            }

            for (Object item : items) {
                Map album = (Map) item;

                String nombreAlbumSpotify = (String) album.get("name");
                String albumType = (String) album.get("album_type");
                String releaseDate = (String) album.get("release_date");
                Integer totalTracks = (Integer) album.get("total_tracks");
                List artistas = (List) album.get("artists");

                System.out.println("----- SALIDA DE SPOTIFY -----");
                System.out.println("Nombre: " + nombreAlbumSpotify);
                System.out.println("Album type: " + albumType);
                System.out.println("Release date: " + releaseDate);
                System.out.println("Total tracks: " + totalTracks);

                if (!"album".equalsIgnoreCase(albumType)) {
                    System.out.println("DESCARTADO: no es album");
                    continue;
                }

                if (contienePalabraProhibida(nombreAlbumSpotify)) {
                    System.out.println("DESCARTADO: palabra prohibida");
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

                System.out.println("Titulo BD normalizado: " + tituloBD);
                System.out.println("Titulo Spotify normalizado: " + tituloSpotify);
                System.out.println("Puntuacion: " + puntuacion);

                Map externalUrls = (Map) album.get("external_urls");

                if (externalUrls == null) {
                    continue;
                }

                String spotifyUrl = (String) externalUrls.get("spotify");

                if (spotifyUrl == null) {
                    continue;
                }

                if (puntuacion > mejorPuntuacion) {
                    mejorPuntuacion = puntuacion;
                    mejorUrl = spotifyUrl;
                    fechaMejorResultado = releaseDate;
                } else if (puntuacion == mejorPuntuacion && esFechaMasAntigua(releaseDate, fechaMejorResultado)) {
                    mejorUrl = spotifyUrl;
                    fechaMejorResultado = releaseDate;
                }
            }
        }

        System.out.println("MEJOR PUNTUACION FINAL: " + mejorPuntuacion);
        System.out.println("MEJOR URL FINAL: " + mejorUrl);

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
                || textoNormalizado.contains("originally performed");
    }
    
    private boolean esFechaMasAntigua(String nuevaFecha, String fechaActual) {
        if (nuevaFecha == null || nuevaFecha.isBlank()) {
            return false;
        }

        if (fechaActual == null || fechaActual.isBlank()) {
            return true;
        }

        return nuevaFecha.compareTo(fechaActual) < 0;
    }
}
