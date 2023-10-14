package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.service.StatsService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatsController {
    private final StatsService statsService;

    @PostMapping("/hit")
    public ResponseEntity<?> addStatsRecord(@RequestBody @Valid EndpointHitDto endpointHitDto) {
        return new ResponseEntity<>(statsService.addStatsRecord(endpointHitDto), CREATED);
    }

    @GetMapping("/stats")
    public ResponseEntity<List<ViewStatsDto>> getStats(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                       LocalDateTime start,
                                                       @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                       LocalDateTime end,
                                                       @RequestParam(name = "uris", required = false) List<String> uris,
                                                       @RequestParam(name = "unique", defaultValue = "false")
                                                       boolean unique) {

        return new ResponseEntity<>(statsService.getStats(start, end, uris, unique), OK);
    }
}