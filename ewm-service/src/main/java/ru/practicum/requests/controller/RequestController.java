package ru.practicum.requests.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.requests.dto.RequestDto;
import ru.practicum.requests.service.RequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/requests")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto createRequest(@PathVariable Long userId,
                                    @RequestParam Long eventId) {
        return requestService.createRequest(userId, eventId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<RequestDto> findRequestsByUserId(@PathVariable Long userId) {
        return requestService.findRequestsByUserId(userId);
    }

    @PatchMapping("/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public RequestDto cancellingRequest(@PathVariable Long userId,
                                                     @PathVariable Long requestId) {
        return requestService.cancellingRequest(userId, requestId);
    }
}