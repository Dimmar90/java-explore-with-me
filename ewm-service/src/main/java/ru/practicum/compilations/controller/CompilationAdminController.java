package ru.practicum.compilations.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.CreatedCompilation;
import ru.practicum.compilations.dto.UpdatedCompilation;
import ru.practicum.compilations.service.CompilationService;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/admin/compilations")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class CompilationAdminController {

    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createCompilation(@Valid @RequestBody CreatedCompilation createdCompilation) {
        return compilationService.createCompilation(createdCompilation);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilationById(@PathVariable Long compId) {
        compilationService.deleteCompilationById(compId);
    }

    @PatchMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto updateCompilation(@PathVariable Long compId,
                                            @Valid @RequestBody UpdatedCompilation updatedCompilation) {
        return compilationService.updateCompilation(compId, updatedCompilation);
    }
}