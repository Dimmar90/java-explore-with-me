package ru.practicum.mapper;

import ru.practicum.EndpointHitDto;
import ru.practicum.model.StatsRecord;

public class StatsMapper {
    public static StatsRecord fromEndpointHitDto(EndpointHitDto endpointHitDto) {
        return StatsRecord.builder()
                .app(endpointHitDto.getApp())
                .uri(endpointHitDto.getUri())
                .ip(endpointHitDto.getIp())
                .timestamp(endpointHitDto.getTimestamp())
                .build();
    }
}
