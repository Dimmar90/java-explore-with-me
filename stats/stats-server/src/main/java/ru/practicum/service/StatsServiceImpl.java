package ru.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.model.StatsRecord;
import ru.practicum.repository.StatsRepository;

@Slf4j
@Service
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    public StatsServiceImpl(StatsRepository statsRepository) {
        this.statsRepository = statsRepository;
    }

    @Override
    public String addStatsRecord(StatsRecord statsRecord) {
        statsRepository.save(statsRecord);
        log.info("Information saved: add statistical record");
        return "Information saved";
    }
}
