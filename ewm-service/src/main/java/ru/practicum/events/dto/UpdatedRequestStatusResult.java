package ru.practicum.events.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.requests.dto.RequestDto;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatedRequestStatusResult {

    private List<RequestDto> confirmedRequests;

    private List<RequestDto> rejectedRequests;
}