package ru.practicum;

import lombok.Data;

@Data
public class EndpointHit {
    private String app;
    private String uri;
    private Long hits;

    public EndpointHit(String app, String uri, Long hits) {
        this.app = app;
        this.uri = uri;
        this.hits = hits;
    }
}
