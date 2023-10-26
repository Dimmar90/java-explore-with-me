package ru.practicum.events.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.events.dto.*;
import ru.practicum.events.service.EventService;
import ru.practicum.requests.dto.RequestDto;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/events")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class EventPrivateController {

    private final EventService eventService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto createEvent(@PathVariable Long userId,
                                @Valid @RequestBody CreatedEventDto createdEvent) {
        return eventService.createEvent(userId, createdEvent);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getEventsByInitiator(@PathVariable Long userId,
                                                    @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                                    @RequestParam(value = "size", defaultValue = "10") @PositiveOrZero Integer size) {
        return eventService.getEventsByInitiator(userId, from, size);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventDto getEventByIdAndInitiator(@PathVariable Long userId,
                                             @PathVariable Long eventId) {
        return eventService.getEventByIdAndInitiator(userId, eventId);
    }

    @GetMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<RequestDto> getRequestsByInitiatorId(@PathVariable Long userId,
                                                     @PathVariable Long eventId) {
        return eventService.getRequestsByInitiatorId(eventId, userId);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventDto updateEventByUser(@PathVariable Long userId,
                                      @PathVariable Long eventId,
                                      @Valid @RequestBody UpdatedByUserEvent updatedByUserEvent) {
        return eventService.updateEventByUser(userId, eventId, updatedByUserEvent);
    }

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public UpdatedRequestStatusResult updateRequestStatus(@PathVariable Long userId,
                                                          @PathVariable Long eventId,
                                                          @RequestBody UpdatedRequestStatus updatedRequestStatus) {
        return eventService.updateRequestStatus(userId, eventId, updatedRequestStatus);
    }
}