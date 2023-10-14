package ru.practicum.service;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import ru.practicum.exception.BadRequestException;
import ru.practicum.model.StatsRecord;
import ru.practicum.repository.StatsRepository;
import ru.practicum.EndpointHitDto;

import static ru.practicum.mapper.StatsMapper.fromEndpointHitDto;

import ru.practicum.ViewStatsDto;

import java.util.List;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;

    @Override
    public String addStatsRecord(EndpointHitDto endpointHitDto) {
        StatsRecord statsRecord = fromEndpointHitDto(endpointHitDto);
        statsRepository.save(statsRecord);
        log.info("Statistic record added to: {}", endpointHitDto.getUri());
        return "Statistic record added";
    }

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (end.isBefore(start)) {
            throw new BadRequestException("Wrong parameter end");
        }
        if (unique) {
            if (uris == null) {
                log.info("Get stats by unique without URI: {}", statsRepository.getStatsByUniqueWithoutUris(start, end));
                return statsRepository.getStatsByUniqueWithoutUris(start, end);
            }
            log.info("Get stats by unique: {}", statsRepository.getStatsByUniqueWithUris(start, end, uris));
            return statsRepository.getStatsByUniqueWithUris(start, end, uris);
        } else {
            if (uris == null) {
                log.info("Get stats without unique and uris: {}", statsRepository.getStatsWithoutUnique(start, end));
                return statsRepository.getStatsWithoutUnique(start, end);
            }
            log.info("Get all stats: {}", statsRepository.getStats(start, end, uris));
            return statsRepository.getStats(start, end, uris);
        }
    }
}
