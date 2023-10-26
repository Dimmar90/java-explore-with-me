package ru.practicum.categories.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.service.CategoryService;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/categories")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class CategoryPublicController {

    private final CategoryService categoryService;

    @GetMapping("/{catId}")
    @ResponseStatus(code = HttpStatus.OK)
    public CategoryDto findCategoryById(@PathVariable Long catId) {
        return categoryService.findCategoryById(catId);
    }

    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<CategoryDto> findAllCategories(@Valid @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                               @Valid @RequestParam(defaultValue = "10") @PositiveOrZero Integer size) {
        return categoryService.findAllCategories(from, size);
    }
}