package ru.practicum.events.service;

import ru.practicum.events.dto.*;
import ru.practicum.events.model.EventParam;
import ru.practicum.requests.dto.RequestDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {

    EventDto createEvent(Long userId, CreatedEventDto createdEventDto);

    List<EventDto> getEventsByAdmin(EventParam eventParam);

    List<EventShortDto> getEventsByInitiator(Long userId, Integer from, Integer size);

    List<EventShortDto> getPublicEventsWithFiltration(EventParam eventParam, HttpServletRequest request);

    EventDto getEventById(Long id, HttpServletRequest request);

    EventDto getEventByIdAndInitiator(Long userId, Long eventId);

    List<RequestDto> getRequestsByInitiatorId(Long eventId, Long userId);

    EventDto updateEventByAdmin(Long eventId, UpdatedByAdminEvent updateEventAdminRequest);

    EventDto updateEventByUser(Long userId, Long eventId, UpdatedByUserEvent updatedByUserEvent);

    UpdatedRequestStatusResult updateRequestStatus(Long userId, Long eventId,
                                                   UpdatedRequestStatus updatedRequestStatus);
}