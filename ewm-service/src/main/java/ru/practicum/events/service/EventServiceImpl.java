package ru.practicum.events.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.categories.model.Category;
import ru.practicum.categories.repository.CategoryRepository;
import ru.practicum.client.StatsClient;
import ru.practicum.events.dto.*;
import ru.practicum.events.location.dto.LocationMapper;
import ru.practicum.events.location.model.Location;
import ru.practicum.events.location.repository.LocationRepository;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.EventParam;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exception.BadRequestException;
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

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    private final LocationRepository locationRepository;

    private final UserRepository userRepository;

    private final CategoryRepository categoryRepository;

    private final RequestRepository requestRepository;

    private final StatsClient statsClient;

    @Override
    @Transactional
    public EventDto createEvent(Long userId, CreatedEventDto createdEventDto) {
        User initiator = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));

        Category category = categoryRepository.findById(createdEventDto.getCategory())
                .orElseThrow(() -> new NotFoundException("Category: " + createdEventDto.getCategory() + " was not found"));

        Location location = createdEventDto.getLocation();

        locationRepository.save(location);

        if (createdEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException("Field: eventDate. Error: должно содержать дату, которая еще не наступила. " +
                    "Value: " + createdEventDto.getEventDate());
        }

        Event eventToAdd = EventMapper.getEvent(createdEventDto);

        eventToAdd.setCategory(category);
        eventToAdd.setLocation(location);
        eventToAdd.setInitiator(initiator);
        eventToAdd.setCreatedOn(LocalDateTime.now());
        eventToAdd.setState(EventState.PENDING);

        if (category.getEventsAmount() == null) {
            category.setEventsAmount(1);
        } else {
            category.setEventsAmount(category.getEventsAmount() + 1);
        }

        eventRepository.save(eventToAdd);
        categoryRepository.save(category);

        log.info("User with id: {}, saved event: {}", userId, createdEventDto.getTitle());

        return EventMapper.getEventDto(eventToAdd);
    }

    @Override
    public List<EventDto> getEventsByAdmin(EventParam eventParam) {
        List<EventDto> eventsSearchingByAdmin = new ArrayList<>();

        List<Event> eventsFromDb = eventRepository.findEventsByAdmin(eventParam.getUsers(), eventParam.getStates(),
                eventParam.getCategories(), eventParam.getRangeStart(), eventParam.getRangeEnd(), eventParam.getPageable());
        for (Event searchingEvent : eventsFromDb) {
            eventsSearchingByAdmin.add(EventMapper.getEventDto(searchingEvent));
        }

        for (EventDto eventDto : eventsSearchingByAdmin) {
            Integer confirmedRequest = requestRepository.countByEventIdAndStatus(eventDto.getId(), RequestState.CONFIRMED);
            eventDto.setConfirmedRequests(confirmedRequest);
        }

        log.info("Get events by admin");

        return eventsSearchingByAdmin;
    }

    @Override
    public List<EventShortDto> getEventsByInitiator(Long userId, Integer from, Integer size) {

        List<EventShortDto> eventsSearchingByInitiator = new ArrayList<>();

        Pageable pageable = PageRequest.of(from / size, size);

        for (Event searchingEventFromDb : eventRepository.findAllByInitiatorId(userId, pageable)) {
            eventsSearchingByInitiator.add(EventMapper.getEventShortDto(searchingEventFromDb));
        }

        log.info("Get all events from initiator with id: {}", userId);

        return eventsSearchingByInitiator;
    }

    @Override
    public List<EventShortDto> getPublicEventsWithFiltration(EventParam eventParam, HttpServletRequest request) {

        List<EventShortDto> eventsWithFiltration = new ArrayList<>();

        statsClient.createEndpointHit(EndpointHitDto.builder()
                .app("Explore_with_me")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build());

        List<Event> searchingEventsFromDb = eventRepository.findAllPublic(eventParam.getText(), eventParam.getCategories(), eventParam.getPaid(),
                eventParam.getRangeStart(), eventParam.getRangeEnd(), eventParam.getPageable());

        if (eventParam.getOnlyAvailable()) {
            searchingEventsFromDb = searchingEventsFromDb.stream().filter(x -> x.getParticipantLimit().equals(0)).collect(Collectors.toList());
        }

        for (Event event : searchingEventsFromDb) {
            eventsWithFiltration.add(EventMapper.getEventShortDto(event));
        }

        List<String> uris = new ArrayList<>();
        for (EventShortDto event : eventsWithFiltration) {
            uris.add("/events/" + event.getId());
        }

        List<ViewStatsDto> views = statsClient.getStats(eventParam.getRangeStart(), eventParam.getRangeEnd(), uris, true);

        if (!views.isEmpty()) {
            Map<Long, Long> mapEventsViews = new HashMap<>();

            for (ViewStatsDto viewStatsDto : views) {
                String uri = viewStatsDto.getUri();
                Long id = Long.valueOf(uri.substring(8));
                mapEventsViews.put(id, viewStatsDto.getHits());
            }

            for (EventShortDto event : eventsWithFiltration) {
                event.setViews(mapEventsViews.get(event.getId()));
            }

        } else {
            for (EventShortDto event : eventsWithFiltration) {
                event.setViews(0L);
            }
        }

        if (eventParam.getSort().equals(SortState.EVENT_DATE)) {
            eventsWithFiltration = eventsWithFiltration.stream().sorted(Comparator.comparing(EventShortDto::getEventDate)).collect(Collectors.toList());
        } else if (eventParam.getSort().equals(SortState.VIEWS)) {
            eventsWithFiltration = eventsWithFiltration.stream().sorted(Comparator.comparing(EventShortDto::getViews)).collect(Collectors.toList());
        }

        log.info("Get public events with filtration");

        return eventsWithFiltration;
    }

    @Override
    public EventDto getEventById(Long id, HttpServletRequest request) {
        Event searchingEventFromDb = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event with id=" + id + " was not found"));

        if (!searchingEventFromDb.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException("Event must be published");
        }

        statsClient.createEndpointHit(EndpointHitDto.builder()
                .app("Explore_with_me")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build());

        eventRepository.save(searchingEventFromDb);

        EventDto searchingEventById = EventMapper.getEventDto(searchingEventFromDb);

        searchingEventById.setConfirmedRequests(requestRepository.countByEventIdAndStatus(searchingEventFromDb.getId(), RequestState.CONFIRMED));

        List<ViewStatsDto> views = statsClient.getStats(LocalDateTime.now().minusYears(100), LocalDateTime.now().plusYears(100),
                List.of("/events/" + id), true);

        searchingEventById.setViews(views.get(0).getHits());
        log.info("Find event by id: {}", id);

        return searchingEventById;
    }

    @Override
    public EventDto getEventByIdAndInitiator(Long userId, Long eventId) {

        log.info("Get event with id: {}, added by user with id: {}", eventId, userId);

        return EventMapper.getEventDto(eventRepository.findByIdAndInitiatorId(eventId, userId));
    }

    @Override
    public List<RequestDto> getRequestsByInitiatorId(Long eventId, Long userId) {

        List<RequestDto> requestsByInitiator = new ArrayList<>();
        List<Request> requests = requestRepository.findAllByEventId(eventId);

        for (Request request : requests) {
            requestsByInitiator.add(RequestMapper.getRequestDto(request));
        }

        log.info("Get requests for event with id: {}, user with id: {}", eventId, userId);
        return requestsByInitiator;
    }

    @Override
    @Transactional
    public EventDto updateEventByAdmin(Long eventId, UpdatedByAdminEvent updatedByAdminEvent) {
        Event eventToUpdate = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (updatedByAdminEvent.getEventDate() != null &&
                updatedByAdminEvent.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new ForbiddenException("Field: eventDate. Error: должно быть не ранее, чем за час от даты публикации " +
                    "Value: " + updatedByAdminEvent.getEventDate());
        }

        if (updatedByAdminEvent.getStateAction() != null) {
            if (updatedByAdminEvent.getStateAction().equals(StateActionAdminRequest.PUBLISH_EVENT) &&
                    !eventToUpdate.getState().equals(EventState.PENDING)) {
                throw new ConflictException("Cannot publish the event because it's not in the right state: " + eventToUpdate.getState());
            }

            if (updatedByAdminEvent.getStateAction().equals(StateActionAdminRequest.REJECT_EVENT) &&
                    eventToUpdate.getState().equals(EventState.PUBLISHED)) {
                throw new ConflictException("Cannot reject the event because it's not in the right state: " + eventToUpdate.getState());
            }
        }

        updateEvent(updatedByAdminEvent, eventToUpdate);

        if (updatedByAdminEvent.getStateAction() != null) {
            if (updatedByAdminEvent.getStateAction().equals(StateActionAdminRequest.PUBLISH_EVENT)) {
                eventToUpdate.setState(EventState.PUBLISHED);
                eventToUpdate.setPublishedOn(LocalDateTime.now());
            }
            if (updatedByAdminEvent.getStateAction().equals(StateActionAdminRequest.REJECT_EVENT)) {
                eventToUpdate.setState(EventState.CANCELED);
            }
        }

        eventRepository.save(eventToUpdate);
        locationRepository.save(eventToUpdate.getLocation());

        log.info("Event with id: {} updated", eventId);

        return EventMapper.getEventDto(eventToUpdate);
    }

    @Override
    @Transactional
    public EventDto updateEventByUser(Long userId, Long eventId, UpdatedByUserEvent updatedByUserEvent) {
        Event eventToUpdate = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (updatedByUserEvent.getEventDate() != null &&
                updatedByUserEvent.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ForbiddenException("Field: eventDate. Error: должно содержать дату, которая еще не наступила. " +
                    "Value: " + updatedByUserEvent.getEventDate());
        }

        if (eventToUpdate.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Only pending or canceled events can be changed");
        }

        updateEvent(updatedByUserEvent, eventToUpdate);

        if (updatedByUserEvent.getStateAction() != null) {
            if (updatedByUserEvent.getStateAction().equals(StateActionUserRequest.SEND_TO_REVIEW)) {
                eventToUpdate.setState(EventState.PENDING);
            }
            if (updatedByUserEvent.getStateAction().equals(StateActionUserRequest.CANCEL_REVIEW)) {
                eventToUpdate.setState(EventState.CANCELED);
            }
        }

        eventRepository.save(eventToUpdate);
        return EventMapper.getEventDto(eventToUpdate);
    }

    @Override
    @Transactional
    public UpdatedRequestStatusResult updateRequestStatus(Long userId, Long eventId,
                                                          UpdatedRequestStatus updatedRequestStatus) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (!event.getInitiator().getId().equals(userId)) {
            throw new ForbiddenException("Wrong user id: not an initiator");
        }

        Integer confirmLimit = requestRepository.countByEventIdAndStatus(eventId, RequestState.CONFIRMED);

        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() <= confirmLimit) {
            throw new ConflictException("The participant limit has been reached");
        }

        List<Request> requestsToUpdate = requestRepository.findAllByIdIn(updatedRequestStatus.getRequestIds());
        List<Request> confirmedRequests = new ArrayList<>();
        List<Request> rejectedRequests = new ArrayList<>();

        for (Request request : requestsToUpdate) {
            if (!request.getStatus().equals(RequestState.PENDING)) {
                throw new ForbiddenException("Request must have status PENDING");
            }

            if (!request.getEvent().getId().equals(eventId)) {
                rejectedRequests.add(request);
                continue;
            }

            if (updatedRequestStatus.getStatus().equals(UpdatedRequestStatus.StateAction.CONFIRMED)) {
                if (confirmLimit < event.getParticipantLimit()) {
                    request.setStatus(RequestState.CONFIRMED);
                    confirmLimit++;
                    confirmedRequests.add(request);
                } else {
                    request.setStatus(RequestState.REJECTED);
                    rejectedRequests.add(request);
                }
            } else if (updatedRequestStatus.getStatus().equals(UpdatedRequestStatus.StateAction.REJECTED)) {
                request.setStatus(RequestState.REJECTED);
                rejectedRequests.add(request);
            }

        }

        requestRepository.saveAll(confirmedRequests);

        log.info("Changed status for requests with ids: {}",
                updatedRequestStatus.getRequestIds());

        return UpdatedRequestStatusResult.builder()
                .rejectedRequests(rejectedRequests.stream().map(RequestMapper::getRequestDto).collect(Collectors.toList()))
                .confirmedRequests(confirmedRequests.stream().map(RequestMapper::getRequestDto).collect(Collectors.toList()))
                .build();
    }

    private void updateEvent(Updatable updateEventRequest, Event eventFromDb) {
        if (updateEventRequest.getAnnotation() != null) {
            eventFromDb.setAnnotation(updateEventRequest.getAnnotation());
        }

        if (updateEventRequest.getCategory() != null) {
            Category categoryUpdateEventUserRequest = categoryRepository.findById(updateEventRequest.getCategory())
                    .orElseThrow(() ->
                            new NotFoundException("Category with id=" + updateEventRequest.getCategory() + " was not found"));
            eventFromDb.setCategory(categoryUpdateEventUserRequest);
        }

        if (updateEventRequest.getDescription() != null) {
            eventFromDb.setDescription(updateEventRequest.getDescription());
        }

        if (updateEventRequest.getEventDate() != null) {
            eventFromDb.setEventDate(updateEventRequest.getEventDate());
        }

        if (updateEventRequest.getLocation() != null) {
            eventFromDb.setLocation(LocationMapper.getLocation(updateEventRequest.getLocation()));
        }

        if (updateEventRequest.getPaid() != null) {
            eventFromDb.setPaid(updateEventRequest.getPaid());
        }

        if (updateEventRequest.getParticipantLimit() != null) {
            eventFromDb.setParticipantLimit(updateEventRequest.getParticipantLimit());
        }

        if (updateEventRequest.getRequestModeration() != null) {
            eventFromDb.setRequestModeration(updateEventRequest.getRequestModeration());
        }

        if (updateEventRequest.getTitle() != null) {
            eventFromDb.setTitle(updateEventRequest.getTitle());
        }
    }
}