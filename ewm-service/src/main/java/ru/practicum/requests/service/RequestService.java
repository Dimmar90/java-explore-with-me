package ru.practicum.requests.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.requests.dto.RequestDto;

import java.util.List;

public interface RequestService {
    @Transactional
    RequestDto createRequest(Long userId, Long eventId);

    List<RequestDto> findRequestsByUserId(Long userId);

    RequestDto cancellingRequest(Long userId, Long requestId);
}