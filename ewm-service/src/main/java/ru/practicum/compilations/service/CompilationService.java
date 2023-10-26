package ru.practicum.compilations.service;

import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.CreatedCompilation;
import ru.practicum.compilations.dto.UpdatedCompilation;

import java.util.List;

public interface CompilationService {

    CompilationDto createCompilation(CreatedCompilation createdCompilation);

    void deleteCompilationById(Long compId);

    CompilationDto updateCompilation(Long compId, UpdatedCompilation updatedCompilation);

    List<CompilationDto> getAllCompilations(Boolean pinned, Integer from, Integer size);

    CompilationDto getCompilationById(Long compId);
}