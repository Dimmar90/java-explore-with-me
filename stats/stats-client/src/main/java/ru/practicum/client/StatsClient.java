package ru.practicum.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class StatsClient {

    private final WebClient webClient;

    @Autowired
    public StatsClient(@Value("${ewm.server.url}") String serverUrl) {
        webClient = WebClient.builder().baseUrl(serverUrl).build();
    }

    public ClientResponse createEndpointHit(EndpointHitDto endpointHit) {

        return webClient.post()
                .uri("/hit")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(endpointHit))
                .exchange().block();
    }

    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {

        return webClient.get()
                .uri("/stats?start={start}&end={end}&uris={uris}&unique={unique}",
                        start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                        end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                        String.join(",", uris),
                        unique)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToFlux(ViewStatsDto.class)
                .collectList().block();
    }
}