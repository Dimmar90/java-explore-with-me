package ru.practicum.events.dto;

import ru.practicum.events.location.dto.LocationDto;

import java.time.LocalDateTime;

public interface Updatable {

    String getAnnotation();

    Long getCategory();

    String getDescription();

    LocalDateTime getEventDate();

    LocationDto getLocation();

    Boolean getPaid();

    Integer getParticipantLimit();

    Boolean getRequestModeration();

    String getTitle();
}