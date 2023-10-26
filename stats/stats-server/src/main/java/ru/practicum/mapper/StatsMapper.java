package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.model.StatsRecord;
import ru.practicum.model.ViewStat;

@UtilityClass
public class StatsMapper {

    public static StatsRecord getStatsRecord(EndpointHitDto endpointHit) {
        return StatsRecord.builder()
                .app(endpointHit.getApp())
                .uri(endpointHit.getUri())
                .ip(endpointHit.getIp())
                .timestamp(endpointHit.getTimestamp())
                .build();
    }

    public static EndpointHitDto getStatsHitDto(StatsRecord statsRecord) {
        return EndpointHitDto.builder()
                .id(statsRecord.getId())
                .app(statsRecord.getApp())
                .uri(statsRecord.getUri())
                .timestamp(statsRecord.getTimestamp())
                .build();
    }

    public static ViewStatsDto getViewStatDto(ViewStat viewStat) {
        return ViewStatsDto.builder()
                .app(viewStat.getApp())
                .uri(viewStat.getUri())
                .hits(viewStat.getHits())
                .build();
    }
}
