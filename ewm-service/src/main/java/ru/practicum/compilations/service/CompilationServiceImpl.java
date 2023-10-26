package ru.practicum.compilations.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.CompilationMapper;
import ru.practicum.compilations.dto.CreatedCompilation;
import ru.practicum.compilations.dto.UpdatedCompilation;
import ru.practicum.compilations.model.Compilation;
import ru.practicum.compilations.repository.CompilationRepository;
import ru.practicum.events.dto.EventMapper;
import ru.practicum.events.dto.EventShortDto;
import ru.practicum.events.model.Event;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.NotFoundException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;

    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CompilationDto createCompilation(CreatedCompilation createdCompilation) {

        if (createdCompilation.getTitle() == null || createdCompilation.getTitle().isEmpty() || createdCompilation.getTitle().isBlank()) {
            throw new BadRequestException("Field: title. Error: must not be blank. Value: null");
        }

        Set<Event> events = new HashSet<>();

        if (createdCompilation.getEvents() != null) {
            for (Long eventId : createdCompilation.getEvents()) {
                Event event = eventRepository.findById(eventId)
                        .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
                events.add(event);
            }
        }

        Compilation compilation = CompilationMapper.getCompilation(createdCompilation);
        compilation.setEvents(events);

        compilation = compilationRepository.save(compilation);

        log.info("Added compilation with id: {}", compilation.getId());

        return CompilationMapper.getCompilationDto(compilation);
    }

    @Override
    @Transactional
    public void deleteCompilationById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found"));
        log.info("Deleted compilation by id: {}", compId);
        compilationRepository.delete(compilation);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(Long compId, UpdatedCompilation updatedCompilation) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found"));

        Set<Event> events = new HashSet<>();

        if (updatedCompilation.getTitle() != null) {
            compilation.setTitle(updatedCompilation.getTitle());
        }
        if (updatedCompilation.getPinned() != null) {
            compilation.setPinned(updatedCompilation.getPinned());
        }

        if (updatedCompilation.getEvents() != null) {
            for (Long eventId : updatedCompilation.getEvents()) {
                Event event = eventRepository.findById(eventId)
                        .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
                if (!events.contains(event)) {
                    events.add(event);
                }
            }
        }

        compilation.setEvents(events);

        compilationRepository.save(compilation);

        CompilationDto compilationDto = CompilationMapper.getCompilationDto(compilation);

        log.info("Update compilation with id: {}", compId);

        return compilationDto;
    }

    @Override
    @Transactional
    public List<CompilationDto> getAllCompilations(Boolean pinned, Integer from, Integer size) {
        List<CompilationDto> compilations = new ArrayList<>();

        Pageable pageable = PageRequest.of(from / size, size);

        for (Compilation compilation : compilationRepository.findAllByPinned(pinned, pageable)) {
            compilations.add(CompilationMapper.getCompilationDto(compilation));
        }

        log.info("Get all compilations");

        return compilations;
    }

    @Override
    @Transactional
    public CompilationDto getCompilationById(Long compId) {
        log.info("Get compilation with id: {}", compId);

        return CompilationMapper.getCompilationDto(compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found")));
    }
}