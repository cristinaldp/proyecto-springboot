package com.musica.musica_api.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class YoutubeService {

    @Value("${youtube.api.key}")
    private String youtubeApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String obtenerMiniaturaDesdeUrl(String youtubeUrl) {

        String videoId = extraerVideoId(youtubeUrl);

        if (videoId == null) {
            return null;
        }

        String url = UriComponentsBuilder
                .fromUriString("https://www.googleapis.com/youtube/v3/videos")
                .queryParam("part", "snippet")
                .queryParam("id", videoId)
                .queryParam("key", youtubeApiKey)
                .toUriString();

        Map respuesta = restTemplate.getForObject(url, Map.class);

        List items = (List) respuesta.get("items");

        if (items.isEmpty()) {
            return null;
        }

        Map video = (Map) items.get(0);
        Map snippet = (Map) video.get("snippet");
        Map thumbnails = (Map) snippet.get("thumbnails");
        Map miniatura = (Map) thumbnails.get("high");

        return (String) miniatura.get("url");
    }

    private String extraerVideoId(String youtubeUrl) {

        Pattern pattern = Pattern.compile("(?:v=|youtu\\.be/|embed/)([a-zA-Z0-9_-]{11})");
        Matcher matcher = pattern.matcher(youtubeUrl);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }
}
