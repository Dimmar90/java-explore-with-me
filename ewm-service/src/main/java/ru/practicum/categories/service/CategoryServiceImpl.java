package ru.practicum.categories.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.dto.CategoryMapper;
import ru.practicum.categories.model.Category;
import ru.practicum.categories.repository.CategoryRepository;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public Category createCategory(Category category) {
        categoryNameValidation(category.getName());
        if (categoryRepository.existsCategoryByName(category.getName())) {
            throw new ConflictException("Field: name. Error: category '" + category.getName() + "' already exist");
        }
        categoryRepository.save(category);
        log.info("Saved category: {}", category.getName());
        return category;
    }

    @Override
    @Transactional
    public Category updateCategory(Long catId, Category updatedCategory) {
        categoryNameValidation(updatedCategory.getName());
        Category categoryToUpdate = getCategoryFromDbById(catId);
        if (categoryRepository.existsCategoryByName(updatedCategory.getName())
                && !updatedCategory.getName().equals(categoryToUpdate.getName())) {
            throw new ConflictException("Field: name. Error: category '" + updatedCategory.getName()
                    + "' already exist");
        }
        categoryToUpdate.setName(updatedCategory.getName());
        log.info("Updated category with id: {}", catId);
        return categoryToUpdate;
    }

    @Override
    @Transactional
    public void deleteCategoryById(Long catId) {
        Category categoryToDelete = getCategoryFromDbById(catId);

        if (categoryToDelete.getEventsAmount() == null) {
            categoryRepository.deleteById(catId);
        } else {
            throw new ConflictException("Category can't be deleted: contains events");
        }

        log.info("Category with id :{} deleted", catId);
    }

    @Override
    public CategoryDto findCategoryById(Long catId) {
        Category searchingCategory = getCategoryFromDbById(catId);
        log.info("Get category by id: {}", catId);
        return CategoryMapper.getCategoryDto(searchingCategory);
    }

    @Override
    public List<CategoryDto> findAllCategories(Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Category> listOfAllCategories = categoryRepository.findAll(pageable).getContent();
        log.info("Get list of categories with size: {} ", listOfAllCategories.size());
        return listOfAllCategories.stream().map(CategoryMapper::getCategoryDto).collect(Collectors.toList());
    }

    private Category getCategoryFromDbById(Long catId) {
        Category categoryFromDb = categoryRepository.findById(catId)
                .orElseThrow(() ->
                        new NotFoundException("Category with id=" + catId + " was not found"));
        return categoryFromDb;
    }

    private void categoryNameValidation(String categoryName) {
        if (categoryName == null || categoryName.isBlank()) {
            throw new BadRequestException("Field: name. Error: must not be blank. Value: null");
        }
        if (categoryName.length() < 1 || categoryName.length() > 50) {
            throw new BadRequestException("Field: name. Error: length must not be less 1, or larger 50 symbols. Value: "
                    + categoryName.length());
        }
    }
}