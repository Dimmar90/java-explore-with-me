package ru.practicum.events.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.categories.dto.CategoryMapper;
import ru.practicum.events.location.dto.LocationMapper;
import ru.practicum.events.model.Event;
import ru.practicum.users.dto.UserMapper;

@UtilityClass
public class EventMapper {

    public static Event getEvent(CreatedEventDto createdEvent) {
        return Event.builder()
                .annotation(createdEvent.getAnnotation())
                .description(createdEvent.getDescription())
                .eventDate(createdEvent.getEventDate())
                .location(createdEvent.getLocation())
                .paid(createdEvent.getPaid())
                .participantLimit(createdEvent.getParticipantLimit())
                .requestModeration(createdEvent.getRequestModeration())
                .title(createdEvent.getTitle())
                .build();
    }

    public static EventDto getEventDto(Event event) {
        return EventDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.getCategoryDto(event.getCategory()))
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .initiator(UserMapper.getUserShortDto(event.getInitiator()))
                .location(LocationMapper.getLocationDto(event.getLocation()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .build();
    }

    public static EventShortDto getEventShortDto(Event event) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.getCategoryDto(event.getCategory()))
                .eventDate(event.getEventDate())
                .initiator(UserMapper.getUserShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .build();
    }
}