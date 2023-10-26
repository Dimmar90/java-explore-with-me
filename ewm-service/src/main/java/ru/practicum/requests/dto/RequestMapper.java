package ru.practicum.requests.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.requests.model.Request;

@UtilityClass
public class RequestMapper {

    public static RequestDto getRequestDto(Request request) {
        return RequestDto.builder()
                .id(request.getId())
                .created(request.getCreated())
                .event(request.getEvent().getId())
                .requester(request.getRequester().getId())
                .status(request.getStatus())
                .build();
    }
}