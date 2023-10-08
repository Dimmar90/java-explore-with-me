package ru.practicum.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.StatsRecord;
import ru.practicum.service.StatsService;

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
