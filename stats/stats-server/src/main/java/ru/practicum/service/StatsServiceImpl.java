package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.ViewStatsDto;
import ru.practicum.exception.BadRequestException;
import ru.practicum.mapper.StatsMapper;
import ru.practicum.EndpointHitDto;
import ru.practicum.model.StatsRecord;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;

    @Override
    @Transactional
    public EndpointHitDto create(EndpointHitDto endpointHit) {
        StatsRecord statsToSave = StatsMapper.getStatsRecord(endpointHit);
        statsRepository.save(statsToSave);
        log.info("Add stats record with id: {}", statsToSave.getId());
        return StatsMapper.getStatsHitDto(statsToSave);
    }

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (end.isBefore(start)) {
            throw new BadRequestException("");
        }
        if (unique.equals(false)) {
            log.info("Get all stats by uris: {}", uris);
            return statsRepository.findStatsByUri(uris, start, end).stream().map(StatsMapper::getViewStatDto).collect(Collectors.toList());
        } else {
            log.info("Get unique stats by uris: {}", uris);
            return statsRepository.findUniqueStatsByUri(uris, start, end).stream().map(StatsMapper::getViewStatDto).collect(Collectors.toList());
        }
    }
}