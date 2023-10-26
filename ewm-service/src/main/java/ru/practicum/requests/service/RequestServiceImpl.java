package ru.practicum.requests.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.events.dto.EventState;
import ru.practicum.events.model.Event;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.requests.dto.RequestDto;
import ru.practicum.requests.dto.RequestMapper;
import ru.practicum.requests.dto.RequestState;
import ru.practicum.requests.model.Request;
import ru.practicum.requests.repository.RequestRepository;
import ru.practicum.users.model.User;
import ru.practicum.users.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;

    private final UserRepository userRepository;

    private final EventRepository eventRepository;

    @Override
    @Transactional
    public RequestDto createRequest(Long userId, Long eventId) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Инициатор события не может добавить запрос на участие в своём событии");
        }

        if (event.getParticipantLimit() > 0) {
            if (event.getParticipantLimit() <= requestRepository.countByEventIdAndStatus(eventId, RequestState.CONFIRMED)) {
                throw new ConflictException("У события достигнут лимит запросов на участие");
            }
        }

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Нельзя участвовать в неопубликованном событии");
        }

        if (requestRepository.countByEventIdAndRequesterId(eventId, userId) > 0) {
            throw new ConflictException("Нельзя добавить повторный запрос");
        }

        Request request = Request.builder().created(LocalDateTime.now()).status(RequestState.PENDING).build();

        request.setRequester(requester);
        request.setEvent(event);
        request.setStatus((event.getRequestModeration() && !event.getParticipantLimit().equals(0)) ?
                RequestState.PENDING : RequestState.CONFIRMED);

        request = requestRepository.save(request);

        log.info("Saved request for event with id: {}, for user with id: {}",
                eventId, request);

        return RequestMapper.getRequestDto(request);
    }

    @Override
    public List<RequestDto> findRequestsByUserId(Long requesterId) {

        List<RequestDto> requestsByRequesterId = new ArrayList<>();

        for (Request request : requestRepository.findAllByRequesterId(requesterId)) {
            requestsByRequesterId.add(RequestMapper.getRequestDto(request));
        }

        log.info("Get requests for user with id: {}", requesterId);

        return requestsByRequesterId;
    }

    @Override
    public RequestDto cancellingRequest(Long userId, Long requestId) {

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request with id=" + requestId + " was not found"));

        if (!request.getRequester().getId().equals(userId)) {
            throw new NotFoundException();
        }

        request.setStatus(RequestState.CANCELED);
        request = requestRepository.save(request);

        log.info("User with id: {} canceled request with id: {}",
                userId, requestId);

        return RequestMapper.getRequestDto(request);
    }
}