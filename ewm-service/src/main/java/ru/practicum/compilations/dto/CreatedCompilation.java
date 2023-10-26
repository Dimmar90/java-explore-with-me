package ru.practicum.compilations.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatedCompilation {

    private Long id;

    private List<Long> events;

    private Boolean pinned = false;

    @Size(min = 1, max = 50)
    private String title;
}