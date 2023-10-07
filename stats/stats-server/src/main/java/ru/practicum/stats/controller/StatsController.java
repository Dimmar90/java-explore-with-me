package ru.practicum.stats.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.stats.model.StatsRecord;
import ru.practicum.stats.service.StatsService;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping
public class StatsController {
    private StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @PostMapping("/hit")
    public ResponseEntity<?> addStatsRecord(@RequestBody @Valid StatsRecord createdStatsRecord) {
        return new ResponseEntity<>(statsService.addStatsRecord(createdStatsRecord), CREATED);
    }
}
