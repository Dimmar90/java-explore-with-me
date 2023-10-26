package ru.practicum.events.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatedRequestStatus {

    private List<Long> requestIds;

    private UpdatedRequestStatus.StateAction status;

    public enum StateAction {
        CONFIRMED,
        REJECTED
    }
}