package ru.practicum.events.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.events.dto.EventDto;
import ru.practicum.events.dto.EventShortDto;
import ru.practicum.events.dto.SortState;
import ru.practicum.events.model.EventParam;
import ru.practicum.events.service.EventService;
import ru.practicum.exception.BadRequestException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class EventPublicController {

    private final EventService eventService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getPublicEventsWithFiltration(@RequestParam(defaultValue = "") String text,
                                                             @RequestParam(required = false) List<Long> categories,
                                                             @RequestParam(required = false) Boolean paid,
                                                             @RequestParam(required = false)
                                                             @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                                             @RequestParam(required = false)
                                                             @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                                             @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                                             @RequestParam(defaultValue = "VIEWS") SortState sort,
                                                             @Valid @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                             @Valid @RequestParam(defaultValue = "10") @Positive Integer size,
                                                             HttpServletRequest request) {

        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.now().plusDays(100);
        }
        if (rangeStart.isAfter(rangeEnd)) {
            throw new BadRequestException("Range end can't be before range start");
        }

        EventParam eventParam = EventParam.builder()
                //.request(request)
                .text(text)
                .categories(categories)
                .paid(paid)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .onlyAvailable(onlyAvailable)
                .sort(sort)
                .from(from)
                .size(size)
                .build();

        return eventService.getPublicEventsWithFiltration(eventParam, request);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventDto getEventById(@PathVariable Long id,
                                 HttpServletRequest request) {
        return eventService.getEventById(id, request);
    }
}