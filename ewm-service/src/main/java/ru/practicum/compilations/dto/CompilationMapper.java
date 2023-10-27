package ru.practicum.compilations.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.compilations.model.Compilation;
import ru.practicum.events.dto.EventMapper;

import java.util.stream.Collectors;

@UtilityClass
public class CompilationMapper {
    public static Compilation getCompilation(CreatedCompilation createdCompilation) {
        return Compilation.builder()
                .title(createdCompilation.getTitle())
                .pinned(createdCompilation.getPinned())
                .build();
    }

    public static CompilationDto getCompilationDto(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .pinned(compilation.getPinned())
                .events(compilation.getEvents().stream().map(EventMapper::getEventShortDto).collect(Collectors.toList()))
                .build();
    }
}