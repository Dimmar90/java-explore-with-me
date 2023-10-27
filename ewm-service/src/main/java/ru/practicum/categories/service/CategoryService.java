package ru.practicum.categories.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.model.Category;

import java.util.List;

public interface CategoryService {

    @Transactional
    Category createCategory(Category category);

    @Transactional
    Category updateCategory(Long catId, Category updatedCategory);

    @Transactional
    void deleteCategoryById(Long catId);

    CategoryDto findCategoryById(Long catId);

    List<CategoryDto> findAllCategories(Integer from, Integer size);
}
